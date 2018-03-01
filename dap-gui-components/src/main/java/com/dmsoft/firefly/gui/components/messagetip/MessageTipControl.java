package com.dmsoft.firefly.gui.components.messagetip;

 import javafx.fxml.FXML;
 import javafx.scene.control.Button;
 import javafx.scene.control.Label;
 import javafx.scene.layout.GridPane;
 import javafx.stage.Popup;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;


/**
 * Created by Ethan.Yang on 2017/3/17.
 */
public class MessageTipControl {
    private static final Logger logger = LoggerFactory.getLogger(MessageTipControl.class);

    @FXML
    private GridPane messageTip;

    @FXML
    private Button closeBtn;

    @FXML
    private Label titleLbl;

    @FXML
    private Label iconLbl;

    @FXML
    private Label contentLbl;

    @FXML
    private void initialize() {
    }


    public GridPane initInfo(Popup popup, String title, String msg){
        clearAll();
        this.closeBtnEvent(popup);
        titleLbl.setText(title);
        contentLbl.setText(msg);
        messageTip.getStyleClass().add("message-tip-info");
        iconLbl.getStyleClass().add("message-tip-info-mark");
        return messageTip;
    }

    public GridPane initWarn(Popup popup, String title, String msg){
        clearAll();
        this.closeBtnEvent(popup);
        titleLbl.setText(title);
        contentLbl.setText(msg);
        messageTip.getStyleClass().add("message-tip-warn");
        iconLbl.getStyleClass().add("message-tip-warn-mark");
        return messageTip;
    }

    public GridPane initNormal(Popup popup, String title, String msg){
        this.clearAll();
        this.closeBtnEvent(popup);
        titleLbl.setText(title);
        contentLbl.setText(msg);
        messageTip.getStyleClass().add("message-tip-normal");
        iconLbl.getStyleClass().add("message-tip-normal-mark");
        return messageTip;
    }

    private void clearAll() {
        titleLbl.setText("");
        contentLbl.setText("");
        messageTip.getStyleClass().removeAll("message-tip-info", "message-tip-warn", "message-tip-normal");
        iconLbl.getStyleClass().removeAll("message-tip-info-mark", "message-tip-warn-mark", "message-tip-normal-mark");
    }

    private void closeBtnEvent(Popup popup) {
        closeBtn.setOnAction(event -> {
            popup.hide();
        });
    }
}
