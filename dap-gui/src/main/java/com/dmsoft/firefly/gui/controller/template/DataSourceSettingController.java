/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.firefly.gui.GuiApplication;
import com.dmsoft.firefly.gui.components.table.NewTableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.model.ItemDataTableModel;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.utils.ResourceMassages;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * Created by Alice on 2018/2/10.
 */
public class DataSourceSettingController {
    @FXML
    private Button chooseItem, newTemplate, clearAll, help, ok, oK, cancel, apply;
    @FXML
    private Tab basicTab, advanceTab;
    @FXML
    private TableColumn<ItemTableModel, CheckBox> select;
    @FXML
    private TableColumn<ItemTableModel, TestItemWithTypeDto> item;
    @FXML
    private TableView itemDataTable;

    private ItemDataTableModel itemDataTableModel;
    private ChooseColDialogController chooseCumDialogController;
    private SearchDataFrame dataFrame;
    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private SourceDataService sourceDataService = RuntimeContext.getBean(SourceDataService.class);

    @FXML
    private void initialize() {
        initButton();
        this.buildChooseColumnDialog();
        this.setTableData();
        this.initComponentEvent();

    }

    private void initButton() {
        chooseItem.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_test_items_normal.png")));
        basicTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_basic_search_normal.png")));
        advanceTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_advance_search_normal.png")));
        newTemplate.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_new_template_normal.png")));
        clearAll.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_clear_all_normal.png")));
        help.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_help.svg")));
        ok.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/icon_choose_one_white.png")));
    }

    private void initComponentEvent() {
        chooseItem.setOnAction(event -> getChooseColumnBtnEvent());
        itemDataTableModel.getAllCheckBox().setOnAction(event -> getAllCheckBoxEvent());
    }

    private void buildChooseColumnDialog() {
        FXMLLoader loader = new FXMLLoader(GuiApplication.class.getClassLoader().getResource("view/choosecol_dialog.fxml"), ResourceBundle.getBundle("i18n.message_en_US_GUI"));
        Pane root = null;
        try {
            root = loader.load();
            chooseCumDialogController = loader.getController();
            WindowFactory.createSimpleWindowAsModel("spcViewDataColumn", GuiFxmlAndLanguageUtils.getString(ResourceMassages.CHOOSE_ITEMS_TITLE), root,
                    getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getChooseColumnBtnEvent() {
        StageMap.showStage("spcViewDataColumn");
        chooseCumDialogController.setSelectResultName(dataFrame.getAllTestItemName());
    }

    private void setTableData() {

        List<RowDataDto> rowDataDtoList = new LinkedList<>();
        List<String> projectNames = envService.findActivatedProjectName();
        List<TestItemWithTypeDto> testItemWithTypeDtos = envService.findTestItems();
        List<String> testItems = new ArrayList<>();
        if (testItemWithTypeDtos != null && !testItemWithTypeDtos.isEmpty()) {
            for (TestItemWithTypeDto dto : testItemWithTypeDtos) {
                testItems.add(dto.getTestItemName());
            }
        }
        List<RowDataDto> rowDataDtos = sourceDataService.findTestData(projectNames, testItems);

        //assemble data of csv
        for (String projectName : projectNames) {
            RowDataDto uslDataDto = new RowDataDto();
            RowDataDto lslDataDto = new RowDataDto();
            RowDataDto unitDtaDto = new RowDataDto();
            uslDataDto.setRowKey(projectName + "_!@#_" + 2);
            lslDataDto.setRowKey(projectName + "_!@#_" + 3);
            unitDtaDto.setRowKey(projectName + "_!@#_" + 4);

            Map<String, String> uslDataMap = new HashMap<>();
            Map<String, String> lslDataMap = new HashMap<>();
            Map<String, String> unitDataMap = new HashMap<>();
            int i = 0;
            for (TestItemWithTypeDto testItemWithTypeDto : testItemWithTypeDtos) {
                if (i == 0) {
                    uslDataMap.put(testItemWithTypeDto.getTestItemName(), "Upper Limited----------->");
                    lslDataMap.put(testItemWithTypeDto.getTestItemName(), "Lower Limited----------->");
                    unitDataMap.put(testItemWithTypeDto.getTestItemName(), "Measurement Units---->");
                } else {
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
                i++;
            }
            uslDataDto.setData(uslDataMap);
            lslDataDto.setData(lslDataMap);
            unitDtaDto.setData(unitDataMap);

            rowDataDtoList.add(uslDataDto);
            rowDataDtoList.add(lslDataDto);
            rowDataDtoList.add(unitDtaDto);

            for (RowDataDto rowDataDto : rowDataDtos) {
                if (rowDataDto.getRowKey().contains(projectName)) {
                    rowDataDtoList.add(rowDataDto);
                }
                ;
            }
        }

        if (testItems != null && !testItems.isEmpty()) {
            itemDataTableModel = new ItemDataTableModel(testItems, rowDataDtoList);
            NewTableViewWrapper.decorate(itemDataTable, itemDataTableModel);
        }
    }

    private void getAllCheckBoxEvent() {
        Map<String, SimpleObjectProperty<Boolean>> checkMap = itemDataTableModel.getCheckMap();
        for (String key : itemDataTableModel.getRowKey()) {
            if (checkMap.get(key) != null) {
                checkMap.get(key).set(itemDataTableModel.getAllCheckBox().isSelected());
            } else {
                checkMap.put(key, new SimpleObjectProperty<>(itemDataTableModel.getAllCheckBox().isSelected()));
            }
        }
    }
}
