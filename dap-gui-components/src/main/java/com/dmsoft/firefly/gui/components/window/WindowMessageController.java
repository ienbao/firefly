package com.dmsoft.firefly.gui.components.window;

import com.dmsoft.firefly.gui.components.utils.CommonResourceMassages;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.sdk.dai.service.LanguageService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Julia on 2018/03/09.
 */
@Component
public class WindowMessageController implements Initializable {
    private Button okBtn, cancelBtn;
    @FXML
    private GridPane btnPane;
    @FXML
    private Label iconLbl;
    @FXML
    private Label smLbl;
    private WindowCustomListener windowCustomListener;

    @Autowired
    private LanguageService languageService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        iconLbl.getStyleClass().addAll("icon_error_svg");
        okBtn = new Button();
        okBtn.setFocusTraversable(false);
        okBtn.setPrefWidth(80);
        cancelBtn = new Button();
        cancelBtn.setPrefWidth(80);
        cancelBtn.setFocusTraversable(false);
    }

    public void updateMsgLbl(String msg) {
        smLbl.setText(msg);
    }

    public void updateCancelBtn(String msg) {
        cancelBtn.setText(msg);
    }

    public WindowMessageController showOk() {
        initOKBtn();
        btnPane.getChildren().clear();
        btnPane.addColumn(2, okBtn);
        return this;
    }

    public WindowMessageController showCancel() {
        initCancelBtn();
        btnPane.getChildren().clear();
        btnPane.addColumn(2, cancelBtn);
        return this;
    }

    public WindowMessageController showOKAndCancel() {
        initOKBtn();
        initCancelBtn();
        btnPane.getChildren().clear();
        btnPane.addColumn(0, okBtn);
        btnPane.getColumnConstraints().get(2).setMinWidth(80);
        btnPane.getColumnConstraints().get(2).setMaxWidth(80);
        btnPane.getColumnConstraints().get(2).setPrefWidth(80);
        btnPane.addColumn(2, cancelBtn);
        return this;
    }

    public void closeDialog() {
        smLbl.setText("");
        StageMap.closeStage(CommonResourceMassages.COMPONENT_STAGE_WINDOW_MESSAGE);
        if (windowCustomListener != null) {
            windowCustomListener.onCloseAndCancelCustomEvent();
        }
    }

    public void addProcessMonitorListener(WindowCustomListener windowCustomListener) {
        this.windowCustomListener = windowCustomListener;
    }

    public void onShowingRequest() {
        if (windowCustomListener != null) {
             windowCustomListener.onShowCustomEvent();
        }
    }

    private void initOKBtn() {
        okBtn.setText(languageService.getResourceBundle().getString("GLOBAL_BTN_OK"));
        okBtn.setOnAction(event -> {
            okEvent();
        });
    }

    private void okEvent() {
        smLbl.setText("");
        StageMap.closeStage(CommonResourceMassages.COMPONENT_STAGE_WINDOW_MESSAGE);
        if (windowCustomListener != null) {
           windowCustomListener.onOkCustomEvent();
        }
    }

    private void initCancelBtn() {
        cancelBtn.setText(languageService.getResourceBundle().getString("GLOBAL_BTN_CANCEL"));
        cancelBtn.setOnAction(event -> {
            closeDialog();
        });
    }


}
