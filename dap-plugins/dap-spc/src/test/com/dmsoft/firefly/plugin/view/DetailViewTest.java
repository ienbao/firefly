package com.dmsoft.firefly.plugin.view;

import com.dmsoft.firefly.gui.components.window.WindowPane;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ResourceBundle;

public class DetailViewTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane root = FXMLLoader.load(getClass().getClassLoader().getResource("view/spc_detail.fxml"), ResourceBundle.getBundle("i18n.message_en_US_SPC"));
        HBox hBox = new HBox();
        hBox.getChildren().add(new Label("Detail"));
        WindowPane windowPane = new WindowPane(primaryStage, hBox, root);

        Scene scene = new Scene(windowPane, 430, 380);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setResizable(false);
        windowPane.init();
        primaryStage.toFront();
        primaryStage.show();
    }
}
