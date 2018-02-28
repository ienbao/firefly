package com.dmsoft.firefly.gui.components.searchcombobox;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CalendarChooserExample extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        CalendarChooser calendarChooser = new CalendarChooser();
        Scene scene = new Scene(calendarChooser);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        primaryStage.setWidth(358);
        primaryStage.setHeight(300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
