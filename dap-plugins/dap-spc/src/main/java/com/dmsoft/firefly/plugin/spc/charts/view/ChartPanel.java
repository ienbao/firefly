package com.dmsoft.firefly.plugin.spc.charts.view;

import com.dmsoft.firefly.gui.components.chart.ChartUtils;
import com.dmsoft.firefly.plugin.spc.charts.MultipleAxisXYChart;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
/**
 * Created by cherry on 2018/2/8.
 */
public class ChartPanel<T extends Node> extends VBox {

    private T chart;
    private VBox chartPane;
    private BorderPane titlePane;

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
            if (chart instanceof  XYChart) {
                chartUtils = new ChartUtils((XYChart) chart);
            } else if (chart instanceof MultipleAxisXYChart) {

            }
        }
        if (chartDraggingEnable && chartUtils != null) {
            chartUtils.activeChartDraggable();
        }
    }

    private void initComponents() {

        legendPane = new Label();
        customPane = new HBox();
        titlePane = new BorderPane();
        chartPane = new VBox();
        zoomInBtn = new Button();
        zoomOutBtn = new Button();

        extensionBtn = new Button();
        contextMenu = new ContextMenu();

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
        menuBar.getMenus().addAll(extensionMenu);

        contextMenu.getItems().addAll(saveMenuItem, printMenuItem, copyMenuItem, ratioMenu);

        HBox commonBox = new HBox();
        commonBox.getChildren().add(customPane);
        commonBox.getChildren().add(zoomInBtn);
        commonBox.getChildren().add(zoomOutBtn);
        commonBox.getChildren().add(menuBar);
        Pane blankPane = new Pane();
        blankPane.setPrefHeight(3);
        blankPane.setMinHeight(3);
        blankPane.setMaxHeight(3);
        titlePane.setTop(blankPane);
        titlePane.setLeft(legendPane);
        titlePane.setRight(commonBox);
        chartPane.getChildren().add(chart);
        this.getChildren().add(titlePane);
        this.getChildren().add(chartPane);
    }

    private void initComponentRender() {

        zoomInBtn.getStyleClass().addAll("btn-icon-b");
        zoomOutBtn.getStyleClass().addAll("btn-icon-b");
        extensionBtn.getStyleClass().addAll("btn-icon-b");
        extensionMenu.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_more_normal.png")));
        extensionBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_more_normal.png")));
        zoomInBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_zoom_normal.png")));
        zoomOutBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_narrow_normal.png")));

//        menuBar.getStyleClass().remove("menu-bar");
        zoomInBtn.setPrefWidth(20);
        zoomInBtn.setMaxWidth(20);
        zoomInBtn.setMinWidth(20);
        zoomOutBtn.setPrefWidth(20);
        zoomOutBtn.setMaxWidth(20);
        zoomOutBtn.setMinWidth(20);
        extensionBtn.setPrefWidth(20);
        extensionBtn.setMaxWidth(20);
        extensionBtn.setMinWidth(20);

        if (chart instanceof XYChart) {
            XYChart xyChart = (XYChart) chart;
            xyChart.setPrefHeight(250);
            xyChart.setPrefWidth(500);
            xyChart.setLegendVisible(false);
        }

        if (chart instanceof StackPane) {
            ((StackPane) chart).setPrefWidth(500);
            ((StackPane) chart).setPrefHeight(250);
            ((StackPane) chart).setStyle("-fx-background-color: #462300");
        }

    }

    private void initEvent() {

        zoomInBtn.setOnAction(event -> {
            if (chartSizeChangeEnable) {
                if (chartUtils == null) {
                    XYChart xyChart = (XYChart) chart;
                    chartUtils = new ChartUtils(xyChart);
                }
                chartUtils.zoomInChart();
            }
        });

        zoomOutBtn.setOnAction(event -> {
            if (chartSizeChangeEnable) {

                if (chartUtils == null) {
                    XYChart xyChart = (XYChart) chart;
                    chartUtils = new ChartUtils(xyChart);
                }
                chartUtils.zoomOutChart();
            }
        });

//        extensionBtn.setOnMouseClicked(event -> {
//
//            double screenX = extensionBtn.getScene().getWindow().getX() +
//                    extensionBtn.getScene().getX() + extensionBtn.localToScene(0, 0).getX();
//            double screenY = extensionBtn.getScene().getWindow().getY() +
//                    extensionBtn.getScene().getY() + extensionBtn.localToScene(0, 0).getY() +
//                    extensionBtn.getHeight();
//            contextMenu.show(this, screenX, screenY);
//        });
    }

    private Label legendPane;
    private HBox customPane;
    private Button zoomInBtn;
    private Button zoomOutBtn;
    private Button extensionBtn;
    private ContextMenu contextMenu;

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

    public Label getLegendPane() {
        return legendPane;
    }

    public HBox getCustomPane() {
        return customPane;
    }
}
