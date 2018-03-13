/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Created by Alice on 2018/2/10.
 */
public class DataSourceSettingController {
    @FXML
    private Button chooseItem, newTemplate, clearAll, help, ok, oK, cancel, apply;
    @FXML
    private Tab basicTab, advanceTab;

    @FXML
    private void initialize() {
        initButton();
    }

    private void initButton() {
        chooseItem.setGraphic( ImageUtils.getImageView( getClass().getResourceAsStream( "/images/btn_choose_test_items_normal.png" ) ) );
        basicTab.setGraphic( ImageUtils.getImageView( getClass().getResourceAsStream( "/images/btn_basic_search_normal.png" ) ) );
        advanceTab.setGraphic( ImageUtils.getImageView( getClass().getResourceAsStream( "/images/btn_advance_search_normal.png" ) ) );
        newTemplate.setGraphic( ImageUtils.getImageView( getClass().getResourceAsStream( "/images/btn_new_template_normal.png" ) ) );
        clearAll.setGraphic( ImageUtils.getImageView( getClass().getResourceAsStream( "/images/btn_clear_all_normal.png" ) ) );
        help.setGraphic( ImageUtils.getImageView( getClass().getResourceAsStream( "/images/btn_help.svg" ) ) );
        ok.setGraphic( ImageUtils.getImageView( getClass().getResourceAsStream( "/images/btn_ok.svg" ) ) );
    }


}
