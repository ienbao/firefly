package com.dmsoft.firefly.plugin.yield.service.impl;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.utils.JsonFileUtil;
import com.dmsoft.firefly.plugin.yield.dto.*;
import com.dmsoft.firefly.plugin.yield.handler.ParamKeys;
import com.dmsoft.firefly.plugin.yield.utils.YieldOverviewKey;
import com.dmsoft.firefly.plugin.yield.service.YieldSettingService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.plugin.apis.IConfig;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.Config;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.ExcludeMethod;
import com.dmsoft.firefly.sdk.utils.DAPDoubleUtils;
import com.dmsoft.firefly.sdk.utils.FileUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;

@Service
public class YieldSettingServiceImpl implements YieldSettingService, IConfig {

    private final Logger logger = LoggerFactory.getLogger(YieldSettingServiceImpl.class);
    private JsonMapper jsonMapper = JsonMapper.defaultMapper();

    @Autowired
    private PluginContext pluginContext;


    @Override
    public String getConfigName(){
        return "MENU_YIELD_SETTING";
    }

    @Override
    public byte[] exportConfig() {
        YieldSettingDto yieldSettingDto=findYieldSetting();
        if (yieldSettingDto != null ) {
            return jsonMapper.toJson(yieldSettingDto).getBytes();
        }
        return new byte[0];
    }

    @Override
    public void importConfig(byte[] config) {
        if (config == null) {
            return;
        }
        YieldSettingDto yieldSettingDto = jsonMapper.fromJson(new String(config), YieldSettingDto.class);
        saveYieldSetting(yieldSettingDto);
    }

    @Override
    public void restoreConfig() {
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.YieldPlugin").getFolderPath() + File.separator + "config";
        String defaultParentPath = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.YieldPlugin").getFolderPath() + File.separator + "default";
        FileUtils.delFolder(path);
        FileUtils.copyFolder(defaultParentPath, path);
    }

    @Override
    public List<YieldOverviewResultAlarmDto> setStatisticalResultAlarm(List<YieldOverviewDto> overViewDtoList, YieldSettingDto yieldSettingDto) {
        if (overViewDtoList == null || yieldSettingDto == null) {
            logger.debug("The statistical result or setting data is null");
            return null;
        }
        Map<String, Double[]> abilityAlarmRule = yieldSettingDto.getAbilityAlarmRule();
        YieldOverviewKey[] overViewKeys = YieldOverviewKey.values();
        if (overViewKeys == null) {
            return null;
        }
        List<YieldOverviewResultAlarmDto> yieldOverviewResultAlarmDtoList = Lists.newArrayList();
        overViewDtoList.forEach(yieldOverviewDto -> {
            YieldOverviewResultAlarmDto yieldOverviewResultAlarmDto = new YieldOverviewResultAlarmDto();
            yieldOverviewResultAlarmDto.setKey(yieldOverviewDto.getKey());
            yieldOverviewResultAlarmDto.setItemName(yieldOverviewDto.getItemName());
            yieldOverviewResultAlarmDto.setLslOrFail(yieldOverviewDto.getLslOrPass());
            yieldOverviewResultAlarmDto.setUslOrPass(yieldOverviewDto.getUslOrPass());
            yieldOverviewResultAlarmDto.setTotalSamples(yieldOverviewDto.getTotalSamples());
            yieldOverviewResultAlarmDto.setPassSamples(yieldOverviewDto.getPassSamples());
            yieldOverviewResultAlarmDto.setNtfSamples(yieldOverviewDto.getNtfSamples());
            yieldOverviewResultAlarmDto.setNgSamples(yieldOverviewDto.getNgSamples());
            yieldOverviewResultAlarmDto.setFpySamples(yieldOverviewDto.getFpySamples());
            yieldOverviewResultAlarmDto.setTestItemType(yieldOverviewDto.getTestItemType());

            Map<String, OverviewAlarmDto> statisticalAlarmDtoMap = Maps.newHashMap();
            for (int i = 0; i < overViewKeys.length; i++) {
                String overviewName = overViewKeys[i].getCode();
                Double value = null;
                if (overviewName.equals(YieldOverviewKey.FPYPER.getCode())) {
                    value = yieldOverviewDto.getFpyPercent();
                } else if (overviewName.equals(YieldOverviewKey.NTFPER.getCode())) {
                    value = yieldOverviewDto.getNtfPercent();
                } else if (overviewName.equals(YieldOverviewKey.NGPER.getCode())) {
                    value = yieldOverviewDto.getNgPersent();
                }

                OverviewAlarmDto overviewAlarmDto = new OverviewAlarmDto();
                overviewAlarmDto.setValue(value);
                String level = null;

                if (YieldOverviewKey.isAbilityAlarmResultName(overviewName)) {
                    level = this.getAbilityAlarmLevel(overviewName, value, abilityAlarmRule);
                }
                overviewAlarmDto.setLevel(level);
                statisticalAlarmDtoMap.put(overviewName, overviewAlarmDto);
            }
            yieldOverviewResultAlarmDto.setOverviewAlarmDtoMap(statisticalAlarmDtoMap);
            yieldOverviewResultAlarmDtoList.add(yieldOverviewResultAlarmDto);
        });
        return yieldOverviewResultAlarmDtoList;
    }
    @Override
    public YieldChartResultAlermDto setChartResultAlarm(YieldTotalProcessesDto totalProcessesDtos, YieldSettingDto yieldSettingDto) {
        if (totalProcessesDtos == null || yieldSettingDto == null){
            return null;
        }
        Map<String, Double[]> abilityAlarmRule = yieldSettingDto.getAbilityAlarmRule();
        YieldOverviewKey[] overViewKeys = YieldOverviewKey.values();
        if (overViewKeys == null) {
            return null;
        }
        YieldChartResultAlermDto yieldChartResultAlermDto = new YieldChartResultAlermDto();
        yieldChartResultAlermDto.setFpyPercent(totalProcessesDtos.getFpyPercent());
        yieldChartResultAlermDto.setNtfPercent(totalProcessesDtos.getNtfPercent());
        yieldChartResultAlermDto.setNgPercent(totalProcessesDtos.getNgPercent());
       Map<String,YieldChartAlermDto> yieldChartAlermDtoMap = Maps.newHashMap();

       for (int i = 0 ; i < overViewKeys.length ; i++){
           String overviewName = overViewKeys[i].getCode();
           if (YieldOverviewKey.isAbilityAlarmResultName(overviewName)){
               Double value = null;
               if(YieldOverviewKey.FPYPER.getCode().equals(overviewName)){
                   value = totalProcessesDtos.getFpyPercent();
               }else if(YieldOverviewKey.NTFPER.getCode().equals(overviewName)){
                   value = totalProcessesDtos.getNtfPercent();
               }else if (YieldOverviewKey.NGPER.getCode().equals(overviewName)){
                   value = totalProcessesDtos.getNgPercent();
               }
               if (value == null) {
                   value = 0D;
               }
               YieldChartAlermDto yieldChartAlermDto = new YieldChartAlermDto();
               yieldChartAlermDto.setValue(value);
               String leval = null;
                   leval = this.getAbilityAlarmLevel(overviewName,value,abilityAlarmRule);
                   yieldChartAlermDto.setLevel(leval);
                   yieldChartAlermDtoMap.put(overviewName,yieldChartAlermDto);
               }
       }

       yieldChartResultAlermDto.setYieldChartResultAlermDtoMap(yieldChartAlermDtoMap);

        return yieldChartResultAlermDto;
    }

    private String getAbilityAlarmLevel(String name, Double value, Map<String, Double[]> abilityAlarmRule) {
        if (DAPDoubleUtils.isSpecialNumber(value)) {
            return null;
        }
        Double[] alarmData = abilityAlarmRule.get(name);
        String level = null;
        if (alarmData != null) {
            if (name.equals(YieldOverviewKey.FPYPER.getCode())) {
                value = Math.abs(value);
                if ( value >= alarmData[0]/100) {
                    level = YieldOverviewKey.EXCELLENT.getCode();
                } else if (value >= alarmData[1]/100) {
                    level = YieldOverviewKey.ADEQUATE.getCode();
                } else if (value >= alarmData[2]/100) {
                    level = YieldOverviewKey.MARGINAL.getCode();
                } else {
                    level = YieldOverviewKey.BAD.getCode();
                }
            } else {
                if (value < alarmData[0]/100) {
                    level = YieldOverviewKey.EXCELLENT.getCode();
                } else if (value < alarmData[1]/100) {
                    level = YieldOverviewKey.ADEQUATE.getCode();
                } else if (value < alarmData[2]/100) {
                    level = YieldOverviewKey.MARGINAL.getCode();
                } else {
                    level = YieldOverviewKey.BAD.getCode();
                }
            }
        }
        return level;
    }
    @ExcludeMethod
    @Override
    public YieldSettingDto findYieldSetting() {
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.YieldPlugin").getFolderPath() + File.separator + "config";
        String json = JsonFileUtil.readJsonFile(path, ParamKeys.YIELD_SETTING_FILE_NAME);
        if (json == null) {
            logger.debug("Don`t find " + ParamKeys.YIELD_SETTING_FILE_NAME);
        }

        YieldSettingDto yieldSettingDto = null;
        if (!StringUtils.isEmpty(json)) {
            yieldSettingDto = jsonMapper.fromJson(json, YieldSettingDto.class);
        }
        return yieldSettingDto;
    }
    @ExcludeMethod
    @Override
    public void saveYieldSetting(YieldSettingDto yieldSettingDto) {
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.YieldPlugin").getFolderPath() + File.separator + "config";
        String json = JsonFileUtil.readJsonFile(path, ParamKeys.YIELD_SETTING_FILE_NAME);
        if (json == null) {
            logger.debug("Don`t find " + ParamKeys.YIELD_SETTING_FILE_NAME);
        }

        JsonFileUtil.writeJsonFile(yieldSettingDto, path, ParamKeys.YIELD_SETTING_FILE_NAME);
    }


}
