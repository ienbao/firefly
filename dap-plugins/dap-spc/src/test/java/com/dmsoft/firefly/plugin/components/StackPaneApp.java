package com.dmsoft.firefly.plugin.components;

import com.google.common.collect.Lists;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

/**
 * Created by cherry on 2018/4/14.
 */
public class StackPaneApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        VBox vBox = new VBox();
        HBox hBox = new HBox();
        Button addBtn = new Button("Add");
        Button clearBtn = new Button("Clear");
        hBox.getChildren().addAll(addBtn, clearBtn);

        LineChart lineChart = new LineChart(new NumberAxis(), new NumberAxis());
        XYChart.Series<Double, Double> series = new XYChart.Series<>();
        series.getData().addAll(
                new XYChart.Data<>(1D, 3D),
                new XYChart.Data<>(2D, 5D),
                new XYChart.Data<>(3D, 5D),
                new XYChart.Data<>(4D, 7D));
        lineChart.getData().add(series);

        List<XYChart.Data> dataList = Lists.newArrayList();

        series.getData().forEach(dataItem -> {
            dataItem.getNode().setOnMouseClicked(event -> {
                Text text = new Text("part1");
                text.setStyle("-fx-fill: red");
                StackPane pane = (StackPane) dataItem.getNode();
                pane.setAlignment(Pos.BOTTOM_CENTER);
                pane.getStyleClass().add("chart-symbol-triangle");
                pane.getChildren().add(text);
                dataList.add(dataItem);
            });
            dataItem.getNode().getStyleClass().add("chart-line-symbol");
            dataItem.getNode().setStyle("-fx-stroke: #4B910E; -fx-background-color: #4B910E");
        });

        clearBtn.setOnAction(event -> {
            series.getData().forEach(dataItem -> {
                StackPane stackPane = (StackPane) dataItem.getNode();
                if (dataList.contains(dataItem)) {
                    if (dataItem.getNode().getStyleClass().contains("chart-line-symbol")) {
                        dataItem.getNode().getStyleClass().removeAll("chart-line-symbol");
                    }
                    for (int i = 0; i < stackPane.getChildren().size(); i++) {
                        System.out.println(stackPane.getStyleClass());
//                        if (stackPane.getChildren().get(i) instanceof Text) {
//                            stackPane.getChildren().removeAll(stackPane.getChildren().get(i));
//                        }
                        //三角
                        if (stackPane.getStyleClass().contains("chart-symbol-triangle")) {
                            stackPane.getStyleClass().removeAll("chart-symbol-triangle");
                        }
                        //圆形
                        if (!stackPane.getStyleClass().contains("chart-line-symbol")) {
                            stackPane.getStyleClass().add( "chart-line-symbol");
                        }
                    }
                }
            });
        });

        vBox.getChildren().addAll(lineChart, hBox);
        vBox.setMargin(lineChart, new Insets(10, 0 ,10, 10));

        HBox rightHBox = new HBox();
        rightHBox.getChildren().add(new Button("right"));
        rightHBox.setStyle("-fx-background-color: red");

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().add(vBox);
        splitPane.getItems().add(rightHBox);

        Scene scene = new Scene(splitPane, 615, 315);
        primaryStage.setScene(scene);
//        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/charts.css").toExternalForm());
        primaryStage.show();
    }
}
