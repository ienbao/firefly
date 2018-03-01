package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


/**
 * Created by GuangLi on 2018/2/28.
 */
public class NewNameController {
    @FXML
    private Button ok, cancel;
    @FXML
    private TextField name;
    private String paneName = "";
    @FXML
    private void initialize(){
        initEvent();
    }

    private void initEvent(){
        cancel.setOnAction(event -> {
            name.setText("");
            StageMap.closeStage(paneName);
        });
    }

    public void setPaneName(String paneName){
        this.paneName = paneName;
    }
    public Button getOk() {
        return ok;
    }

    public TextField getName() {
        return name;
    }
}
