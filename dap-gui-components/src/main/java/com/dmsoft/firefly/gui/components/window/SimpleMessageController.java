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
public class SimpleMessageController {
    private Button ok, cancel;
    @FXML
    private GridPane btnPane;
    @FXML
    private Label iconLbl;
    @FXML
    private Label smLbl;
    @FXML
    private void initialize(){
        iconLbl.getStyleClass().addAll("icon_error_svg");
        ok = new Button();
        ok.setPrefWidth(80);
        cancel = new Button();
        cancel.setPrefWidth(80);
    }

    public void updateSmLbl(String msg) {
        smLbl.setText(msg);
    }

    private void initOKBtn() {
        ok.setText(FxmlAndLanguageUtils.getString("GLOBAL_BTN_OK"));
        ok.setOnAction(event -> {
            closeDialog();
        });
    }

    private void initCancelBtn() {
        cancel.setText(FxmlAndLanguageUtils.getString("GLOBAL_BTN_CANCEL"));
        cancel.setOnAction(event -> {
            closeDialog();
        });
    }

    public void showOk() {
        initOKBtn();
        btnPane.getChildren().clear();
        btnPane.addColumn(2,  ok);
    }

    public void showCancel() {
        initCancelBtn();
        btnPane.getChildren().clear();
        btnPane.addColumn(2,  cancel);
    }

    public void showOKAndCancel() {
        initOKBtn();
        initCancelBtn();
        btnPane.getChildren().clear();
        btnPane.addColumn(0, ok);
        btnPane.getColumnConstraints().get(2).setMinWidth(80);
        btnPane.getColumnConstraints().get(2).setMaxWidth(80);
        btnPane.getColumnConstraints().get(2).setPrefWidth(80);
        btnPane.addColumn(2, cancel);
    }

    public void closeDialog() {
        smLbl.setText("");
        StageMap.closeStage(ResourceMassages.COMPONENT_STAGE_SIMPLE_MESSAGE_TEMPLATE);
    }

    public Button getOk() {
        return ok;
    }
}
