package com.dmsoft.firefly.gui.controller;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.utils.GuiConst;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;


/**
 * controller for change password
 *
 * @author Julia
 */
@Component
public class ChangePasswordBackController {

    @FXML
    private Label iconLbl;

    @FXML
    private Hyperlink backHlk;

    @FXML
    private Button btnOK;

    @FXML
    private void initialize() {
        iconLbl.getStyleClass().add("icon-success-svg");
        iconLbl.setPrefWidth(26);
        iconLbl.setMinWidth(26);
        iconLbl.setMaxWidth(26);
        initEvent();
    }

    private void initEvent() {
        btnOK.setOnAction(event -> {
            StageMap.getStage(GuiConst.PLARTFORM_STAGE_CHANGE_PASSWORD_BACK).close();
            StageMap.getStage(GuiConst.PLARTFORM_STAGE_CHANGE_PASSWORD).close();
        });

        backHlk.setOnMouseClicked(event -> {
            GuiFxmlAndLanguageUtils.buildChangePasswordDialog();
            StageMap.getStage(GuiConst.PLARTFORM_STAGE_CHANGE_PASSWORD_BACK).close();

        });
    }
}
