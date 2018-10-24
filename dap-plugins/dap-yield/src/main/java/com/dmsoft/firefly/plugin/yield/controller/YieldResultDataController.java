package com.dmsoft.firefly.plugin.yield.controller;

import com.dmsoft.firefly.plugin.yield.dto.YieldOverviewResultAlarmDto;
import com.dmsoft.firefly.plugin.yield.dto.YieldTotalProcessesDto;
import com.dmsoft.firefly.plugin.yield.utils.ResourceMassages;
import com.dmsoft.firefly.plugin.yield.utils.YieldFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.google.common.collect.Lists;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class YieldResultDataController implements Initializable {
    @FXML
    private Label TotalSamples;
    @FXML
    private Label FpySamples;
    @FXML
    private Label PassSamples;
    @FXML
    private Label NtfSamples;
    @FXML
    private Label NgSamples;
    private YieldMainController yieldMainController;
    private OverViewController overViewController;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    public void init(YieldMainController yieldMainController) {
        this.yieldMainController = yieldMainController;
        this.overViewController=yieldMainController.getOverViewController();
    }
    public void setOverviewResultData(List<YieldTotalProcessesDto> list, String rowKey, boolean isTimer) {
//        String rowKey="V_Mic_Bias";
        TotalSamples.setText(list.get(0).getTotalSamples().toString());
        TotalSamples.setOnMouseClicked(event ->overViewController.fireClickEvent(rowKey,"Total Samples"));
        FpySamples.setText(list.get(0).getFpySamples().toString());
        FpySamples.setOnMouseClicked(event ->overViewController.fireClickEvent(rowKey,"FPY Samples"));
        PassSamples.setText(list.get(0).getPassSamples().toString());
        PassSamples.setOnMouseClicked(event ->overViewController.fireClickEvent(rowKey,"Pass Samples"));
        NtfSamples.setText(list.get(0).getNtfSamples().toString());
        NtfSamples.setOnMouseClicked(event ->overViewController.fireClickEvent(rowKey,"NTF Samples"));
        NgSamples.setText(list.get(0).getNgSamples().toString());
        NgSamples.setOnMouseClicked(event ->overViewController.fireClickEvent(rowKey,"NG Samples"));

    }

}
