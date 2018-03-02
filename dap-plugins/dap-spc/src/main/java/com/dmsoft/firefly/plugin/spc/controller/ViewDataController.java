/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.table.NewTableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.spc.model.ChooseTableRowData;
import com.dmsoft.firefly.plugin.spc.model.ViewDataTableModel;
import com.dmsoft.firefly.plugin.spc.utils.*;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

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

    private SpcMainController spcMainController;

    private List<ChooseTableRowData> chooseTableRowDataList = Lists.newArrayList();

    private SearchDataFrame dataFrame;
    private Map<String, FilterSettingAndGraphic> columnFilterSetting = Maps.newHashMap();

    private ChooseDialogController chooseDialogController;

    private ViewDataTableModel viewDataTableModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.buildChooseColumnDialog();
        this.initBtnIcon();
        this.initViewDataTable();
        this.initComponentEvent();
        List<TestItemWithTypeDto> typeDtoList = RuntimeContext.getBean(EnvService.class).findTestItems();
        for (TestItemWithTypeDto typeDto : typeDtoList) {
            ChooseTableRowData chooseTableRowData = new ChooseTableRowData(false, typeDto.getTestItemName());
            chooseTableRowDataList.add(chooseTableRowData);
        }
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
//        this.checkBoxMap.clear();
//        this.rowKeys.clear();
//        this.clearViewDataTable();
//        if (dataFrame.getRowSize() != 0) {
//            dataFrame.getAllTestItemName().forEach(this::buildViewDataColumn);
//            this.rowKeys = new ObservableListWrapper<>(dataFrame.getAllRowKeys());
//            this.rowKeys.forEach(s -> {
//                this.checkBoxMap.put(s, new TableCheckBox());
//            });
//            this.filteredList = this.rowKeys.filtered(p -> true);
//            viewDataTable.setItems(this.filteredList);
//        }

        viewDataTable.getColumns().clear();
        viewDataTableModel.initData(dataFrame);
        viewDataTable.getColumns().forEach(this::decorateColumnHeader);
        for (ChooseTableRowData rowData : chooseTableRowDataList) {
            if (dataFrame.isTestItemExist(rowData.getValue())) {
                rowData.getSelector().setValue(true);
            } else {
                rowData.getSelector().setValue(false);
            }
        }
        chooseDialogController.setTableData(chooseTableRowDataList);
    }

    private void decorateColumnHeader(TableColumn<String, ?> col) {
        String title = col.getText();
        if (StringUtils.isBlank(title)) {
            return;
        }
        col.setText(null);
        Label label = new Label(title);
        label.getStyleClass().add("filter-header");
        Button filterBtn = new Button();
        filterBtn.getStyleClass().add("filter-normal");
        FilterSettingAndGraphic fsg = new FilterSettingAndGraphic();
        fsg.setFilterBtn(filterBtn);
        filterBtn.setOnAction(event -> {
            QuickSearchController quickSearchController = new QuickSearchController();
            FXMLLoader fxmlLoader = FXMLLoaderUtils.getInstance().getLoaderFXML(ViewResource.SPC_QUICK_SEARCH_VIEW_RES);
            fxmlLoader.setController(quickSearchController);
            Pane root = null;
            Stage stage = null;
            try {
                root = fxmlLoader.load();
                stage = WindowFactory.createOrUpdateSimpleWindowAsModel("spcQuickSearch", ResourceBundleUtils.getString(ResourceMassages.QUICK_SEARCH), root);
            } catch (IOException e) {
                e.printStackTrace();
            }
            quickSearchController.setStage(stage);
            FilterType type = columnFilterSetting.get(title).getType();
            switch (type) {
                case ALL_DATA:
                    quickSearchController.activeAllData();
                    break;
                case WITHIN_RANGE:
                    quickSearchController.activeWithinRange();
                    quickSearchController.getWithinLowerTf().setText(columnFilterSetting.get(title).getWithinLowerLimit());
                    quickSearchController.getWithinUpperTf().setText(columnFilterSetting.get(title).getWithinUpperLimit());
                    break;
                case WITHOUT_RANGE:
                    quickSearchController.activeWithoutRange();
                    quickSearchController.getWithoutLowerTf().setText(columnFilterSetting.get(title).getWithoutLowerLimit());
                    quickSearchController.getWithoutUpperTf().setText(columnFilterSetting.get(title).getWithoutUpperLimit());
                    break;
                default:
                    break;
            }
            quickSearchController.getSearchBtn().setOnAction(event1 -> {
                FilterType type1 = quickSearchController.getFilterType();
                if (type1 != type) {
                    switch (type1) {
                        case ALL_DATA:
                            columnFilterSetting.get(title).setType(FilterType.ALL_DATA);
                            columnFilterSetting.get(title).setWithinLowerLimit(null);
                            columnFilterSetting.get(title).setWithinUpperLimit(null);
                            columnFilterSetting.get(title).setWithoutLowerLimit(null);
                            columnFilterSetting.get(title).setWithoutUpperLimit(null);
                            quickSearchHandler(title);
                            quickSearchController.getStage().close();
                            break;
                        case WITHIN_RANGE:
                            columnFilterSetting.get(title).setType(FilterType.WITHIN_RANGE);
                            columnFilterSetting.get(title).setWithinLowerLimit(quickSearchController.getWithinLowerTf().getText());
                            columnFilterSetting.get(title).setWithinUpperLimit(quickSearchController.getWithinUpperTf().getText());
                            columnFilterSetting.get(title).setWithoutLowerLimit(null);
                            columnFilterSetting.get(title).setWithoutUpperLimit(null);
                            quickSearchHandler(title);
                            quickSearchController.getStage().close();
                            break;
                        case WITHOUT_RANGE:
                            columnFilterSetting.get(title).setType(FilterType.WITHOUT_RANGE);
                            columnFilterSetting.get(title).setWithinLowerLimit(null);
                            columnFilterSetting.get(title).setWithinUpperLimit(null);
                            columnFilterSetting.get(title).setWithoutLowerLimit(quickSearchController.getWithoutLowerTf().getText());
                            columnFilterSetting.get(title).setWithoutUpperLimit(quickSearchController.getWithoutUpperTf().getText());
                            quickSearchHandler(title);
                            quickSearchController.getStage().close();
                            break;
                        default:
                            break;
                    }
                }
            });
            quickSearchController.getCancelBtn().setOnAction(event1 -> {
                quickSearchController.getStage().close();
            });
            stage.show();
        });
        columnFilterSetting.put(title, fsg);
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().add(label);
        hBox.getChildren().add(filterBtn);
        hBox.getStyleClass().add("filter-hbox");
        col.setGraphic(hBox);
    }

    /**
     * clear view data Table
     */
//    public void clearViewDataTable() {
//        viewDataTable.getColumns().remove(1, viewDataTable.getColumns().size());
////        allCheckBox.setSelected(false);
//    }

//    private void buildViewDataColumn(String title) {
//        TableColumn<String, String> col = new TableColumn<String, String>();
//        Label label = new Label(title);
//        label.getStyleClass().add("filter-header");
//        Button filterBtn = new Button();
//        filterBtn.getStyleClass().add("filter-normal");
//        FilterSettingAndGraphic fsg = new FilterSettingAndGraphic();
//        fsg.setFilterBtn(filterBtn);
//        filterBtn.setOnAction(event -> {
//            QuickSearchController quickSearchController = new QuickSearchController();
//            FXMLLoader fxmlLoader = FXMLLoaderUtils.getInstance().getLoaderFXML(ViewResource.SPC_QUICK_SEARCH_VIEW_RES);
//            fxmlLoader.setController(quickSearchController);
//            Pane root = null;
//            Stage stage = null;
//            try {
//                root = fxmlLoader.load();
//                stage = WindowFactory.createOrUpdateSimpleWindowAsModel("spcQuickSearch", ResourceBundleUtils.getString(ResourceMassages.QUICK_SEARCH), root);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            quickSearchController.setStage(stage);
//            FilterType type = columnFilterSetting.get(title).getType();
//            switch (type) {
//                case ALL_DATA:
//                    quickSearchController.activeAllData();
//                    break;
//                case WITHIN_RANGE:
//                    quickSearchController.activeWithinRange();
//                    quickSearchController.getWithinLowerTf().setText(columnFilterSetting.get(title).getWithinLowerLimit());
//                    quickSearchController.getWithinUpperTf().setText(columnFilterSetting.get(title).getWithinUpperLimit());
//                    break;
//                case WITHOUT_RANGE:
//                    quickSearchController.activeWithoutRange();
//                    quickSearchController.getWithoutLowerTf().setText(columnFilterSetting.get(title).getWithoutLowerLimit());
//                    quickSearchController.getWithoutUpperTf().setText(columnFilterSetting.get(title).getWithoutUpperLimit());
//                    break;
//                default:
//                    break;
//            }
//            quickSearchController.getSearchBtn().setOnAction(event1 -> {
//                FilterType type1 = quickSearchController.getFilterType();
//                if (type1 != type) {
//                    switch (type1) {
//                        case ALL_DATA:
//                            columnFilterSetting.get(title).setType(FilterType.ALL_DATA);
//                            columnFilterSetting.get(title).setWithinLowerLimit(null);
//                            columnFilterSetting.get(title).setWithinUpperLimit(null);
//                            columnFilterSetting.get(title).setWithoutLowerLimit(null);
//                            columnFilterSetting.get(title).setWithoutUpperLimit(null);
//                            quickSearchHandler(title);
//                            quickSearchController.getStage().close();
//                            break;
//                        case WITHIN_RANGE:
//                            columnFilterSetting.get(title).setType(FilterType.WITHIN_RANGE);
//                            columnFilterSetting.get(title).setWithinLowerLimit(quickSearchController.getWithinLowerTf().getText());
//                            columnFilterSetting.get(title).setWithinUpperLimit(quickSearchController.getWithinUpperTf().getText());
//                            columnFilterSetting.get(title).setWithoutLowerLimit(null);
//                            columnFilterSetting.get(title).setWithoutUpperLimit(null);
//                            quickSearchHandler(title);
//                            quickSearchController.getStage().close();
//                            break;
//                        case WITHOUT_RANGE:
//                            columnFilterSetting.get(title).setType(FilterType.WITHOUT_RANGE);
//                            columnFilterSetting.get(title).setWithinLowerLimit(null);
//                            columnFilterSetting.get(title).setWithinUpperLimit(null);
//                            columnFilterSetting.get(title).setWithoutLowerLimit(quickSearchController.getWithoutLowerTf().getText());
//                            columnFilterSetting.get(title).setWithoutUpperLimit(quickSearchController.getWithoutUpperTf().getText());
//                            quickSearchHandler(title);
//                            quickSearchController.getStage().close();
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            });
//            quickSearchController.getCancelBtn().setOnAction(event1 -> {
//                quickSearchController.getStage().close();
//            });
//            stage.show();
//        });
//        columnFilterSetting.put(title, fsg);
//        HBox hBox = new HBox();
//        hBox.setAlignment(Pos.CENTER_LEFT);
//        hBox.getChildren().add(label);
//        hBox.getChildren().add(filterBtn);
//        hBox.getStyleClass().add("filter-hbox");
//        col.setGraphic(hBox);
//
//        col.setCellValueFactory(cellData -> new SimpleObjectProperty<>(this.dataFrame.getCellValue(cellData.getValue(), title)));
//        viewDataTable.getColumns().add(col);
//    }
    private void quickSearchHandler(String columnName) {
        //TODO
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
        viewDataTableModel = new ViewDataTableModel();
        NewTableViewWrapper.decorate(viewDataTable, viewDataTableModel);
    }

    private void initComponentEvent() {
        clearFilterBtn.setOnAction(event -> getClearFilterBtnEvent());
        filterTf.textProperty().addListener((observable, oldValue, newValue) -> getFilterTextFieldEvent());
        chooseItemBtn.setOnAction(event -> getChooseColumnBtnEvent());
        unSelectedCheckBox.setOnAction(event -> getUnSelectedCheckBoxEvent());
    }

    private void getClearFilterBtnEvent() {
        for (String s : columnFilterSetting.keySet()) {
            FilterSettingAndGraphic fsg = columnFilterSetting.get(s);
            fsg.setType(FilterType.ALL_DATA);
            fsg.setWithinLowerLimit(null);
            fsg.setWithinUpperLimit(null);
            fsg.setWithoutLowerLimit(null);
            fsg.setWithoutUpperLimit(null);
        }
    }

    private void getFilterTextFieldEvent() {
        viewDataTableModel.filterTestItem(filterTf.getText());
    }

    private void getChooseColumnBtnEvent() {
        StageMap.showStage("spcViewDataColumn");
    }

    private void getUnSelectedCheckBoxEvent() {
        viewDataTableModel.invertCheckBoxRow();
    }

    private Stage getFilterStage() {
        return StageMap.getStage("spcQuickSearch");
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

    /**
     * inner class for filter setting and graphic
     */
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
