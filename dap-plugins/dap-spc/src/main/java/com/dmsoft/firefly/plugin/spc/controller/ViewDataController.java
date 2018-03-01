/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.spc.utils.*;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.google.common.collect.Maps;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Ethan.Yang on 2018/2/2.
 */
public class ViewDataController implements Initializable {
    @FXML
    private Button clearFilterBtn;
    @FXML
    private Button chooseItemBtn;
    @FXML
    private CheckBox unSelectedCheckBox;
    @FXML
    private TextField filterTf;
    @FXML
    private TableView<String> viewDataTable;
    @FXML
    private TableColumn<String, CheckBox> checkBoxColumn;
    private CheckBox allCheckBox;
    private SpcMainController spcMainController;

    private SearchDataFrame dataFrame;
    private Map<String, TableCheckBox> checkBoxMap = Maps.newHashMap();
    private ObservableList<String> rowKeys = FXCollections.observableArrayList();
    private FilteredList<String> filteredList = null;
    private Map<String, FilterSettingAndGraphic> columnFilterSetting = Maps.newHashMap();

    private QuickSearchController quickSearchController;
    private ChooseDialogController chooseDialogController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.buildQuickSearchDialog();
        this.buildChooseColumnDialog();
        this.initBtnIcon();
        this.initViewDataTable();
        this.initComponentEvent();
    }

    /**
     * set view data table dataList
     *
     * @param dataFrame search data frame
     */
    public void setViewData(SearchDataFrame dataFrame) {
        if (dataFrame == null) {
            return;
        }
        this.dataFrame = dataFrame;
        this.checkBoxMap.clear();
        this.rowKeys.clear();
        this.clearViewDataTable();
        if (dataFrame.getRowSize() != 0) {
            dataFrame.getAllTestItemName().forEach(this::buildViewDataColumn);
            this.rowKeys = new ObservableListWrapper<>(dataFrame.getAllRowKeys());
            this.rowKeys.forEach(s -> {
                this.checkBoxMap.put(s, new TableCheckBox());
            });
            this.filteredList = this.rowKeys.filtered(p -> true);
            viewDataTable.setItems(this.filteredList);
        }
    }

    /**
     * clear view data Table
     */
    public void clearViewDataTable() {
        viewDataTable.getColumns().remove(1, viewDataTable.getColumns().size());
        allCheckBox.setSelected(false);
    }

    private void buildViewDataColumn(String title) {
        TableColumn<String, String> col = new TableColumn<String, String>();
        Label label = new Label(title);
        label.getStyleClass().add("filter-header");
        Button filterBtn = new Button();
        filterBtn.getStyleClass().add("filter-normal");
        FilterSettingAndGraphic fsg = new FilterSettingAndGraphic();
        fsg.setFilterBtn(filterBtn);
//        filterBtn.setOnAction(event -> {
//            //TODO
//        });
        columnFilterSetting.put(title, fsg);
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().add(label);
        hBox.getChildren().add(filterBtn);
        hBox.getStyleClass().add("filter-hbox");
        col.setGraphic(hBox);

        col.setCellValueFactory(cellData -> new SimpleObjectProperty<>(this.dataFrame.getCellValue(cellData.getValue(), title)));
        viewDataTable.getColumns().add(col);
//        hBox.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
//            hBox.getChildren().add(filterButton);
//        });
//        hBox.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
//            hBox.getChildren().remove(filterButton);
//        });
    }

    private void buildQuickSearchDialog() {
        FXMLLoader fxmlLoader = FXMLLoaderUtils.getInstance().getLoaderFXML(ViewResource.SPC_QUICK_SEARCH_VIEW_RES);
        Pane root = null;
        try {
            root = fxmlLoader.load();
            quickSearchController = fxmlLoader.getController();
            WindowFactory.createSimpleWindowAsModel("spcQuickSearch", ResourceBundleUtils.getString(ResourceMassages.QUICK_SEARCH), root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildChooseColumnDialog() {
        FXMLLoader fxmlLoader = FXMLLoaderUtils.getInstance().getLoaderFXML(ViewResource.SPC_CHOOSE_STATISTICAL_VIEW_RES);
        Pane root = null;
        try {
            root = fxmlLoader.load();
            chooseDialogController = fxmlLoader.getController();
            WindowFactory.createSimpleWindowAsModel("spcViewDataColumn", ResourceBundleUtils.getString(ResourceMassages.CHOOSE_ITEMS_TITLE), root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initViewDataTable() {
        checkBoxColumn.setCellValueFactory(cellData -> this.checkBoxMap.get(cellData.getValue()).getCheckBox());
        allCheckBox = new CheckBox();
        checkBoxColumn.setGraphic(allCheckBox);
        viewDataTable.setItems(this.rowKeys);
    }

    private void initComponentEvent() {
        clearFilterBtn.setOnAction(event -> getClearFilterBtnEvent());
        filterTf.textProperty().addListener((observable, oldValue, newValue) -> getFilterTextFieldEvent());
        chooseItemBtn.setOnAction(event -> getChooseColumnBtnEvent());
        unSelectedCheckBox.setOnAction(event -> getUnSelectedCheckBoxEvent());
        allCheckBox.setOnAction(event -> getAllSelectEvent());
        quickSearchController.getCancelBtn().setOnAction(event -> closeQuickSearchDialogEvent());
    }

    private void getClearFilterBtnEvent() {

    }

    private void getFilterTextFieldEvent() {
        this.filteredList.setPredicate(p -> {
            List<String> datas = dataFrame.getDataRowList(p);
            for (String s : datas) {
                if (s.contains(filterTf.getText())) {
                    return true;
                }
            }
            return false;
        });
    }

    private void getChooseColumnBtnEvent() {
        StageMap.showStage("spcViewDataColumn");
    }

    private void closeQuickSearchDialogEvent() {
        StageMap.closeStage("spcQuickSearch");
    }

    private void getUnSelectedCheckBoxEvent() {
        for (TableCheckBox checkBox : this.checkBoxMap.values()) {
            checkBox.setValue(!checkBox.isSelected());
        }
    }

    private void getAllSelectEvent() {
        for (TableCheckBox checkBox : this.checkBoxMap.values()) {
            checkBox.setValue(allCheckBox.isSelected());
        }
    }

    private void getFilterBtnEvent() {
        StageMap.showStage("spcQuickSearch");
    }

    /**
     * init main controller
     *
     * @param spcMainController main controller
     */
    public void init(SpcMainController spcMainController) {
        this.spcMainController = spcMainController;
    }

    private void initBtnIcon() {
        clearFilterBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_clear_filter_normal.png")));
        chooseItemBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_test_items_normal.png")));
    }

    private enum FilterType {
        ALL_DATA, WITHIN_RANGE, WITHOUT_RANGE
    }

    private class FilterSettingAndGraphic {
        private static final String NORMAL_STYLE = "filter-normal";
        private static final String FILTER_ACTIVE = "filter-active";
        private FilterType type = FilterType.ALL_DATA;
        private String withinLowerLimit;
        private String withinUpperLimit;
        private String withoutLowerLimit;
        private String withoutUpperLimit;
        private Button filterBtn;

        FilterType getType() {
            return type;
        }

        void setType(FilterType type) {
            if (FilterType.ALL_DATA.equals(type)) {
                filterBtn.getStyleClass().remove(FILTER_ACTIVE);
                filterBtn.getStyleClass().add(NORMAL_STYLE);
            } else {
                filterBtn.getStyleClass().remove(NORMAL_STYLE);
                filterBtn.getStyleClass().add(FILTER_ACTIVE);
            }
            this.type = type;
        }

        String getWithinLowerLimit() {
            return withinLowerLimit;
        }

        void setWithinLowerLimit(String withinLowerLimit) {
            this.withinLowerLimit = withinLowerLimit;
        }

        String getWithinUpperLimit() {
            return withinUpperLimit;
        }

        void setWithinUpperLimit(String withinUpperLimit) {
            this.withinUpperLimit = withinUpperLimit;
        }

        String getWithoutLowerLimit() {
            return withoutLowerLimit;
        }

        void setWithoutLowerLimit(String withoutLowerLimit) {
            this.withoutLowerLimit = withoutLowerLimit;
        }

        String getWithoutUpperLimit() {
            return withoutUpperLimit;
        }

        void setWithoutUpperLimit(String withoutUpperLimit) {
            this.withoutUpperLimit = withoutUpperLimit;
        }

        Button getFilterBtn() {
            return filterBtn;
        }

        void setFilterBtn(Button filterBtn) {
            this.filterBtn = filterBtn;
        }
    }
}
