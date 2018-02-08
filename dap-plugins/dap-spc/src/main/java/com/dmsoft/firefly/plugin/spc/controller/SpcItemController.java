/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.plugin.spc.dto.SpcStatisticalResultDto;
import com.dmsoft.firefly.plugin.spc.model.ItemTableModel;
import com.dmsoft.firefly.plugin.spc.model.StatisticalTableRowData;
import com.dmsoft.firefly.plugin.spc.service.SpcServiceImpl;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcService;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


/**
 * Created by Ethan.Yang on 2018/2/6.
 */
public class SpcItemController implements Initializable {
    @FXML
    private Button analysisBtn;
    @FXML
    private Button importBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Tab itemTab;
    @FXML
    private Tab configTab;
    @FXML
    private Tab timeTab;
    @FXML
    private TableColumn<ItemTableModel, CheckBox> select;
    @FXML
    private TableColumn<ItemTableModel, String> item;
    @FXML
    private TableView itemTable;
    private ObservableList<ItemTableModel> items = FXCollections.observableArrayList();

    private SpcService spcService = new SpcServiceImpl();
    @FXML
    private VBox testItemPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initBtnIcon();
        this.initComponentEvent();

//        select.setCellFactory(CheckBoxTableCell.forTableColumn(select));
        select.setCellValueFactory(cellData -> cellData.getValue().getSelector().getCheckBox());
        item.setCellValueFactory(cellData -> cellData.getValue().itemProperty());
        initItemData();
    }

    private void initBtnIcon() {
        analysisBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_analysis_white_normal.png")));
        importBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_load_script_normal.png")));
        saveBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_save_normal.png")));
        itemTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_datasource_normal.png")));
        configTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_config_normal.png")));
        timeTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_timer_normal.png")));
    }

    private void initComponentEvent() {
        analysisBtn.setOnAction(event -> getAnalysisBtnEvent());
    }

    private void initItemData() {
//        EnvService envService = RuntimeContext.getBean(EnvService.class);
//        List<TestItemDto> itemDtos = envService.findTestItem();
        List<TestItemDto> itemDtos = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            TestItemDto dto = new TestItemDto();
            dto.setItemName("lsls");
            itemDtos.add(dto);
        }
        if (itemDtos != null) {
            for (TestItemDto dto : itemDtos) {
                ItemTableModel tableModel = new ItemTableModel(dto);
                items.add(tableModel);
            }
            itemTable.setItems(items);
        }

    }

    private void getAnalysisBtnEvent() {
        //todo find spc statistical Result from service
//        List<SearchConditionDto> searchConditionDtoList = Lists.newArrayList();
//        SpcSearchConfigDto spcSearchConfigDto = new SpcSearchConfigDto();
//        List<SpcStatisticalResultDto> spcStatisticalResultDtoList = spcService.findStatisticalResult(searchConditionDtoList,spcSearchConfigDto);
        List<SpcStatisticalResultDto> spcStatisticalResultDtoList = initData();
        VBox statisticalPane = (VBox) testItemPane.getParent().getParent().getParent().lookup("#statisticalPane");
        TableView statisticalResultTb = (TableView) statisticalPane.lookup("#statisticalResultTb");

        if (spcStatisticalResultDtoList == null) {
            return;
        }
        ObservableList<StatisticalTableRowData> observableList = FXCollections.observableArrayList();
        for (SpcStatisticalResultDto statisticalResultDto : spcStatisticalResultDtoList) {
            StatisticalTableRowData statisticalTableRowData = new StatisticalTableRowData(statisticalResultDto);
            observableList.add(statisticalTableRowData);
        }
        statisticalResultTb.setItems(observableList);
    }


    @Deprecated
    private List<SpcStatisticalResultDto> initData() {
        List<SpcStatisticalResultDto> spcStatisticalResultDtoList = Lists.newArrayList();
        for (int i = 0; i < 100; i++) {
            SpcStatisticalResultDto statisticalResultDto = new SpcStatisticalResultDto();
            statisticalResultDto.setItemName("itemName");
            statisticalResultDto.setCondition("itemName > 22");
            statisticalResultDto.setSamples("343.2");
            statisticalResultDto.setAvg("32.2");
            statisticalResultDto.setMax("312");
            statisticalResultDto.setMin("34");
            statisticalResultDto.setStDev("124");
            statisticalResultDto.setLsl("35");
            statisticalResultDto.setUsl("21");
            statisticalResultDto.setCenter("53");
            statisticalResultDto.setRange("13");
            statisticalResultDto.setLcl("452");
            statisticalResultDto.setUcl("323");
            statisticalResultDto.setKurtosis("234");
            statisticalResultDto.setCpk("234");
            statisticalResultDto.setSkewness("6");
            statisticalResultDto.setCa("43.5");
            statisticalResultDto.setCp("35.76");
            statisticalResultDto.setCpl("34.7");
            statisticalResultDto.setCpu("324.67");
            statisticalResultDto.setWithinPPM("324.6");
            statisticalResultDto.setOverallPPM("343.65");
            statisticalResultDto.setPp("342.76");
            statisticalResultDto.setPpk("34.5");
            statisticalResultDto.setPpl("343.5");
            statisticalResultDto.setPpu("324.87");
            spcStatisticalResultDtoList.add(statisticalResultDto);
        }
        return spcStatisticalResultDtoList;
    }


}
