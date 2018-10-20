package com.dmsoft.firefly.plugin.charts;

import com.dmsoft.firefly.plugin.yield.charts.NDChart;
import com.dmsoft.firefly.plugin.yield.charts.data.NDBarChartData;
import com.dmsoft.firefly.plugin.yield.charts.data.basic.IBarChartData;
import com.dmsoft.firefly.plugin.yield.charts.data.basic.IXYChartData;
import com.google.common.collect.Lists;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

public class BuildChartTest extends Application {
    private NDChart ndChart;
    private Parent getContent(){
        VBox vBox = new VBox();
        List<NDBarChartData> ndBarChartData = buildNDNBarChartData();
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        ndChart = new NDChart(xAxis,yAxis);
        ndChart.setData(ndBarChartData,null);
        vBox.getChildren().add(ndChart);


        return vBox;
    }
//    new BarChart.Data(years[0], 1002),
//            new BarChart.Data(years[1], 1665),
//            new BarChart.Data(years[2], 2559)))

    private List<NDBarChartData> buildNDNBarChartData() {
    List<NDBarChartData> data = Lists.newArrayList();
    Double[] barData = new Double[]{1002D,1665D,2559D,445D,558D};
    Double[] xData = new Double[]{1d,3D,5D,7D,9D};

        NDBarChartData ndBarChartData = new NDBarChartData() {
            @Override
            public IBarChartData getBarChartData() {
                return new IBarChartData() {
                    @Override
                    public Object getValueByIndex(int index) {
                        return barData[index];
                    }

                    @Override
                    public Object getStartValueByIndex(int index) {
                        return xData[index];
                    }

                    @Override
                    public Object getBarWidthByIndex(int index) {
                        return 2;
                    }

                    @Override
                    public Object getEndValueByIndex(int index) {
                        return null;
                    }

                    @Override
                    public int getLen() {
                        return barData.length;
                    }
                };
            }

            @Override
            public IXYChartData getXYChartData() {
                return null;
            }

            @Override
            public Color getColor() {
                return null;
            }

            @Override
            public String getUniqueKey() {
                return null;
            }
        };
        data.add(ndBarChartData);
        return data;

    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(getContent(), 415, 315);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
