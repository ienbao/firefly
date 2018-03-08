package com.dmsoft.firefly.plugin.chart;

import com.dmsoft.firefly.plugin.spc.charts.view.ChartOperateButton;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.Arrays;

/**
 * Created by cherry on 2018/2/9.
 */
public class ChartOperateButtonApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        ChartOperateButton button = new ChartOperateButton();
        button.setListViewData(Arrays.asList(UIConstant.SPC_CHART_XBAR_EXTERN_MENU));
        button.setListViewSize(140, 120);
        button.setSelectCallBack((name, selected, selectedNames) -> {
            System.out.println(name);
        });

        GridPane gridPane = new GridPane();
        gridPane.addRow(0, button);
        button.setPrefSize(20, 20);
        button.getStyleClass().add("btn-icon-b");
        button.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_lines_normal.png")));

        Scene scene = new Scene(gridPane, 200, 100);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        primaryStage.setTitle("CheckBox Table Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
