package com.dmsoft.firefly.plugin.components;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.stage.Stage;

/**
 * Created by cherry on 2018/2/8.
 */
public class PopupExample extends Application {

    Button show = new Button("Show");
    Button hide = new Button("Hide");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        final Popup popup = createPopup();
        final HBox layout = new HBox(10);
        layout.getChildren().addAll(show, hide);
        primaryStage.setTitle("Popup Example");
        primaryStage.setScene(new Scene(layout));
        primaryStage.show();

        show.setOnAction(event -> {
            double x = show.getScene().getWindow().getX();
            double y = show.getScene().getWindow().getY();
            popup.show(layout, x, y);
        });
        hide.setOnAction(event -> {
            popup.hide();
        });
    }

    private Popup createPopup() {
        final Popup popup = new Popup();
        popup.setAutoHide(true);
        popup.setX(300);
        popup.setY(200);
        popup.getContent().addAll(new Circle(25, 25, 50, Color.AQUAMARINE));
        return popup;
    }
}
