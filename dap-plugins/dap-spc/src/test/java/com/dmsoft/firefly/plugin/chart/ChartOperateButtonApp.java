package com.dmsoft.firefly.plugin.chart;

import com.dmsoft.firefly.plugin.spc.charts.view.ChartOperateButton;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Arrays;

/**
 * Created by cherry on 2018/2/9.
 */
public class ChartOperateButtonApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        ChartOperateButton button = new ChartOperateButton("test", new String[] {"1", "2"}, 0, true);
        button.setTableRowKeys(Arrays.asList(UIConstant.SPC_CHART_XBAR_EXTERN_MENU));
        Scene scene = new Scene(button, 100, 50);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        primaryStage.setTitle("CheckBox Table Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
