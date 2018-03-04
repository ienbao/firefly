package com.dmsoft.firefly.gui.utils;

/**
 * Created by ChenQiang on 2017/12/21.
 */
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.utils.enums.LanguageType;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ResourceBundleFactory {
    public static final ResourceBundle RE_EN_US = ResourceBundle.getBundle("i18n.message_en_US_GUI");
    public static final ResourceBundle RE_ZH_CN = ResourceBundle.getBundle("i18n.message_zh_CN_GUI");

    public ResourceBundleFactory() {
    }

    public static String getString(String key) {
        try {
            LanguageType languageType = RuntimeContext.getBean(EnvService.class).getLanguageType();
           // ResourceBundleUtils.getString(key)
            return "";
        } catch (Exception var2) {
            return key;
        }
    }

    public static ResourceBundle getLanguageType() {
        try {
            LanguageType languageType = RuntimeContext.getBean(EnvService.class).getLanguageType();
            if (languageType.equals(LanguageType.ZH)) {
                return RE_ZH_CN;
            } else {
                return RE_EN_US;
            }
        } catch (Exception var2) {
            return RE_EN_US;
        }
    }

    public static String getString(String key, Object[] params) {
        try {
            String result = null;
            LanguageType languageType = RuntimeContext.getBean(EnvService.class).getLanguageType();
            if (languageType.equals(LanguageType.ZH)) {
                result = RE_ZH_CN.getString(key);
            } else {
                result = RE_EN_US.getString(key);
            }
            return MessageFormat.format(result, params);
        } catch (Exception var3) {
            return key;
        }
    }

}
