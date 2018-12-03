package com.dmsoft.firefly.gui.controller;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.WindowPane;
import com.dmsoft.firefly.gui.utils.GuiConst;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * controller of legal notice
 *
 * @author Julia
 */
@Component
public class LegalNoticeController {

    @FXML
    private CheckBox acceptCkb;

    @FXML
    private Button legalOkBtn;

    @FXML
    private TextArea legalNoticeTextArea;

    @Autowired
    private UserService userService;


    @FXML
    private void initialize() {
        legalNoticeTextArea.setText(GuiFxmlAndLanguageUtils.getString("LEGAL_NOTICE_CONTENT"));
        legalNoticeTextArea.setWrapText(true);
        legalNoticeTextArea.setMouseTransparent(false);
        legalNoticeTextArea.setFocusTraversable(false);
        acceptCkb.setFocusTraversable(false);
        legalOkBtn.setFocusTraversable(false);
        legalOkBtn.setDisable(true);

        if (userService.findLegal()) {
            acceptCkb.setVisible(true);
            acceptCkb.setSelected(true);
            legalOkBtn.setDisable(false);
        }

        acceptCkb.setOnAction(event -> {
            if (acceptCkb.isSelected()) {
                legalOkBtn.setDisable(false);
            } else {
                legalOkBtn.setDisable(true);
            }
        });
        legalOkBtn.setOnAction(event -> {
            if (!userService.findLegal()) {
                userService.updateLegal(acceptCkb.isSelected());
                StageMap.getStage(GuiConst.PLARTFORM_STAGE_LEGAL).close();
                Stage stage = StageMap.getPrimaryStage(GuiConst.PLARTFORM_STAGE_MAIN);
                stage.show();
                if (stage.getScene().getRoot() instanceof WindowPane) {
                    WindowPane windowPane = (WindowPane) stage.getScene().getRoot();
                    windowPane.getController().maximizePropertyProperty().set(true);
                }
                GuiFxmlAndLanguageUtils.buildLoginDialog();
            } else {
                StageMap.getStage(GuiConst.PLARTFORM_STAGE_LEGAL).close();
            }

        });
    }

}
