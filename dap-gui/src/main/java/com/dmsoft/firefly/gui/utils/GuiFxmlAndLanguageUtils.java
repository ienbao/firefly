/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.utils;

import com.dmsoft.firefly.gui.components.utils.ModuleType;
import com.dmsoft.firefly.gui.components.utils.StageMap;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(GuiFxmlAndLanguageUtils.class);

    private static ResourceBundle getResourceBundle() {
        LanguageType languageType = RuntimeContext.getBean(EnvService.class).getLanguageType();
        if (languageType == null) {
            languageType = LanguageType.EN;
        }
        String bundleKey = "i18n.message_en_US_";
        if (LanguageType.ZH.equals(languageType)) {
            bundleKey = "i18n.message_zh_CN_";
        }
        bundleKey = bundleKey + ModuleType.GUI.name();
        LOGGER.debug("Language: {}", bundleKey);
        return ResourceBundle.getBundle(bundleKey);
    }

    /**
     * get loaderFxml
     *
     * @param res the path of fxml
     * @return loader
     */
    public static FXMLLoader getLoaderFXML(String res) {
        return new FXMLLoader(GuiFxmlAndLanguageUtils.class.getClassLoader().getResource(res), getResourceBundle());
    }

    /**
     * method to convert string from i18n
     *
     * @param key key
     * @return i18n string
     */
    public static String getString(String key) {
        try {
            return getResourceBundle().getString(key);
        } catch (Exception var2) {
            return key;
        }
    }

    /**
     * method to convert string from i18n
     *
     * @param key    key
     * @param params params
     * @return i18n string
     */
    public static String getString(String key, Object[] params) {
        try {
            String result = getString(key);
            return MessageFormat.format(result, params);
        } catch (Exception var3) {
            return key;
        }
    }

    /**
     * method to get string from i18n
     *
     * @param key key
     * @return i18n string
     */
    public static String getString(int key) {
        try {
            return getString(String.valueOf(key));
        } catch (Exception var2) {
            return String.valueOf(key);
        }
    }

    /**
     * method to build login dialog
     */
    public static void buildLoginDialog() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/login.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel(GuiConst.PLARTFORM_STAGE_LOGIN, "", root, getResource("css/platform_app.css").toExternalForm());
            stage.setResizable(false);
            stage.toFront();
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * method to build change password
     */
    public static void buildChangePasswordDialog() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/change_password.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel(GuiConst.PLARTFORM_STAGE_CHANGE_PASSWORD, GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD"), root, getResource("css/platform_app.css").toExternalForm());
            stage.setResizable(false);
            stage.toFront();
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * method to build legal notice dialog
     */
    public static void buildLegalDialog() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/legal_notice.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel(GuiConst.PLARTFORM_STAGE_LEGAL,
                    GuiFxmlAndLanguageUtils.getString(ResourceMassages.MENU_LEGAL_NOTICE), root, getResource("css/platform_app.css").toExternalForm());
            stage.setResizable(false);
            stage.toFront();
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * method to build about dialog
     */
    public static void buildAboutDialog() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/about.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel(GuiConst.PLARTFORM_STAGE_ABOUT, "", root,
                    getResource("css/platform_app.css").toExternalForm());
            stage.setResizable(false);
            stage.toFront();
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * method to change password back dialog
     */
    public static void buildChangePasswordBackDialog() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/change_password_back.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel(GuiConst.PLARTFORM_STAGE_CHANGE_PASSWORD_BACK,
                    GuiFxmlAndLanguageUtils.getString("CHANGE_PASSWORD"), root, getResource("css/platform_app.css").toExternalForm());
            stage.setResizable(false);
            stage.toFront();
            stage.show();
            stage.setOnCloseRequest(event -> {
                StageMap.getStage(GuiConst.PLARTFORM_STAGE_CHANGE_PASSWORD_BACK).close();
                StageMap.getStage(GuiConst.PLARTFORM_STAGE_CHANGE_PASSWORD).close();
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * method to build template
     */
    public static void buildTemplateDialog() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/template.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("template",
                    GuiFxmlAndLanguageUtils.getString(ResourceMassages.TEMPLATE), root, getResource("css/platform_app.css").toExternalForm());
            stage.setResizable(false);
            stage.toFront();
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * method to build select data source
     */
    public static void buildSelectDataSource() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/data_source.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("dataSource",
                    GuiFxmlAndLanguageUtils.getString(ResourceMassages.DATA_SOURCE), root, getResource("css/platform_app.css").toExternalForm());
            stage.toFront();
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
