package com.dmsoft.firefly.plugin.yield.utils;

import com.dmsoft.firefly.core.sdkimpl.dai.ModuleType;
import com.dmsoft.firefly.core.sdkimpl.plugin.PluginContextImpl;
import com.dmsoft.firefly.core.utils.DapLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.utils.enums.LanguageType;
import javafx.fxml.FXMLLoader;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class YieldFxmlAndLanguageUtils {
    public static boolean isDebug = false;
    private static PluginContextImpl pluginContext = new PluginContextImpl();
    private static ResourceBundle getResourceBundle() {
        LanguageType languageType = DapLanguageUtils.getLanguageType();
        if (languageType == null) {
            languageType = LanguageType.EN;
        }
        String bundleKey = "i18n.message_en_US_";
        if (LanguageType.ZH.equals(languageType)) {
            bundleKey = "i18n.message_zh_CN_";
        }
        bundleKey = bundleKey + ModuleType.Yield.name();
        return ResourceBundle.getBundle(bundleKey);
    }

    /**
     * get loaderFxml
     *
     * @param res the path of fxml
     * @return loader
     */
    public static FXMLLoader getLoaderFXML(String res) {
        FXMLLoader fxmlLoader = new FXMLLoader(YieldFxmlAndLanguageUtils.class.getClassLoader().getResource(res), getResourceBundle());
        if (isDebug == false) {
            fxmlLoader.setClassLoader(pluginContext.getDAPClassLoader("com.dmsoft.dap.YieldPlugin"));
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
