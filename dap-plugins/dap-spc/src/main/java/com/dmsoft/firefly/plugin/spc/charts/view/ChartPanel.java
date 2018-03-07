package com.dmsoft.firefly.plugin.spc.charts.view;

import com.dmsoft.firefly.gui.components.chart.ChartUtils;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import javafx.geometry.Insets;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Created by cherry on 2018/2/8.
 */
public class ChartPanel<T extends XYChart> extends VBox {

    private T chart;
    private VBox chartPane;
    private BorderPane titlePane;

    private ChartUtils chartUtils;
    private boolean chartSizeChangeEnable = true;
    private boolean chartDraggingEnable = true;

    private final double spacing = 10;
    private final double threshold = 1;

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
        if (chartDraggingEnable && chartUtils != null) {
            chartUtils.activeChartDraggable();
        }
    }

    private void initComponents() {

        legendLbl = new Label();
        legendBtn = new Button();
        leftHBox = new HBox();
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
        Pane topPane = new Pane();
        topPane.setPrefHeight(3);
        topPane.setMinHeight(3);
        topPane.setMaxHeight(3);
        rightHBox = new HBox();
        rightHBox.getChildren().add(customPane);
        rightHBox.getChildren().add(zoomInBtn);
        rightHBox.getChildren().add(zoomOutBtn);
        rightHBox.getChildren().add(menuBar);
        leftHBox.getChildren().add(legendLbl);
        titlePane.setTop(topPane);
        titlePane.setLeft(leftHBox);
        titlePane.setRight(rightHBox);
        chartPane.getChildren().add(chart);
        this.getChildren().add(titlePane);
        this.getChildren().add(chartPane);
    }

    private void initComponentRender() {

        zoomInBtn.getStyleClass().addAll("btn-icon-b");
        zoomOutBtn.getStyleClass().addAll("btn-icon-b");
        extensionBtn.getStyleClass().addAll("btn-icon-b");
        legendBtn.getStyleClass().setAll("btn-icon-b");

        extensionMenu.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_more_normal.png")));
        extensionBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_more_normal.png")));
        zoomInBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_zoom_normal.png")));
        zoomOutBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_narrow_normal.png")));
        legendBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_unfold_normal.png")));
        legendBtn.setPadding(new Insets(0, 0, 0, 3));
        legendLbl.setPadding(new Insets(3, 0, 0, 0));
        leftHBox.setPadding(new Insets(0, 0, 0, spacing * 2));

        zoomInBtn.setPrefWidth(20);
        zoomInBtn.setMaxWidth(20);
        zoomInBtn.setMinWidth(20);
        zoomOutBtn.setPrefWidth(20);
        zoomOutBtn.setMaxWidth(20);
        zoomOutBtn.setMinWidth(20);
        extensionBtn.setPrefWidth(20);
        extensionBtn.setMaxWidth(20);
        extensionBtn.setMinWidth(20);
        legendBtn.setPrefWidth(25);
        legendBtn.setMaxWidth(25);
        legendBtn.setMinWidth(25);
        legendBtn.setPrefHeight(25);
        legendBtn.setMaxHeight(25);
        legendBtn.setMinHeight(25);
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

        this.boundsInLocalProperty().addListener((observable, oldValue, newValue) -> {

            double titlePaneWidth = titlePane.getWidth();
            double legendLabelWidth = legendLbl.getWidth();
            double rightPaneWidth = rightHBox.getWidth();
            double totalWidth = legendLabelWidth + rightPaneWidth;

            if (titlePaneWidth > 0 && leftHBox.getWidth() > 0 && totalWidth > 0) {

//                System.out.println("titlePaneWidth: " + titlePaneWidth);
//                System.out.println("legendLabelWidth: " + legendLabelWidth);
//                System.out.println("rightPaneWidth: " + rightPaneWidth);
//                System.out.println("totalWidth: " + totalWidth);
                if (titlePaneWidth <= totalWidth + spacing * 2 + threshold) {
                    leftHBox.getChildren().setAll(legendBtn);
                } else {
                    leftHBox.getChildren().setAll(legendLbl);
                }
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

    public void setLegend(String legend) {
        legendLbl.setText(legend);
        Tooltip.install(legendBtn, new Tooltip(legendLbl.getText()));
    }

    private Button legendBtn;
    private Label legendLbl;
    private HBox leftHBox;
    private HBox rightHBox;
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

    public HBox getLegendPane() {
        return leftHBox;
    }

    public HBox getCustomPane() {
        return customPane;
    }
}
