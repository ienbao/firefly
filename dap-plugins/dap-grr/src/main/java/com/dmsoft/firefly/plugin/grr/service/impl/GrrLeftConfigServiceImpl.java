package com.dmsoft.firefly.plugin.grr.service.impl;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.utils.JsonFileUtil;
import com.dmsoft.firefly.plugin.grr.dto.GrrLeftConfigDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import org.springframework.stereotype.Service;

/**
 * Created by Julia on 2018/3/18.
 */
@Service
public class GrrLeftConfigServiceImpl {
    private Logger logger = LoggerFactory.getLogger(GrrLeftConfigServiceImpl.class);
    private JsonMapper mapper = JsonMapper.defaultMapper();

    public GrrLeftConfigDto importGrrConfig(File file) {
        String json = JsonFileUtil.readJsonFile(file);
        if (json == null) {
            logger.debug("Don`t find " + file.getName());
        }
        GrrLeftConfigDto grrLeftConfigDto = mapper.fromJson(json, GrrLeftConfigDto.class);
        return grrLeftConfigDto;
    }

    public void exportGrrConfig(GrrLeftConfigDto grrLeftConfigDto, File file) {
        String json = JsonFileUtil.readJsonFile(file);
        if (json == null) {
            logger.debug("Don`t find " + file.getName());
        }
        if (grrLeftConfigDto != null) {
            JsonFileUtil.writeJsonFile(grrLeftConfigDto, file);
            logger.debug("Export success");

        }

    }
}
