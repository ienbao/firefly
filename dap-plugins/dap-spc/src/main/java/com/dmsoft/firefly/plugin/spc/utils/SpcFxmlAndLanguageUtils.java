/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.utils;

import com.dmsoft.firefly.core.utils.DapLanguageUtils;
import com.dmsoft.firefly.gui.components.utils.ModuleType;
import com.dmsoft.firefly.plugin.spc.service.SpcFxmlLoadService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.utils.enums.LanguageType;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.springframework.context.ApplicationContext;

/**
 * Created by Ethan.Yang on 2018/2/11.
 */
public class SpcFxmlAndLanguageUtils {
    public static boolean isDebug = false;

    private static ApplicationContext context;

    public static ApplicationContext getContext() {
        return context;
    }

    public static void setContext(ApplicationContext context) {
        SpcFxmlAndLanguageUtils.context = context;
    }

    private static ResourceBundle getResourceBundle() {
        LanguageType languageType = DapLanguageUtils.getLanguageType();
        if (languageType == null) {
            languageType = LanguageType.EN;
        }
        String bundleKey = "i18n.message_en_US_";
        if (LanguageType.ZH.equals(languageType)) {
            bundleKey = "i18n.message_zh_CN_";
        }
        bundleKey = bundleKey + ModuleType.SPC.name();
        return ResourceBundle.getBundle(bundleKey);
    }

    /**
     * get loaderFxml
     *
     * @param res the path of fxml
     * @return loader
     */
    public static FXMLLoader getLoaderFXML(String res) {
        FXMLLoader fxmlLoader = context.getBean(SpcFxmlLoadService.class).getFxmlLoader(res);
        return fxmlLoader;
    }

    public static Node load(String fileFxml){

        return context.getBean(SpcFxmlLoadService.class).loadFxml(fileFxml);
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
