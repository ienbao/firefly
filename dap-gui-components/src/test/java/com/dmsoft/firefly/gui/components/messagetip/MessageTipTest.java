package com.dmsoft.firefly.gui.components.messagetip;
import javafx.application.Application;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicBoolean;

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
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setMaxHeight(250);
        scrollPane.setMinViewportHeight(10);
        scrollPane.setMinViewportWidth(160);
        scrollPane.setMaxWidth(250);
        Label label = new Label();
        //label.setText("2hjkffsdfksjfkldsjfkljslkdfjsldfjskdlfjkskfkflsjdkfldsjlfjslkfdd\nfdsjfklsjfklsjfdsfkjsdlkf");
        label.setText("2hjkhhkjhjhkjhjkhj0/29\nfdsfdsfsfdsfdsfsdfdsffdsfsffsdfsffs2hjkhhkjhjhkjhjkhj0/29\nfdsfdsfsfsdfsffs2hjkhhkjhjhkjhjkhj0/29\nfdsfdsfsfsdfsffs2hjkhhkjhjhkjhjkhj0/29\nfdsfdsfsfsdfsffs2hjkhhkjhjhkjhjkhj0/29\nfdsfdsfsfdsfdsfsdfdsffdsfsffsdfsffs2hjkhhkjhjhkjhjkhj0/29\nfdsfdsfsfsdfsffs2hjkhhkjhjhkjhjkhj0/29\nfdsfdsfsfsdfsffs2hjkhhkjhjhkjhjkhj0/29\nfdsfdsfsfsdfsffs" +
                "\nfdsfdsfsfsdfsffs2hjkhhkjhjhkjhjkhj0/29\nfdsfdsfsfsdfsffs\nfdsfdsfsfsdfsffs2hjkhhkjhjhkjhjkhj0/29\nfdsfdsfsfsdfsffs\nfdsfdsfsfsdfsffs2hjkhhkjhjhkjhjkhj0/29\nfdsfdsfsfsdfsffs"+
                "\nfdsfdsfsfsdfsffs2hjkhhkjhjhkjhjkhj0/29\nfdsfdsfsfsdfsffs\nfdsfdsfsfsdfsffs2hjkhhkjhjhkjhjkhj0/29\nfdsfdsfsfsdfsffs\nfdsfdsfsfsdfsffs2hjkhhkjhjhkjhjkhj0/29\nfdsfdsfsfsdfsffs");
        label.setContentDisplay(ContentDisplay.CENTER);
        scrollPane.setContent(label);
        AtomicBoolean isHover = new AtomicBoolean(false);

        Tooltip tooltip = new Tooltip(){
            @Override
            protected void show() {
                super.show();
            }
            @Override
            public void hide() {
                System.out.println(isHover.get());
                if (!isHover.get()) {
                    super.hide();
                }
            }

        };

        scrollPane.setOnMouseEntered(event -> {
            isHover.set(true);
        });
        scrollPane.setOnMouseExited(event -> {
            isHover.set(false);
            tooltip.hide();
        });
        tooltip.setMaxHeight(250);
        tooltip.setMaxWidth(250);
        tooltip.setGraphic(scrollPane);
        tooltip.setStyle("-fx-padding: 0");
        tooltip.setConsumeAutoHidingEvents(true);
        tooltip.setAutoHide(true);
        for (int i = 0; i < 1; i++) {
            Tab tab = new Tab();
            tab.setText("hhjkhjhdsfsf" + i);
            Label label2 = new Label();
            label2.setText("fdsfsf9999999sdfsdfsf");
            Tooltip.install(label2, tooltip);
            tab.setContent(label2);
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
