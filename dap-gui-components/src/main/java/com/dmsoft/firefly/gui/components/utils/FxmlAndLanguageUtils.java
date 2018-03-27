/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.components.utils;

import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.utils.enums.LanguageType;
import javafx.fxml.FXMLLoader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Created by Ethan.Yang on 2018/2/11.
 */
public class FxmlAndLanguageUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FxmlAndLanguageUtils.class);
    private static boolean IS_DEBUG = false;

    private static ResourceBundle getResourceBundle() {
        return FxmlAndLanguageUtils.getBundle(ModuleType.COM);
    }

    /**
     * get loaderFxml
     *
     * @param res the path of fxml
     * @return loader
     */
    public static FXMLLoader getLoaderFXML(String res) {
        FXMLLoader fxmlLoader = new FXMLLoader(FxmlAndLanguageUtils.class.getClassLoader().getResource(res), getResourceBundle());
        return fxmlLoader;
    }

    /**
     * get loaderFxml
     *
     * @param res the path of fxml
     * @return loader
     */
    public static FXMLLoader getLoaderFXML(String res, String pluginId) {
        FXMLLoader fxmlLoader = getLoaderFXML(res);
        if (StringUtils.isNotBlank(pluginId)) {
            fxmlLoader.setClassLoader(RuntimeContext.getBean(PluginContext.class).getDAPClassLoader(pluginId));
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


    public static ResourceBundle getBundle(ModuleType moduleKey) {
        LanguageType languageType = null;
        if (IS_DEBUG == false) {
            languageType = RuntimeContext.getBean(EnvService.class).getLanguageType();
        }
        if (languageType == null) {
            languageType = LanguageType.EN;
        }
        String bundleKey = "i18n.message_en_US_";
        if (languageType.equals(LanguageType.ZH)) {
            bundleKey = "i18n.message_zh_CN_";
        }
        if (StringUtils.isNotBlank(moduleKey.name())) {
            bundleKey = bundleKey + moduleKey.name();
        } else {
            LOGGER.error("The module key is null.");
            return null;
        }
        return ResourceBundle.getBundle(bundleKey);
    }

    public static void setIsDebug(boolean isDebug) {
        IS_DEBUG = isDebug;
    }
}
