/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.utils;


import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.spc.controller.SpcExportSettingController;
import com.google.common.collect.Maps;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Map;

/**
 * Created by Ethan.Yang on 2018/4/27.
 */
public class SpcStageFactory {
    private static SpcStageFactory instance = null;

    private Map<String, Initializable> controllerMap = Maps.newHashMap();

    /**
     * Get SpcStageFactory instance
     *
     * @return SpcStageFactory instance
     */
    public static SpcStageFactory newInstance() {
        if (null == instance) {
            instance = new SpcStageFactory();
        }
        return instance;
    }

    /**
     * create spc export setting dialog
     *
     * @return controller
     */
    public SpcExportSettingController createSpcExportSettingDialog() {
        SpcExportSettingController spcExportSettingController = null;
        try {
            FXMLLoader fxmlLoader = SpcFxmlAndLanguageUtils.getLoaderFXML("view/spc_export_setting.fxml");
            Pane root = fxmlLoader.load();
            spcExportSettingController = fxmlLoader.getController();
            controllerMap.put(StateKey.SPC_EXPORT_TEMPLATE_SETTING, spcExportSettingController);
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel(StateKey.SPC_EXPORT_TEMPLATE_SETTING, SpcFxmlAndLanguageUtils.getString(ResourceMassages.EXPORT_SETTING_TITLE), root, getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
            stage.setResizable(false);
            stage.toFront();
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return spcExportSettingController;
    }

    /**
     * get controller
     *
     * @param key key
     * @return controller
     */
    public Initializable getController(String key) {
        return controllerMap.get(key);
    }
}
