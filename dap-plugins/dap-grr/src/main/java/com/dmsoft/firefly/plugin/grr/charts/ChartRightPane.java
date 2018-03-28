package com.dmsoft.firefly.plugin.grr.charts;

import com.dmsoft.firefly.gui.components.chart.ChartSaveUtils;
import com.dmsoft.firefly.gui.components.chart.ChartUtils;
import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.plugin.grr.utils.UIConstant;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * Created by cherry on 2018/3/28.
 */
public class ChartRightPane extends HBox {

    private ChartUtils chartUtils;
    private boolean chartSizeChangeEnable = false;
    private boolean chartDraggingEnable = false;

    private String chartName = "default";
    private final String suffix = ".png";

    private XYChart chart;

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

    public ChartRightPane(XYChart xyChart) {
        this.chart = xyChart;
        if (chart.getXAxis() instanceof ValueAxis && chart.getYAxis() instanceof ValueAxis) {
            this.chartUtils = new ChartUtils(chart);
        }
        this.initComponents();
        this.initComponentsRender();
        this.initComponentsEvent();
        this.setComponentsTooltip();
    }

    private void setComponentsTooltip() {
        Tooltip.install(zoomInBtn, new Tooltip(UIConstant.BTN_CHART_ZOOM_IN));
        Tooltip.install(zoomOutBtn, new Tooltip(UIConstant.BTN_CHART_ZOOM_OUT));
        Tooltip.install(menuBar, new Tooltip(UIConstant.BTN_CHART_EXTENSION_MENU));
    }

    private void initComponents() {

        customPane = new HBox();
        zoomInBtn = new Button();
        zoomOutBtn = new Button();
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
        extensionMenu.getItems().addAll(saveMenuItem, printMenuItem, copyMenuItem, ratioMenu);
        menuBar.getMenus().addAll(extensionMenu);

        this.getChildren().add(customPane);
        this.getChildren().add(zoomInBtn);
        this.getChildren().add(zoomOutBtn);
        this.getChildren().add(menuBar);
    }

    private void initComponentsRender() {
        zoomInBtn.setDisable(true);
        zoomOutBtn.setDisable(true);
        zoomInBtn.setPrefWidth(20);
        zoomInBtn.setMaxWidth(20);
        zoomInBtn.setMinWidth(20);
        zoomOutBtn.setPrefWidth(20);
        zoomOutBtn.setMaxWidth(20);
        zoomOutBtn.setMinWidth(20);
        zoomInBtn.getStyleClass().addAll("btn-icon-b");
        zoomOutBtn.getStyleClass().addAll("btn-icon-b");
        menuBar.getStyleClass().removeAll("menu-icon");
        menuBar.getStyleClass().add("menu-icon");
        this.setMargin(zoomInBtn, new Insets(0, 0, 0, 5));
        this.setMargin(zoomOutBtn, new Insets(0, 0, 0, 5));
        this.setMargin(menuBar, new Insets(-3, 0, 0, 5));
        extensionMenu.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_more_normal.png")));
        zoomInBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_zoom_normal.png")));
        zoomOutBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_narrow_normal.png")));
    }

    public void updateChartData() {
        if (chartUtils != null) {
            ValueAxis xAxis = (ValueAxis) chart.getXAxis();
            ValueAxis yAxis = (ValueAxis) chart.getYAxis();
            chartUtils.setOriginalXUpper(xAxis.getUpperBound());
            chartUtils.setOriginalXLower(xAxis.getLowerBound());
            chartUtils.setOriginalYUpper(yAxis.getUpperBound());
            chartUtils.setOriginalYLower(yAxis.getLowerBound());
        }
    }

    private void initComponentsEvent() {
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

        if (chartDraggingEnable) {
            chartUtils.activeChartDraggable();
        }

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
                    if (imagePath.contains(suffix)) {
                        imagePath += suffix;
                    }
                    file = new File(imagePath);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    WritableImage writableImage = chart.snapshot(new SnapshotParameters(), null);
                    ChartSaveUtils.saveImageUsingJPGWithQuality(SwingFXUtils.fromFXImage(writableImage, null), file, 0.9f);
                } catch (Exception e) {
                    System.out.println("Save error, " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    public void addCustomPaneChildren(Node node) {
        this.customPane.getChildren().add(node);
    }

    public void setChartSizeChangeEnable(boolean chartSizeChangeEnable) {
        this.chartSizeChangeEnable = chartSizeChangeEnable;
    }

    public void setChartDraggingEnable(boolean chartDraggingEnable) {
        this.chartDraggingEnable = chartDraggingEnable;
    }
}
