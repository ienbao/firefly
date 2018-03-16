/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.spc.model.ChooseTableRowData;
import com.dmsoft.firefly.plugin.spc.model.ViewDataDFModel;
import com.dmsoft.firefly.plugin.spc.utils.*;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dataframe.DataColumn;
import com.dmsoft.firefly.sdk.dataframe.DataFrameFactory;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.utils.RangeUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

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
    private TextFieldFilter filterTf;
    @FXML
    private TableView<String> viewDataTable;
    private SpcMainController spcMainController;

    private List<ChooseTableRowData> chooseTableRowDataList = Lists.newArrayList();

    private SearchDataFrame dataFrame;
    private Map<String, FilterSettingAndGraphic> columnFilterSetting = Maps.newHashMap();
    private ViewDataDFModel model;
    private List<TestItemWithTypeDto> typeDtoList;
    private List<String> selectedProjectNames;

    private ChooseDialogController chooseDialogController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.filterTf.getTextField().setPromptText(SpcFxmlAndLanguageUtils.getString(ResourceMassages.FILTER_VALUE_PROMPT));
        this.buildChooseColumnDialog();
        this.initBtnIcon();
        this.initComponentEvent();
        this.typeDtoList = RuntimeContext.getBean(EnvService.class).findTestItems();
        this.selectedProjectNames = RuntimeContext.getBean(EnvService.class).findActivatedProjectName();
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
        Platform.runLater(() -> {
            if (dataFrame == null) {
                viewDataTable.getItems().clear();
                viewDataTable.getColumns().clear();
                return;
            }
            this.dataFrame = dataFrame;
            this.model = new ViewDataDFModel(dataFrame);
            this.model.setMainController(spcMainController);
            TableViewWrapper.decorate(viewDataTable, model);
            model.getAllCheckBox().setOnMouseClicked(event -> {
                for (String s : model.getRowKeyArray()) {
                    model.getCheckValue(s, "").setValue(model.getAllCheckBox().selectedProperty().getValue());
                }
            });
            viewDataTable.getColumns().forEach(this::decorate);
            for (ChooseTableRowData rowData : chooseTableRowDataList) {
                if (dataFrame.isTestItemExist(rowData.getValue())) {
                    rowData.getSelector().setValue(true);
                } else {
                    rowData.getSelector().setValue(false);
                }
            }
            chooseDialogController.setTableData(chooseTableRowDataList);
        });
    }

    private void decorate(TableColumn<String, ?> tableColumn) {
        if ("CheckBox".equals(tableColumn.getText())) {
            return;
        }
        Button filterBtn = new Button();
        filterBtn.getStyleClass().add("filter-normal");
        FilterSettingAndGraphic fsg = new FilterSettingAndGraphic();
        fsg.setFilterBtn(filterBtn);
        filterBtn.setOnAction(event -> {
            QuickSearchController quickSearchController = new QuickSearchController();
            FXMLLoader fxmlLoader = SpcFxmlAndLanguageUtils.getLoaderFXML(ViewResource.SPC_QUICK_SEARCH_VIEW_RES);
            fxmlLoader.setController(quickSearchController);
            Pane root = null;
            Stage stage = null;
            try {
                root = fxmlLoader.load();
                stage = WindowFactory.createOrUpdateSimpleWindowAsModel("spcQuickSearch", SpcFxmlAndLanguageUtils.getString(ResourceMassages.QUICK_SEARCH), root);
            } catch (IOException e) {
                e.printStackTrace();
            }
            quickSearchController.setStage(stage);
            FilterType type = columnFilterSetting.get(tableColumn.getText()).getType();
            switch (type) {
                case ALL_DATA:
                    quickSearchController.activeAllData();
                    break;
                case WITHIN_RANGE:
                    quickSearchController.activeWithinRange();
                    quickSearchController.getWithinLowerTf().setText(columnFilterSetting.get(tableColumn.getText()).getWithinLowerLimit());
                    quickSearchController.getWithinUpperTf().setText(columnFilterSetting.get(tableColumn.getText()).getWithinUpperLimit());
                    break;
                case WITHOUT_RANGE:
                    quickSearchController.activeWithoutRange();
                    quickSearchController.getWithoutLowerTf().setText(columnFilterSetting.get(tableColumn.getText()).getWithoutLowerLimit());
                    quickSearchController.getWithoutUpperTf().setText(columnFilterSetting.get(tableColumn.getText()).getWithoutUpperLimit());
                    break;
                default:
                    break;
            }
            quickSearchController.getSearchBtn().setOnAction(event1 -> {
                FilterType type1 = quickSearchController.getFilterType();
                switch (type1) {
                    case ALL_DATA:
                        columnFilterSetting.get(tableColumn.getText()).setType(FilterType.ALL_DATA);
                        columnFilterSetting.get(tableColumn.getText()).setWithinLowerLimit(null);
                        columnFilterSetting.get(tableColumn.getText()).setWithinUpperLimit(null);
                        columnFilterSetting.get(tableColumn.getText()).setWithoutLowerLimit(null);
                        columnFilterSetting.get(tableColumn.getText()).setWithoutUpperLimit(null);
                        quickSearchController.getStage().close();
                        break;
                    case WITHIN_RANGE:
                        columnFilterSetting.get(tableColumn.getText()).setType(FilterType.WITHIN_RANGE);
                        columnFilterSetting.get(tableColumn.getText()).setWithinLowerLimit(quickSearchController.getWithinLowerTf().getText());
                        columnFilterSetting.get(tableColumn.getText()).setWithinUpperLimit(quickSearchController.getWithinUpperTf().getText());
                        columnFilterSetting.get(tableColumn.getText()).setWithoutLowerLimit(null);
                        columnFilterSetting.get(tableColumn.getText()).setWithoutUpperLimit(null);
                        quickSearchController.getStage().close();
                        break;
                    case WITHOUT_RANGE:
                        columnFilterSetting.get(tableColumn.getText()).setType(FilterType.WITHOUT_RANGE);
                        columnFilterSetting.get(tableColumn.getText()).setWithinLowerLimit(null);
                        columnFilterSetting.get(tableColumn.getText()).setWithinUpperLimit(null);
                        columnFilterSetting.get(tableColumn.getText()).setWithoutLowerLimit(quickSearchController.getWithoutLowerTf().getText());
                        columnFilterSetting.get(tableColumn.getText()).setWithoutUpperLimit(quickSearchController.getWithoutUpperTf().getText());
                        quickSearchController.getStage().close();
                        break;
                    default:
                        break;
                }
                filterTF();
                filterHeaderBtn();
            });
            quickSearchController.getCancelBtn().setOnAction(event1 -> {
                quickSearchController.getStage().close();
            });
            if (stage != null) {
                stage.show();
            }
        });

        columnFilterSetting.put(tableColumn.getText(), fsg);
        tableColumn.setGraphic(filterBtn);
        tableColumn.getStyleClass().add("filter-header");
        tableColumn.widthProperty().addListener((ov, w1, w2) -> {
            Platform.runLater(() -> {
                filterBtn.relocate(w2.doubleValue() - 21, 0);
            });
        });
    }


    private void buildChooseColumnDialog() {
        FXMLLoader fxmlLoader = SpcFxmlAndLanguageUtils.getLoaderFXML(ViewResource.SPC_CHOOSE_STATISTICAL_VIEW_RES);
        Pane root = null;
        try {
            root = fxmlLoader.load();
            chooseDialogController = fxmlLoader.getController();
            WindowFactory.createSimpleWindowAsModel("spcViewDataColumn", SpcFxmlAndLanguageUtils.getString(ResourceMassages.CHOOSE_ITEMS_TITLE), root,
                    getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initComponentEvent() {
        clearFilterBtn.setOnAction(event -> getClearFilterBtnEvent());
        filterTf.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            filterTF();
            filterHeaderBtn();
        });
        chooseItemBtn.setOnAction(event -> getChooseColumnBtnEvent());
        chooseDialogController.getChooseOkButton().setOnAction(event -> {
            StageMap.getStage("spcViewDataColumn").close();
            List<String> selectedTestItems = chooseDialogController.getSelectResultName();
            int curIndex = 0;
            for (int i = 0; i < typeDtoList.size(); i++) {
                TestItemWithTypeDto typeDto = typeDtoList.get(i);
                if (selectedTestItems.contains(typeDto.getTestItemName())) {
                    if (!dataFrame.isTestItemExist(typeDto.getTestItemName())) {
                        List<RowDataDto> rowDataDtoList = RuntimeContext.getBean(SourceDataService.class).findTestData(this.selectedProjectNames,
                                Lists.newArrayList(typeDto.getTestItemName()));
                        DataColumn dataColumn = RuntimeContext.getBean(DataFrameFactory.class).createDataColumn(Lists.newArrayList(typeDto), rowDataDtoList).get(0);
                        dataFrame.appendColumn(curIndex, dataColumn);
                    }
                    curIndex++;
                } else {
                    dataFrame.removeColumns(Lists.newArrayList(typeDto.getTestItemName()));
                }
            }
            setViewData(this.dataFrame);
        });
        unSelectedCheckBox.setOnAction(event -> getInvertCheckBoxEvent());
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

    private void filterTF() {
        model.getRowKeyArray().clear();
        for (String s : dataFrame.getAllRowKeys()) {
            List<String> datas = dataFrame.getDataRowList(s);
            for (String data : datas) {
                if (data.toLowerCase().contains(filterTf.getTextField().getText().toLowerCase())) {
                    model.getRowKeyArray().add(s);
                    break;
                }
            }
        }
    }

    private void filterHeaderBtn() {
        for (String testItem : model.getHeaderArray()) {
            if (columnFilterSetting.get(testItem) != null) {
                FilterSettingAndGraphic fsg = columnFilterSetting.get(testItem);
                if (FilterType.ALL_DATA.equals(fsg.getType())) {
                    break;
                } else if (FilterType.WITHIN_RANGE.equals(fsg.getType())) {
                    List<String> toBeRemovedList = Lists.newArrayList();
                    for (int i = 0; i < model.getRowKeyArray().size(); i++) {
                        String rowKey = model.getRowKeyArray().get(i);
                        String cellData = model.getCellData(rowKey, testItem).get();
                        String upperLimit = fsg.getWithinUpperLimit();
                        String lowerLimit = fsg.getWithinLowerLimit();
                        if (!RangeUtils.isWithinRange(cellData, upperLimit, lowerLimit)) {
                            toBeRemovedList.add(rowKey);
                        }
                    }
                    model.getRowKeyArray().removeAll(toBeRemovedList);
                } else if (FilterType.WITHOUT_RANGE.equals(fsg.getType())) {
                    List<String> toBeRemovedList = Lists.newArrayList();
                    for (int i = 0; i < model.getRowKeyArray().size(); i++) {
                        String rowKey = model.getRowKeyArray().get(i);
                        String cellData = model.getCellData(rowKey, testItem).get();
                        String upperLimit = fsg.getWithoutUpperLimit();
                        String lowerLimit = fsg.getWithoutLowerLimit();
                        if (!RangeUtils.isWithoutRange(cellData, upperLimit, lowerLimit)) {
                            toBeRemovedList.add(rowKey);
                        }
                    }
                    model.getRowKeyArray().removeAll(toBeRemovedList);
                }
            }
        }
    }

    private void getChooseColumnBtnEvent() {
        StageMap.showStage("spcViewDataColumn");
        chooseDialogController.setSelectResultName(dataFrame.getAllTestItemName());
    }

    private void getInvertCheckBoxEvent() {
        for (String s : model.getRowKeyArray()) {
            model.getCheckValue(s, "").setValue(!model.getCheckValue(s, "").getValue());
        }
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
