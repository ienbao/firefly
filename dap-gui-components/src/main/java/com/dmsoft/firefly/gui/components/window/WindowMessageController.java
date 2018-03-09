package com.dmsoft.firefly.gui.components.window;

import com.dmsoft.firefly.gui.components.utils.FxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.components.utils.ResourceMassages;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * Created by Julia on 2018/03/09.
 */
public class WindowMessageController {
    private Button okBtn, cancelBtn;
    @FXML
    private GridPane btnPane;
    @FXML
    private Label iconLbl;
    @FXML
    private Label smLbl;
    private WindowCustomListener windowCustomListener;

    @FXML
    private void initialize(){
        iconLbl.getStyleClass().addAll("icon_error_svg");
        okBtn = new Button();
        okBtn.setFocusTraversable(false);
        okBtn.setPrefWidth(80);
        cancelBtn = new Button();
        cancelBtn.setPrefWidth(80);
        cancelBtn.setFocusTraversable(false);
    }

    public void updateSmLbl(String msg) {
        smLbl.setText(msg);
    }

    private void initOKBtn() {
        okBtn.setText(FxmlAndLanguageUtils.getString("GLOBAL_BTN_OK"));
        okBtn.setOnAction(event -> {
            okEvent();
        });
    }

    private void okEvent() {
        if (windowCustomListener != null) {
            windowCustomListener.onOkCustomEvent();
        }
        smLbl.setText("");
        StageMap.closeStage(ResourceMassages.COMPONENT_STAGE_WINDOW_MESSAGE);
    }

    private void initCancelBtn() {
        cancelBtn.setText(FxmlAndLanguageUtils.getString("GLOBAL_BTN_CANCEL"));
        cancelBtn.setOnAction(event -> {
            closeDialog();
        });
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
        if (windowCustomListener != null) {
            windowCustomListener.onCloseAndCancelCustomEvent();
        }
        smLbl.setText("");
        StageMap.closeStage(ResourceMassages.COMPONENT_STAGE_WINDOW_MESSAGE);
    }

    public void addProcessMonitorListener(WindowCustomListener windowCustomListener) {
        this.windowCustomListener = windowCustomListener;
    }
}
