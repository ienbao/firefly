package com.dmsoft.firefly.gui.controller;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.utils.GuiConst;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;


public class LegalNoticeController {
    private UserService userService = RuntimeContext.getBean(UserService.class);
    @FXML
    private CheckBox acceptCkb;

    @FXML
    private Button legalOkBtn;

    @FXML
    private void initialize() {
        legalOkBtn.setDisable(true);
        acceptCkb.setOnAction(event -> {
            if (acceptCkb.isSelected()) {
                legalOkBtn.setDisable(false);
            } else {
                legalOkBtn.setDisable(true);
            }
        });
        legalOkBtn.setOnAction(event -> {
            userService.updateLegal(acceptCkb.isSelected());
            StageMap.getStage(GuiConst.PLARTFORM_STAGE_LEGAL).close();
            StageMap.showStage(GuiConst.PLARTFORM_STAGE_MAIN);
            GuiFxmlAndLanguageUtils.buildLoginDialog();
        });
    }

}
