/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.service.impl;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.utils.JsonFileUtil;
import com.dmsoft.firefly.plugin.spc.dto.*;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcStatsResultDto;
import com.dmsoft.firefly.plugin.spc.handler.ParamKeys;
import com.dmsoft.firefly.plugin.spc.service.SpcSettingService;
import com.dmsoft.firefly.sdk.utils.RangeUtils;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.dmsoft.firefly.plugin.spc.utils.enums.SpcKey;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.plugin.apis.IConfig;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.Config;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.ExcludeMethod;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * Created by Ethan.Yang on 2018/3/12.
 */
@Config
public class SpcSettingServiceImpl implements SpcSettingService, IConfig {
    private final Logger logger = LoggerFactory.getLogger(SpcSettingServiceImpl.class);
    private JsonMapper jsonMapper = JsonMapper.defaultMapper();
    private PluginContext pluginContext = RuntimeContext.getBean(PluginContext.class);

    @ExcludeMethod
    @Override
    public void saveSpcSetting(SpcSettingDto spcSettingDto) {
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.SpcPlugin").getFolderPath() + File.separator + "config";
        String json = JsonFileUtil.readJsonFile(path, ParamKeys.SPC_SETTING_FILE_NAME);
        if (json == null) {
            logger.debug("Don`t find " + ParamKeys.SPC_SETTING_FILE_NAME);
        }

        JsonFileUtil.writeJsonFile(spcSettingDto, path, ParamKeys.SPC_SETTING_FILE_NAME);
    }

    @ExcludeMethod
    @Override
    public SpcSettingDto findSpcSetting() {
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.SpcPlugin").getFolderPath() + File.separator + "config";
        String json = JsonFileUtil.readJsonFile(path, ParamKeys.SPC_SETTING_FILE_NAME);
        if (json == null) {
            logger.debug("Don`t find " + ParamKeys.SPC_SETTING_FILE_NAME);
        }

        SpcSettingDto spcSettingDto = null;
        if (!StringUtils.isEmpty(json)) {
            spcSettingDto = jsonMapper.fromJson(json, SpcSettingDto.class);
        }
        return spcSettingDto;
    }

    @ExcludeMethod
    @Override
    public List<SpcStatisticalResultAlarmDto> setStatisticalResultAlarm(List<SpcStatsDto> spcStatsDtoList) {
        if (spcStatsDtoList == null) {
            logger.debug("The statistical result is null");
            return null;
        }
        SpcSettingDto spcSettingDto = this.findSpcSetting();
        Map<String, Double[]> abilityAlarmRule = spcSettingDto.getAbilityAlarmRule();
        Map<String, List<CustomAlarmDto>> statisticalAlarmSetting = spcSettingDto.getStatisticalAlarmSetting();
        List<String> statisticalResultNameList = asList(UIConstant.SPC_CHOOSE_RESULT);
        List<SpcStatisticalResultAlarmDto> spcStatisticalResultAlarmDtoList = Lists.newArrayList();
        spcStatsDtoList.forEach(spcStatsDto -> {
            SpcStatisticalResultAlarmDto spcStatisticalResultAlarmDto = new SpcStatisticalResultAlarmDto();
            spcStatisticalResultAlarmDto.setKey(spcStatsDto.getKey());
            spcStatisticalResultAlarmDto.setItemName(spcStatsDto.getItemName());
            spcStatisticalResultAlarmDto.setCondition(spcStatsDto.getCondition());

            SpcStatsResultDto spcStatsResultDto = spcStatsDto.getStatsResultDto();
            Map<String, StatisticalAlarmDto> statisticalAlarmDtoMap = Maps.newHashMap();
            statisticalResultNameList.forEach(statisticalResultName -> {
                Double value = null;
                if (statisticalResultName.equals(SpcKey.SAMPLES.getCode())) {
                    value = spcStatsResultDto.getSamples();
                } else if (statisticalResultName.equals(SpcKey.AVG.getCode())) {
                    value = spcStatsResultDto.getAvg();
                } else if (statisticalResultName.equals(SpcKey.MAX.getCode())) {
                    value = spcStatsResultDto.getMax();
                } else if (statisticalResultName.equals(SpcKey.MIN.getCode())) {
                    value = spcStatsResultDto.getMin();
                } else if (statisticalResultName.equals(SpcKey.ST_DEV.getCode())) {
                    value = spcStatsResultDto.getStDev();
                } else if (statisticalResultName.equals(SpcKey.LSL.getCode())) {
                    value = spcStatsResultDto.getLsl();
                } else if (statisticalResultName.equals(SpcKey.USL.getCode())) {
                    value = spcStatsResultDto.getUsl();
                } else if (statisticalResultName.equals(SpcKey.CENTER.getCode())) {
                    value = spcStatsResultDto.getCenter();
                } else if (statisticalResultName.equals(SpcKey.RANGE.getCode())) {
                    value = spcStatsResultDto.getRange();
                } else if (statisticalResultName.equals(SpcKey.LCL.getCode())) {
                    value = spcStatsResultDto.getLcl();
                } else if (statisticalResultName.equals(SpcKey.UCL.getCode())) {
                    value = spcStatsResultDto.getUcl();
                } else if (statisticalResultName.equals(SpcKey.KURTOSIS.getCode())) {
                    value = spcStatsResultDto.getKurtosis();
                } else if (statisticalResultName.equals(SpcKey.SKEWNESS.getCode())) {
                    value = spcStatsResultDto.getSkewness();
                } else if (statisticalResultName.equals(SpcKey.CPK.getCode())) {
                    value = spcStatsResultDto.getCpk();
                } else if (statisticalResultName.equals(SpcKey.CA.getCode())) {
                    value = spcStatsResultDto.getCa();
                } else if (statisticalResultName.equals(SpcKey.CP.getCode())) {
                    value = spcStatsResultDto.getCp();
                } else if (statisticalResultName.equals(SpcKey.CPL.getCode())) {
                    value = spcStatsResultDto.getCpl();
                } else if (statisticalResultName.equals(SpcKey.CPU.getCode())) {
                    value = spcStatsResultDto.getCpu();
                } else if (statisticalResultName.equals(SpcKey.WITHIN_PPM.getCode())) {
                    value = spcStatsResultDto.getWithinPPM();
                } else if (statisticalResultName.equals(SpcKey.OVERALL_PPM.getCode())) {
                    value = spcStatsResultDto.getOverallPPM();
                } else if (statisticalResultName.equals(SpcKey.PP.getCode())) {
                    value = spcStatsResultDto.getPp();
                } else if (statisticalResultName.equals(SpcKey.PPK.getCode())) {
                    value = spcStatsResultDto.getPpk();
                } else if (statisticalResultName.equals(SpcKey.PPL.getCode())) {
                    value = spcStatsResultDto.getPpl();
                } else if (statisticalResultName.equals(SpcKey.PPU.getCode())) {
                    value = spcStatsResultDto.getPpu();
                }

                StatisticalAlarmDto statisticalAlarmDto = new StatisticalAlarmDto();
                statisticalAlarmDto.setValue(value);
                String level = null;
                List<CustomAlarmDto> customAlarmDtoList = statisticalAlarmSetting.get(spcStatsDto.getItemName());
                if (customAlarmDtoList != null && SpcKey.isCustomAlarmResultName(statisticalResultName)) {
                    level = this.getCustomAlarmLevel(statisticalResultName, value, customAlarmDtoList);
                }

                if (SpcKey.isAbilityAlarmResultName(statisticalResultName)) {
                    level = this.getAbilityAlarmLevel(statisticalResultName, value, abilityAlarmRule);
                }
                statisticalAlarmDto.setLevel(level);
                statisticalAlarmDtoMap.put(statisticalResultName, statisticalAlarmDto);
            });
            spcStatisticalResultAlarmDto.setStatisticalAlarmDtoMap(statisticalAlarmDtoMap);
            spcStatisticalResultAlarmDtoList.add(spcStatisticalResultAlarmDto);
        });

        return spcStatisticalResultAlarmDtoList;
    }

    @ExcludeMethod
    @Override
    public void saveSpcExportTemplateSetting(Map<String, Boolean> exportSetting) {
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.SpcPlugin").getFolderPath() + File.separator + "config";
        String json = JsonFileUtil.readJsonFile(path, ParamKeys.SPC_EXPORT_SETTING_FILE_NAME);
        if (json == null) {
            logger.debug("Don`t find " + ParamKeys.SPC_EXPORT_SETTING_FILE_NAME);
        }

        JsonFileUtil.writeJsonFile(exportSetting, path, ParamKeys.SPC_EXPORT_SETTING_FILE_NAME);
    }

    @ExcludeMethod
    @Override
    public Map<String, Boolean> findSpcExportTemplateSetting() {
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.SpcPlugin").getFolderPath() + File.separator + "config";
        String json = JsonFileUtil.readJsonFile(path, ParamKeys.SPC_EXPORT_SETTING_FILE_NAME);
        if (json == null) {
            logger.debug("Don`t find " + ParamKeys.SPC_EXPORT_SETTING_FILE_NAME);
        }

        Map<String, Boolean> exportSetting = null;
        if (!StringUtils.isEmpty(json)) {
            exportSetting = jsonMapper.fromJson(json, Map.class);
        }
        return exportSetting;
    }

    @Override
    public String getConfigName() {
        return "Spc Setting";
    }

    @Override
    public byte[] exportConfig() {
        SpcSettingDto spcSettingDto = findSpcSetting();
        Map<String, Boolean> exportSetting = findSpcExportTemplateSetting();
        Map<String, Object> settingMap = Maps.newHashMap();
        if (spcSettingDto != null || exportSetting != null) {
            settingMap.put(ParamKeys.SPC_SETTING_FILE_NAME, spcSettingDto);
            settingMap.put(ParamKeys.SPC_EXPORT_SETTING_FILE_NAME, exportSetting);
            return jsonMapper.toJson(settingMap).getBytes();
        }
        return new byte[0];
    }

    @Override
    public void importConfig(byte[] config) {
        if (config == null) {
            return;
        }
        Map<String, Object> settingMap = jsonMapper.fromJson(new String(config), Map.class);
        saveSpcSetting((SpcSettingDto) settingMap.get(ParamKeys.SPC_SETTING_FILE_NAME));
        saveSpcExportTemplateSetting((Map<String, Boolean>) settingMap.get(ParamKeys.SPC_EXPORT_SETTING_FILE_NAME));
    }

    private String getAbilityAlarmLevel(String name, Double value, Map<String, Double[]> abilityAlarmRule) {
        if (value == null) {
            return null;
        }
        Double[] alarmData = abilityAlarmRule.get(name);
        String level = null;
        if (alarmData != null) {
            if (name.equals(SpcKey.CA.getCode())) {
                if (value < alarmData[0]) {
                    level = SpcKey.EXCELLENT.getCode();
                } else if (value < alarmData[1]) {
                    level = SpcKey.ACCEPTABLE.getCode();
                } else if (value < alarmData[2]) {
                    level = SpcKey.RECTIFICATION.getCode();
                } else {
                    level = SpcKey.BAD.getCode();
                }
            } else {
                if (value >= alarmData[0]) {
                    level = SpcKey.EXCELLENT.getCode();
                } else if (value >= alarmData[1]) {
                    level = SpcKey.GOOD.getCode();
                } else if (value >= alarmData[2]) {
                    level = SpcKey.ACCEPTABLE.getCode();
                } else if (value >= alarmData[3]) {
                    level = SpcKey.RECTIFICATION.getCode();
                } else {
                    level = SpcKey.BAD.getCode();
                }
            }
        }
        return level;
    }

    private String getCustomAlarmLevel(String name, Double value, List<CustomAlarmDto> customAlarmDtoList) {
        if (value == null || customAlarmDtoList == null) {
            return null;
        }
        String level = null;
        for (CustomAlarmDto customAlarmDto : customAlarmDtoList) {
            if (name.equals(customAlarmDto.getStatisticName())) {
                Double lowerLimit = customAlarmDto.getLowerLimit();
                Double upperLimit = customAlarmDto.getUpperLimit();
                if (RangeUtils.isWithinRange(String.valueOf(value), String.valueOf(lowerLimit), String.valueOf(upperLimit))) {
                    level = SpcKey.PASS.getCode();
                } else {
                    level = SpcKey.FAIL.getCode();
                }
            }
        }
        return level;

    }
}
