/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.spc;


import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.Plugin;
import com.dmsoft.firefly.sdk.plugin.PluginImageContext;
import com.dmsoft.firefly.sdk.utils.enums.InitModel;

/**
 * spc plugin
 */
public class SpcPlugin extends Plugin {
    @Override
    public void initialize(InitModel model) {
        RuntimeContext.getBean(PluginImageContext.class).registerPluginInstance("com.dmsoft.dap.SpcPlugin", "com.dmsoft.firefly.plugin.spc.SpcService", new SpcService());
        System.out.println("SPC Initialized!!!!!!");
    }

    @Override
    public void start() {
        System.out.println("SPC Started!!!!!!");
    }

    @Override
    public void destroy() {
        System.out.println("SPC Destroyed!!!!!!");
    }
}
