/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.gui.components.dialog.ChooseTestItemDialog;
import com.dmsoft.firefly.gui.components.skin.ExpandableTableViewSkin;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.plugin.spc.dto.SearchConditionDto;
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
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.dmsoft.firefly.sdk.utils.RangeUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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
    @FXML
    private VBox vbox;
    private SpcMainController spcMainController;

    private SearchDataFrame dataFrame;
    private Map<String, FilterSettingAndGraphic> columnFilterSetting = Maps.newHashMap();
    private ViewDataDFModel model;
    private List<TestItemWithTypeDto> typeDtoList;
    private List<String> selectedProjectNames;
    private List<String> selectedRowKeys;
    private List<String> testItemNames;

    private List<SearchConditionDto> statisticalSearchConditionDtoList;
    private ChooseTestItemDialog chooseTestItemDialog;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.typeDtoList = RuntimeContext.getBean(EnvService.class).findTestItems();
        this.testItemNames = Lists.newArrayList();
        if (this.typeDtoList != null) {
            for (TestItemWithTypeDto typeDto : typeDtoList) {
                testItemNames.add(typeDto.getTestItemName());
            }
        }
        this.filterTf.getTextField().setPromptText(SpcFxmlAndLanguageUtils.getString(ResourceMassages.FILTER_VALUE_PROMPT));
        this.buildChooseColumnDialog();
        this.initBtnIcon();
        this.initComponentEvent();
        this.selectedProjectNames = RuntimeContext.getBean(EnvService.class).findActivatedProjectName();
        filterTf.setDisable(true);
        unSelectedCheckBox.setDisable(true);
        clearFilterBtn.setDisable(true);
        chooseItemBtn.setDisable(true);
    }

    /**
     * method to clear view data
     */
    public void clearViewData() {
        unSelectedCheckBox.setSelected(false);
        filterTf.getTextField().setText(null);
        this.setViewData(null, null, null);
    }

    /**
     * set view data table dataList
     *
     * @param dataFrame                         search data frame
     * @param selectedRowKey                    selected row key
     * @param statisticalSearchConditionDtoList statisticalSearchConditionDtoList
     */
    public void setViewData(SearchDataFrame dataFrame, List<String> selectedRowKey, List<SearchConditionDto> statisticalSearchConditionDtoList) {
        this.setViewData(dataFrame, selectedRowKey, statisticalSearchConditionDtoList, false);
    }

    /**
     * set view data table dataList
     *
     * @param dataFrame                         search data frame
     * @param selectedRowKey                    selected row key
     * @param statisticalSearchConditionDtoList statisticalSearchConditionDtoList
     * @param isTimer                           isTimer
     */
    public void setViewData(SearchDataFrame dataFrame, List<String> selectedRowKey, List<SearchConditionDto> statisticalSearchConditionDtoList, boolean isTimer) {
        this.setViewData(dataFrame, selectedRowKey, statisticalSearchConditionDtoList, isTimer, isTimer);
    }

    private void setViewData(SearchDataFrame dataFrame, List<String> selectedRowKey, List<SearchConditionDto> statisticalSearchConditionDtoList, boolean isTimer, boolean isAutoRefresh) {
        this.statisticalSearchConditionDtoList = statisticalSearchConditionDtoList;
        this.selectedRowKeys = selectedRowKey;
        this.dataFrame = dataFrame;
        this.columnFilterSetting.clear();
        if (dataFrame == null) {
            filterTf.setDisable(true);
            unSelectedCheckBox.setDisable(true);
            clearFilterBtn.setDisable(true);
            chooseItemBtn.setDisable(true);
            viewDataTable.getColumns().clear();
            chooseTestItemDialog.resetSelectedItems(null);
            try {
                if (model != null) {
                    this.model.getRowKeyArray().clear();
                }
            } catch (NullPointerException ignored) {
                ignored.printStackTrace();
            }
            this.model = null;
            return;
        }
        filterTf.setDisable(false);
        unSelectedCheckBox.setDisable(false);
        clearFilterBtn.setDisable(false);
        chooseItemBtn.setDisable(false);
        List<TableColumn<String, ?>> sortedColumnList = null;
        if (isTimer && this.model != null) {
            sortedColumnList = Lists.newArrayList(viewDataTable.getSortOrder());
        }
        if (isAutoRefresh) {
            expandDataFrameByTestItem(chooseTestItemDialog.getSelectedItems());
        }
        vbox.getChildren().remove(viewDataTable);
        this.viewDataTable = new TableView<>();
        this.viewDataTable.setSkin(new ExpandableTableViewSkin(this.viewDataTable));
        this.viewDataTable.setStyle("-fx-border-width: 1 0 0 0");
        VBox.setVgrow(viewDataTable, Priority.ALWAYS);
        this.vbox.setAlignment(Pos.CENTER);
        this.vbox.getChildren().add(viewDataTable);
        this.model = new ViewDataDFModel(dataFrame, selectedRowKey);
        this.model.setStatisticalSearchConditionDtoList(statisticalSearchConditionDtoList);
        this.model.setMainController(spcMainController);
        if (model.getHeaderArray().size() > 51) {
            model.getHeaderArray().remove(51, model.getHeaderArray().size());
        }
        unSelectedCheckBox.setDisable(false);
        if (isTimer) {
            this.model.setIsTimer(true);
            unSelectedCheckBox.setDisable(true);
        }
        TableViewWrapper.decorate(viewDataTable, model);
        if (isTimer) {
            viewDataTable.getColumns().get(0).setSortable(false);
            viewDataTable.getColumns().get(0).setResizable(false);
            viewDataTable.getColumns().get(0).setPrefWidth(32);
        }
        if (model.getAllCheckBox() != null) {
            model.getAllCheckBox().setOnMouseClicked(event -> {
                for (String s : model.getRowKeyArray()) {
                    model.getCheckValue(s, "").setValue(model.getAllCheckBox().selectedProperty().getValue());
                }
            });
        }
        viewDataTable.getColumns().forEach(this::decorate);
        String filterTxt = filterTf.getTextField().getText();
        if (DAPStringUtils.isNotBlank(filterTxt)) {
            filterTf.getTextField().setText("");
            filterTf.getTextField().setText(filterTxt);
        }
        chooseTestItemDialog.resetSelectedItems(model.getHeaderArray().subList(1, model.getHeaderArray().size()));
        if (sortedColumnList != null && !sortedColumnList.isEmpty()) {
            final TableColumn<String, ?> sortedColumn = sortedColumnList.get(0);
            Platform.runLater(() -> {
                TableColumn<String, ?> column1 = null;
                for (TableColumn<String, ?> column : viewDataTable.getColumns()) {
                    if (sortedColumn.getText().equals(column.getText())) {
                        column.setSortType(sortedColumn.getSortType());
                        column1 = column;
                        break;
                    }
                }
                if (column1 != null) {
                    viewDataTable.getSortOrder().add(column1);
                    viewDataTable.sort();
                }
            });
        }
    }

    /**
     * is changed or not
     *
     * @return is changed or not
     */
    public boolean isChanged() {
        List<String> newSelectedRowKeys = getSelectedRowKeys();
        if (this.selectedRowKeys != null) {
            if (newSelectedRowKeys == null) {
                return true;
            }
            if (newSelectedRowKeys.size() != this.selectedRowKeys.size()) {
                return true;
            }
            for (String s : this.selectedRowKeys) {
                if (!newSelectedRowKeys.contains(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * set focus row data
     *
     * @param rowKey row key
     */
    public void setFocusRowData(String rowKey) {
        if (viewDataTable != null && viewDataTable.getItems() != null) {
            this.viewDataTable.getSelectionModel().focus(viewDataTable.getItems().indexOf(rowKey));
            this.viewDataTable.scrollTo(viewDataTable.getItems().indexOf(rowKey));
        }
    }

    /**
     * method to get selected row keys
     *
     * @return list of selected row key
     */
    public List<String> getSelectedRowKeys() {
        if (this.model != null) {
            return this.model.getSelectedRowKeys();
        } else {
            return null;
        }
    }

    /**
     * method to get selected row keys
     *
     * @return list of selected row key
     */
    public List<String> getUnSelectedRowKeys() {
        if (this.model != null) {
            return this.model.getUnSelectedRowKeys();
        } else {
            return null;
        }
    }


    private void decorate(TableColumn<String, ?> tableColumn) {
        if (" ".equals(tableColumn.getText())) {
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
                stage.setResizable(false);
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
                if (quickSearchController.isError()) {
                    WindowMessageFactory.createWindowMessageHasOk(SpcFxmlAndLanguageUtils.getString(ResourceMassages.TIP_WARN_HEADER), SpcFxmlAndLanguageUtils.getString(ResourceMassages.SPC_QUICK_SEARCH_MESSAGE));
                    return;
                }
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
                Platform.runLater(() -> {
                    filterTF();
                    filterHeaderBtn();
                });
            });
            quickSearchController.getCancelBtn().setOnAction(event1 -> {
                quickSearchController.getStage().close();
            });
            if (stage != null) {
                stage.toFront();
                stage.show();
            }
        });

        columnFilterSetting.put(tableColumn.getText(), fsg);
        tableColumn.setGraphic(filterBtn);
        tableColumn.getStyleClass().add("filter-header");
        tableColumn.widthProperty().addListener((ov, w1, w2) -> {
            Platform.runLater(() -> filterBtn.relocate(w2.doubleValue() - 21, 0));
        });
    }


    private void buildChooseColumnDialog() {
        chooseTestItemDialog = new ChooseTestItemDialog(testItemNames, null);
    }

    private void initComponentEvent() {
        clearFilterBtn.setOnAction(event -> getClearFilterBtnEvent());
        filterTf.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                filterTF();
                filterHeaderBtn();
            });
        });
        chooseItemBtn.setOnAction(event -> getChooseColumnBtnEvent());
        chooseTestItemDialog.getOkBtn().setOnAction(event -> {
            boolean isTimer = model.isTimer();
            chooseTestItemDialog.close();
            if (dataFrame == null) {
                return;
            }
            List<String> selectedTestItems = chooseTestItemDialog.getSelectedItems();
            int curIndex = 0;
            for (TestItemWithTypeDto typeDto : typeDtoList) {
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
            setViewData(this.dataFrame, getSelectedRowKeys(), statisticalSearchConditionDtoList, isTimer, false);
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
        if (model == null) {
            return;
        }
        model.getRowKeyArray().clear();
        model.getRowKeyArray().addAll(dataFrame.getAllRowKeys());
    }

    private void filterTF() {
        if (model == null) {
            return;
        }
        model.getRowKeyArray().clear();
        for (String s : dataFrame.getAllRowKeys()) {
            List<String> datas = dataFrame.getDataRowList(s);
            for (String data : datas) {
                if (filterTf.getTextField().getText() == null || data.toLowerCase().contains(filterTf.getTextField().getText().toLowerCase())) {
                    model.getRowKeyArray().add(s);
                    break;
                }
            }
        }
    }

    private void filterHeaderBtn() {
        if (model == null) {
            return;
        }
        for (String testItem : model.getHeaderArray()) {
            if (columnFilterSetting.get(testItem) != null) {
                FilterSettingAndGraphic fsg = columnFilterSetting.get(testItem);
                if (FilterType.ALL_DATA.equals(fsg.getType())) {
                    continue;
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
        chooseTestItemDialog.show();
    }

    private void getInvertCheckBoxEvent() {
        if (model == null) {
            return;
        }
        if (model != null) {
            for (String s : model.getRowKeyArray()) {
                model.getCheckValue(s, "").setValue(!model.getCheckValue(s, "").getValue());
            }
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
        TooltipUtil.installNormalTooltip(clearFilterBtn, SpcFxmlAndLanguageUtils.getString(ResourceMassages.CLEAR_SEARCH));
        chooseItemBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_test_items_normal.png")));
        TooltipUtil.installNormalTooltip(chooseItemBtn, SpcFxmlAndLanguageUtils.getString(ResourceMassages.CHOOSE_ITEMS_TITLE));
    }

    public List<SearchConditionDto> getStatisticalSearchCondition() {
        return statisticalSearchConditionDtoList;
    }

    /**
     * method to update stats search condition
     *
     * @param statisticalSearchConditionDtoList list of stats search condition dto
     */
    public void updateStatisticalSearchCondition(List<SearchConditionDto> statisticalSearchConditionDtoList) {
        this.statisticalSearchConditionDtoList = statisticalSearchConditionDtoList;
        if (model != null) {
            model.setStatisticalSearchConditionDtoList(statisticalSearchConditionDtoList);
        }
    }

    private void expandDataFrameByTestItem(List<String> selectedTestItems) {
        int curIndex = 0;
        for (TestItemWithTypeDto typeDto : typeDtoList) {
            if (selectedTestItems.contains(typeDto.getTestItemName())) {
                if (!dataFrame.isTestItemExist(typeDto.getTestItemName())) {
                    List<RowDataDto> rowDataDtoList = RuntimeContext.getBean(SourceDataService.class).findTestData(this.selectedProjectNames,
                            Lists.newArrayList(typeDto.getTestItemName()));
                    DataColumn dataColumn = RuntimeContext.getBean(DataFrameFactory.class).createDataColumn(Lists.newArrayList(typeDto), rowDataDtoList).get(0);
                    dataFrame.appendColumn(curIndex, dataColumn);
                }
                curIndex++;
            }
        }
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
