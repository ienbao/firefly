package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;


/**
 * Created by GuangLi on 2018/2/28.
 */
public class RenameTemplateController {
    @FXML
    private Button ok, cancel;
    @FXML
    private TextField name;
    private ObservableList<String> names;
    private String oldName = "";

    @FXML
    private void initialize() {
        initEvent();
    }

    private void initEvent() {
        ok.setOnAction(event -> {
            if (StringUtils.isNotEmpty(name.getText()) && !name.getText().equals(oldName)) {
                for (int i = 0; i < names.size(); i++) {
                    if (names.get(i).equals(oldName)) {
                        names.set(i, name.getText());
                    }
                }
            }
            StageMap.closeStage("renameTemplate");
        });
        cancel.setOnAction(event -> {
            StageMap.closeStage("renameTemplate");
        });
    }

    public void setName(String oldName) {
        this.oldName = oldName;
        name.setText(oldName);
    }

    public void setNameList(ObservableList<String> names){
        this.names = names;

    }
}
