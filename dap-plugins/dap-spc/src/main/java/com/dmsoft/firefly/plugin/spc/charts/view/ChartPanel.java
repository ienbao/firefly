package com.dmsoft.firefly.plugin.spc.charts.view;

import com.dmsoft.firefly.gui.components.chart.ChartOperatorUtils;
import com.dmsoft.firefly.gui.components.chart.ChartUtils;
import com.dmsoft.firefly.plugin.spc.charts.utils.LegendUtils;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.sun.javafx.charts.Legend;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * Created by cherry on 2018/2/8.
 */

/**
 * Chart pane
 *
 * @param <T> chart class
 */
public class ChartPanel<T extends XYChart> extends VBox {

    private T chart;
    private BorderPane titlePane;
    private String chartName = "default";
    private final String suffix = ".png";

    private ChartUtils chartUtils;
    private boolean chartSizeChangeEnable = true;
    private boolean chartDraggingEnable = true;
    private boolean showLegend = false;

    private final double spacing = 10;
    private final double threshold = 1;
    private final double legendWidth = 255;
    private final double legendHeight = 25;

    /**
     * Constructor for ChartPanel
     *
     * @param chart chart
     */
    public ChartPanel(T chart) {
        this(chart, false, true, true);
    }

    /**
     * Constructor for ChartPanel
     *
     * @param chart      chart
     * @param showLegend whether show legend or not
     */
    public ChartPanel(T chart, boolean showLegend) {
        this(chart, showLegend, true, true);
    }

    /**
     * Constructor for ChartPanel
     *
     * @param chart                 chart
     * @param chartSizeChangeEnable enable change chart size
     * @param chartDraggingEnable   enable drag chart
     */
    public ChartPanel(T chart, boolean showLegend, boolean chartSizeChangeEnable, boolean chartDraggingEnable) {
        this.showLegend = showLegend;
        this.chartSizeChangeEnable = chartSizeChangeEnable;
        this.chartDraggingEnable = chartDraggingEnable;
        this.chart = chart;
        if (chart != null && chart.getXAxis() instanceof ValueAxis && chart.getYAxis() instanceof ValueAxis) {
            this.chartUtils = new ChartUtils(chart);
        }
        this.initComponents();
        this.initComponentRender();
        this.setComponentsTooltip();
        this.initEvent();
    }

    /**
     * Update chart x, y lower and upper range
     */
    public void updateChartData() {
        if (chartUtils != null) {
            ValueAxis xAxis = (ValueAxis) chart.getXAxis();
            ValueAxis yAxis = (ValueAxis) chart.getYAxis();
            chartUtils.backToDefaultCurrentRate();
            chartUtils.setOriginalXUpper(xAxis.getUpperBound());
            chartUtils.setOriginalXLower(xAxis.getLowerBound());
            chartUtils.setOriginalYUpper(yAxis.getUpperBound());
            chartUtils.setOriginalYLower(yAxis.getLowerBound());
            if (chartDraggingEnable) {
                chartUtils.activeChartDraggable();
            }
        }
    }

    private void initComponents() {
        legendBtn = new Button();
        leftHBox = new HBox();
        customPane = new HBox();
        titlePane = new BorderPane();
        zoomInBtn = new Button();
        zoomOutBtn = new Button();
        extensionBtn = new Button();
        contextMenu = new ContextMenu();
        menuBar = new MenuBar();
        extensionMenu = new Menu();
        copyMenuItem = new MenuItem("Copy");
        saveMenuItem = new MenuItem("Save As");
        printMenuItem = new MenuItem("Print");
        defaultRatioMenuItem = new RadioMenuItem("Default Display");
        oneToOneRatioMenuItem = new RadioMenuItem("1:1 Display");
        oneToOneRatioMenuItem.setDisable(true);
        ratioMenu = new Menu("Show Ratio");
        final ToggleGroup toggleGroup = new ToggleGroup();
        defaultRatioMenuItem.setSelected(true);
        defaultRatioMenuItem.setToggleGroup(toggleGroup);
        oneToOneRatioMenuItem.setToggleGroup(toggleGroup);
        ratioMenu.getItems().addAll(defaultRatioMenuItem, oneToOneRatioMenuItem);
//        extensionMenu.getItems().addAll(saveMenuItem, printMenuItem, copyMenuItem, ratioMenu);
        extensionMenu.getItems().addAll(saveMenuItem);
        menuBar.getMenus().addAll(extensionMenu);
//        contextMenu.getItems().addAll(saveMenuItem, printMenuItem, copyMenuItem, ratioMenu);
        Pane topPane = new Pane();
        topPane.setPrefHeight(3);
        topPane.setMinHeight(3);
        topPane.setMaxHeight(3);
        rightHBox = new HBox();
        rightHBox.getChildren().add(customPane);
        rightHBox.getChildren().add(zoomInBtn);
        rightHBox.getChildren().add(zoomOutBtn);
        rightHBox.getChildren().add(menuBar);
        legend = new LegendUtils().buildReferenceLineLegend();
        if (showLegend) {
            leftHBox.getChildren().add(legend);
        }
        titlePane.setLeft(leftHBox);
        titlePane.setRight(rightHBox);
        this.getChildren().add(titlePane);
        this.getChildren().add(chart);
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
        rightHBox.setMargin(zoomInBtn, new Insets(0, 0, 0, 5));
        rightHBox.setMargin(zoomOutBtn, new Insets(0, 0, 0, 5));
        rightHBox.setMargin(menuBar, new Insets(-3, 0, 0, 5));
        titlePane.setMargin(leftHBox, new Insets(3, 0, 0, spacing));
        titlePane.setMargin(rightHBox, new Insets(3, 0, 0, 0));

//        extensionMenu.setStyle("-fx-padding: 0em 1em 0em -0.8em");
        menuBar.getStyleClass().removeAll("menu-icon");
        menuBar.getStyleClass().add("menu-icon");

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
        legend.setPrefWidth(legendWidth);
        legend.setPrefHeight(legendHeight);
        Tooltip tooltip = new Tooltip();
        tooltip.setGraphic(legend);
        tooltip.getStyleClass().add("chart-legend-tooltip");
        Tooltip.install(legendBtn, tooltip);
    }

    private void setComponentsTooltip() {
        Tooltip.install(zoomInBtn, new Tooltip(UIConstant.BTN_CHART_ZOOM_IN));
        Tooltip.install(zoomOutBtn, new Tooltip(UIConstant.BTN_CHART_ZOOM_OUT));
        Tooltip.install(menuBar, new Tooltip(UIConstant.BTN_CHART_EXTENSION_MENU));
    }

    private void initEvent() {

        zoomInBtn.setOnAction(event -> {
            if (chartSizeChangeEnable && chartUtils != null) {
                chartUtils.zoomInChart();
            }
        });

        zoomOutBtn.setOnAction(event -> {
            if (chartSizeChangeEnable && chartUtils != null) {
                chartUtils.zoomOutChart();
            }
        });

        this.boundsInLocalProperty().addListener((observable, oldValue, newValue) -> {
            double titlePaneWidth = titlePane.getWidth();
            double legendLabelWidth = legend.getWidth();
            double rightPaneWidth = rightHBox.getWidth();
            double totalWidth = legendLabelWidth + rightPaneWidth;
            if (titlePaneWidth > 0 && leftHBox.getWidth() > 0 && totalWidth > 0) {
//                System.out.println("titlePaneWidth: " + titlePaneWidth);
//                System.out.println("legendLabelWidth: " + legendLabelWidth);
//                System.out.println("rightPaneWidth: " + rightPaneWidth);
//                System.out.println("totalWidth: " + totalWidth);
                if (titlePaneWidth <= totalWidth + spacing + threshold) {
                    leftHBox.getChildren().setAll(legendBtn);
                } else {
                    leftHBox.getChildren().setAll(legend);
                }
            }
        });

        saveMenuItem.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save as spc chart");
            fileChooser.setInitialDirectory(
                    new File(System.getProperty("user.home"))
            );
            fileChooser.setInitialFileName(chartName);
            FileChooser.ExtensionFilter pdfExtensionFilter =
                    new FileChooser.ExtensionFilter(
                            "PNG - Portable Network Graphics (.png)", "*.png");
            fileChooser.getExtensionFilters().add(pdfExtensionFilter);
            fileChooser.setSelectedExtensionFilter(pdfExtensionFilter);
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                try {
                    String imagePath = file.getAbsolutePath();
                    if (!imagePath.contains(suffix)) {
                        imagePath += suffix;
                    }
                    file = new File(imagePath);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    final float quality = 0.9f;
                    WritableImage writableImage = chart.snapshot(new SnapshotParameters(), null);
                    ChartOperatorUtils.saveImageUsingJPGWithQuality(SwingFXUtils.fromFXImage(writableImage, null), file, quality);
                } catch (Exception e) {
                    e.printStackTrace();
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

    /**
     * Toggle disable custom button show or disabled
     *
     * @param flag if true, button disabled; if false, button relieve disabled
     */
    public void toggleCustomButtonDisable(boolean flag) {
        customPane.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setDisable(flag);
            }
        });
        zoomInBtn.setDisable(flag);
        zoomOutBtn.setDisable(flag);
        extensionMenu.setDisable(flag);
    }

    private Button legendBtn;
    private Legend legend;
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
