package com.dmsoft.firefly.plugin.csvresolver.service;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.plugin.csvresolver.dto.CsvTemplateDto;
import com.dmsoft.firefly.sdk.plugin.apis.IConfig;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.Config;

/**
 * Created by GuangLi on 2018/3/9.
 */
@Config
public class CsvConfigService implements IConfig {
    private CsvResolverService csvResolverService = new CsvResolverService();
    private JsonMapper jsonMapper = JsonMapper.defaultMapper();

    @Override
    public String getConfigName() {
        return "CsvResolver";
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
        //TODO
    }
}
