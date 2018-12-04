package com.dmsoft.firefly.plugin.am.csvresolver.service;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.plugin.am.csvresolver.dto.CsvTemplateDto;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.plugin.apis.IConfig;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.Config;
import com.dmsoft.firefly.sdk.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Created by GuangLi on 2018/3/9.
 */
@Service
public class CsvConfigService implements IConfig {

    @Autowired
    private PluginContext pluginContext;
    private CsvResolverService csvResolverService = new CsvResolverService();
    private JsonMapper jsonMapper = JsonMapper.defaultMapper();

    @Override
    public String getConfigName() {
        return "MENU_AM_CSV_RESOLVER";
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
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.AMCsvResolverPlugin").getFolderPath() + File.separator + "config";
        String defaultParentPath = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.AMCsvResolverPlugin").getFolderPath() + File.separator + "default";
        FileUtils.delFolder(path);
        FileUtils.copyFolder(defaultParentPath, path);
    }
}
