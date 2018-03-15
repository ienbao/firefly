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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

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

    private CheckBox box;
    private ItemDataTableModel itemDataTableModel;
    private ChooseColDialogController chooseCumDialogController;
    private SearchDataFrame dataFrame;
    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private SourceDataService sourceDataService = RuntimeContext.getBean(SourceDataService.class);

    @FXML
    private void initialize() {
        initButton();
        this.buildChooseColumnDialog();
        this.initComponentEvent();
        this.setTableData();

    }

    private void initButton() {
        chooseItem.setGraphic( ImageUtils.getImageView( getClass().getResourceAsStream( "/images/btn_choose_test_items_normal.png" ) ) );
        basicTab.setGraphic( ImageUtils.getImageView( getClass().getResourceAsStream( "/images/btn_basic_search_normal.png" ) ) );
        advanceTab.setGraphic( ImageUtils.getImageView( getClass().getResourceAsStream( "/images/btn_advance_search_normal.png" ) ) );
        newTemplate.setGraphic( ImageUtils.getImageView( getClass().getResourceAsStream( "/images/btn_new_template_normal.png" ) ) );
        clearAll.setGraphic( ImageUtils.getImageView( getClass().getResourceAsStream( "/images/btn_clear_all_normal.png" ) ) );
        help.setGraphic( ImageUtils.getImageView( getClass().getResourceAsStream( "/images/btn_help.svg" ) ) );
        ok.setGraphic( ImageUtils.getImageView( getClass().getResourceAsStream( "/images/icon_choose_one_white.png" ) ) );
    }

    private void initComponentEvent() {
        chooseItem.setOnAction(event -> getChooseColumnBtnEvent());
    }

    private void buildChooseColumnDialog() {
        FXMLLoader loader = new FXMLLoader(GuiApplication.class.getClassLoader().getResource( "view/choosecol_dialog.fxml" ), ResourceBundle.getBundle("i18n.message_en_US_GUI"));
        Pane root = null;
        try {
            root = loader.load();
           chooseCumDialogController = loader.getController();
            WindowFactory.createSimpleWindowAsModel("spcViewDataColumn", GuiFxmlAndLanguageUtils.getString( ResourceMassages.CHOOSE_ITEMS_TITLE), root,
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
        List<String> projectNames = envService.findActivatedProjectName();
        List<TestItemWithTypeDto> testItemWithTypeDtos =envService.findTestItems();
        List<String> testItems = new ArrayList<>();
        if(testItemWithTypeDtos!= null && !testItemWithTypeDtos.isEmpty()){
            for(TestItemWithTypeDto dto: testItemWithTypeDtos){
                testItems.add(dto.getTestItemName());
            }
        }
        List<RowDataDto> rowDataDtos = sourceDataService.findTestData(projectNames, testItems);

        if(testItems!= null && !testItems.isEmpty()){
            itemDataTableModel = new ItemDataTableModel(testItems,rowDataDtos);
            NewTableViewWrapper.decorate(itemDataTable, itemDataTableModel);
        }
    }
}
