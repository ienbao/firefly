/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.controller;

import com.dmsoft.firefly.plugin.spc.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcSearchConfigDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcStatisticalResultDto;
import com.dmsoft.firefly.plugin.spc.service.SpcServiceImpl;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcService;
import com.google.common.collect.Lists;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.List;


/**
 * Created by Ethan.Yang on 2018/2/6.
 */
public class SpcItemController {
    @FXML
    private Button analysisBtn;

    private SpcService spcService = new SpcServiceImpl();

    @FXML
    private void initialize(){
        this.initComponentEvent();

    }

    private void initComponentEvent(){
        analysisBtn.setOnAction(event -> getAnalysisBtnEvent());
    }

    private void getAnalysisBtnEvent(){
        List<SearchConditionDto> searchConditionDtoList = Lists.newArrayList();
        SpcSearchConfigDto spcSearchConfigDto = new SpcSearchConfigDto();
        List<SpcStatisticalResultDto> spcStatisticalResultDtoList = spcService.findStatisticalResult(searchConditionDtoList,spcSearchConfigDto);
    }


}
