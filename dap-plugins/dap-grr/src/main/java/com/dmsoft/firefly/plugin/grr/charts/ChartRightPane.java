package com.dmsoft.firefly.plugin.grr.charts;

import com.dmsoft.firefly.gui.components.chart.ChartOperatorUtils;
import com.dmsoft.firefly.gui.components.chart.ChartUtils;
import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.grr.utils.ResourceMassages;
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
    private MenuItem saveMenuItem;
    private RadioMenuItem defaultRatioMenuItem;
    private RadioMenuItem oneToOneRatioMenuItem;

    /**
     * Construct a new LineChart with the given xyChart.
     *
     * @param xyChart XYChart
     */
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
        Tooltip.install(zoomInBtn, new Tooltip(GrrFxmlAndLanguageUtils.getString(UIConstant.BTN_CHART_ZOOM_IN)));
        Tooltip.install(zoomOutBtn, new Tooltip(GrrFxmlAndLanguageUtils.getString(UIConstant.BTN_CHART_ZOOM_OUT)));
        Tooltip.install(menuBar, new Tooltip(GrrFxmlAndLanguageUtils.getString(UIConstant.BTN_CHART_EXTENSION_MENU)));
    }

    private void initComponents() {

        customPane = new HBox();
        zoomInBtn = new Button();
        zoomOutBtn = new Button();
        menuBar = new MenuBar();
        extensionMenu = new Menu();
        saveMenuItem = new MenuItem(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_EXTENSION_MENU_SAVE));
        defaultRatioMenuItem = new RadioMenuItem("Default Display");
        oneToOneRatioMenuItem = new RadioMenuItem("1:1 Display");
        oneToOneRatioMenuItem.setDisable(true);

        ratioMenu = new Menu("Show Ratio");
        final ToggleGroup toggleGroup = new ToggleGroup();
        defaultRatioMenuItem.setSelected(true);
        defaultRatioMenuItem.setToggleGroup(toggleGroup);
        oneToOneRatioMenuItem.setToggleGroup(toggleGroup);
        ratioMenu.getItems().addAll(defaultRatioMenuItem, oneToOneRatioMenuItem);
        extensionMenu.getItems().addAll(saveMenuItem);
        menuBar.getMenus().addAll(extensionMenu);

        this.getChildren().add(customPane);
        this.getChildren().add(zoomInBtn);
        this.getChildren().add(zoomOutBtn);
        this.getChildren().add(menuBar);
    }

    /**
     * Enable extension menu
     *
     * @param flag whether disable menu or not
     */
    public void toggleExtensionMenu(boolean flag) {
        extensionMenu.setDisable(!flag);
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
        this.setMargin(menuBar, new Insets(-3, 7, 0, 5));
        extensionMenu.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/more.svg")));
        zoomInBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/zoom.svg")));
        zoomOutBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/narrow.svg")));
    }

    /**
     * Update chart zoom original axis
     */
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
            fileChooser.setTitle(GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_SAVE_AS_TITLE));
            fileChooser.setInitialDirectory(
                    new File(System.getProperty("user.home"))
            );
            fileChooser.setInitialFileName(chartName);
            FileChooser.ExtensionFilter pdfExtensionFilter = new FileChooser.ExtensionFilter(
                    GrrFxmlAndLanguageUtils.getString(UIConstant.CHART_SAVE_AS_PNG_EXTENSION)
                            + " (.png)", "*.png");
            fileChooser.getExtensionFilters().add(pdfExtensionFilter);
            fileChooser.setSelectedExtensionFilter(pdfExtensionFilter);
            File file = fileChooser.showSaveDialog(StageMap.getStage(ResourceMassages.PLATFORM_STAGE_MAIN));
            final float quality = 0.9f;
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
                    WritableImage writableImage = chart.snapshot(new SnapshotParameters(), null);
                    ChartOperatorUtils.saveImageUsingJPGWithQuality(SwingFXUtils.fromFXImage(writableImage, null), file, quality);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Add custom pane children node
     *
     * @param node Node
     */
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
