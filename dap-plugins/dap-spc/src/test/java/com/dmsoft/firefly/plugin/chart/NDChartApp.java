package com.dmsoft.firefly.plugin.chart;

import com.dmsoft.firefly.plugin.spc.charts.NDChart;
import com.dmsoft.firefly.plugin.spc.charts.data.XYChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.BarCategoryData;
import com.dmsoft.firefly.plugin.spc.dto.chart.BarChartData;
import com.google.common.collect.Lists;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.List;
import java.util.Random;

/**
 * Created by cherry on 2018/3/5.
 */
public class NDChartApp extends Application {

    private NDChart<Double, Double> chart;
    String seriesName = "A1:All";

    private void initData() {

        List<BarCategoryData<Double, Double>> barCategoryData = Lists.newArrayList();
        List<BarCategoryData<Double, Double>> _barCategoryData = Lists.newArrayList();
        Double[] x = new Double[]{1D, 4D, 7D, 10D, 13D, 16D, 19D, 22D, 25D, 28D};
        Double[] y = new Double[10];
        Double[] y1 = new Double[10];
        Double[] barWidth = new Double[10];

        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            BarCategoryData<Double, Double> barCategoryData1 = new BarCategoryData();
            BarCategoryData<Double, Double> barCategoryData2 = new BarCategoryData();
            double startValue = x[i];
            double endValue = startValue + 2;
            double value = random.nextInt(10);
            y[i] = value;
            y1[i] = Double.valueOf(random.nextInt(10));
            barWidth[i] = (startValue + endValue) / 2;
            System.out.println("start value: " + startValue);
            System.out.println("value: " + y[i]);

            barCategoryData1.setStartValue(startValue);
            barCategoryData1.setEndValue(endValue);
            barCategoryData1.setBarWidth(endValue - startValue);
            barCategoryData1.setValue(y[i]);
            barCategoryData.add(barCategoryData1);

            barCategoryData2.setStartValue(startValue);
            barCategoryData2.setEndValue(endValue);
            barCategoryData2.setBarWidth(endValue - startValue);
            barCategoryData2.setValue(y1[i]);
            _barCategoryData.add(barCategoryData2);
        }

        Color barColor = Color.RED;

        BarChartData<Double, Double> barChartData = new BarChartData<>(seriesName);
        barChartData.setBarData(barCategoryData);
        barChartData.setColor(barColor);
        XYChartData<Double, Double> xyChartData = new XYChartData();

        Double[] barX = new Double[] {};
        Double[] barY = new Double[] {};

        List<BarCategoryData<Double, Double>> ndChartData = Lists.newArrayList();
        for (int i = 0; i < barX.length; i++) {
            BarCategoryData data = new BarCategoryData();
            ndChartData.add(data);
        }

        Double[] seriesX = new Double[] {
                -5.213991587992461,-4.997937108877358,-4.781882629762255,-4.565828150647152,-4.349773671532049,-4.133719192416947,-3.917664713301844,-3.7016102341867416,-3.4855557550716387,-3.269501275956536,-3.053446796841433,-2.83739231772633,-2.621337838611227,-2.4052833594961243,-2.189228880381022,-1.973174401265919,-1.757119922150816,-1.5410654430357131,-1.3250109639206102,-1.1089564848055078,-0.8929020056904049,-0.676847526575302,-0.4607930474601991,-0.24473856834509622,-0.028684089229993326,0.18737038988510957,0.40342486900021246,0.6194793481153145,0.8355338272304174,1.0515883063455203,1.2676427854606231,1.483697264575726,1.699751743690829,1.9158062228059318,2.1318607019210347,2.3479151810361376,2.5639696601512405,2.7800241392663434,2.9960786183814454,3.2121330974965483,3.428187576611651,3.644242055726754,3.860296534841857,4.07635101395696,4.292405493072063,4.508459972187166,4.7245144513022685,4.940568930417371,5.156623409532474,5.372677888647577,5.58873236776268,5.804786846877783,6.020841325992886,6.236895805107989,6.45295028422309,6.669004763338193,6.885059242453296,7.101113721568399,7.3171682006835015,7.533222679798604,7.749277158913707,7.96533163802881,8.181386117143912,8.397440596259017,8.613495075374118,8.829549554489223,9.045604033604324,9.261658512719428,9.47771299183453,9.693767470949634,9.909821950064735,10.12587642917984,10.341930908294941,10.557985387410046,10.774039866525147,10.990094345640252,11.206148824755353,11.422203303870457,11.638257782985558,11.854312262100663,12.070366741215764,12.286421220330869,12.50247569944597,12.718530178561075,12.934584657676176,13.15063913679128,13.366693615906382,13.582748095021483,13.798802574136587,14.014857053251689,14.230911532366793,14.446966011481894,14.663020490596999,14.8790749697121,15.095129448827205,15.311183927942306,15.52723840705741,15.743292886172512,15.959347365287616,16.175401844402717
        };
        Double[] seriesY = new Double[] {
                5.005479980079981E-5,6.892960397428679E-5,9.430395750102361E-5,1.2817937041245626E-4,1.7308938317392146E-4,2.322131484948367E-4,3.0950471121958333E-4,4.0983760209018936E-4,5.391634208210151E-4,7.046819036206154E-4,9.150185491891913E-4,0.00118040423217916,0.0015128494031103632,0.0019263035237120927,0.0024367884102408847,0.0030624922778356104,0.0038238096481881395,0.00474331107677032,0.00584562606742624,0.007157222770053379,0.00870606929164444,0.010521163856726884,0.012631924749813225,0.01506743601445354,0.017855551241410826,0.02102186532276993,0.024588572538719197,0.028573238423624425,0.03298752206235792,0.037835894236780666,0.043114404547756245,0.048809556620414016,0.054897354113595656,0.06134258091773399,0.06809837617722045,0.07510615832318417,0.08229594207631594,0.08958707855212064,0.0968894316142151,0.10410498417973847,0.11112984722791265,0.11785662293352236,0.1241770529021795,0.12998486422708483,0.1351787112711111,0.13966510081147768,0.1433611833393868,0.14619729444330695,0.14811913751264758,0.14908951227016579,0.14908951227016576,0.14811913751264758,0.14619729444330695,0.1433611833393868,0.13966510081147768,0.1351787112711111,0.12998486422708483,0.1241770529021795,0.11785662293352236,0.11112984722791265,0.10410498417973847,0.0968894316142151,0.08958707855212064,0.08229594207631588,0.07510615832318417,0.0680983761772204,0.06134258091773399,0.0548973541135956,0.048809556620414016,0.043114404547756204,0.037835894236780666,0.03298752206235788,0.028573238423624425,0.024588572538719156,0.02102186532276992,0.017855551241410795,0.01506743601445354,0.012631924749813202,0.010521163856726884,0.00870606929164441,0.007157222770053373,0.005845626067426227,0.004743311076770314,0.003823809648188131,0.0030624922778356065,0.0024367884102408787,0.0019263035237120914,0.0015128494031103632,0.0011804042321791545,9.150185491891913E-4,7.046819036206122E-4,5.391634208210151E-4,4.098376020901872E-4,3.095047112195828E-4,2.3221314849483548E-4,1.7308938317392084E-4,1.2817937041245556E-4,9.430395750102326E-5,6.892960397428643E-5,5.005479980079963E-5
        };
        xyChartData.setX(seriesX);
        xyChartData.setY(seriesY);
        xyChartData.setColor(barColor);
        chart.createChartSeries(barChartData, seriesName);
        chart.addAreaSeries(xyChartData, seriesName, barColor);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        chart = new NDChart(xAxis, yAxis);

        initData();

//        barChartData.setSeriesName("aaaaa");
//        barChartData.setBarData(_barCategoryData);
//        barChartData.setColor(Color.GREEN);

//        chart.createChartSeries(barChartData);
//        xyChartData.setColor(Color.GREEN);
//        xyChartData.setY(y1);
//        chart.addAreaSeries(xyChartData);

        VBox vBox = new VBox();
        HBox hBox = new HBox();
        Button clearBtn = new Button("Clear");
        Button addBtn = new Button("Add Data");
        Button changeColorBtn = new Button("ChangeColor");
        hBox.getChildren().add(clearBtn);
        hBox.getChildren().add(addBtn);
        hBox.getChildren().add(changeColorBtn);

        vBox.getChildren().add(chart);
        vBox.getChildren().add(hBox);
//        clearBtn.setOnAction(event -> {
//            chart.removeAllChildren();
//        });
//        addBtn.setOnAction(event -> initData());

        changeColorBtn.setOnAction(event -> chart.updateChartColor(seriesName, Color.RED));

        Scene scene = new Scene(vBox, 600, 400);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/charts.css").toExternalForm());
        primaryStage.setTitle("XY Chart panel example.");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
