/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.firefly.gui.components.dialog.ChooseTestItemDialog;
import com.dmsoft.firefly.gui.components.searchtab.SearchTab;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.CommonResourceMassages;
import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.gui.components.window.WindowPane;
import com.dmsoft.firefly.gui.components.window.WindowProgressTipController;
import com.dmsoft.firefly.gui.handler.importcsv.DataFrameHandler;
import com.dmsoft.firefly.gui.handler.importcsv.FindTestDataHandler;
import com.dmsoft.firefly.gui.handler.importcsv.ParamKeys;
import com.dmsoft.firefly.gui.model.ItemDataTableModel;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.utils.ResourceMassages;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.job.core.*;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.*;

/**
 * Created by Alice on 2018/2/10.
 */
public class DataSourceSettingController {
    private static final Double D100 = 100.0;
    @FXML
    private Button chooseItem, searchBtn, oK, cancel, apply;
    @FXML
    private TableView itemDataTable;
    @FXML
    private SplitPane split;
    private SearchTab searchTab;
    private ItemDataTableModel itemDataTableModel;
    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private SourceDataService sourceDataService = RuntimeContext.getBean(SourceDataService.class);
    private List<String> testItems = new ArrayList<>();
    private List<String> selectTestItemName = Lists.newArrayList();
    private List<String> projectNames = new ArrayList<>();
    private List<TestItemWithTypeDto> testItemWithTypeDtos = Lists.newArrayList();
    private ChooseTestItemDialog chooseTestItemDialog;

    @FXML
    private void initialize() {
        initButton();
        this.initTableData();
        searchTab = new SearchTab(false);
        searchTab.hiddenGroupAdd();
        searchTab.getGroup1().setVisible(false);
        searchTab.getGroup2().setVisible(false);
        searchTab.getAutoDivideLbl().setVisible(false);
        split.getItems().add(searchTab);

    }

    /**
     * init Button
     */
    private void initButton() {
        chooseItem.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_test_items_normal.png")));
        TooltipUtil.installNormalTooltip(chooseItem, GuiFxmlAndLanguageUtils.getString(ResourceMassages.CHOOSE_ITEMS_TITLE));
        searchBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/icon_choose_one_white.png")));
        TooltipUtil.installNormalTooltip(searchBtn, GuiFxmlAndLanguageUtils.getString(ResourceMassages.SEARCH));
    }

    /**
     * init Component
     */
    private void initComponentEvent() {
        chooseItem.setOnAction(event -> getChooseColumnBtnEvent());
        chooseTestItemDialog.getOkBtn().setOnAction(event -> getChooseTestItemEvent());
        if (itemDataTableModel.getAllCheckBox() != null) {
            itemDataTableModel.getAllCheckBox().setOnAction(event -> getAllCheckBoxEvent());
        }
        searchBtn.setOnAction(event -> getSearchConditionEvent());
        oK.setOnAction(event -> {
            List<String> trueSet = new ArrayList<>();
            List<String> falseSet = new ArrayList<>();
            //get change List
            List<RowDataDto> rowDataDtos = itemDataTableModel.getRowDataDtoList();
            if (rowDataDtos != null && !rowDataDtos.isEmpty()) {
                int i = 0;
                for (RowDataDto rowDataDto : rowDataDtos) {
                    if (i > 2) {
                        if (!(itemDataTableModel.getFalseSet()).contains(String.valueOf(i))) {
                            trueSet.add(rowDataDto.getRowKey());
                        } else {
                            falseSet.add(rowDataDto.getRowKey());
                        }
                    }
                    i++;
                }
            }
            sourceDataService.changeRowDataInUsed(trueSet, true);
            sourceDataService.changeRowDataInUsed(falseSet, false);
            StageMap.closeStage("sourceSetting");
        });
        apply.setOnAction(event -> {
            List<String> trueSet = new ArrayList<>();
            List<String> falseSet = new ArrayList<>();
            //get change List
            List<RowDataDto> rowDataDtos = itemDataTableModel.getRowDataDtoList();
            if (rowDataDtos != null && !rowDataDtos.isEmpty()) {
                int i = 0;
                for (RowDataDto rowDataDto : rowDataDtos) {
                    if (i > 2) {
                        if (!(itemDataTableModel.getFalseSet()).contains(String.valueOf(i))) {
                            trueSet.add(rowDataDto.getRowKey());
                        } else {
                            falseSet.add(rowDataDto.getRowKey());
                        }
                    }
                    i++;
                }
            }
            sourceDataService.changeRowDataInUsed(trueSet, true);
            sourceDataService.changeRowDataInUsed(falseSet, false);
        });
        cancel.setOnAction(event -> {
            StageMap.closeStage("sourceSetting");
        });
    }

    /**
     * build Choose Column Dialog
     */
    private void buildChooseColumnDialog() {
        chooseTestItemDialog = new ChooseTestItemDialog(testItems, null);
    }


    /**
     * Choose Column Btn Event
     */
    private void getChooseColumnBtnEvent() {
        chooseTestItemDialog.resetSelectedItems(itemDataTableModel.getHeaderArray());
        chooseTestItemDialog.show();
    }

    /**
     * init Table Data
     */
    private void initTableData() {
        projectNames = envService.findActivatedProjectName();
        testItemWithTypeDtos = envService.findTestItems();

        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
        windowProgressTipController.setAutoHide(false);

        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
        Stage stage1 = StageMap.getStage(CommonResourceMassages.COMPONENT_STAGE_WINDOW_PROGRESS_TIP);
        WindowPane windowPane = null;
        if (stage1.getScene().getRoot() instanceof WindowPane) {
            windowPane = (WindowPane) stage1.getScene().getRoot();
        }
        if (windowPane != null) {
            windowPane.getCloseBtn().setOnAction(event -> {
                windowProgressTipController.setCancelingText();
                context.interruptBeforeNextJobHandler();
            });

        }
        JobManager jobManager = RuntimeContext.getBean(JobManager.class);

        context.put(ParamKeys.PROJECT_NAME_LIST, projectNames);
        context.put(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST, testItemWithTypeDtos);
        context.addJobEventListener(event -> windowProgressTipController.getTaskProgress().setProgress(event.getProgress()));

        windowProgressTipController.getCancelBtn().setOnAction(event -> context.interruptBeforeNextJobHandler());
        JobPipeline jobPipeline = RuntimeContext.getBean(JobFactory.class).createJobPipeLine()
                .addLast(new FindTestDataHandler())
                .addLast(new DataFrameHandler().setWeight(D100));
        jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                SearchDataFrame dataFrame = context.getParam(ParamKeys.SEARCH_DATA_FRAME, SearchDataFrame.class);
                if (testItemWithTypeDtos != null && !testItemWithTypeDtos.isEmpty()) {
                    for (TestItemWithTypeDto dto : testItemWithTypeDtos) {
                        testItems.add(dto.getTestItemName());
                    }
                }
                List<RowDataDto> rowDataDtoList;
                rowDataDtoList = addRowData(testItems);
                itemDataTableModel = new ItemDataTableModel(dataFrame, rowDataDtoList);
                TableViewWrapper.decorate(itemDataTable, itemDataTableModel);
                buildChooseColumnDialog();
                initComponentEvent();
                windowProgressTipController.closeDialog();
            }
        });
        jobPipeline.setInterruptHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                windowProgressTipController.closeDialog();
            }
        });
        jobManager.fireJobASyn(jobPipeline, context);
    }

    /**
     * get All Check Box Event
     */
    private void getAllCheckBoxEvent() {
        Map<String, SimpleObjectProperty<Boolean>> checkMap = itemDataTableModel.getCheckMap();
        if (checkMap != null && checkMap.size() > 0) {
            for (String key : itemDataTableModel.getRowKey()) {
                if (checkMap.get(key) != null) {
                    checkMap.get(key).set(itemDataTableModel.getAllCheckBox().isSelected());
                } else {
                    checkMap.put(key, new SimpleObjectProperty<>(itemDataTableModel.getAllCheckBox().isSelected()));
                }

                if (!itemDataTableModel.getAllCheckBox().isSelected()) {
                    itemDataTableModel.getFalseSet().add(key);
                }

            }
        }
    }

    /**
     * get Choose Test Item Event
     */
    private void getChooseTestItemEvent() {
        selectTestItemName = chooseTestItemDialog.getSelectedItems();
        itemDataTable.getColumns().remove(0, itemDataTable.getColumns().size());
        itemDataTableModel.updateTestItemColumn(selectTestItemName);
        itemDataTableModel.setTableView(itemDataTable);

        List<TestItemWithTypeDto> testItemWithTypeDtoList = new LinkedList<>();
        if (selectTestItemName != null && !selectTestItemName.isEmpty()) {
            for (int i = 0; i < selectTestItemName.size(); i++) {
                TestItemWithTypeDto testItemWithTypeDto = new TestItemWithTypeDto();
                testItemWithTypeDto.setTestItemName(selectTestItemName.get(i));
                testItemWithTypeDtoList.add(testItemWithTypeDto);

            }
        }
        chooseTestItemDialog.close();

        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
        windowProgressTipController.setAutoHide(false);
        JobManager jobManager = RuntimeContext.getBean(JobManager.class);
        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
        context.put(ParamKeys.PROJECT_NAME_LIST, projectNames);
        context.put(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST, testItemWithTypeDtoList);
        Stage stage1 = StageMap.getStage(CommonResourceMassages.COMPONENT_STAGE_WINDOW_PROGRESS_TIP);
        WindowPane windowPane = null;
        if (stage1.getScene().getRoot() instanceof WindowPane) {
            windowPane = (WindowPane) stage1.getScene().getRoot();
        }
        if (windowPane != null) {
            windowPane.getCloseBtn().setOnAction(event -> {
                windowProgressTipController.setCancelingText();
                context.interruptBeforeNextJobHandler();
            });
        }
        context.addJobEventListener(event -> windowProgressTipController.getTaskProgress().setProgress(event.getProgress()));

        windowProgressTipController.getCancelBtn().setOnAction(event -> context.interruptBeforeNextJobHandler());
        JobPipeline jobPipeline = RuntimeContext.getBean(JobFactory.class).createJobPipeLine()
                .addLast(new FindTestDataHandler().setWeight(D100))
                .addLast(new DataFrameHandler());

        jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                SearchDataFrame dataFrame = context.getParam(ParamKeys.SEARCH_DATA_FRAME, SearchDataFrame.class);
                List<RowDataDto> rowDataDtos = dataFrame.getAllDataRow();
                List<RowDataDto> rowDataDtoList = addRowData(testItems);

                if (itemDataTableModel.getRowDataDtoList() != null && !itemDataTableModel.getRowDataDtoList().isEmpty()) {
                    for (int i = 0; i < itemDataTableModel.getRowDataDtoList().size(); i++) {
                        for (RowDataDto rowDataDto : rowDataDtos) {
                            if (itemDataTableModel.getRowDataDtoList().get(i).getRowKey().equals(rowDataDto.getRowKey())) {
                                rowDataDtoList.add(rowDataDto);
                            }
                        }

                    }
                }
                itemDataTableModel.updateRowDataList(rowDataDtoList);

                windowProgressTipController.closeDialog();
            }
        });
        jobPipeline.setInterruptHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                windowProgressTipController.closeDialog();
            }
        });
        jobManager.fireJobASyn(jobPipeline, context);

    }

    /**
     * get Search Condition Event
     */
    private void getSearchConditionEvent() {
        if (!searchTab.verifySearchTextArea()) {
            return;
        }
        List<TestItemWithTypeDto> testItemWithTypeDtoList = new LinkedList<>();
        List<String> columnKey = new LinkedList<>();
        if (itemDataTableModel.getHeaderArray() != null && !itemDataTableModel.getHeaderArray().isEmpty()) {
            for (int i = 0; i < itemDataTableModel.getHeaderArray().size(); i++) {
                if (i != 0) {
                    columnKey.add(itemDataTableModel.getHeaderArray().get(i));

                    TestItemWithTypeDto testItemWithTypeDto = new TestItemWithTypeDto();
                    testItemWithTypeDto.setTestItemName(itemDataTableModel.getHeaderArray().get(i));
                    testItemWithTypeDtoList.add(testItemWithTypeDto);
                }
            }
        }

        WindowProgressTipController windowProgressTipController = WindowMessageFactory.createWindowProgressTip();
        windowProgressTipController.setAutoHide(false);
        JobManager jobManager = RuntimeContext.getBean(JobManager.class);
        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
        context.put(ParamKeys.PROJECT_NAME_LIST, projectNames);
        context.put(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST, testItemWithTypeDtoList);
        windowProgressTipController.getCancelBtn().setOnAction(event -> context.interruptBeforeNextJobHandler());
        context.addJobEventListener(event -> windowProgressTipController.getTaskProgress().setProgress(event.getProgress()));
        Stage stage1 = StageMap.getStage(CommonResourceMassages.COMPONENT_STAGE_WINDOW_PROGRESS_TIP);
        WindowPane windowPane = null;
        if (stage1.getScene().getRoot() instanceof WindowPane) {
            windowPane = (WindowPane) stage1.getScene().getRoot();
        }
        if (windowPane != null) {
            windowPane.getCloseBtn().setOnAction(event -> {
                windowProgressTipController.setCancelingText();
                context.interruptBeforeNextJobHandler();
            });
        }
        JobPipeline jobPipeline = RuntimeContext.getBean(JobFactory.class).createJobPipeLine()
                .addLast(new FindTestDataHandler().setWeight(D100))
                .addLast(new DataFrameHandler());
        jobPipeline.setCompleteHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                SearchDataFrame dataFrame = context.getParam(ParamKeys.SEARCH_DATA_FRAME, SearchDataFrame.class);
                List<String> searchCondition = searchTab.getSearch();
                List<RowDataDto> rowDataDtoList;
                if (!searchCondition.isEmpty()) {
                    rowDataDtoList = dataFrame.getDataRowArray(searchCondition.get(0));
                    List<RowDataDto> searchResultDtos = addRowData(columnKey);
                    searchResultDtos.addAll(rowDataDtoList);
                    itemDataTableModel.updateRowDataList(searchResultDtos);
                }
                windowProgressTipController.closeDialog();
            }
        });
        jobPipeline.setInterruptHandler(new AbstractBasicJobHandler() {
            @Override
            public void doJob(JobContext context) {
                windowProgressTipController.closeDialog();
            }
        });
        jobManager.fireJobASyn(jobPipeline, context);
    }

    /**
     * add Row Data
     *
     * @param columKey columKey
     * @return rowDataDtoList
     */
    private List<RowDataDto> addRowData(List<String> columKey) {
        List<RowDataDto> rowDataDtoList = new LinkedList<>();
        RowDataDto uslDataDto = new RowDataDto();
        RowDataDto lslDataDto = new RowDataDto();
        RowDataDto unitDtaDto = new RowDataDto();
        uslDataDto.setRowKey("UsL_!@#_" + 2);
        lslDataDto.setRowKey("Lsl_!@#_" + 3);
        unitDtaDto.setRowKey("Unit_!@#_" + 4);
        Map<String, String> uslDataMap = new HashMap<>();
        Map<String, String> lslDataMap = new HashMap<>();
        Map<String, String> unitDataMap = new HashMap<>();
        if (columKey != null && !columKey.isEmpty()) {
            for (String selectTestItem : columKey) {
                if (testItemWithTypeDtos != null && !testItemWithTypeDtos.isEmpty()) {
                    for (TestItemWithTypeDto testItemWithTypeDto : testItemWithTypeDtos) {
                        if (testItemWithTypeDto.getTestItemName().equals(selectTestItem)) {
                            if (DAPStringUtils.isNotBlank(testItemWithTypeDto.getUsl())) {
                                uslDataMap.put(testItemWithTypeDto.getTestItemName(), testItemWithTypeDto.getUsl());
                            }
                            if (DAPStringUtils.isNotBlank(testItemWithTypeDto.getLsl())) {
                                lslDataMap.put(testItemWithTypeDto.getTestItemName(), testItemWithTypeDto.getLsl());
                            }
                            if (DAPStringUtils.isNotBlank(testItemWithTypeDto.getUnit())) {
                                unitDataMap.put(testItemWithTypeDto.getTestItemName(), testItemWithTypeDto.getUnit());
                            }
                        }

                    }
                }
            }
        }
        uslDataDto.setData(uslDataMap);
        lslDataDto.setData(lslDataMap);
        unitDtaDto.setData(unitDataMap);
        rowDataDtoList.add(uslDataDto);
        rowDataDtoList.add(lslDataDto);
        rowDataDtoList.add(unitDtaDto);
        return rowDataDtoList;
    }

    /**
     * get Data Frame
     *
     * @param testItemWithTypeDtoList list of test item with type dto
     * @return dataFrame
     */

    private SearchDataFrame getDataFrame(List<TestItemWithTypeDto> testItemWithTypeDtoList) {

        JobManager jobManager = RuntimeContext.getBean(JobManager.class);
        JobContext context = RuntimeContext.getBean(JobFactory.class).createJobContext();
        context.put(ParamKeys.PROJECT_NAME_LIST, projectNames);
        context.put(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST, testItemWithTypeDtoList);

        JobPipeline jobPipeline = RuntimeContext.getBean(JobFactory.class).createJobPipeLine()
                .addLast(new FindTestDataHandler().setWeight(D100))
                .addLast(new DataFrameHandler());

        JobContext jobContext = jobManager.fireJobSyn(jobPipeline, context);
        return jobContext.getParam(ParamKeys.SEARCH_DATA_FRAME, SearchDataFrame.class);

    }

}
