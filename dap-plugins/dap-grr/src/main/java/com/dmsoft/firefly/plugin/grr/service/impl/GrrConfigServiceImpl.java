package com.dmsoft.firefly.plugin.grr.service.impl;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.utils.JsonFileUtil;
import com.dmsoft.firefly.plugin.grr.dto.GrrConfigDto;
import com.dmsoft.firefly.plugin.grr.service.GrrConfigService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.plugin.apis.IConfig;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.Config;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.ExcludeMethod;
import com.dmsoft.firefly.sdk.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

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
        return "MENU_GRR_SETTING";
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
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.GrrPlugin").getFolderPath() + File.separator + "config";
        String json = JsonFileUtil.readJsonFile(path, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
        }
        GrrConfigDto oldGrrConfigDto = jsonMapper.fromJson(json, GrrConfigDto.class);
        grrConfigDto.setExport(oldGrrConfigDto.getExport());
        JsonFileUtil.writeJsonFile(grrConfigDto, path, fileName);
    }

    @ExcludeMethod
    @Override
    public void saveGrrExportConfig(Map<String, Boolean> export) {
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.GrrPlugin").getFolderPath() + File.separator + "config";
        String json = JsonFileUtil.readJsonFile(path, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
        }
        GrrConfigDto oldGrrConfigDto = jsonMapper.fromJson(json, GrrConfigDto.class);
        oldGrrConfigDto.setExport(export);
        JsonFileUtil.writeJsonFile(oldGrrConfigDto, path, fileName);
    }

    @ExcludeMethod
    @Override
    public GrrConfigDto findGrrConfig() {
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.GrrPlugin").getFolderPath() + File.separator + "config";
        String json = JsonFileUtil.readJsonFile(path, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
        }

        GrrConfigDto grrConfigDto = null;
        if (!StringUtils.isEmpty(json)) {
            grrConfigDto = jsonMapper.fromJson(json, GrrConfigDto.class);
        }
        return grrConfigDto;
    }

    @Override
    public Map<String, Boolean> findGrrExportConfig() {
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.GrrPlugin").getFolderPath() + File.separator + "config";
        String json = JsonFileUtil.readJsonFile(path, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
        }

        Map<String, Boolean> result = null;
        if (!StringUtils.isEmpty(json)) {
            GrrConfigDto grrConfigDto = jsonMapper.fromJson(json, GrrConfigDto.class);
            result = grrConfigDto.getExport();
        }
        return result;
    }

    @Override
    public void restoreConfig() {
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.GrrPlugin").getFolderPath() + File.separator + "config";
        String defaultParentPath = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.GrrPlugin").getFolderPath() + File.separator + "default";
        FileUtils.delFolder(path);
        FileUtils.copyFolder(defaultParentPath, path);
    }
}
