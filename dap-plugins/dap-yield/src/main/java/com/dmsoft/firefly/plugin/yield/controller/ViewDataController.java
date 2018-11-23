package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.firefly.gui.components.dialog.ChooseTestItemDialog;
import com.dmsoft.firefly.gui.components.skin.ExpandableTableViewSkin;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.plugin.yield.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.yield.dto.YieldOverviewResultAlarmDto;
import com.dmsoft.firefly.plugin.yield.model.ViewDataModel;
import com.dmsoft.firefly.plugin.yield.utils.FilterType;
import com.dmsoft.firefly.plugin.yield.utils.ImageUtils;
import com.dmsoft.firefly.plugin.yield.utils.ResourceMassages;
import com.dmsoft.firefly.plugin.yield.utils.YieldFxmlAndLanguageUtils;
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
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ViewDataController implements Initializable {
    private final Logger logger = LoggerFactory.getLogger(ViewDataController.class);

    @FXML
    private Button chooseColumnBtn;  //选择按钮
    @FXML
    private TextFieldFilter filteValueTf; //搜索框
    @FXML
    private TableView<String> viewDataTable; //表格
    @FXML
    private VBox vbox;
    @FXML
    private Label viewDataR;
    @FXML
    private Label viewDataC;

    private YieldMainController yieldMainController;
    private ViewDataModel model;
    private SearchDataFrame dataFrame;
    private List<SearchConditionDto> searchViewDataConditionDto = Lists.newArrayList();
    private List<String> selectedRowKeys;
    private List<String> testItemNames;
    private Map<String, FilterSettingAndGraphic> columnFilterSetting = Maps.newHashMap();
    private List<TestItemWithTypeDto> typeDtoList;
    private List<String> selectedProjectNames;
    private SearchConditionDto searchConditionDto;
    private ChooseTestItemDialog chooseTestItemDialog;
    private List<String> cacheSelectTestItemName = Lists.newArrayList();
    private String rowKey;
    private String columnLabel;
    private String flag;//标记点击事件发生的位置，当flag为空时，点击事件发生在OverView表中
    private boolean dataFrameFlag;
    private List<YieldOverviewResultAlarmDto> RowDataList = Lists.newArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.typeDtoList = RuntimeContext.getBean(EnvService.class).findTestItems();/*获取到所有测试项 */
        this.testItemNames = Lists.newArrayList();
        if (this.typeDtoList != null) {
            for (TestItemWithTypeDto typeDto : typeDtoList) {
                testItemNames.add(typeDto.getTestItemName());
            }
        }
        filteValueTf.getTextField().setPromptText(YieldFxmlAndLanguageUtils.getString(ResourceMassages.FILTER_VALUE_PROMPT));
        this.buildChooseColumnDialog();
        this.initBtnIcon();
        this.initComponentEvent();
        this.selectedProjectNames = RuntimeContext.getBean(EnvService.class).findActivatedProjectName();
        viewDataTable.getColumns().clear();
        chooseColumnBtn.setDisable(true);
        filteValueTf.setDisable(true);
    }

    /**
     * init main controller
     *
     * @param yieldMainController main controller
     */
    public void init(YieldMainController yieldMainController) {
        this.yieldMainController = yieldMainController;
    }


    private void initBtnIcon() {
        chooseColumnBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/choose-test-items.svg")));
        TooltipUtil.installNormalTooltip(chooseColumnBtn, YieldFxmlAndLanguageUtils.getString(ResourceMassages.CHOOSE_ITEMS_TITLE));
    }

    private void buildChooseColumnDialog() {
        chooseTestItemDialog = new ChooseTestItemDialog(Lists.newArrayList(testItemNames), null);
    }

    /**
     * method to clear view data
     */
    public void clearViewData() {
        filteValueTf.getTextField().setText(null);
        cacheSelectTestItemName.clear();
        this.setViewData(null, null, null, null, null, null);
    }

    /**
     * set view data table dataList
     *
     * @param dataFrame                  search data frame
     * @param selectedRowKey             selected row key
     * @param searchViewDataConditionDto searchViewDataConditionDto
     */
    public void setViewData(SearchDataFrame dataFrame, List<String> selectedRowKey, List<SearchConditionDto> searchViewDataConditionDto, String rowKey, String columnLable, String flag) {
        this.setViewData(dataFrame, selectedRowKey, searchViewDataConditionDto, false, rowKey, columnLable, flag);
    }

    /**
     * set view data table dataList
     *
     * @param dataFrame                  search data frame
     * @param selectedRowKey             selected row key
     * @param searchViewDataConditionDto searchViewDataConditionDto
     * @param isTimer                    isTimer
     */
    public void setViewData(SearchDataFrame dataFrame, List<String> selectedRowKey, List<SearchConditionDto> searchViewDataConditionDto, boolean isTimer, String rowKey, String columnLable, String flag) {
        this.setViewData(dataFrame, selectedRowKey, searchViewDataConditionDto, isTimer, isTimer, rowKey, columnLable, flag);
    }


    /**
     * set view data table dataList
     *
     * @param dataFrame                  search data frame
     * @param selectedRowKey             selected row key
     * @param searchViewDataConditionDto statisticalSearchConditionDtoList
     */
    private void setViewData(SearchDataFrame dataFrame, List<String> selectedRowKey, List<SearchConditionDto> searchViewDataConditionDto, boolean isTimer, boolean isAutoRefresh, String rowKey, String columnLable, String flag) {
        if (searchViewDataConditionDto != null) {
            this.searchViewDataConditionDto = searchViewDataConditionDto;
        }
        this.selectedRowKeys = selectedRowKey;
        this.rowKey = rowKey;
        this.columnLabel = columnLable;
        this.RowDataList = yieldMainController.getOverViewController().getAllRowStatsData();
        String row;
        if ("-".equals(rowKey)) {
            row = rowKey != null ? rowKey : null;
        } else {
            row = rowKey != null ? rowKey + "::" : null;
        }
        viewDataR.setText(row);
        viewDataC.setText(columnLable);
        this.initialize(null, null);
        this.dataFrame = dataFrame;
        this.flag = flag;
        if (dataFrame == null) {
            filteValueTf.setDisable(true);
            viewDataTable.getColumns().clear();
            chooseColumnBtn.setDisable(true);
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
        } else {
            List<String> resultTestItemName = Lists.newArrayList();
            if (flag == null) {
                resultTestItemName.add(searchViewDataConditionDto.get(0).getItemName());
                resultTestItemName.add(searchViewDataConditionDto.get(1).getItemName());
            } else {
                resultTestItemName.add(searchViewDataConditionDto.get(0).getItemName());
            }

            if (!cacheSelectTestItemName.isEmpty()) {
                resultTestItemName.addAll(cacheSelectTestItemName);
                int curIndex = 0;
                for (TestItemWithTypeDto typeDto : typeDtoList) {
                    String testItemName = typeDto.getTestItemName();
                    if (resultTestItemName.contains(testItemName) && dataFrame.isTestItemExist(testItemName)) {
                        curIndex++;
                    }
                }

                for (TestItemWithTypeDto typeDto : typeDtoList) {
                    String testItemName = typeDto.getTestItemName();
                    if (resultTestItemName.contains(testItemName)) {
                        if (resultTestItemName.contains(testItemName) && !dataFrame.isTestItemExist(testItemName)) {
                            List<RowDataDto> rowDataDtoList = RuntimeContext.getBean(SourceDataService.class).findTestData(this.selectedProjectNames,
                                    Lists.newArrayList(testItemName));
                            DataColumn dataColumn = RuntimeContext.getBean(DataFrameFactory.class).createDataColumn(Lists.newArrayList(typeDto), rowDataDtoList).get(0);/* 新增表中的列 */
                            this.dataFrame.appendColumn(curIndex, dataColumn);
                            curIndex++;
                        }
                        if (!(testItemName.equals(searchViewDataConditionDto.get(0).getItemName()))) {
                            if (!(testItemName.equals(searchViewDataConditionDto.get(1).getItemName()))) {
                                searchConditionDto = new SearchConditionDto();
                                searchConditionDto.setItemName(testItemName);
                                for (int i = 0; i < RowDataList.size(); i++) {
                                    if (RowDataList.get(i).getItemName().equals(testItemName)) {
                                        if (typeDto.getLsl().equals(RowDataList.get(i).getLslOrFail())) {
                                            searchConditionDto.setLslOrFail(typeDto.getLsl());
                                        } else {
                                            searchConditionDto.setLslOrFail(RowDataList.get(i).getLslOrFail());
                                        }
                                        if (typeDto.getUsl().equals(RowDataList.get(i).getUslOrPass())) {
                                            searchConditionDto.setUslOrPass(typeDto.getUsl());
                                        } else {
                                            searchConditionDto.setUslOrPass(RowDataList.get(i).getUslOrPass());
                                        }
                                    }
                                }
                                searchConditionDto.setTestItemType(typeDto.getTestItemType());
                                searchViewDataConditionDto.add(searchConditionDto);
                            }
                        }
                    } else {

                        this.dataFrame.removeColumns(Lists.newArrayList(typeDto.getTestItemName()));
                    }
                }
            }
        }

        filteValueTf.setDisable(false);
        chooseColumnBtn.setDisable(false);
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
        this.model = new ViewDataModel(this.dataFrame, selectedRowKey, flag, cacheSelectTestItemName);
        this.model.setTestItemDtoMap(searchViewDataConditionDto);
        TableViewWrapper.decorate(viewDataTable, model);

        /* ViewData Table Result列名=TestItem */
        if (flag == null) {
            if (viewDataTable.getColumns().size() >= 2) {
                viewDataTable.getColumns().get(1).setText("Result");
            }
        }

        String filterTxt = filteValueTf.getTextField().getText();
        if (DAPStringUtils.isNotBlank(filterTxt)) {
            filteValueTf.getTextField().setText("");
            filteValueTf.getTextField().setText(filterTxt);
        }

        if (flag == null) {
            List<String> dataFrameItem = dataFrame.getAllTestItemName();
            List<String> preItem = Lists.newArrayList();
            String primaryKey = searchViewDataConditionDto.get(0).getItemName();
            String selectTestItemName = searchViewDataConditionDto.get(1).getItemName();
            if (dataFrameItem.contains(primaryKey)) {
                preItem.add(dataFrameItem.get(0));
            }
            if (dataFrameItem.contains(selectTestItemName) && !primaryKey.equals(selectTestItemName)) {
                preItem.add(dataFrameItem.get(1));
            }
            dataFrameItem.removeAll(preItem);
            chooseTestItemDialog.removeSelectedItems(preItem);
            chooseTestItemDialog.resetSelectedItems(dataFrameItem);
        } else if (flag != null) {
            List<String> dataFrameItem = dataFrame.getAllTestItemName();
            List<String> preItem = Lists.newArrayList();
            preItem.add(dataFrameItem.get(0));
            dataFrameItem.removeAll(preItem);
            chooseTestItemDialog.removeSelectedItems(preItem);
            chooseTestItemDialog.resetSelectedItems(dataFrame.getAllTestItemName());
        }

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

    private void initComponentEvent() {
        filteValueTf.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                filterTF();
                filterHeaderBtn();
            });
        });
        chooseColumnBtn.setOnAction(event -> getChooseColumnBtnEvent());
        chooseTestItemDialog.getOkBtn().setOnAction(event -> {
            chooseTestItemDialog.close();
            if (dataFrame == null) {
                return;
            }

            List<String> selectedTestItems = Lists.newArrayList();
            cacheSelectTestItemName = chooseTestItemDialog.getSelectedItems();
            if (flag == null) {
                List<String> dataFrameItem = dataFrame.getAllTestItemName();
                String primaryKey = searchViewDataConditionDto.get(0).getItemName();
                String selectTestItemName = searchViewDataConditionDto.get(1).getItemName();
                if (dataFrameItem.contains(primaryKey)) {
                    selectedTestItems.add(dataFrameItem.get(0));
                }
                if (dataFrameItem.contains(selectTestItemName) && !primaryKey.equals(selectTestItemName)) {
                    selectedTestItems.add(dataFrameItem.get(1));
                }
                selectedTestItems.addAll(cacheSelectTestItemName);
            } else {
                List<String> lastItem = chooseTestItemDialog.getSelectedItems();
                List<String> dataFrameItem = dataFrame.getAllTestItemName();
                selectedTestItems.add(dataFrameItem.get(0));
                selectedTestItems.addAll(lastItem);
            }

            int curIndex = 0;
            for (TestItemWithTypeDto typeDto : typeDtoList) {
                String testItemName = typeDto.getTestItemName();
                if (selectedTestItems.contains(testItemName) && dataFrame.isTestItemExist(testItemName)) {
                    curIndex++;
                }
            }

            for (TestItemWithTypeDto typeDto : typeDtoList) {
                String testItemName = typeDto.getTestItemName();
                if (selectedTestItems.contains(testItemName)) {
                    if (selectedTestItems.contains(testItemName) && !dataFrame.isTestItemExist(testItemName)) {
                        List<RowDataDto> rowDataDtoList = RuntimeContext.getBean(SourceDataService.class).findTestData(this.selectedProjectNames,
                                Lists.newArrayList(testItemName));
                        DataColumn dataColumn = RuntimeContext.getBean(DataFrameFactory.class).createDataColumn(Lists.newArrayList(typeDto), rowDataDtoList).get(0);/* 新增表中的列 */
                        dataFrame.appendColumn(curIndex, dataColumn);
                        curIndex++;
                    }
                    if (!(testItemName.equals(searchViewDataConditionDto.get(0).getItemName()))) {
                        if (!(testItemName.equals(searchViewDataConditionDto.get(1).getItemName()))) {
                            searchConditionDto = new SearchConditionDto();
                            searchConditionDto.setItemName(testItemName);
                            for (int i = 0; i < RowDataList.size(); i++) {
                                if (RowDataList.get(i).getItemName().equals(testItemName)) {
                                    if (typeDto.getLsl().equals(RowDataList.get(i).getLslOrFail())) {
                                        searchConditionDto.setLslOrFail(typeDto.getLsl());
                                    } else {
                                        searchConditionDto.setLslOrFail(RowDataList.get(i).getLslOrFail());
                                    }
                                    if (typeDto.getUsl().equals(RowDataList.get(i).getUslOrPass())) {
                                        searchConditionDto.setUslOrPass(typeDto.getUsl());
                                    } else {
                                        searchConditionDto.setUslOrPass(RowDataList.get(i).getUslOrPass());
                                    }
                                }
                            }
                            searchConditionDto.setTestItemType(typeDto.getTestItemType());
                            searchViewDataConditionDto.add(searchConditionDto);
                        }
                    }

                } else {
                    dataFrame.removeColumns(Lists.newArrayList(typeDto.getTestItemName()));
                }
            }

            if (flag == null) {
                setViewData(this.dataFrame, getSelectedRowKeys(), searchViewDataConditionDto, false, rowKey, columnLabel, null);
            } else {
                setViewData(this.dataFrame, getSelectedRowKeys(), searchViewDataConditionDto, false, rowKey, columnLabel, flag);
            }
        });

    }

    private void getChooseColumnBtnEvent() {
        chooseTestItemDialog.show();
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

    private void filterTF() {
        if (model == null) {
            return;
        }
        model.getRowKeyArray().clear();
        for (String s : dataFrame.getAllRowKeys()) {
            List<String> datas = dataFrame.getDataRowList(s);
            for (String data : datas) {
                if (filteValueTf.getTextField().getText() == null || data.toLowerCase().contains(filteValueTf.getTextField().getText().toLowerCase())) {
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


        String getWithinUpperLimit() {
            return withinUpperLimit;
        }


        String getWithoutLowerLimit() {
            return withoutLowerLimit;
        }


        String getWithoutUpperLimit() {
            return withoutUpperLimit;
        }
    }


}
