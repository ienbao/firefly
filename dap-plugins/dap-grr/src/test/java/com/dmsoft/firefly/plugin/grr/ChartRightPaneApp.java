package com.dmsoft.firefly.plugin.grr;

import com.dmsoft.firefly.plugin.grr.charts.ChartRightPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Created by cherry on 2018/3/28.
 */
public class ChartRightPaneApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(new Label("This is legend area"));
        borderPane.setRight(new ChartRightPane(new LineChart(new NumberAxis(), new NumberAxis())));
        Scene scene = new Scene(borderPane);

        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/grr_app.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/grr_chart.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
