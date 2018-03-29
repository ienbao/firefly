/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.gui.handler.importcsv;

import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.PluginClass;
import com.dmsoft.firefly.sdk.plugin.PluginClassType;
import com.dmsoft.firefly.sdk.plugin.PluginImageContext;
import com.dmsoft.firefly.sdk.plugin.apis.IDataParser;

import java.util.List;

/**
 * Created by Garen.Pang on 2018/3/2.
 */
public class ResolverSelectHandler implements JobInboundHandler {

    @Override
    public void doJob(JobHandlerContext context, Object... in) throws Exception {
        if (in == null || in.length != 2) {
            //throw exception
        }
        PluginImageContext pluginImageContext = RuntimeContext.getBean(PluginImageContext.class);
        List<PluginClass> pluginClasses = pluginImageContext.getPluginClassByType(PluginClassType.DATA_PARSER);
        IDataParser service = null;
        for (int i = 0; i < pluginClasses.size(); i++) {
            if (((IDataParser) pluginClasses.get(0).getInstance()).getName().equals(in[1])) {
                service = (IDataParser) pluginClasses.get(0).getInstance();
            }
        }
        context.fireDoJob(in[0], service);
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {

    }
}
