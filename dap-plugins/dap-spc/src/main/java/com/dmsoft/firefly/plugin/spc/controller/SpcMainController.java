/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.plugin.spc.dto.SpcServiceStatsResultDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcViewDataDto;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.List;
import java.util.Map;
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
     * @param list the data list
     */
    public void setStatisticalResultData(List<SpcServiceStatsResultDto> list) {
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
    private List<SpcViewDataDto> initData() {
        List<SpcViewDataDto> spcViewDataDtoList = Lists.newArrayList();
        for (int i = 0; i < 100; i++) {
            SpcViewDataDto spcViewDataDto = new SpcViewDataDto();
            Map<String, Object> map = Maps.newHashMap();
            for (int j = 0; j < 10; j++) {
                map.put("itemName" + j, "value" + i + j);
            }
            spcViewDataDto.setTestData(map);
            spcViewDataDtoList.add(spcViewDataDto);
        }
        return spcViewDataDtoList;
    }

}
