package com.dmsoft.firefly.plugin.chart;

import com.dmsoft.firefly.plugin.spc.charts.view.ChartAnnotationButton;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Arrays;

/**
 * Created by cherry on 2018/2/28.
 */
public class ChartAnnotationButtonApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        ChartAnnotationButton button = new ChartAnnotationButton();
        button.setGraphic(ImageUtils.getImageView(getClass()
                .getResourceAsStream("/images/btn_tracing_point_normal.png")));
        button.setData(Arrays.asList("", "item0", "item1", "item2"));
        Scene scene = new Scene(button, 1280, 704);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/charts.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
