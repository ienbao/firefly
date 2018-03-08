package com.dmsoft.firefly.plugin.grr.service.impl;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.utils.JsonFileUtil;
import com.dmsoft.firefly.plugin.grr.dto.GrrConfigDto;
import com.dmsoft.firefly.plugin.grr.service.GrrConfigService;
import com.dmsoft.firefly.plugin.grr.utils.enums.GrrAnalysisMethod;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.plugin.apis.IConfig;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.Config;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.ExcludeMethod;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Created by GuangLi on 2018/3/7.
 */
@Config
public class GrrConfigServiceImpl implements GrrConfigService, IConfig {
    private final Logger logger = LoggerFactory.getLogger(GrrConfigServiceImpl.class);

    private PluginContext pluginContext = RuntimeContext.getBean(PluginContext.class);
    private String fileName = "grrSetting";
    private JsonMapper jsonMapper = JsonMapper.defaultMapper();

    @Override
    public String getConfigName() {
        return "Grr Setting";
    }

    @Override
    public byte[] exportConfig() {
        GrrConfigDto grrConfigDto = findGrrConfig();
        if (grrConfigDto != null) {
            return jsonMapper.toJson(grrConfigDto).getBytes();
        }
        return new byte[0];
    }

    @Override
    public void importConfig(byte[] config) {
        if (config == null) {
            return;
        }
        saveGrrConfig(jsonMapper.fromJson(new String(config), GrrConfigDto.class));
    }

    @ExcludeMethod
    @Override
    public void saveGrrConfig(GrrConfigDto grrConfigDto) {
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.GrrPlugin").getFolderPath();
        String json = JsonFileUtil.readJsonFile(path, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
        }

        JsonFileUtil.writeJsonFile(grrConfigDto, path, fileName);
    }

    @ExcludeMethod
    @Override
    public GrrConfigDto findGrrConfig() {
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.GrrPlugin").getFolderPath();
        String json = JsonFileUtil.readJsonFile(path, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
        }

        GrrConfigDto grrConfigDto = null;
        if (!StringUtils.isEmpty(json)) {
            grrConfigDto = jsonMapper.fromJson(json, GrrConfigDto.class);
        }
        if (grrConfigDto == null) {
            grrConfigDto = new GrrConfigDto();
            grrConfigDto.setAnalysisMethod(GrrAnalysisMethod.ANOVA);
            grrConfigDto.setCoverage(5.15);
            grrConfigDto.setSignLevel("0.005");
            grrConfigDto.setSortMethod("Appraisers");
            grrConfigDto.setAlarmSetting(Arrays.asList(new Double[]{5.0, 10.0}));
        }
        return grrConfigDto;
    }
}
