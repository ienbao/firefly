/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.plugin.spc.dto.SpcStatsDto;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dataframe.DataFrameFactory;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * Created by Ethan.Yang on 2018/2/2.
 */
public class SpcMainController implements Initializable {

    @FXML
    private Button resetBtn;
    @FXML
    private Button printBtn;
    @FXML
    private Button exportBtn;
    @FXML
    private Button chooseBtn;

    @FXML
    private SpcItemController spcItemController;
    @FXML
    private StatisticalResultController statisticalResultController;
    @FXML
    private ViewDataController viewDataController;
    @FXML
    private ChartResultController chartResultController;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.spcItemController.init(this);
        this.statisticalResultController.init(this);
        this.viewDataController.init(this);
        this.chartResultController.init(this);
        this.initBtnIcon();
        this.initComponentEvent();
    }

    /**
     * set statistical result data
     *
     * @param list the data list
     */
    public void setStatisticalResultData(List<SpcStatsDto> list) {
        statisticalResultController.setStatisticalResultTableData(list);
    }

    private void initComponentEvent() {
        resetBtn.setOnAction(event -> getResetBtnEvent());
        printBtn.setOnAction(event -> getPrintBtnEvent());
        exportBtn.setOnAction(event -> getExportBtnEvent());
        chooseBtn.setOnAction(event -> getChooseBtnEvent());
    }

    private void getResetBtnEvent() {

    }

    private void getPrintBtnEvent() {

    }

    private void getExportBtnEvent() {

    }

    private void getChooseBtnEvent() {
        viewDataController.setViewData(initData());
    }

    private void initBtnIcon() {
        resetBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_reset_normal.png")));
        printBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_print_normal.png")));
        exportBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_export_normal.png")));
        chooseBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/icon_choose_one_white.png")));
    }

    @Deprecated
    private SearchDataFrame initData() {
        List<TestItemWithTypeDto> typeDtoList = Lists.newArrayList();
        List<RowDataDto> rowDataDtoList = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            TestItemWithTypeDto typeDto = new TestItemWithTypeDto();
            typeDto.setTestItemName("itemName" + i);
            typeDto.setLsl("10");
            typeDto.setUsl("30");
            typeDtoList.add(typeDto);
        }
        Random random = new Random();
        int k = random.nextInt(100);
        for (int i = 0; i < k; i++) {
            RowDataDto rowDataDto = new RowDataDto();
            Map<String, String> map = Maps.newHashMap();
            rowDataDto.setRowKey(i + "");
            for (int j = 0; j < 10; j++) {
                map.put(typeDtoList.get(j).getTestItemName(), i + j + "");
            }
            rowDataDto.setData(map);
            rowDataDtoList.add(rowDataDto);
        }
        return RuntimeContext.getBean(DataFrameFactory.class).createSearchDataFrame(typeDtoList, rowDataDtoList);
    }

}
