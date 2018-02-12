package com.dmsoft.firefly.gui.components.searchcombobox;

import com.dmsoft.firefly.gui.components.window.WindowFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class WindowFactoryExample extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("view/demo.fxml"));
        Stage stage = WindowFactory.createFullWindow("demo", "demo", fxmlLoader.load(), getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        stage.show();
    }


}
