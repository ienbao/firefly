package com.dmsoft.firefly.plugin.spc.service.impl;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.utils.JsonFileUtil;
import com.dmsoft.firefly.plugin.spc.dto.SpcLeftConfigDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * Created by GuangLi on 2018/3/5.
 */
public class SpcLeftConfigServiceImpl {
    private Logger logger = LoggerFactory.getLogger(SpcLeftConfigServiceImpl.class);
    private JsonMapper mapper = JsonMapper.defaultMapper();

    public SpcLeftConfigDto importSpcConfig(File file) {
        String json = JsonFileUtil.readJsonFile(file);
        if (json == null) {
            logger.debug("Don`t find " + file.getName());
        }
        SpcLeftConfigDto spcLeftConfigDto = mapper.fromJson(json, SpcLeftConfigDto.class);
        return spcLeftConfigDto;
    }

    public void exportSpcConfig(SpcLeftConfigDto spcLeftConfigDto, File file) {
        String json = JsonFileUtil.readJsonFile(file);
        if (json == null) {
            logger.debug("Don`t find " + file.getName());
        }
//        SpcLeftConfigDto spcLeftConfigDto = mapper.fromJson(json, SpcLeftConfigDto.class);
        if (spcLeftConfigDto != null) {
            JsonFileUtil.writeJsonFile(spcLeftConfigDto, file);
            logger.debug("Export success");

        }

    }
}
