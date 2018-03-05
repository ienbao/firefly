/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.model;

import com.dmsoft.firefly.plugin.spc.dto.SpcStatsDto;
import com.dmsoft.firefly.plugin.spc.utils.TableCheckBox;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.google.common.collect.Maps;
import javafx.beans.property.SimpleStringProperty;

import java.util.Map;

/**
 * Created by Ethan.Yang on 2018/2/6.
 */
public class StatisticalTableRowData {
    private static final String[] STATISTICAL_TITLE = UIConstant.SPC_SR_ALL;
    private TableCheckBox selector = new TableCheckBox();
    private Map<String, SimpleStringProperty> rowDataMap = Maps.newHashMap();

    public StatisticalTableRowData(SpcStatsDto statisticalResultDto) {
        if (statisticalResultDto == null) {
            return;
        }
//        selector = new SimpleBooleanProperty(true);
        rowDataMap.put(STATISTICAL_TITLE[0], new SimpleStringProperty(statisticalResultDto.getItemName()));
        rowDataMap.put(STATISTICAL_TITLE[1], new SimpleStringProperty(statisticalResultDto.getCondition()));
        rowDataMap.put(STATISTICAL_TITLE[2], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getSamples())));
        rowDataMap.put(STATISTICAL_TITLE[3], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getAvg())));
        rowDataMap.put(STATISTICAL_TITLE[4], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getMax())));
        rowDataMap.put(STATISTICAL_TITLE[5], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getMin())));
        rowDataMap.put(STATISTICAL_TITLE[6], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getStDev())));
        rowDataMap.put(STATISTICAL_TITLE[7], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getLsl())));

        rowDataMap.put(STATISTICAL_TITLE[8], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getUsl())));
        rowDataMap.put(STATISTICAL_TITLE[9], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getCenter())));
        rowDataMap.put(STATISTICAL_TITLE[10], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getRange())));
        rowDataMap.put(STATISTICAL_TITLE[11], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getLcl())));
        rowDataMap.put(STATISTICAL_TITLE[12], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getUcl())));
        rowDataMap.put(STATISTICAL_TITLE[13], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getKurtosis())));
        rowDataMap.put(STATISTICAL_TITLE[14], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getSkewness())));
        rowDataMap.put(STATISTICAL_TITLE[15], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getCpk())));

        rowDataMap.put(STATISTICAL_TITLE[16], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getCa())));
        rowDataMap.put(STATISTICAL_TITLE[17], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getCp())));
        rowDataMap.put(STATISTICAL_TITLE[18], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getCpl())));
        rowDataMap.put(STATISTICAL_TITLE[19], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getCpu())));
        rowDataMap.put(STATISTICAL_TITLE[20], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getWithinPPM())));
        rowDataMap.put(STATISTICAL_TITLE[21], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getOverallPPM())));
        rowDataMap.put(STATISTICAL_TITLE[22], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getPp())));
        rowDataMap.put(STATISTICAL_TITLE[23], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getPpk())));

        rowDataMap.put(STATISTICAL_TITLE[24], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getPpl())));
        rowDataMap.put(STATISTICAL_TITLE[25], new SimpleStringProperty(String.valueOf(statisticalResultDto.getStatsResultDto().getPpu())));
    }

    public Map<String, SimpleStringProperty> getRowDataMap() {
        return rowDataMap;
    }

    public TableCheckBox getSelector() {
        return selector;
    }

    public void setSelector(TableCheckBox selector) {
        this.selector = selector;
    }
}
