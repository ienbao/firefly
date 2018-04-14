package com.dmsoft.firefly.plugin.tm.csvresolver.service;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.plugin.tm.csvresolver.dto.CsvTemplateDto;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.plugin.apis.IConfig;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.Config;
import com.dmsoft.firefly.sdk.utils.FileUtils;

import java.io.File;

/**
 * Created by GuangLi on 2018/3/9.
 */
@Config
public class CsvConfigService implements IConfig {
    private PluginContext pluginContext = RuntimeContext.getBean(PluginContext.class);
    private CsvResolverService csvResolverService = new CsvResolverService();
    private JsonMapper jsonMapper = JsonMapper.defaultMapper();

    @Override
    public String getConfigName() {
        return "MENU_TM_CSV_RESOLVER";
    }

    @Override
    public byte[] exportConfig() {
        CsvTemplateDto csvTemplateDto = csvResolverService.findCsvTemplate();
        if (csvTemplateDto != null) {
            return jsonMapper.toJson(csvTemplateDto).getBytes();
        }
        return new byte[0];
    }

    @Override
    public void importConfig(byte[] config) {
        if (config != null) {
            CsvTemplateDto csvTemplateDto = jsonMapper.fromJson(new String(config), CsvTemplateDto.class);
            csvResolverService.saveCsvTemplate(csvTemplateDto);
        }
    }

    @Override
    public void restoreConfig() {
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.TMCsvResolverPlugin").getFolderPath() + File.separator + "config";
        String defaultParentPath = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.TMCsvResolverPlugin").getFolderPath() + File.separator + "default";
        FileUtils.delFolder(path);
        FileUtils.copyFolder(defaultParentPath, path);
    }
}
