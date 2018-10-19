package com.dmsoft.firefly.plugin.yield.dto;

import java.util.Map;

public class YieldSettingDto {
    private Map<String, Double[]> abilityAlarmRule;
    private String exportTemplateName;
    private String primaryKey;

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Map<String, Double[]> getAbilityAlarmRule() {
        return abilityAlarmRule;
    }

    public void setAbilityAlarmRule(Map<String, Double[]> abilityAlarmRule) {
        this.abilityAlarmRule = abilityAlarmRule;
    }

    public String getExportTemplateName() {
        return exportTemplateName;
    }

    public void setExportTemplateName(String exportTemplateName) {
        this.exportTemplateName = exportTemplateName;
    }
}
