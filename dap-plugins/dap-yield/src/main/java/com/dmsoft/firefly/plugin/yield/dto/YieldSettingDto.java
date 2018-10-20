package com.dmsoft.firefly.plugin.yield.dto;

import java.util.Map;

public class YieldSettingDto {
    private Map<String, Double[]> abilityAlarmRule;

    public Map<String, Double[]> getAbilityAlarmRule() {
        return abilityAlarmRule;
    }

    public void setAbilityAlarmRule(Map<String, Double[]> abilityAlarmRule) {
        this.abilityAlarmRule = abilityAlarmRule;
    }


}
