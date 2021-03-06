package com.dmsoft.firefly.plugin.grr;

import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class TestTest extends Application {
    static {
        GrrFxmlAndLanguageUtils.isDebug = true;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        AnchorPane a = new AnchorPane();
        Pane root = null;
        try {

            root = (Pane) GrrFxmlAndLanguageUtils.load("view/grr_setting.fxml");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        a.getChildren().add(root);
        Scene scene = new Scene(a);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/grr_app.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        System.out.println("ASF");
    }
}
