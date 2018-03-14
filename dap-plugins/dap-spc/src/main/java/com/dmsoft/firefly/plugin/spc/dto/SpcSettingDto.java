/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.dto;

import java.util.List;
import java.util.Map;

/**
 * Created by Ethan.Yang on 2018/3/8.
 */
public class SpcSettingDto {

    private int customGroupNumber;
    private int chartIntervalNumber;
    private Map<String, Double[]> abilityAlarmRule;
    private Map<String, List<CustomAlarmDto>> statisticalAlarmSetting;
    private List<ControlRuleDto> controlChartRule;
    private String exportTemplateName;

    public int getCustomGroupNumber() {
        return customGroupNumber;
    }

    public void setCustomGroupNumber(int customGroupNumber) {
        this.customGroupNumber = customGroupNumber;
    }

    public int getChartIntervalNumber() {
        return chartIntervalNumber;
    }

    public void setChartIntervalNumber(int chartIntervalNumber) {
        this.chartIntervalNumber = chartIntervalNumber;
    }

    public Map<String, Double[]> getAbilityAlarmRule() {
        return abilityAlarmRule;
    }

    public void setAbilityAlarmRule(Map<String, Double[]> abilityAlarmRule) {
        this.abilityAlarmRule = abilityAlarmRule;
    }

    public Map<String, List<CustomAlarmDto>> getStatisticalAlarmSetting() {
        return statisticalAlarmSetting;
    }

    public void setStatisticalAlarmSetting(Map<String, List<CustomAlarmDto>> statisticalAlarmSetting) {
        this.statisticalAlarmSetting = statisticalAlarmSetting;
    }

    public List<ControlRuleDto> getControlChartRule() {
        return controlChartRule;
    }

    public void setControlChartRule(List<ControlRuleDto> controlChartRule) {
        this.controlChartRule = controlChartRule;
    }

    public String getExportTemplateName() {
        return exportTemplateName;
    }

    public void setExportTemplateName(String exportTemplateName) {
        this.exportTemplateName = exportTemplateName;
    }
}
