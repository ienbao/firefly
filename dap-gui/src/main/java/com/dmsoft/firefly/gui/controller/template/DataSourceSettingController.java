/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.firefly.gui.components.searchtab.SearchTab;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.model.ChooseTableRowData;
import com.dmsoft.firefly.gui.model.ItemDataTableModel;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.utils.ResourceMassages;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.dmsoft.firefly.sdk.utils.FilterUtils;
import com.google.common.collect.Lists;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

/**
 * Created by Alice on 2018/2/10.
 */
public class DataSourceSettingController {
    @FXML
    private Button chooseItem, searchBtn, oK, cancel, apply;
    @FXML
    private TableView itemDataTable;
    @FXML
    private SplitPane split;
    private SearchTab searchTab;
    private ItemDataTableModel itemDataTableModel;
    private ChooseColDialogController chooseCumDialogController;
    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private SourceDataService sourceDataService = RuntimeContext.getBean(SourceDataService.class);
    private List<String> testItems = new ArrayList<>();
    private List<String> selectTestItemName = Lists.newArrayList();
    private List<String> projectNames = new ArrayList<>();
    private List<TestItemWithTypeDto> testItemWithTypeDtos = Lists.newArrayList();

    @FXML
    private void initialize() {
        initButton();
        this.initTableData();
        searchTab = new SearchTab();
        split.getItems().add(searchTab);
        this.buildChooseColumnDialog();
        this.initComponentEvent();
    }

    /**
     * init Button
     */
    private void initButton() {
        chooseItem.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_test_items_normal.png")));
        searchBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/icon_choose_one_white.png")));
    }

    /**
     * init Component
     */
    private void initComponentEvent() {
        chooseItem.setOnAction(event -> getChooseColumnBtnEvent());
        chooseCumDialogController.getChooseOkButton().setOnAction(event -> getChooseTestItemEvent());
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
        FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/choosecol_dialog.fxml");
        Pane root = null;
        try {
            root = fxmlLoader.load();
            chooseCumDialogController = fxmlLoader.getController();
            chooseCumDialogController.setValueColumnText("Test Item");
            this.initChooseColumnTableData();
            Stage stage = WindowFactory.createNoManagedStage(GuiFxmlAndLanguageUtils.getString(ResourceMassages.CHOOSE_ITEMS_TITLE), root,
                    getClass().getClassLoader().getResource("css/platform_app.css").toExternalForm());
            chooseCumDialogController.setStage(stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Choose Column Btn Event
     */
    private void getChooseColumnBtnEvent() {
        chooseCumDialogController.setSelectResultName(itemDataTableModel.getHeaderArray());
        chooseCumDialogController.getStage().show();
    }

    /**
     * init Table Data
     */
    private void initTableData() {
        projectNames = envService.findActivatedProjectName();
        testItemWithTypeDtos = envService.findTestItems();
        if (testItemWithTypeDtos != null && !testItemWithTypeDtos.isEmpty()) {
            for (TestItemWithTypeDto dto : testItemWithTypeDtos) {
                testItems.add(dto.getTestItemName());
            }
        }
        List<RowDataDto> rowDataDtos = sourceDataService.findTestData(projectNames, testItems, true);
        List<RowDataDto> rowDataDtoList = this.addRowData(testItems);
        rowDataDtoList.addAll(rowDataDtos);

        itemDataTableModel = new ItemDataTableModel(testItems, rowDataDtoList);
        TableViewWrapper.decorate(itemDataTable, itemDataTableModel);
    }

    /**
     * init Choose Column Table Data
     */
    private void initChooseColumnTableData() {
        List<ChooseTableRowData> chooseTableRowDataList = Lists.newArrayList();
        testItems.forEach(v -> {
            ChooseTableRowData chooseTableRowData = new ChooseTableRowData(false, v);
            chooseTableRowDataList.add(chooseTableRowData);
        });
        chooseCumDialogController.setTableData(chooseTableRowDataList);
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
            }
        }
    }

    /**
     * get Choose Test Item Event
     */
    private void getChooseTestItemEvent() {
        selectTestItemName = chooseCumDialogController.getSelectResultName();
        itemDataTable.getColumns().remove(0, itemDataTable.getColumns().size());
        itemDataTableModel.updateTestItemColumn(selectTestItemName);
        itemDataTableModel.setTableView(itemDataTable);

        List<RowDataDto> rowDataDtos = sourceDataService.findTestData(projectNames, selectTestItemName, true);
        List<RowDataDto> rowDataDtoList = this.addRowData(testItems);
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
        chooseCumDialogController.getStage().close();
    }

    /**
     * get Search Condition Event
     */
    private void getSearchConditionEvent() {
        List<String> columKey = new LinkedList<>();
        if (itemDataTableModel.getHeaderArray() != null && !itemDataTableModel.getHeaderArray().isEmpty()) {
            for (int i = 0; i < itemDataTableModel.getHeaderArray().size(); i++) {
                if (i != 0) {
                    columKey.add(itemDataTableModel.getHeaderArray().get(i));
                }
            }
        }


        List<RowDataDto> rowDataDtoList = new LinkedList<>();
        List<RowDataDto> rowDataDtos = sourceDataService.findTestData(projectNames, columKey, true);
        rowDataDtoList.addAll(rowDataDtos);

        List<RowDataDto> searchResultDtos = this.addRowData(columKey);
        Boolean flag = false;
        List<String> searchCondition = searchTab.getSearch();
        TemplateSettingDto templateSettingDto = envService.findActivatedTemplate();
        if (templateSettingDto.getTimePatternDto() != null) {
            FilterUtils filterUtils = new FilterUtils(templateSettingDto.getTimePatternDto().getTimeKeys(), templateSettingDto.getTimePatternDto().getPattern());
            if (!searchCondition.isEmpty() && searchCondition != null) {
                for (String condition : searchCondition) {
                    if (rowDataDtoList != null && !rowDataDtoList.isEmpty()) {
                        for (RowDataDto rowDataDto : rowDataDtoList) {
                            flag = filterUtils.filterData(condition, rowDataDto.getData());
                            if (flag) {
                                searchResultDtos.add(rowDataDto);
                            }
                        }
                    }
                }
                itemDataTableModel.updateRowDataList(searchResultDtos);
            }
        }
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

}
