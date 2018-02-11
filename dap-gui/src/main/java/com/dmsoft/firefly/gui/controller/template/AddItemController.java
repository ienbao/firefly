/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Created by Guang.Li on 2018/2/11.
 */
public class AddItemController {
    @FXML
    private Button addItemOk;

    @FXML
    private void initialize() {
        addItemOk.setOnAction(event -> {
            StageMap.closeStage("addItem");
        });
    }
}
