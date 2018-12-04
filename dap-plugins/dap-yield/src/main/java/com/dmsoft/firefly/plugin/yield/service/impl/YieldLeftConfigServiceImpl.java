package com.dmsoft.firefly.plugin.yield.service.impl;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.utils.JsonFileUtil;
import com.dmsoft.firefly.plugin.yield.dto.YieldLeftConfigDto;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class YieldLeftConfigServiceImpl {
    private Logger logger = LoggerFactory.getLogger(YieldLeftConfigServiceImpl.class);
    private JsonMapper mapper = JsonMapper.defaultMapper();

    @Autowired
    private PluginContext pluginContext;
    public YieldLeftConfigDto importSpcConfig(File file) {
        String json = JsonFileUtil.readJsonFile(file);
        if (json == null) {
            logger.debug("Don`t find " + file.getName());
        }
        YieldLeftConfigDto YieldLeftConfigDto = mapper.fromJson(json, YieldLeftConfigDto.class);
        return YieldLeftConfigDto;
    }

    public void exportYieldConfig(YieldLeftConfigDto YieldLeftConfigDto, File file) {
        String json = JsonFileUtil.readJsonFile(file);
        if (json == null) {
            logger.debug("Don`t find " + file.getName());
        }
        if (YieldLeftConfigDto != null) {
            JsonFileUtil.writeJsonFile(YieldLeftConfigDto, file);
            logger.debug("Export success");

        }

    }
//    public List<String> findSpcTimerTime() {
//        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.SpcPlugin").getFolderPath() + File.separator + "config";
//        String json = JsonFileUtil.readJsonFile(path, ParamKeys.SPC_CONFIG_FILE_NAME);
//        if (json == null) {
//            logger.debug("Don`t find " + ParamKeys.SPC_CONFIG_FILE_NAME);
//        }
//
//        List<String> timeList = Lists.newArrayList();
//        if (!StringUtils.isEmpty(json)) {
//            timeList = mapper.fromJson(json, List.class);
//        }
//        return timeList;
//    }
}