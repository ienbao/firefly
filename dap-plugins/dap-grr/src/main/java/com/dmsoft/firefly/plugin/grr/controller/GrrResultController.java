package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.plugin.grr.charts.ChartOperateButton;
import com.dmsoft.firefly.plugin.grr.charts.LinearChart;
import com.dmsoft.firefly.plugin.grr.charts.data.ILineData;
import com.dmsoft.firefly.plugin.grr.dto.GrrSummaryDto;
import com.dmsoft.firefly.plugin.grr.dto.analysis.*;
import com.dmsoft.firefly.plugin.grr.model.*;
import com.dmsoft.firefly.plugin.grr.utils.ScrollPaneValueUtils;
import com.dmsoft.firefly.plugin.grr.utils.UIConstant;
import com.dmsoft.firefly.plugin.grr.utils.charts.ChartUtils;
import com.dmsoft.firefly.plugin.grr.utils.charts.LegendUtils;
import com.dmsoft.firefly.plugin.grr.utils.enums.GrrResultName;
import com.dmsoft.firefly.plugin.grr.utils.table.TableCellCallBack;
import com.dmsoft.firefly.plugin.grr.utils.table.TableRender;
import com.google.common.collect.Lists;
import com.sun.javafx.charts.Legend;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by cherry on 2018/3/12.
 */
public class GrrResultController implements Initializable {

    /****** Summary *****/
    @FXML
    private HBox itemFilterHBox;
    @FXML
    private ComboBox resultBasedCmb;
    @FXML
    private TableView<GrrSingleSummary> summaryTb;

    private TextFieldFilter summaryItemTf;
    @FXML
    private TableView anovaTb;
    @FXML
    private TableView sourceTb;
    @FXML
    private Button categoryBtn;
    @FXML
    private Button grrDataBtn;
    @FXML
    private Button grrChartBtn;
    @FXML
    private Button grrResultBtn;
    @FXML
    private ScrollPane grrResultScrollPane;
    @FXML
    private VBox chartVBox;
    @FXML
    private VBox resultVBox;

    private GrrSummaryModel summaryModel = new GrrSummaryModel();
    private GrrAnovaModel anovaModel = new GrrAnovaModel();
    private GrrSourceModel sourceModel = new GrrSourceModel();
    final FilteredList<GrrSingleSummary> filteredList = summaryModel.getSummaries().filtered(p -> true);

    /****** Chart ******/
    @FXML
    private VBox xBarAppraiserVBox;
    @FXML
    private VBox rangeAppraiserVBox;
    @FXML
    private VBox rrByAppraiserVBox;
    @FXML
    private BorderPane componentBp;
    @FXML
    private BorderPane partAppraiserBp;
    @FXML
    private BorderPane xBarAppraiserBp;
    @FXML
    private BorderPane rangeAppraiserBp;
    @FXML
    private BorderPane rrbyAppraiserBp;
    @FXML
    private BorderPane rrbyPartBp;
    @FXML
    private BarChart componentChart;
    @FXML
    private LineChart partAppraiserChart;
    private LinearChart xBarAppraiserChart;
    private LineChart rrByAppraiserChart;
    private VBox rrbyPartVBox;

    List<String> parts = Lists.newArrayList("part1", "part2", "part3", "part4");
    List<String> appraisers = Lists.newArrayList("1", "2", "3", "4");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initComponents();
        this.initComponentsRender();
        this.initComponentEvents();
        summaryTb.setItems(filteredList);
        anovaTb.setItems(anovaModel.getAnovas());
        sourceTb.setItems(sourceModel.getSouces());
        this.setSummaryData(null);
        this.setResultData(new GrrDetailResultDto());
    }

    public void setSummaryData(List<GrrSummaryDto> summaryData) {

        List<GrrSummaryDto> summaryDtos = Lists.newArrayList();
        for (int i = 0; i < 5; i++) {
            GrrSummaryDto grrSummaryDto = new GrrSummaryDto();
            GrrSummaryResultDto summaryResultDto = new GrrSummaryResultDto();
            grrSummaryDto.setItemName("A" + (i + 1));
            summaryResultDto.setLsl(Double.valueOf(i + 1));
            summaryResultDto.setUsl(Double.valueOf(i * 10 + 1));
            summaryResultDto.setTolerance(Double.valueOf(i + 2));
            summaryResultDto.setRepeatabilityOnTolerance(Double.valueOf(i * 2 + 2));
            summaryResultDto.setRepeatabilityOnContribution(Double.valueOf(i * 3 + 2));
            summaryResultDto.setReproducibilityOnTolerance(Double.valueOf(i * 2 + 3));
            summaryResultDto.setReproducibilityOnContribution(Double.valueOf(i * 3 + 3));
            summaryResultDto.setGrrOnTolerance(Double.valueOf(i * 4 + 2));
            summaryResultDto.setGrrOnContribution(Double.valueOf(i * 4 + 3));
            grrSummaryDto.setSummaryResultDto(summaryResultDto);
            summaryDtos.add(grrSummaryDto);
        }
        summaryModel.setData(summaryDtos, resultBasedCmb.getSelectionModel().getSelectedItem().toString());
    }

    public void setResultData(GrrDetailResultDto grrDetailResultDto) {
        setComponentChart(buildComponentChartData());
        setPartAppraiserChart(buildPartAppraiserChartData(), parts, appraisers);
        setXBarAppraiserChart(buildXBarAppraiserChartData(), parts, appraisers);
        setRrByAppraiserChart(buildRrByAppraiserChartData());
        setAnovaAndSourceTb(buildAnovaAndSourceData());
    }

    private void setComponentChart(GrrComponentCResultDto componentCResult) {
        XYChart.Series series1 = new XYChart.Series();
        XYChart.Series series2 = new XYChart.Series();
        XYChart.Series series3 = new XYChart.Series();
        series1.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[0], componentCResult.getGrrContri()));
        series1.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[1], componentCResult.getRepeatContri()));
        series1.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[2], componentCResult.getReprodContri()));
        series1.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[3], componentCResult.getPartContri()));
        series2.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[0], componentCResult.getGrrVar()));
        series2.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[1], componentCResult.getRepeatVar()));
        series2.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[2], componentCResult.getReprodVar()));
        series2.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[3], componentCResult.getPartVar()));
        series3.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[0], componentCResult.getGrrTol()));
        series3.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[1], componentCResult.getRepeatVar()));
        series3.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[2], componentCResult.getReprodVar()));
        series3.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[3], componentCResult.getPartVar()));
        componentChart.getData().addAll(series1, series2, series3);
        String[] colors = new String[UIConstant.CHART_COMPONENT_CATEGORY.length + 2];
        for (int i = 0; i < UIConstant.CHART_COMPONENT_CATEGORY.length; i++) {
            XYChart.Series series = (XYChart.Series) componentChart.getData().get(i);
            series.setName(UIConstant.CHART_COMPONENT_CATEGORY[i]);
            colors[i] = "default-color" + i;
        }
        colors[UIConstant.CHART_COMPONENT_CATEGORY.length] = "bar-legend-symbol";
        colors[UIConstant.CHART_COMPONENT_CATEGORY.length + 1] = "chart-bar";
        Legend legend = LegendUtils.buildLegend(componentChart.getData(), colors);
        componentBp.setLeft(legend);
        componentBp.setMargin(legend, new Insets(0, 0, 1, 0));
        ChartUtils.setChartText(componentChart.getData(), "%");
    }

    private void setPartAppraiserChart(GrrPACResultDto partAppraiserChartDto,
                                       List<String> parts,
                                       List<String> appraisers) {
        double[][] data = partAppraiserChartDto.getDatas();
        ObservableList<XYChart.Series> seriesData = FXCollections.observableArrayList();
        for (int i = 0; i < data.length; i++) {
            XYChart.Series series = new XYChart.Series();
            series.setName(appraisers.get(i));
            double[] appraiser = data[i];
            for (int j = 0; j < appraiser.length; j++) {
                series.getData().add(new XYChart.Data<>(parts.get(j), appraiser[j], (j + 1)));
            }
            seriesData.add(series);
        }
        partAppraiserChart.getData().addAll(seriesData);
        Legend legend = LegendUtils.buildLegend(partAppraiserChart.getData(), "chart-line-symbol", "bar-legend-symbol");
        partAppraiserBp.setLeft(legend);
        partAppraiserBp.setMargin(legend, new Insets(0, 0, 1, 0));
    }

    private void setXBarAppraiserChart(GrrControlChartDto xBarAppraiserChartDto,
                                       List<String> parts,
                                       List<String> appraisers) {
        int partCount = parts.size();
        int appraiserCount = appraisers.size();
        Double[] x = xBarAppraiserChartDto.getX();
        Double[] y = xBarAppraiserChartDto.getY();
        List<ILineData> lineData = Lists.newArrayList();
        XYChart.Series series = new XYChart.Series();
//        draw vertical line
        for (int i = 0; i < x.length; i++) {
            if ((i + 1) % partCount == 0 && i != x.length - 1) {
                double value = (x[i] + x[i + 1]) / 2;
                lineData.add(new ILineData() {
                    @Override
                    public String getName() {
                        return "";
                    }

                    @Override
                    public double getValue() {
                        return value;
                    }

                    @Override
                    public Color getColor() {
                        return Color.rgb(237, 237, 237);
                    }
                });
            }
            series.getData().add(new XYChart.Data<>(x[i], y[i], parts.get(i % partCount)));
        }
        lineData.add(new ILineData() {
            @Override
            public String getName() {
                return UIConstant.CHART_OPERATE_NAME[0];
            }

            @Override
            public double getValue() {
                return xBarAppraiserChartDto.getUcl();
            }

            @Override
            public Color getColor() {
                return Color.rgb(102, 102, 102);
            }

            @Override
            public Orientation getPlotOrientation() {
                return Orientation.HORIZONTAL;
            }

            @Override
            public String getLineClass() {
                return "dashed2-line";
            }
        });

        lineData.add(new ILineData() {
            @Override
            public String getName() {
                return UIConstant.CHART_OPERATE_NAME[1];
            }

            @Override
            public double getValue() {
                return xBarAppraiserChartDto.getCl();
            }

            @Override
            public Orientation getPlotOrientation() {
                return Orientation.HORIZONTAL;
            }

            @Override
            public String getLineClass() {
                return "solid-line";
            }
        });

        lineData.add(new ILineData() {
            @Override
            public String getName() {
                return UIConstant.CHART_OPERATE_NAME[2];
            }

            @Override
            public double getValue() {
                return xBarAppraiserChartDto.getLcl();
            }

            @Override
            public Color getColor() {
                return Color.rgb(178, 178, 178);
            }

            @Override
            public Orientation getPlotOrientation() {
                return Orientation.HORIZONTAL;
            }

            @Override
            public String getLineClass() {
                return "dashed1-line";
            }
        });

        xBarAppraiserChart.getData().add(series);
        xBarAppraiserChart.buildValueMarker(lineData, false);
    }

    private void setRrByAppraiserChart(GrrScatterChartDto rrbyAppraiserDto) {
        Double[] x = rrbyAppraiserDto.getX();
        Double[] y = rrbyAppraiserDto.getY();
        Double[] clX = rrbyAppraiserDto.getClX();
        Double[] clY = rrbyAppraiserDto.getClY();
        XYChart.Series scatterSeries = new XYChart.Series();
        XYChart.Series lineSeries = new XYChart.Series();
        for (int i = 0; i < x.length; i++) {
            scatterSeries.getData().add(new XYChart.Data<>(x[i], y[i]));
        }
        for (int i = 0; i < clX.length; i++) {
            lineSeries.getData().add(new XYChart.Data<>(clX[i], clY[i]));
        }
        rrByAppraiserChart.getData().addAll(scatterSeries, lineSeries);
        scatterSeries.getNode().getStyleClass().add("chart-series-hidden-line");

    }

    private void setAnovaAndSourceTb(GrrAnovaAndSourceResultDto anovaAndSourceResultDto) {
        anovaModel.setData(anovaAndSourceResultDto.getGrrAnovaDtos());
        sourceModel.setData(anovaAndSourceResultDto.getGrrSourceDtos());
        categoryBtn.setText(String.valueOf(anovaAndSourceResultDto.getNumberOfDc()));
    }

    private void removeComponentChartAll() {
        componentChart.getData().setAll(FXCollections.observableArrayList());
        Node leftNode = componentBp.getLeft();
        componentBp.getChildren().remove(leftNode);
    }

    private GrrComponentCResultDto buildComponentChartData() {
        GrrComponentCResultDto componentCResult = new GrrComponentCResultDto();
        componentCResult.setGrrContri(80D);
        componentCResult.setGrrTol(55.32);
        componentCResult.setGrrVar(100D);
        componentCResult.setRepeatContri(80D);
        componentCResult.setRepeatTol(55.32);
        componentCResult.setRepeatVar(100D);
        componentCResult.setReprodContri(80D);
        componentCResult.setReprodTol(55.32);
        componentCResult.setReprodVar(100D);
        componentCResult.setPartContri(80D);
        componentCResult.setPartTol(55.32);
        componentCResult.setPartVar(100D);
        return componentCResult;
    }

    private GrrPACResultDto buildPartAppraiserChartData() {
        GrrPACResultDto partAppraiserChartDto = new GrrPACResultDto();
        double[][] datas = new double[][]{{10, 20, 30, 40}, {50, 60, 70, 80}, {100, 110, 120, 130}, {150, 160, 170, 180}};
        partAppraiserChartDto.setDatas(datas);
        return partAppraiserChartDto;
    }

    private GrrControlChartDto buildXBarAppraiserChartData() {
        GrrControlChartDto xbarAppraiserChartDto = new GrrControlChartDto();
        Double[] x = new Double[]{1D, 2D, 3D, 4D, 5D, 6D, 7D, 8D, 9D, 10D, 11D, 12D, 13D, 14D, 15D, 16D};
        Double[] y = new Double[]{10D, 12D, 13D, 14D, 15D, 26D, 27D, 28D, 29D, 30D, 16D, 17D, 18D, 19D, 20D, 21D};
        xbarAppraiserChartDto.setX(x);
        xbarAppraiserChartDto.setY(y);
        xbarAppraiserChartDto.setCl(29D);
        xbarAppraiserChartDto.setLcl(13D);
        xbarAppraiserChartDto.setUcl(20D);
        return xbarAppraiserChartDto;
    }

    private GrrScatterChartDto buildRrByAppraiserChartData() {
        GrrScatterChartDto rrbyAppraiserDto = new GrrScatterChartDto();
        Double[] x = new Double[]{1D, 1D, 1D, 1D, 1D, 1D, 2D, 2D, 2D, 2D, 2D, 2D, 3D, 3D, 3D, 3D, 3D, 3D, 4D, 4D, 4D, 4D, 4D, 4D};
        Double[] y = new Double[]{1.1D, 1.2D, 1.3D, 1.4D, 1.5D, 1.6D, 2.1D, 2.2D, 2.3D, 2.4D, 2.5D, 2.6D, 3.1D, 3.2D, 3.3D, 3.4D, 3.5D, 3.6D, 4.1D, 4.2D, 4.3D, 4.4D, 4.5D, 4.6D};
        Double[] clX = new Double[]{1D, 2D, 3D, 4D};
        Double[] clY = new Double[]{1.35D, 2.35D, 3.35D, 4.35D};
        rrbyAppraiserDto.setX(x);
        rrbyAppraiserDto.setY(y);
        rrbyAppraiserDto.setClX(clX);
        rrbyAppraiserDto.setClY(clY);
        return rrbyAppraiserDto;
    }

    private GrrAnovaAndSourceResultDto buildAnovaAndSourceData() {
        GrrAnovaAndSourceResultDto anovaAndSourceResultDto = new GrrAnovaAndSourceResultDto();
        List<GrrAnovaDto> anovaDtos = Lists.newArrayList();
        List<GrrSourceDto> sourceDtos = Lists.newArrayList();
        for (int i = 0; i < UIConstant.GRR_ANOVA_SOURCE.length; i++) {
            GrrAnovaDto grrAnovaDto = new GrrAnovaDto();
            grrAnovaDto.setName(GrrResultName.Appraisers);
            grrAnovaDto.setDf(Double.valueOf(i + 2));
            grrAnovaDto.setF(Double.valueOf(i + 3));
            grrAnovaDto.setMs(Double.valueOf(i + 5));
            grrAnovaDto.setSs(Double.valueOf(i + 9));
            grrAnovaDto.setProbF(Double.valueOf(i + 11));
            anovaDtos.add(grrAnovaDto);
        }
        for (int i = 0; i < 7; i++) {
            GrrSourceDto grrSourceDto = new GrrSourceDto();
            grrSourceDto.setName(GrrResultName.Appraisers);
            grrSourceDto.setContribution(Double.valueOf(i * 2 + 1));
            grrSourceDto.setSigma(Double.valueOf(i * 2 + 2));
            grrSourceDto.setStudyVar(Double.valueOf(i * 2 + 4));
            grrSourceDto.setTotalTolerance(Double.valueOf(i * 2 + 7));
            grrSourceDto.setTotalVariation(Double.valueOf(i * 2 + 9));
            grrSourceDto.setVariation(Double.valueOf(i * 3 + 11));
            sourceDtos.add(grrSourceDto);
        }
        anovaAndSourceResultDto.setGrrAnovaDtos(anovaDtos);
        anovaAndSourceResultDto.setGrrSourceDtos(sourceDtos);
        anovaAndSourceResultDto.setNumberOfDc(10.0);
        return anovaAndSourceResultDto;
    }

    private void initComponents() {
        summaryItemTf = new TextFieldFilter();
        summaryItemTf.getTextField().setPromptText("Test Item");
        itemFilterHBox.getChildren().setAll(summaryItemTf);
        resultBasedCmb.getItems().addAll(UIConstant.GRR_RESULT_TYPE);
        resultBasedCmb.setValue(UIConstant.GRR_RESULT_TYPE[0]);
//        Init summary table
        summaryTb.getColumns().addAll(buildSummaryTbColumn());
        anovaTb.getColumns().addAll(buildAnovaTbColumn());
        sourceTb.getColumns().addAll(buildSourceTbColumn());
        this.initXBarAppraiserChartPane();
        this.initRrByAppraiserChartPane();
    }

    private void initXBarAppraiserChartPane() {
        NumberAxis xBarAppraiserXAxis = new NumberAxis();
        NumberAxis xBarAppraiserYAxis = new NumberAxis();
        xBarAppraiserXAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return (Double) object % ((+parts.size()) / 2) == 0 ? String.valueOf(object) : null;
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });
        xBarAppraiserXAxis.setMinorTickVisible(false);
        xBarAppraiserXAxis.setTickMarkVisible(false);
        xBarAppraiserYAxis.setMinorTickVisible(false);
        xBarAppraiserYAxis.setTickMarkVisible(false);
        xBarAppraiserChart = new LinearChart(xBarAppraiserXAxis, xBarAppraiserYAxis);
        xBarAppraiserVBox.getChildren().add(xBarAppraiserChart);
        ChartOperateButton button = new ChartOperateButton(true, com.dmsoft.firefly.plugin.grr.utils.enums.Orientation.BOTTOMLEFT);
        button.setListViewData(Lists.newArrayList(UIConstant.CHART_OPERATE_NAME));
        button.setSelectCallBack((name, selected, selectedNames) -> xBarAppraiserChart.toggleValueMarker(name, selected));
        button.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_lines_normal.png")));
        button.setListViewSize(80, 80);
        button.getStyleClass().add("btn-icon-b");
        xBarAppraiserBp.setMargin(button, new Insets(0, 10, 0, 0));
        xBarAppraiserBp.setRight(button);
    }

    private void initRrByAppraiserChartPane() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickUnit(100);
        xAxis.setMinorTickVisible(false);
        xAxis.setMinorTickVisible(false);
        xAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);
        yAxis.setTickMarkVisible(false);
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return (Double) object % 1.0 == 0 ? String.valueOf(object) : null;
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });
        rrByAppraiserChart = new LineChart(xAxis, yAxis);
        rrByAppraiserChart.setLegendVisible(false);
        rrByAppraiserChart.setVerticalGridLinesVisible(false);
        rrByAppraiserChart.setHorizontalGridLinesVisible(false);
        rrByAppraiserVBox.getChildren().add(rrByAppraiserChart);
        rrByAppraiserVBox.setMargin(rrByAppraiserChart, new Insets(5, -10, -10, 0));
    }

    private void initComponentsRender() {
        final double INPUT_WIDTH = 200;
        itemFilterHBox.setMargin(summaryItemTf, new Insets(4, 0, 4, 0));
        summaryItemTf.getTextField().setPrefWidth(INPUT_WIDTH);
        summaryItemTf.getTextField().setFocusTraversable(false);
        summaryTb.setEditable(true);
        TableRender tableRender = new TableRender(summaryTb);
        tableRender.buildRadioCellByIndex(0, buildRadioCallBack());
        tableRender.buildEditCellByIndex(2, buildEditCallBack());
        tableRender.buildEditCellByIndex(3, buildEditCallBack());
        tableRender.buildSpecialCellByIndex(7, buildGrrCallBack());
        componentChart.setBarGap(20);
        componentChart.setCategoryGap(100);
        xBarAppraiserChart.setLegendVisible(false);
        xBarAppraiserChart.setVerticalGridLinesVisible(false);
        xBarAppraiserChart.setHorizontalGridLinesVisible(false);
        ObservableList<TableColumn<GrrSingleSummary, ?>> summaryTbColumns = summaryTb.getColumns();
        ObservableList<TableColumn<GrrSingleAnova, ?>> anovaTbColumns = anovaTb.getColumns();
        ObservableList<TableColumn<GrrSingleSource, ?>> sourceTbColumns = sourceTb.getColumns();
        summaryTbColumns.get(0).setPrefWidth(30);
        summaryTbColumns.get(1).setPrefWidth(200);
        summaryTbColumns.get(2).setPrefWidth(150);
        summaryTbColumns.get(3).setPrefWidth(115);
        summaryTbColumns.get(4).setPrefWidth(190);
        summaryTbColumns.get(5).setPrefWidth(110);
        summaryTbColumns.get(6).setPrefWidth(162);
        summaryTbColumns.get(6).setPrefWidth(185);
        summaryTbColumns.get(7).setPrefWidth(110);
        anovaTbColumns.get(0).setPrefWidth(183);
        anovaTbColumns.get(1).setPrefWidth(173);
        anovaTbColumns.get(2).setPrefWidth(181);
        anovaTbColumns.get(3).setPrefWidth(172);
        anovaTbColumns.get(4).setPrefWidth(172);
        anovaTbColumns.get(5).setPrefWidth(179);
        sourceTbColumns.get(0).setPrefWidth(183);
        sourceTbColumns.get(1).setPrefWidth(130);
        sourceTbColumns.get(2).setPrefWidth(149);
        sourceTbColumns.get(3).setPrefWidth(150);
        sourceTbColumns.get(4).setPrefWidth(167);
        sourceTbColumns.get(5).setPrefWidth(139);
        sourceTbColumns.get(6).setPrefWidth(139);
    }

    private void initComponentEvents() {
        resultBasedCmb.setOnAction(event -> fireResultBasedCmbEvent());
        summaryItemTf.getTextField().textProperty().addListener(observable -> fireSummaryItemTfEvent());
        grrDataBtn.setOnAction(event -> fireDataBtnEvent());
        grrChartBtn.setOnAction(event -> fireChartBtnEvent());
        grrResultBtn.setOnAction(event -> fireResultBtnEvent());
    }

    private ObservableList buildSummaryTbColumn() {
        ObservableList<TableColumn> tableColumns = FXCollections.observableArrayList();
        TableColumn<GrrSingleSummary, Boolean> selectTcn = new TableColumn("");
        selectTcn.setCellValueFactory(new PropertyValueFactory(GrrSingleSummary.selectedKey));
        tableColumns.add(selectTcn);
        for (String name : UIConstant.GRR_SUMMARY_TITLE) {
            TableColumn<GrrSingleSummary, String> tableColumn = new TableColumn(name);
            tableColumns.add(tableColumn);
            tableColumn.setCellValueFactory(new PropertyValueFactory(GrrSingleSummary.propertyKeys.get(name)));
        }
        return tableColumns;
    }

    private ObservableList buildAnovaTbColumn() {
        ObservableList<TableColumn> tableColumns = FXCollections.observableArrayList();
        for (String name : UIConstant.GRR_ANOVA_TITLE) {
            TableColumn tableColumn = new TableColumn(name);
            tableColumn.setCellValueFactory(new PropertyValueFactory(GrrSingleAnova.propertyKeys.get(name)));
            tableColumns.add(tableColumn);
        }
        return tableColumns;
    }

    private ObservableList buildSourceTbColumn() {
        ObservableList<TableColumn> tableColumns = FXCollections.observableArrayList();
        for (String name : UIConstant.GRR_SOURCE_TITLE) {
            TableColumn tableColumn = new TableColumn(name);
            tableColumn.setCellValueFactory(new PropertyValueFactory(GrrSingleSource.propertyKeys.get(name)));
            tableColumns.add(tableColumn);
        }
        return tableColumns;
    }

    private void fireResultBasedCmbEvent() {
        String type = resultBasedCmb.getSelectionModel().getSelectedItem().toString();
        summaryModel.updateDataByResultType(type);
        summaryTb.refresh();
    }

    private void fireSummaryItemTfEvent() {
        String textValue = summaryItemTf.getTextField().getText();
        filteredList.setPredicate(singleSummary -> singleSummary.getItemName().contains(textValue));
    }

    private void fireDataBtnEvent() {
        grrResultScrollPane.setVvalue(0);
    }

    private void fireChartBtnEvent() {
        ScrollPaneValueUtils.setScrollVerticalValue(grrResultScrollPane, chartVBox);
    }

    private void fireResultBtnEvent() {
        ScrollPaneValueUtils.setScrollVerticalValue(grrResultScrollPane, resultVBox);
    }



    private TableCellCallBack buildEditCallBack() {
        return new TableCellCallBack() {
            @Override
            public void execute(TableCell cell, int columnIndex) {
                int rowIndex = cell.getTableRow().getIndex();
                GrrSingleSummary singleSummary = (GrrSingleSummary) cell.getTableView().getItems().get(rowIndex);
                cell.setStyle("-fx-text-fill: red");
            }
        };
    }

    private TableCellCallBack buildRadioCallBack() {
        return new TableCellCallBack() {
            @Override
            public void execute(TableCell cell) {
                for (Object item : summaryModel.getSummaries()) {
                    GrrSingleSummary summary = (GrrSingleSummary) item;
                    summary.setSelect(false);
                }
                GrrSingleSummary singleSummary = filteredList.get(cell.getIndex());
                singleSummary.setSelect(true);
                summaryTb.refresh();
            }
        };
    }

    private TableCellCallBack buildGrrCallBack() {
        return new TableCellCallBack() {
            @Override
            public void execute(TableCell cell) {
                cell.setStyle("-fx-text-fill: red");
            }
        };
    }
}
