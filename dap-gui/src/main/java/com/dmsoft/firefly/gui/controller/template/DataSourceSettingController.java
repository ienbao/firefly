/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.firefly.gui.GuiApplication;
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
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
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

import java.io.IOException;
import java.util.*;

import static java.util.Arrays.asList;

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
    private EnvService envService = RuntimeContext.getBean( EnvService.class );
    private SourceDataService sourceDataService = RuntimeContext.getBean( SourceDataService.class );
    private List<String> testItems = new ArrayList<>();
    private List<String> selectTestItemName = Lists.newArrayList();

    @FXML
    private void initialize() {
        initButton();
        this.setTableData();
        searchTab = new SearchTab();
        split.getItems().add( searchTab );
        this.buildChooseColumnDialog();
        this.initComponentEvent();
    }

    private void initButton() {
        chooseItem.setGraphic( ImageUtils.getImageView( getClass().getResourceAsStream( "/images/btn_choose_test_items_normal.png" ) ) );
        searchBtn.setGraphic( ImageUtils.getImageView( getClass().getResourceAsStream( "/images/icon_choose_one_white.png" ) ) );
    }

    private void initComponentEvent() {
        chooseItem.setOnAction( event -> getChooseColumnBtnEvent() );
        itemDataTableModel.getAllCheckBox().setOnAction( event -> getAllCheckBoxEvent() );
        chooseCumDialogController.getChooseOkButton().setOnAction( event -> getChooseTestItemEvent() );
        searchBtn.setOnAction( event -> getSearchConditionEvent() );
        oK.setOnAction( event -> {
            //get change List
//             sourceDataService.changeRowDataInUsed(List<String> rowKeyList, boolean inUsed);
//            StageMap.closeStage("sourceSetting");
        } );
        apply.setOnAction( event -> {
            //get change List
//             sourceDataService.changeRowDataInUsed(List<String> rowKeyList, boolean inUsed);
        } );
        cancel.setOnAction( event -> {
            StageMap.closeStage( "sourceSetting" );
        } );
    }

    private void buildChooseColumnDialog() {
        FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/choosecol_dialog.fxml");
        Pane root = null;
        try {
            root = fxmlLoader.load();
            chooseCumDialogController = fxmlLoader.getController();
            chooseCumDialogController.setValueColumnText("Test Item");
            this.initChooseColumnTableData();
            WindowFactory.createSimpleWindowAsModel( "dataSourceSetting", GuiFxmlAndLanguageUtils.getString( ResourceMassages.CHOOSE_ITEMS_TITLE ), root,
                    getClass().getClassLoader().getResource( "css/platform_app.css" ).toExternalForm() );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getChooseColumnBtnEvent() {
        chooseCumDialogController.setSelectResultName(itemDataTableModel.getHeaderArray());
        StageMap.showStage( "dataSourceSetting" );
    }

    private void setTableData() {

        List<RowDataDto> rowDataDtoList = new LinkedList<>();
        List<String> projectNames = envService.findActivatedProjectName();
        List<TestItemWithTypeDto> testItemWithTypeDtos = envService.findTestItems();
        if (testItemWithTypeDtos != null && !testItemWithTypeDtos.isEmpty()) {
            for (TestItemWithTypeDto dto : testItemWithTypeDtos) {
                testItems.add( dto.getTestItemName() );
            }
        }
        List<RowDataDto> rowDataDtos = sourceDataService.findTestData( projectNames, testItems, true );

        RowDataDto uslDataDto = new RowDataDto();
        RowDataDto lslDataDto = new RowDataDto();
        RowDataDto unitDtaDto = new RowDataDto();
        uslDataDto.setRowKey( "UsL_!@#_" + 2 );
        lslDataDto.setRowKey( "Lsl_!@#_" + 3 );
        unitDtaDto.setRowKey( "Unit_!@#_" + 4 );

        Map<String, String> uslDataMap = new HashMap<>();
        Map<String, String> lslDataMap = new HashMap<>();
        Map<String, String> unitDataMap = new HashMap<>();
        for (TestItemWithTypeDto testItemWithTypeDto : testItemWithTypeDtos) {
            if (DAPStringUtils.isNotBlank( testItemWithTypeDto.getUsl() )) {
                uslDataMap.put( testItemWithTypeDto.getTestItemName(), testItemWithTypeDto.getUsl() );
            }

            if (DAPStringUtils.isNotBlank( testItemWithTypeDto.getLsl() )) {
                lslDataMap.put( testItemWithTypeDto.getTestItemName(), testItemWithTypeDto.getLsl() );
            }

            if (DAPStringUtils.isNotBlank( testItemWithTypeDto.getUnit() )) {
                unitDataMap.put( testItemWithTypeDto.getTestItemName(), testItemWithTypeDto.getUnit() );
            }
        }
        uslDataDto.setData( uslDataMap );
        lslDataDto.setData( lslDataMap );
        unitDtaDto.setData( unitDataMap );

        rowDataDtoList.add( uslDataDto );
        rowDataDtoList.add( lslDataDto );
        rowDataDtoList.add( unitDtaDto );
        rowDataDtoList.addAll( rowDataDtos );

        if (testItems != null && !testItems.isEmpty()) {
            itemDataTableModel = new ItemDataTableModel( testItems, rowDataDtoList );
            TableViewWrapper.decorate( itemDataTable, itemDataTableModel );
        }
    }

    private void initChooseColumnTableData() {
        List<ChooseTableRowData> chooseTableRowDataList = Lists.newArrayList();
        testItems.forEach( v -> {
            ChooseTableRowData chooseTableRowData = new ChooseTableRowData(false, v);
            chooseTableRowDataList.add(chooseTableRowData);
        } );
        chooseCumDialogController.setTableData(chooseTableRowDataList);
    }

    private void getAllCheckBoxEvent() {
        Map<String, SimpleObjectProperty<Boolean>> checkMap = itemDataTableModel.getCheckMap();
        for (String key : itemDataTableModel.getRowKey()) {
            if (checkMap.get( key ) != null) {
                checkMap.get( key ).set( itemDataTableModel.getAllCheckBox().isSelected() );
            } else {
                checkMap.put( key, new SimpleObjectProperty<>( itemDataTableModel.getAllCheckBox().isSelected() ) );
            }
        }
    }

    private void getChooseTestItemEvent() {
        selectTestItemName = chooseCumDialogController.getSelectResultName();
        itemDataTable.getColumns().remove(1, itemDataTable.getColumns().size());
        itemDataTableModel.updateTestItemColumn(selectTestItemName);
        StageMap.closeStage("dataSourceSetting");
    }

    private void getSearchConditionEvent() {
        List<RowDataDto> rowDataDtos = itemDataTableModel.getRowDataDtoList();
        List<RowDataDto> searchResultDtos = new ArrayList<>();
        Boolean flag = false;
        List<String> searchCondition = searchTab.getSearch();
        TemplateSettingDto templateSettingDto = envService.findActivatedTemplate();
        if (templateSettingDto.getTimePatternDto() != null) {
            FilterUtils filterUtils = new FilterUtils( templateSettingDto.getTimePatternDto().getTimeKeys(), templateSettingDto.getTimePatternDto().getPattern() );

            if (!searchCondition.isEmpty() && searchCondition != null) {
                for (String condition : searchCondition) {
                    if (rowDataDtos != null && !rowDataDtos.isEmpty()) {
                        for (RowDataDto rowDataDto : rowDataDtos) {
                            flag = filterUtils.filterData(condition, rowDataDto.getData());
                            if (flag) {
                                searchResultDtos.add( rowDataDto );
                            }
                        }
                    }
                }

                if (itemDataTableModel.getHeaderArray() != null && !itemDataTableModel.getHeaderArray().isEmpty()) {
                    itemDataTableModel = new ItemDataTableModel( itemDataTableModel.getHeaderArray(), searchResultDtos );
                    TableViewWrapper.decorate( itemDataTable, itemDataTableModel );
                }
            }
        }
    }

}
