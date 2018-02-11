package com.dmsoft.firefly.gui.controller;

import com.dmsoft.firefly.gui.component.ContentStackPane;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public class MainController {

    @FXML
    private GridPane grpContent;

    @FXML
    private ToolBar tbaSystem;

    private ContentStackPane contentStackPane;
    private TabPane tabPane;


    @FXML
    private void initialize() {
        tabPane = new TabPane();
        contentStackPane = new ContentStackPane();
        grpContent.add(contentStackPane, 0, 1);
        initToolBar();
        System.out.println("init");

    }

    private void initToolBar() {
        PluginUIContext pc = RuntimeContext.getBean(PluginUIContext.class);
        Set<String> names = pc.getAllMainBodyNames();

        names.forEach(name -> {
            Pane pane = pc.getMainBodyPane(name).getNewPane();
            Button btn = new Button(name);
            pane.setId(name);
            if (StringUtils.isBlank(tabPane.getId())) {
                tabPane = new TabPane();
                tabPane.setId(name);
            } else if (StringUtils.isNotBlank(tabPane.getId()) && !name.equals(tabPane.getId())) {
                tabPane = new TabPane();
                tabPane.setId(name);
            }
            System.out.println(tabPane.getId() +"---------fdsdf");
            initTab(name, pane, tabPane);
            contentStackPane.add(tabPane);
            btn.setOnAction(event -> {
                System.out.println(contentStackPane);
                contentStackPane.navTo(name);
                System.out.println(name);

            });
            tbaSystem.getItems().add(btn);
        });
    }

    private void initTab(String name, Pane pane, TabPane tabPane) {
        if (name.equals(tabPane.getId())) {
            Tab tab = new Tab();
            tab.setText(name + 1);
            tab.setContent(pane);
            tabPane.getTabs().add(tab);
        }
    }


}
