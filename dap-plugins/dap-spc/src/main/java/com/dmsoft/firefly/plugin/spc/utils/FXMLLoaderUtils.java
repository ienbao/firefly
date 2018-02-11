/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.utils;

import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import javafx.fxml.FXMLLoader;
import java.util.ResourceBundle;

/**
 * Created by Ethan.Yang on 2018/2/11.
 */
public class FXMLLoaderUtils {
    private static FXMLLoaderUtils fxmlLoaderUtils = new FXMLLoaderUtils();

    public static FXMLLoaderUtils getInstance() {
        return fxmlLoaderUtils;
    }

    public FXMLLoader getLoaderFXMLPane(String res){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(res), ResourceBundle.getBundle("i18n.message_en_US"));
        fxmlLoader.setClassLoader(RuntimeContext.getBean(PluginContext.class).getDAPClassLoader("com.dmsoft.dap.SpcPlugin"));
        return fxmlLoader;
    }
}