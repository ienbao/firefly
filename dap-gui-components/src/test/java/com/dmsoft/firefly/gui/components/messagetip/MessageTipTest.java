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
            hbox.setAlignment(Pos.CENTER);
            CheckBox checkBox = new CheckBox();
            checkBox.getStyleClass().add("error");
            Label label1 = new Label();
            label1.getStyleClass().add("message-tip-warn-mark");
            label1.setStyle("-fx-padding: 0 26 0 0;");

            Label label = new Label();
            label.setGraphic(label1);
            label.setVisible(true);
            label.setText("20/29");
            label.setContentDisplay(ContentDisplay.LEFT);

            hbox.getChildren().addAll(label);
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
