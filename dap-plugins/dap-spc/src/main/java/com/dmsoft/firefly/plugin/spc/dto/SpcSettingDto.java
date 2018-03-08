/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.dto;

import java.util.Map;

/**
 * Created by Ethan.Yang on 2018/3/8.
 */
public class SpcSettingDto {

    private int decimalDigit;
    private int customGroupNumber;
    private int chartIntervalNumber;


    private Map<String, Double[]> abilityAlarmRule;
    private Map<String, Object[]> controlChartRule;
}
