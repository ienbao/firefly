package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.dialog.ChooseTestItemDialog;
import com.dmsoft.firefly.gui.components.skin.ExpandableTableViewSkin;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.plugin.yield.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.yield.model.ViewDataModel;
import com.dmsoft.firefly.plugin.yield.utils.*;
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
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import javafx.scene.control.TableView;
import javafx.scene.control.Button;
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
    private YieldItemController yieldItemController;
    private YieldMainController yieldMainController;
    private ViewDataModel model;
    private SearchDataFrame dataFrame;
    private List<SearchConditionDto> searchViewDataConditionDto;
    private List<String> selectedRowKeys;
    private List<String> testItemNames;
    private Map<String, FilterSettingAndGraphic> columnFilterSetting = Maps.newHashMap();
    private List<TestItemWithTypeDto> typeDtoList;
    private List<String> selectedProjectNames;
    private List<SearchConditionDto> statisticalSearchConditionDtoList;

    private List<String> selectStatisticalResultName = Lists.newArrayList();
    private EnvService envService = RuntimeContext.getBean(EnvService.class);
//    private UserPreferenceService userPreferenceService = RuntimeContext.getBean(UserPreferenceService.class);
    private JsonMapper mapper = JsonMapper.defaultMapper();
    private ChooseTestItemDialog chooseTestItemDialog;

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
        chooseColumnBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_test_items_normal.png")));
        TooltipUtil.installNormalTooltip(chooseColumnBtn, YieldFxmlAndLanguageUtils.getString(ResourceMassages.CHOOSE_ITEMS_TITLE));
    }

    private void buildChooseColumnDialog() {
        chooseTestItemDialog = new ChooseTestItemDialog(testItemNames, null);
    }

    /**
     * method to clear view data
     */
    public void clearViewData() {
        filteValueTf.getTextField().setText(null);
        this.setViewData(null,null, null);
    }

    /**
     * set view data table dataList
     *
     * @param dataFrame                         search data frame
     * @param selectedRowKey                    selected row key
     * @param statisticalSearchConditionDtoList statisticalSearchConditionDtoList
     */
    public void setViewData(SearchDataFrame dataFrame, List<String> selectedRowKey, List<SearchConditionDto> statisticalSearchConditionDtoList) {
        this.setViewData(dataFrame, selectedRowKey,statisticalSearchConditionDtoList, false);
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


    /**
     * set view data table dataList
     *
     * @param dataFrame                         search data frame
     * @param selectedRowKey                    selected row key
     * @param searchViewDataConditionDto statisticalSearchConditionDtoList
     */
    private void setViewData(SearchDataFrame dataFrame, List<String> selectedRowKey, List<SearchConditionDto> searchViewDataConditionDto, boolean isTimer, boolean isAutoRefresh) {
        this.searchViewDataConditionDto = searchViewDataConditionDto;
        this.selectedRowKeys = selectedRowKey;
        this.dataFrame = dataFrame;

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
        this.model = new ViewDataModel(dataFrame, selectedRowKey);
//        this.model.setStatisticalSearchConditionDtoList(statisticalSearchConditionDtoList);
        this.model.setMainController(yieldMainController);

        TableViewWrapper.decorate(viewDataTable, model);

        String filterTxt = filteValueTf.getTextField().getText();
        if (DAPStringUtils.isNotBlank(filterTxt)) {
            filteValueTf.getTextField().setText("");
            filteValueTf.getTextField().setText(filterTxt);
        }
        chooseTestItemDialog.resetSelectedItems(dataFrame.getAllTestItemName());
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
            List<String> selectedTestItems = chooseTestItemDialog.getSelectedItems();
//            for(int i =0; i<selectedTestItems.size(); i++){
//                if(!selectedTestItems.get(i).equals(searchViewDataConditionDto.get(1).getItemName())){
//                    dataFrame.
//                }
//            }
            int curIndex = 0;
            for (TestItemWithTypeDto typeDto : typeDtoList) {
                if (selectedTestItems.contains(typeDto.getTestItemName())) {
                    if (!dataFrame.isTestItemExist(typeDto.getTestItemName())) {
                        List<RowDataDto> rowDataDtoList = RuntimeContext.getBean(SourceDataService.class).findTestData(this.selectedProjectNames,
                                Lists.newArrayList(typeDto.getTestItemName()));
                        DataColumn dataColumn = RuntimeContext.getBean(DataFrameFactory.class).createDataColumn(Lists.newArrayList(typeDto), rowDataDtoList).get(0);/* 新增表中的列 */
                        dataFrame.appendColumn(curIndex, dataColumn);
                    }
                    curIndex++;
                } else {
                    dataFrame.removeColumns(Lists.newArrayList(typeDto.getTestItemName()));
                }
            }
            setViewData(this.dataFrame,getSelectedRowKeys(), statisticalSearchConditionDtoList,false);
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
