package com.dmsoft.firefly.plugin.yield.service.impl;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.utils.JsonFileUtil;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class YieldLeftConfigServiceImpl {
    private Logger logger = LoggerFactory.getLogger(YieldLeftConfigServiceImpl.class);
    private JsonMapper mapper = JsonMapper.defaultMapper();
    private PluginContext pluginContext = RuntimeContext.getBean(PluginContext.class);


}
