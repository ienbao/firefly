/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.utils;

import com.dmsoft.firefly.gui.components.utils.ModuleType;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.utils.enums.LanguageType;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import static com.google.common.io.Resources.getResource;

/**
 * Created by Ethan.Yang on 2018/2/11.
 */
public class GuiFxmlAndLanguageUtils {
    private static final Logger logger = LoggerFactory.getLogger(GuiFxmlAndLanguageUtils.class);

    private static ResourceBundle getResourceBundle() {
        LanguageType languageType = RuntimeContext.getBean(EnvService.class).getLanguageType();
        String bundleKey = "i18n.message_en_US_";
        if (languageType.equals(LanguageType.ZH)) {
            bundleKey = "i18n.message_zh_CN_";
        }
        bundleKey = bundleKey + ModuleType.GUI.name();
        logger.debug("Language: {}", bundleKey);
        return ResourceBundle.getBundle(bundleKey);
    }

    /**
     * get loaderFxml
     *
     * @param res the path of fxml
     * @return loader
     */
    public static FXMLLoader getLoaderFXML(String res) {
        FXMLLoader fxmlLoader = new FXMLLoader(GuiFxmlAndLanguageUtils.class.getClassLoader().getResource(res), getResourceBundle());
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

    public static void buildLoginDialog() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/login.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel(GuiConst.PLARTFORM_STAGE_LOGIN, "", root, getResource("css/platform_app.css").toExternalForm());
            stage.setResizable(false);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void buildChangePasswordDia() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/change_password.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("platform_gui_change_password", GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD"), root, getResource("css/platform_app.css").toExternalForm());
            stage.setResizable(false);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void buildLegalDialog() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/legal_notice.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createSimpleWindowAsModel(GuiConst.PLARTFORM_STAGE_LEGAL, GuiFxmlAndLanguageUtils.getString(ResourceMassages.DATASOURCE), root, getResource("css/platform_app.css").toExternalForm());
            stage.setResizable(false);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
