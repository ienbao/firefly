package com.dmsoft.firefly.gui.components.messagetip;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MessageTipTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tabs");
        Group root = new Group();
        Scene scene = new Scene(root, 1000, 250, Color.WHITE);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());


        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        BorderPane borderPane = new BorderPane();
        for (int i = 0; i < 8; i++) {
            Tab tab = new Tab();
            tab.setText("hhjkhjhdsfsf" + i);

            HBox hbox = new HBox();
            hbox.getChildren().add(new Label("Tfdsfsfsfsab" + i));
            hbox.setAlignment(Pos.CENTER);
            tab.setContent(hbox);

            tab.setClosable(true);

            tabPane.getTabs().add(tab);
        }
        tabPane.setSide(Side.TOP);//tabPane.setSide(Side.TOP);
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());

        root.getChildren().add(tabPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
