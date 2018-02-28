package com.dmsoft.firefly.gui.components.utils;

/**
 * Created by ChenQiang on 2017/12/21.
 */
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ResourceBundleUtils {
    private static final String RES_NAME = "i18n.message_en_US_GUI";
    private static final ResourceBundle RESB = ResourceBundle.getBundle("i18n.message_en_US_GUI");

    public ResourceBundleUtils() {
    }

    public static String getString(String key) {
        try {
            return RESB.getString(key);
        } catch (Exception var2) {
            return key;
        }
    }

    public static String getString(String key, Object[] params) {
        try {
            String result = RESB.getString(key);
            return MessageFormat.format(result, params);
        } catch (Exception var3) {
            return key;
        }
    }

    public static String getString(int key) {
        return getString(String.valueOf(key));
    }
}
