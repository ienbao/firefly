package com.dmsoft.firefly.gui.controller;

import com.dmsoft.firefly.gui.GuiApplication;
import com.dmsoft.firefly.gui.component.ContentStackPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class MainController {

    @FXML
    private GridPane grpContent;

    @FXML
    private ToolBar tbaSystem;

    private ContentStackPane contentStackPane;

    @FXML
    private void initialize() {
        contentStackPane = new ContentStackPane();
        grpContent.add(contentStackPane, 0, 1);
        initToolBar();
    }

    private void initToolBar() {
        Button btnSpc = new Button("SPC");
        btnSpc.getStyleClass().add("btn-txt");

        btnSpc.setOnAction(event -> {
            try {
                Pane spc = FXMLLoader.load(MainController.class.getClassLoader().getResource("view/spc.fxml"));
                spc.setId("spc");
                contentStackPane.add(spc);
                contentStackPane.navTo("spc");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button btnGrr = new Button("GRR");
        btnGrr.getStyleClass().add("btn-txt");
        btnGrr.setOnAction(event -> {
            try {
                Pane grr = FXMLLoader.load(MainController.class.getClassLoader().getResource("view/grr.fxml"));
                grr.setId("grr");
                contentStackPane.add(grr);
                contentStackPane.navTo("grr");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        tbaSystem.getItems().add(btnSpc);
        tbaSystem.getItems().add(btnGrr);
    }


}
