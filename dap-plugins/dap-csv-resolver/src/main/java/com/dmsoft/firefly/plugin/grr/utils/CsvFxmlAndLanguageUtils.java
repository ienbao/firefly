/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.grr.utils;

import com.dmsoft.firefly.gui.components.utils.ModuleType;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.utils.enums.LanguageType;
import javafx.fxml.FXMLLoader;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Created by Ethan.Yang on 2018/2/11.
 */
public class CsvFxmlAndLanguageUtils {

    public static boolean isDebug = false;

    private static ResourceBundle getResourceBundle() {
        LanguageType languageType = RuntimeContext.getBean(EnvService.class).getLanguageType();
        String bundleKey = "i18n.message_en_US_";
        if (languageType.equals(LanguageType.ZH)) {
            bundleKey = "i18n.message_zh_CN_";
        }
        bundleKey = bundleKey + ModuleType.CSV.name();

        return ResourceBundle.getBundle(bundleKey);
    }

    /**
     * get loaderFxml
     *
     * @param res the path of fxml
     * @return loader
     */
    public static FXMLLoader getLoaderFXML(String res) {
        FXMLLoader fxmlLoader = new FXMLLoader(CsvFxmlAndLanguageUtils.class.getClassLoader().getResource(res), getResourceBundle());
        if (isDebug == false) {
            fxmlLoader.setClassLoader(RuntimeContext.getBean(PluginContext.class).getDAPClassLoader("com.dmsoft.dap.CsvResolverPlugin"));
        }

        return fxmlLoader;
    }

    public static String getString(String key) {
        try {
            return getResourceBundle().getString(key);
        } catch (Exception var2) {
            return key;
        }
    }

    public static String getString(String key, Object[] params) {
        try {
            String result = getString(key);
            return MessageFormat.format(result, params);
        } catch (Exception var3) {
            return key;
        }
    }

    public static String getString(int key) {
        try {
            return getString(String.valueOf(key));
        } catch (Exception var2) {
            return String.valueOf(key);
        }
    }
}
