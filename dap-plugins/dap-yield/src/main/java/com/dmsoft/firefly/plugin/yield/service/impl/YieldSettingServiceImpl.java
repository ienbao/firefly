package com.dmsoft.firefly.plugin.yield.service.impl;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.plugin.yield.dto.OverviewAlarmDto;
import com.dmsoft.firefly.plugin.yield.dto.YieldOverviewDto;
import com.dmsoft.firefly.plugin.yield.dto.YieldOverviewResultAlarmDto;
import com.dmsoft.firefly.plugin.yield.dto.YieldSettingDto;
import com.dmsoft.firefly.plugin.yield.service.YieldSettingService;
import com.dmsoft.firefly.plugin.yield.utils.YieldOverviewKey;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.plugin.apis.IConfig;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.Config;
import com.dmsoft.firefly.sdk.utils.DAPDoubleUtils;
import com.dmsoft.firefly.sdk.utils.FileUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

@Config
public class YieldSettingServiceImpl implements YieldSettingService, IConfig {

    private final Logger logger = LoggerFactory.getLogger(YieldSettingServiceImpl.class);
    private JsonMapper jsonMapper = JsonMapper.defaultMapper();
    private PluginContext pluginContext = RuntimeContext.getBean(PluginContext.class);


    @Override
    public String getConfigName(){
        return "MENU_Yield_SETTING";
    }

    @Override
    public byte[] exportConfig() {
        return new byte[0];
    }

    @Override
    public void importConfig(byte[] config) {
        if (config == null) {
            return;
        }
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
            yieldOverviewResultAlarmDto.setItemName(yieldOverviewDto.getItemName());

            Map<String, OverviewAlarmDto> statisticalAlarmDtoMap = Maps.newHashMap();
            for (int i = 0; i < overViewKeys.length; i++) {
                String overviewName = overViewKeys[i].getCode();
                String value = null;
                if (overviewName.equals(YieldOverviewKey.USL.getCode())) {
                    value = yieldOverviewDto.getUslOrPass();
                } else if (overviewName.equals(YieldOverviewKey.LSL.getCode())) {
                    value = yieldOverviewDto.getLslOrPass();
                } else if (overviewName.equals(YieldOverviewKey.TOTALSAMPLES.getCode())) {
                    value = yieldOverviewDto.getTotalSamples()+"";
                } else if (overviewName.equals(YieldOverviewKey.FPYSAMPLES.getCode())) {
                    value = yieldOverviewDto.getFpySamples()+"";
                } else if (overviewName.equals(YieldOverviewKey.PASSSAMPLES.getCode())) {
                    value = yieldOverviewDto.getPassSamples()+"";
                } else if (overviewName.equals(YieldOverviewKey.NTFSAMPLES.getCode())) {
                    value = yieldOverviewDto.getNtfSamples()+"";
                } else if (overviewName.equals(YieldOverviewKey.NGSAMPLES.getCode())) {
                    value = yieldOverviewDto.getNgSamples()+"";
                } else if (overviewName.equals(YieldOverviewKey.FPYPER.getCode())) {
                    value = yieldOverviewDto.getFpyPercent()+"";
                } else if (overviewName.equals(YieldOverviewKey.NTFPER.getCode())) {
                    value = yieldOverviewDto.getNtfPercent()+"";
                } else if (overviewName.equals(YieldOverviewKey.NGPER.getCode())) {
                    value = yieldOverviewDto.getNgPersent()+"";
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

    private String getAbilityAlarmLevel(String name, String value, Map<String, Double[]> abilityAlarmRule) {
//        if (DAPDoubleUtils.isSpecialNumber(value)) {
//            return null;
//        }
//        Double[] alarmData = abilityAlarmRule.get(name);
        String level = null;
//        if (alarmData != null) {
//            if (name.equals(YieldOverviewKey.CA.getCode())) {
//                value = Math.abs(value);
//                if ( value < alarmData[0]) {
//                    level = SpcKey.EXCELLENT.getCode();
//                } else if (value < alarmData[1]) {
//                    level = SpcKey.ACCEPTABLE.getCode();
//                } else if (value < alarmData[2]) {
//                    level = SpcKey.RECTIFICATION.getCode();
//                } else {
//                    level = SpcKey.BAD.getCode();
//                }
//            } else {
//                if (value >= alarmData[0]) {
//                    level = SpcKey.EXCELLENT.getCode();
//                } else if (value >= alarmData[1]) {
//                    level = SpcKey.GOOD.getCode();
//                } else if (value >= alarmData[2]) {
//                    level = SpcKey.ACCEPTABLE.getCode();
//                } else if (value >= alarmData[3]) {
//                    level = SpcKey.RECTIFICATION.getCode();
//                } else {
//                    level = SpcKey.BAD.getCode();
//                }
//            }
//        }
        return level;
    }
}