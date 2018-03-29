package com.dmsoft.firefly.plugin.components;

import com.dmsoft.firefly.plugin.spc.charts.ControlChart;
import com.dmsoft.firefly.plugin.spc.charts.ControlChart;
import com.dmsoft.firefly.plugin.spc.charts.NDChart;
import com.dmsoft.firefly.plugin.spc.charts.view.ChartPanel;
import com.dmsoft.firefly.plugin.spc.charts.view.VerticalTabPane;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by cherry on 2018/3/6.
 */
public class VerticalTabPaneApp extends Application {

    public Parent createContent() {

        VerticalTabPane tabPane = new VerticalTabPane();
        ChartPanel<ControlChart> chartPanel1 = new ChartPanel(new ControlChart(new NumberAxis(), new NumberAxis()));
        ChartPanel<NDChart> chartPanel2 = new ChartPanel(new ControlChart(new NumberAxis(), new NumberAxis()));
        ChartPanel<ControlChart> chartPanel3 = new ChartPanel(new ControlChart(new NumberAxis(), new NumberAxis()));
        ChartPanel<ControlChart> chartPanel4 = new ChartPanel(new ControlChart(new NumberAxis(), new NumberAxis()));
        ChartPanel<ControlChart> chartPanel5 = new ChartPanel(new ControlChart(new NumberAxis(), new NumberAxis()));
        ChartPanel<ControlChart> chartPanel6 = new ChartPanel(new ControlChart(new NumberAxis(), new NumberAxis()));
        ChartPanel<ControlChart> chartPanel7 = new ChartPanel(new ControlChart(new NumberAxis(), new NumberAxis()));
        ChartPanel<ControlChart> chartPanel8 = new ChartPanel(new ControlChart(new NumberAxis(), new NumberAxis()));
        chartPanel1.setId(UIConstant.SPC_CHART_NAME[0]);
        chartPanel2.setId(UIConstant.SPC_CHART_NAME[1]);
        chartPanel3.setId(UIConstant.SPC_CHART_NAME[2]);
        chartPanel4.setId(UIConstant.SPC_CHART_NAME[3]);
        chartPanel5.setId(UIConstant.SPC_CHART_NAME[4]);
        chartPanel6.setId(UIConstant.SPC_CHART_NAME[5]);
        chartPanel7.setId(UIConstant.SPC_CHART_NAME[6]);
        chartPanel8.setId(UIConstant.SPC_CHART_NAME[7]);
        chartPanel1.setLegend("chart1");
        chartPanel2.setLegend("chart2");
        chartPanel3.setLegend("chart3");
        chartPanel4.setLegend("chart4");
        chartPanel5.setLegend("chart5");
        chartPanel6.setLegend("chart6");
        chartPanel7.setLegend("chart7");
        chartPanel8.setLegend("chart8");
        tabPane.addNode(chartPanel1, 0);
        tabPane.addNode(chartPanel2, 1);
        tabPane.addNode(chartPanel3, 2);
        tabPane.addNode(chartPanel4, 3);
        tabPane.addNode(chartPanel5, 4);
        tabPane.addNode(chartPanel6, 5);
        tabPane.addNode(chartPanel7, 6);
        tabPane.addNode(chartPanel8, 7);
        tabPane.activeTabByIndex(0);
        return tabPane;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox rightPane = new VBox();
        rightPane.setStyle("-fx-background-color: #4B910E");
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);

//        GridPane gridPane = new GridPane();
//        TabPane tabPane = new TabPane();
//        Tab tab = new Tab("Analysis");
//        tabPane.getTabs().add(tab);
//        tab.setContent(gridPane);
//        gridPane.getChildren().add(createContent());

        splitPane.getItems().add(createContent());
        splitPane.getItems().add(rightPane);

        Scene scene = new Scene(splitPane, 800, 250);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/charts.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
