package com.dmsoft.firefly.plugin.spc.charts.view;

import com.dmsoft.firefly.gui.components.chart.ChartUtils;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Created by cherry on 2018/2/8.
 */
public class ChartPanel<T extends XYChart> extends VBox {

    private T chart;
    private Pane chartPane;
    private GridPane titlePane;

    private ChartUtils chartUtils;
    private boolean chartSizeChangeEnable = true;
    private boolean chartDraggingEnable = true;

    public ChartPanel(T chart) {
        this(chart, true, true);
    }

    public ChartPanel(T chart, boolean chartSizeChangeEnable, boolean chartDraggingEnable) {

        this.chartSizeChangeEnable = chartSizeChangeEnable;
        this.chartDraggingEnable = chartDraggingEnable;
        this.chart = chart;
        this.initComponents();
        this.initComponentRender();
        this.initEvent();
    }

    public void activeChartDragging() {

        if (chartUtils == null) {
            chartUtils = new ChartUtils(chart);
        }
        if (chartDraggingEnable) {
            chartUtils.activeChartDraggable();
        }
    }

    private void initComponents() {

        legendPane = new HBox();
        customPane = new HBox();
        titlePane = new GridPane();
        chartPane = new Pane();
        zoomInBtn = new Button();
        zoomOutBtn = new Button();
        menuBar = new MenuBar();
        extensionMenu = new Menu();
        copyMenuItem = new MenuItem("Save As");
        saveMenuItem = new MenuItem("Print");
        printMenuItem = new MenuItem("Copy");
        defaultRatioMenuItem = new RadioMenuItem("Default Display");
        oneToOneRatioMenuItem = new RadioMenuItem("1:1 Display");
        ratioMenu = new Menu("Show Ratio");
        final ToggleGroup toggleGroup = new ToggleGroup();
        defaultRatioMenuItem.setSelected(true);
        defaultRatioMenuItem.setToggleGroup(toggleGroup);
        oneToOneRatioMenuItem.setToggleGroup(toggleGroup);
        ratioMenu.getItems().addAll(defaultRatioMenuItem, oneToOneRatioMenuItem);
        extensionMenu.getItems().addAll(saveMenuItem, printMenuItem, copyMenuItem, ratioMenu);
        menuBar.getMenus().add(extensionMenu);

        titlePane.add(legendPane, 0, 0, 10, 1);
        titlePane.add(customPane, 11, 0, 1, 1);
        titlePane.add(zoomInBtn, 12, 0, 1, 1);
        titlePane.add(zoomOutBtn, 13, 0, 1,1);
        titlePane.add(menuBar, 14, 0, 1,1);

        chartPane.getChildren().add(chart);

        this.getChildren().add(titlePane);
        this.getChildren().add(chartPane);
    }

    private void initComponentRender() {

        chart.setPrefHeight(250);
        chart.setPrefWidth(500);
        legendPane.setPrefWidth(400);
        zoomInBtn.setMaxWidth(25);
        zoomInBtn.setMinWidth(25);
        zoomInBtn.setPrefWidth(25);
        zoomInBtn.setMaxHeight(20);
        zoomInBtn.setMinHeight(20);
        zoomInBtn.setPrefHeight(20);
        zoomOutBtn.setMaxWidth(25);
        zoomOutBtn.setMinWidth(25);
        zoomOutBtn.setPrefWidth(25);
        zoomOutBtn.setMaxHeight(20);
        zoomOutBtn.setMinHeight(20);
        zoomOutBtn.setPrefHeight(20);
        extensionMenu.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_more_normal.png")));
        zoomInBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_zoom_normal.png")));
        zoomOutBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_narrow_normal.png")));

        zoomInBtn.setStyle("-fx-border-width: 0px");
        zoomOutBtn.setStyle("-fx-border-width: 0px");
        extensionMenu.setStyle("-fx-border-width: 0px");
        customPane.setStyle("-fx-padding: 3px 0px 0px 0px; -fx-border-width: 0px");
        chart.setLegendVisible(false);
    }

    private void initEvent() {

        zoomInBtn.setOnAction(event -> {
            if (chartSizeChangeEnable) {

                if (chartUtils == null) {
                    chartUtils = new ChartUtils(chart);
                }
                chartUtils.zoomInChart();
            }
        });

        zoomOutBtn.setOnAction(event -> {
            if (chartSizeChangeEnable) {

                if (chartUtils == null) {
                    chartUtils = new ChartUtils(chart);
                }
                chartUtils.zoomOutChart();
            }
        });
    }

    private HBox legendPane;
    private HBox customPane;
    private Button zoomInBtn;
    private Button zoomOutBtn;
    private MenuBar menuBar;
    private Menu ratioMenu;
    private Menu extensionMenu;
    private MenuItem copyMenuItem;
    private MenuItem saveMenuItem;
    private MenuItem printMenuItem;
    private RadioMenuItem defaultRatioMenuItem;
    private RadioMenuItem oneToOneRatioMenuItem;

    public T getChart() {
        return chart;
    }

    public void setChart(T chart) {
        this.chart = chart;
    }

    public HBox getLegendPane() {
        return legendPane;
    }

    public HBox getCustomPane() {
        return customPane;
    }
}
