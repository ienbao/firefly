/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.model;

import com.dmsoft.firefly.plugin.spc.dto.SpcStatisticalResultDto;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.google.common.collect.Maps;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Map;

/**
 * Created by Ethan.Yang on 2018/2/6.
 */
public class StatisticalTableRowData {
    private SimpleBooleanProperty selector;

    private Map<String,SimpleStringProperty> rowDataMap = Maps.newHashMap();
    private static final String[] STATISTICAL_TITLE = UIConstant.SPC_SR_ALL;

    public StatisticalTableRowData(SpcStatisticalResultDto statisticalResultDto){
        if(statisticalResultDto == null){
            return;
        }
        selector = new SimpleBooleanProperty(true);
        rowDataMap.put(STATISTICAL_TITLE[0],new SimpleStringProperty(statisticalResultDto.getItemName()));
        rowDataMap.put(STATISTICAL_TITLE[1],new SimpleStringProperty(statisticalResultDto.getCondition()));
        rowDataMap.put(STATISTICAL_TITLE[2],new SimpleStringProperty(statisticalResultDto.getSamples()));
        rowDataMap.put(STATISTICAL_TITLE[3],new SimpleStringProperty(statisticalResultDto.getAvg()));
        rowDataMap.put(STATISTICAL_TITLE[4],new SimpleStringProperty(statisticalResultDto.getMax()));
        rowDataMap.put(STATISTICAL_TITLE[5],new SimpleStringProperty(statisticalResultDto.getMin()));
        rowDataMap.put(STATISTICAL_TITLE[6],new SimpleStringProperty(statisticalResultDto.getStDev()));
        rowDataMap.put(STATISTICAL_TITLE[7],new SimpleStringProperty(statisticalResultDto.getLsl()));

        rowDataMap.put(STATISTICAL_TITLE[8],new SimpleStringProperty(statisticalResultDto.getUsl()));
        rowDataMap.put(STATISTICAL_TITLE[9],new SimpleStringProperty(statisticalResultDto.getCenter()));
        rowDataMap.put(STATISTICAL_TITLE[10],new SimpleStringProperty(statisticalResultDto.getRange()));
        rowDataMap.put(STATISTICAL_TITLE[11],new SimpleStringProperty(statisticalResultDto.getLcl()));
        rowDataMap.put(STATISTICAL_TITLE[12],new SimpleStringProperty(statisticalResultDto.getUcl()));
        rowDataMap.put(STATISTICAL_TITLE[13],new SimpleStringProperty(statisticalResultDto.getKurtosis()));
        rowDataMap.put(STATISTICAL_TITLE[14],new SimpleStringProperty(statisticalResultDto.getSkewness()));
        rowDataMap.put(STATISTICAL_TITLE[15],new SimpleStringProperty(statisticalResultDto.getCpk()));

        rowDataMap.put(STATISTICAL_TITLE[16],new SimpleStringProperty(statisticalResultDto.getCa()));
        rowDataMap.put(STATISTICAL_TITLE[17],new SimpleStringProperty(statisticalResultDto.getCp()));
        rowDataMap.put(STATISTICAL_TITLE[18],new SimpleStringProperty(statisticalResultDto.getCpl()));
        rowDataMap.put(STATISTICAL_TITLE[19],new SimpleStringProperty(statisticalResultDto.getCpu()));
        rowDataMap.put(STATISTICAL_TITLE[20],new SimpleStringProperty(statisticalResultDto.getWithinPPM()));
        rowDataMap.put(STATISTICAL_TITLE[21],new SimpleStringProperty(statisticalResultDto.getOverallPPM()));
        rowDataMap.put(STATISTICAL_TITLE[22],new SimpleStringProperty(statisticalResultDto.getPp()));
        rowDataMap.put(STATISTICAL_TITLE[23],new SimpleStringProperty(statisticalResultDto.getPpk()));

        rowDataMap.put(STATISTICAL_TITLE[24],new SimpleStringProperty(statisticalResultDto.getPpl()));
        rowDataMap.put(STATISTICAL_TITLE[25],new SimpleStringProperty(statisticalResultDto.getPpu()));
    }

    public Map<String, SimpleStringProperty> getRowDataMap() {
        return rowDataMap;
    }

    public boolean isSelector() {
        return selector.get();
    }

    public SimpleBooleanProperty selectorProperty() {
        return selector;
    }
}
