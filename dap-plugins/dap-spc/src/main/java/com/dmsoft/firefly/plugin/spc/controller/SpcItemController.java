/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.plugin.spc.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcSearchConfigDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcStatisticalResultDto;
import com.dmsoft.firefly.plugin.spc.service.SpcServiceImpl;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcService;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import com.google.common.collect.Lists;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;

import java.util.List;


/**
 * Created by Ethan.Yang on 2018/2/6.
 */
public class SpcItemController {
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
    private SpcService spcService = new SpcServiceImpl();

    @FXML
    private void initialize(){
        initBtnIcon();
        this.initComponentEvent();

    }

    private void initBtnIcon(){
        analysisBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_analysis_white_normal.png")));
        importBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_load_script_normal.png")));
        saveBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_save_normal.png")));
        itemTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_datasource_normal.png")));
        configTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_config_normal.png")));
        timeTab.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_timer_normal.png")));
    }

    private void initComponentEvent(){
        analysisBtn.setOnAction(event -> getAnalysisBtnEvent());
    }

    private void getAnalysisBtnEvent(){
        List<SearchConditionDto> searchConditionDtoList = Lists.newArrayList();
        SpcSearchConfigDto spcSearchConfigDto = new SpcSearchConfigDto();
        List<SpcStatisticalResultDto> spcStatisticalResultDtoList = spcService.findStatisticalResult(searchConditionDtoList, spcSearchConfigDto);
    }


}
