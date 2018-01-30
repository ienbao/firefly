package com.dmsoft.firefly.gui.controller;

import com.dmsoft.firefly.gui.component.ContentStackPane;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.Set;

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

        PluginUIContext pc = RuntimeContext.getBean(PluginUIContext.class);
        Set<String> names = pc.getAllMainBodyNames();

        names.forEach(name -> {
            Pane pane = pc.getMainBodyPane(name).getNewPane();
            Button btn = new Button(name);
            btn.getStyleClass().add("btn-txt");
            btn.setOnAction(event -> {
                pane.setId("spc");
                contentStackPane.add(pane);
                contentStackPane.navTo(name);
                System.out.println(name);
            });
            tbaSystem.getItems().add(btn);
        });
    }


}
