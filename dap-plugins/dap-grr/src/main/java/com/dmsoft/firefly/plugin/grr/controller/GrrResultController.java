package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.plugin.grr.charts.ChartOperateButton;
import com.dmsoft.firefly.plugin.grr.charts.LinearChart;
import com.dmsoft.firefly.plugin.grr.charts.data.RuleLineData;
import com.dmsoft.firefly.plugin.grr.charts.data.VerticalCutLine;
import com.dmsoft.firefly.plugin.grr.charts.data.ILineData;
import com.dmsoft.firefly.plugin.grr.dto.*;
import com.dmsoft.firefly.plugin.grr.dto.analysis.*;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrAnovaAndSourceResultDto;
import com.dmsoft.firefly.plugin.grr.handler.ParamKeys;
import com.dmsoft.firefly.plugin.grr.model.*;
import com.dmsoft.firefly.plugin.grr.utils.*;
import com.dmsoft.firefly.plugin.grr.utils.charts.ChartUtils;
import com.dmsoft.firefly.plugin.grr.utils.charts.LegendUtils;
import com.dmsoft.firefly.plugin.grr.utils.table.EditTableCell;
import com.dmsoft.firefly.plugin.grr.utils.table.TableCellCallBack;
import com.dmsoft.firefly.plugin.grr.utils.table.TableRender;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.Job;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sun.javafx.charts.Legend;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
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
import java.util.*;
import java.util.function.Function;

/**
 * Created by cherry on 2018/3/12.
 */
public class GrrResultController implements Initializable {

    private GrrSummaryModel summaryModel = new GrrSummaryModel();
    private ItemResultModel itemResultModel = new ItemResultModel();
    private GrrAnovaModel anovaModel = new GrrAnovaModel();
    private GrrSourceModel sourceModel = new GrrSourceModel();
    final FilteredList<GrrSingleSummary> filteredList = summaryModel.getSummaries().filtered(p -> true);
    private List<TableCell> editCells = Lists.newArrayList();

    private GrrMainController grrMainController;
    private JobManager manager = RuntimeContext.getBean(JobManager.class);

    private String appKey = GrrFxmlAndLanguageUtils.getString("APPRAISER") + " ";
    private String trailKey = GrrFxmlAndLanguageUtils.getString("TRAIL") + " ";
    private Set<String> parts = Sets.newLinkedHashSet();
    private Set<String> appraisers = Sets.newLinkedHashSet();

    /**
     * Init grr main controller
     *
     * @param grrMainController grr main controller
     */
    public void init(GrrMainController grrMainController) {
        this.grrMainController = grrMainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initComponents();
        this.initComponentsRender();
        this.initComponentEvents();
        this.initData();
    }

    public void analyzeGrrResult(List<GrrSummaryDto> grrSummaryDtos, GrrDetailDto grrDetailDto) {
//        Set digNum
        DigNumInstance.newInstance().setDigNum(grrMainController.getActiveTemplateSettingDto().getDecimalDigit());
        List<GrrViewDataDto> viewDataDtos = grrMainController.getGrrDataFrame().getIncludeDatas();
        parts.clear();
        appraisers.clear();
        viewDataDtos.forEach(viewDataDto -> {
            parts.add(viewDataDto.getPart());
            appraisers.add(viewDataDto.getOperator());
        });
        this.removeAllResultData();
        this.setSummaryData(grrSummaryDtos);
        this.setItemResultData(grrMainController.getGrrDataFrame(),
                grrMainController.getSearchConditionDto(),
                grrSummaryDtos.get(0).getItemName());
        this.setAnalysisItemResultData(grrDetailDto);
        this.setToleranceValue(filteredList.get(0).getTolerance());
    }

    public void changeGrrResult() {
        submitGrrResult(grrMainController.getSearchConditionDto().getSelectedTestItemDtos().get(0).getTestItemName(), 0);
    }

    public void refreshGrrResult() {
        List<TestItemWithTypeDto> changedTestItemWithTypeDtos = summaryModel.getEditTestItem();
        List<TestItemWithTypeDto> selectTestItemWithTypeDtos = grrMainController.getSearchConditionDto().getSelectedTestItemDtos();
        if (changedTestItemWithTypeDtos.isEmpty() || selectTestItemWithTypeDtos.isEmpty()) {
            return;
        }
//        edit select test item with type
        for (int i = 0; i < selectTestItemWithTypeDtos.size(); i++) {
            for (int j = 0; j < changedTestItemWithTypeDtos.size(); j++) {
                if (selectTestItemWithTypeDtos.get(i).getTestItemName().equals(changedTestItemWithTypeDtos.get(j).getTestItemName())) {
                    selectTestItemWithTypeDtos.set(i, changedTestItemWithTypeDtos.get(j));
                }
            }
        }
        String selectedItem = summaryModel.getSelectedItemName();
        boolean hasSelectedItem = false;
        int selectedIndex = -1;
        for (int i = 0; i < selectTestItemWithTypeDtos.size(); i++) {
            if (selectedItem.equals(selectTestItemWithTypeDtos.get(i).getTestItemName())) {
                hasSelectedItem = true;
                selectedIndex = i;
                break;
            }
        }
        String itemName = hasSelectedItem ? selectedItem : "";
        submitGrrResult(itemName, selectedIndex);
    }

    private void submitGrrResult(String selectedItem, int selectedIndex) {
        this.clearCellStyle();
        Job job = new Job(ParamKeys.GRR_REFRESH_JOB_PIPELINE);
        Map paramMap = Maps.newHashMap();
        paramMap.put(ParamKeys.SEARCH_GRR_CONDITION_DTO, grrMainController.getSearchConditionDto());
        paramMap.put(ParamKeys.SEARCH_VIEW_DATA_FRAME, grrMainController.getGrrDataFrame());
        if (DAPStringUtils.isNotBlank(selectedItem)) {
            paramMap.put(ParamKeys.TEST_ITEM_NAME, selectedItem);
        }

        Platform.runLater(() -> manager.doJobASyn(job, returnValue -> {
            try {
                Platform.runLater(() -> {
                    if (returnValue == null) {
                        //todo message tip
                        return;
                    }
                    if (returnValue instanceof Map) {
                        Map<String, Object> value = (Map<String, Object>) returnValue;
                        if (value.containsKey(UIConstant.ANALYSIS_RESULT_SUMMARY)) {
                            List<GrrSummaryDto> summaryDtos = (List<GrrSummaryDto>) value.get(UIConstant.ANALYSIS_RESULT_SUMMARY);
                            summaryModel.setData(summaryDtos, resultBasedCmb.getSelectionModel().getSelectedItem().toString(), selectedIndex);
                            summaryTb.refresh();
                        }
                        if (value.containsKey(UIConstant.ANALYSIS_RESULT_DETAIL)) {
                            GrrDetailDto grrDetailDto = (GrrDetailDto) value.get(UIConstant.ANALYSIS_RESULT_DETAIL);
                            this.removeSubResultData();
                            this.setItemResultData(grrMainController.getGrrDataFrame(),
                                    grrMainController.getSearchConditionDto(),
                                    selectedItem);
                            this.setAnalysisItemResultData(grrDetailDto);
                            this.setToleranceValue(summaryModel.getSummaries().get(selectedIndex).getTolerance());
                        }
                    }
                });
            } catch (ApplicationException exception) {
                exception.printStackTrace();
            }
        }, paramMap, grrMainController));
    }

    public void reset(List<TestItemWithTypeDto> itemWithTypeDtos) {
        this.clearCellStyle();
    }

    private void analyzeGrrSubResult(TestItemWithTypeDto testItemDto) {
        Map detailParamMap = Maps.newHashMap();
        Job detailJob = new Job(ParamKeys.GRR_DETAIL_ANALYSIS_JOB_PIPELINE);
        detailParamMap.put(ParamKeys.SEARCH_GRR_CONDITION_DTO, grrMainController.getSearchConditionDto());
        detailParamMap.put(ParamKeys.SEARCH_VIEW_DATA_FRAME, grrMainController.getGrrDataFrame());
        detailParamMap.put(ParamKeys.TEST_ITEM_WITH_TYPE_DTO_LIST, Lists.newArrayList(testItemDto));
        this.removeSubResultData();
        this.setItemResultData(grrMainController.getGrrDataFrame(),
                grrMainController.getSearchConditionDto(),
                testItemDto.getTestItemName());

        Platform.runLater(() -> manager.doJobASyn(detailJob, returnValue -> {
            if (returnValue == null) {
                //todo message tip
                return;
            }
            Platform.runLater(() -> setAnalysisItemResultData((GrrDetailDto) returnValue));
        }, detailParamMap, grrMainController));
    }

    private void setSummaryData(List<GrrSummaryDto> summaryData) {
        summaryModel.clearEditTestItem();
        summaryModel.setSelectedItemName(summaryData.get(0).getItemName());
        summaryModel.setData(summaryData, resultBasedCmb.getSelectionModel().getSelectedItem().toString(), 0);
    }

    private void setItemResultData(GrrDataFrameDto grrDataFrameDto, SearchConditionDto conditionDto, String itemName) {

        GrrItemResultDto itemResultDto = DataConvertUtils.convertToItemResult(grrDataFrameDto, itemName);
        Set<String> headerArray = Sets.newLinkedHashSet();
        headerArray.add(appKey);
        headerArray.add(trailKey);
        headerArray.addAll(parts);
        List<String> rowKeyArray = buildItemTbRowKey(conditionDto.getAppraiserInt(),
                conditionDto.getTrialInt(),
                Lists.newArrayList(appraisers));
        itemResultModel.setRowKeyArray(FXCollections.observableArrayList(rowKeyArray));
        itemResultModel.setHeaderArray(FXCollections.observableArrayList(headerArray));
        itemResultModel.setData(grrDataFrameDto.getDataFrame(),
                itemName,
                grrDataFrameDto.getIncludeDatas(),
                itemResultDto);
        TableViewWrapper.decorate(itemDetailTb, itemResultModel);
    }

    private List<String> buildItemTbRowKey(int appraiser, int trial, List<String> appraisers) {
        int rowCount = appraiser * (trial + 2) + 2;
        List<String> rowKeys = new ArrayList<>(rowCount);
        int trialIndex = 0;
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            int reminder = rowIndex % (trial + 2);
            int value = rowIndex / (trial + 2);
//            total mean
            if (rowIndex == rowCount - 2) {
                rowKeys.add(rowIndex, UIConstant.TOTAL_MEAN);
                trialIndex = 0;
                continue;
            }
//            total range
            if (rowIndex == rowCount - 1) {
                rowKeys.add(rowIndex, UIConstant.TOTAL_RANGE);
                trialIndex = 0;
                continue;
            }
//            mean
            if (reminder == trial) {
                rowKeys.add(rowIndex, appraisers.get(value) + UIConstant.SPLIT_FLAG + UIConstant.MEAN);
                trialIndex = 0;
                continue;
            }
//            range
            if (reminder == trial + 1) {
                rowKeys.add(rowIndex, appraisers.get(value) + UIConstant.SPLIT_FLAG + UIConstant.RANGE);
                trialIndex = 0;
                continue;
            }
            trialIndex++;
            rowKeys.add(rowIndex, appraisers.get(value) + UIConstant.SPLIT_FLAG + trialIndex);
        }
        return rowKeys;
    }

    private void setAnalysisItemResultData(GrrDetailDto grrDetailDto) {
        setComponentChart(grrDetailDto.getGrrDetailResultDto().getComponentChartDto());
        setPartAppraiserChart(grrDetailDto.getGrrDetailResultDto().getPartAppraiserChartDto(),
                Lists.newArrayList(parts), Lists.newArrayList(appraisers));
        setControlChartData(grrDetailDto.getGrrDetailResultDto().getXbarAppraiserChartDto(),
                xBarAppraiserBp,
                xBarAppraiserChart,
                xBarAppraiserChartBtn,
                Lists.newArrayList(parts),
                Lists.newArrayList(appraisers));
        setControlChartData(grrDetailDto.getGrrDetailResultDto().getRangeAppraiserChartDto(),
                rangeAppraiserBp,
                rangeAppraiserChart,
                rangeAppraiserChartBtn,
                Lists.newArrayList(parts),
                Lists.newArrayList(appraisers));
        setScatterChartData(grrDetailDto.getGrrDetailResultDto().getRrbyAppraiserChartDto(), rrByAppraiserChart);
        setScatterChartData(grrDetailDto.getGrrDetailResultDto().getRrbyPartChartDto(), rrbyPartChart);
        setAnovaAndSourceTb(grrDetailDto.getGrrDetailResultDto().getAnovaAndSourceResultDto());
    }

    private void removeAllResultData() {
        summaryModel.setData(null, null, 0);
        this.removeSubResultData();
    }

    private void setComponentChart(GrrComponentCResultDto componentCResult) {
        XYChart.Series series1 = new XYChart.Series();
        XYChart.Series series2 = new XYChart.Series();
        XYChart.Series series3 = new XYChart.Series();
        series1.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[0],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getGrrContri()) ? 0 : componentCResult.getGrrContri()));
        series1.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[1],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getRepeatContri()) ? 0 : componentCResult.getRepeatContri()));
        series1.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[2],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getReprodContri()) ? 0 : componentCResult.getReprodContri()));
        series1.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[3],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getPartContri()) ? 0 : componentCResult.getPartContri()));
        series2.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[0],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getGrrVar()) ? 0 : componentCResult.getGrrVar()));
        series2.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[1],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getRepeatVar()) ? 0 : componentCResult.getRepeatVar()));
        series2.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[2],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getReprodVar()) ? 0 : componentCResult.getReprodVar()));
        series2.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[3],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getPartVar()) ? 0 : componentCResult.getPartVar()));
        series3.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[0],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getGrrTol()) ? 0 : componentCResult.getGrrTol()));
        series3.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[1],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getRepeatVar()) ? 0 : componentCResult.getRepeatVar()));
        series3.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[2],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getReprodVar()) ? 0 : componentCResult.getReprodVar()));
        series3.getData().add(new XYChart.Data<>(UIConstant.CHART_COMPONENT_LABEL[3],
                DAPStringUtils.isInfinityAndNaN(componentCResult.getPartVar()) ? 0 : componentCResult.getPartVar()));
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
        int digNum = DigNumInstance.newInstance().getDigNum() - 2;
        componentBp.setMargin(legend, new Insets(0, 0, 1, 0));

        //Chart text format
        ChartUtils.setChartText(componentChart.getData(), s -> {
            if (DAPStringUtils.isNumeric(s)) {
                Double value = Double.valueOf(s);
                if (!DAPStringUtils.isInfinityAndNaN(value)) {
                    return DAPStringUtils.formatDouble(value, digNum) + "%";
                }
            }
            return s + "%";
        });
    }

    private void setPartAppraiserChart(GrrPACResultDto partAppraiserChartDto,
                                       List<String> parts,
                                       List<String> appraisers) {
        double[][] data = partAppraiserChartDto.getDatas();
        double max = MathUtils.getMax(data);
        double min = MathUtils.getMin(data);
        NumberAxis yAxis = (NumberAxis) partAppraiserChart.getYAxis();
        final double factor = 0.20;
        double reserve = (max - min) * factor;
        yAxis.setAutoRanging(false);
        yAxis.setUpperBound(max + reserve);
        yAxis.setLowerBound(min - reserve);
        ObservableList<XYChart.Series> seriesData = FXCollections.observableArrayList();
        for (int i = 0; i < data.length; i++) {
            XYChart.Series series = new XYChart.Series();
            series.setName(appraisers.get(i));
            double[] appraiser = data[i];
            for (int j = 0; j < appraiser.length; j++) {
                if (!DAPStringUtils.isInfinityAndNaN(appraiser[j])) {
                    series.getData().add(new XYChart.Data<>(parts.get(j), appraiser[j], appraisers.get(i)));
                }
            }
            seriesData.add(series);
        }
        partAppraiserChart.getData().addAll(seriesData);

        Legend legend = LegendUtils.buildLegend(partAppraiserChart.getData(),
                "chart-line-symbol", "line-legend-symbol");
        ChartUtils.setChartToolTip(partAppraiserChart.getData(), pointTooltip -> {
            Double value = (Double) pointTooltip.getData().getYValue();
            int digNum = DigNumInstance.newInstance().getDigNum();
            return pointTooltip == null ? "" :
                    "(" + pointTooltip.getData().getExtraValue() + "," +
                            pointTooltip.getData().getXValue() + ")" + "=" + DAPStringUtils.formatDouble(value, digNum);
        });
        partAppraiserBp.setLeft(legend);
        partAppraiserBp.setMargin(legend, new Insets(0, 0, 1, 0));
    }

    private void setControlChartData(GrrControlChartDto chartData,
                                     BorderPane borderPane,
                                     LinearChart chart,
                                     ChartOperateButton button,
                                     List<String> parts,
                                     List<String> appraisers) {

        int partCount = parts.size();
        Double[] x = chartData.getX();
        Double[] y = chartData.getY();
        Double[] ruleData = new Double[]{chartData.getUcl(), chartData.getCl(), chartData.getLcl()};
        double max = MathUtils.getMax(y, ruleData);
        double min = MathUtils.getMin(y, ruleData);
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        final double factor = 0.20;
        double reserve = (max - min) * factor;
        yAxis.setAutoRanging(false);
        yAxis.setUpperBound(max + reserve);
        yAxis.setLowerBound(min - reserve);
        List<ILineData> horizonalLineData = Lists.newArrayList();
        List<ILineData> verticalLineData = Lists.newArrayList();
        XYChart.Series series = new XYChart.Series();
//        draw vertical line
        for (int i = 0; i < x.length; i++) {
            if ((i + 1) % partCount == 0 && i != x.length - 1) {
                double value = (x[i] + x[i + 1]) / 2;
                verticalLineData.add(new VerticalCutLine(value));
            }
            series.getData().add(new XYChart.Data<>(x[i], y[i], parts.get(i % partCount)));
        }

        RuleLineData uclLineData = new RuleLineData(UIConstant.CHART_OPERATE_NAME[0], chartData.getUcl());
        RuleLineData clLineData = new RuleLineData(UIConstant.CHART_OPERATE_NAME[1], chartData.getCl());
        RuleLineData lclLineData = new RuleLineData(UIConstant.CHART_OPERATE_NAME[2], chartData.getLcl());
        uclLineData.setColor(Color.rgb(102, 102, 102));
        lclLineData.setColor(Color.rgb(178, 178, 178));
        uclLineData.setLineClass("dashed2-line");
        clLineData.setLineClass("solid-line");
        lclLineData.setLineClass("dashed1-line");
        horizonalLineData.add(uclLineData);
        horizonalLineData.add(clLineData);
        horizonalLineData.add(lclLineData);
        int digNum = DigNumInstance.newInstance().getDigNum();
        chart.getData().add(series);
        button.setDisable(false);

        chart.buildValueMarkerWithoutTooltip(verticalLineData);
        chart.buildValueMarkerWithTooltip(horizonalLineData, new Function<ILineData, String>() {
            @Override
            public String apply(ILineData oneLineData) {
                return oneLineData.getTitle() + "\n" + oneLineData.getName() + "="
                        + DAPStringUtils.formatDouble(oneLineData.getValue(), digNum);
            }
        });
        ChartUtils.setChartToolTip(chart.getData(), pointTooltip -> {
            Double value = (Double) pointTooltip.getData().getYValue();
            return pointTooltip == null ? "" :
                    "(" + pointTooltip.getData().getExtraValue() + "," +
                            pointTooltip.getData().getXValue() + ")" + "=" + DAPStringUtils.formatDouble(value, digNum);
        });

        Legend legend = LegendUtils.buildLegend(chart.getData(),
                "chart-line-symbol", "line-legend-symbol");
        borderPane.setLeft(legend);
        borderPane.setMargin(legend, new Insets(0, 0, 1, 0));
    }

    private void setScatterChartData(GrrScatterChartDto scatterChartData, LineChart chart) {

        Double[] x = scatterChartData.getX();
        Double[] y = scatterChartData.getY();
        Double[] clX = scatterChartData.getClX();
        Double[] clY = scatterChartData.getClY();
        double max = MathUtils.getMax(y, clY);
        double min = MathUtils.getMin(y, clY);
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        final double factor = 0.20;
        double reserve = (max - min) * factor;
        yAxis.setAutoRanging(false);
        yAxis.setUpperBound(max + reserve);
        yAxis.setLowerBound(min - reserve);
        XYChart.Series scatterSeries = new XYChart.Series();
        XYChart.Series lineSeries = new XYChart.Series();
        for (int i = 0; i < x.length; i++) {
            scatterSeries.getData().add(new XYChart.Data<>(x[i], y[i]));
        }
        for (int i = 0; i < clX.length; i++) {
            lineSeries.getData().add(new XYChart.Data<>(clX[i], clY[i]));
        }
        chart.getData().addAll(scatterSeries, lineSeries);
        ChartUtils.setChartToolTip(chart.getData(), pointTooltip -> {
            Double value = (Double) pointTooltip.getData().getYValue();
            int digNum = DigNumInstance.newInstance().getDigNum();
            return pointTooltip == null ? "" :
                    "(" + DAPStringUtils.formatDouble(value, digNum) + ")";
        });
        scatterSeries.getNode().getStyleClass().add("chart-series-hidden-line");
    }

    private void setAnovaAndSourceTb(GrrAnovaAndSourceResultDto anovaAndSourceResultDto) {
        int digNum = DigNumInstance.newInstance().getDigNum();
        String numberOfDc = digNum >= 0 ? DAPStringUtils.formatDouble(anovaAndSourceResultDto.getNumberOfDc(), digNum)
                : String.valueOf(anovaAndSourceResultDto.getNumberOfDc());
        anovaModel.setData(anovaAndSourceResultDto.getGrrAnovaDtos());
        sourceModel.setData(anovaAndSourceResultDto.getGrrSourceDtos());
        categoryBtn.setText(DAPStringUtils.isBlankWithSpecialNumber(numberOfDc) ? "-" : numberOfDc);
    }

    private void removeSubResultData() {
        xBarAppraiserChart.clear();
        componentChart.getData().setAll(FXCollections.observableArrayList());
        partAppraiserChart.getData().setAll(FXCollections.observableArrayList());
        xBarAppraiserChart.getData().setAll(FXCollections.observableArrayList());
        rrByAppraiserChart.getData().setAll(FXCollections.observableArrayList());
        componentBp.getChildren().remove(componentBp.getLeft());
        partAppraiserBp.getChildren().remove(partAppraiserBp.getLeft());
        xBarAppraiserBp.getChildren().remove(xBarAppraiserBp.getLeft());
        rangeAppraiserBp.getChildren().remove(rangeAppraiserBp.getLeft());
        rrbyAppraiserBp.getChildren().remove(rrbyAppraiserBp.getLeft());
        rrbyPartBp.getChildren().remove(rrbyPartBp.getLeft());
        anovaModel.setData(null);
        sourceModel.setData(null);
    }

    private void initComponents() {
        summaryItemTf = new TextFieldFilter();
        summaryItemTf.getTextField().setPromptText("Test Item");
        itemFilterHBox.getChildren().setAll(summaryItemTf);
        resultBasedCmb.getItems().addAll(UIConstant.GRR_RESULT_TYPE);
        resultBasedCmb.setValue(UIConstant.GRR_RESULT_TYPE[0]);
        summaryTb.getColumns().addAll(buildSummaryTbColumn());
        anovaTb.getColumns().addAll(buildAnovaTbColumn());
        sourceTb.getColumns().addAll(buildSourceTbColumn());

        xBarAppraiserChart = buildControlChart();
        xBarAppraiserVBox.getChildren().add(xBarAppraiserChart);
        xBarAppraiserChartBtn = new ChartOperateButton(true,
                com.dmsoft.firefly.plugin.grr.utils.enums.Orientation.BOTTOMLEFT);
        xBarAppraiserBp.setRight(xBarAppraiserChartBtn);

        rangeAppraiserChart = buildControlChart();
        rangeAppraiserVBox.getChildren().add(rangeAppraiserChart);
        rangeAppraiserChartBtn = new ChartOperateButton(true,
                com.dmsoft.firefly.plugin.grr.utils.enums.Orientation.BOTTOMLEFT);
        rangeAppraiserBp.setRight(rangeAppraiserChartBtn);

        rrByAppraiserChart = buildScatterChart();
        rrByAppraiserVBox.getChildren().add(rrByAppraiserChart);

        rrbyPartChart = buildScatterChart();
        rrbyPartVBox.getChildren().add(rrbyPartChart);
    }

    private LinearChart buildControlChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return (Double) object % ((+parts.size()) / 2) == 0 ? String.valueOf(object) : null;
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });
        xAxis.setMinorTickVisible(false);
        xAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setAutoRanging(false);
        LinearChart chart = new LinearChart(xAxis, yAxis);
        return chart;
    }

    private LineChart buildScatterChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
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
        LineChart chart = new LineChart(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setHorizontalGridLinesVisible(false);
        return chart;
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
//        componentChart.setBarGap(10);
//        componentChart.setCategoryGap(50);
        xBarAppraiserChart.setLegendVisible(false);
        xBarAppraiserChart.setVerticalGridLinesVisible(false);
        xBarAppraiserChart.setHorizontalGridLinesVisible(false);
        rangeAppraiserChart.setVerticalGridLinesVisible(false);
        rangeAppraiserChart.setHorizontalGridLinesVisible(false);
        ObservableList<TableColumn<GrrSingleSummary, ?>> summaryTbColumns = summaryTb.getColumns();
        ObservableList<TableColumn<GrrSingleAnova, ?>> anovaTbColumns = anovaTb.getColumns();
        ObservableList<TableColumn<GrrSingleSource, ?>> sourceTbColumns = sourceTb.getColumns();
        summaryTbColumns.get(0).setPrefWidth(30);
        summaryTbColumns.get(1).setPrefWidth(180);
        summaryTbColumns.get(2).setPrefWidth(80);
        summaryTbColumns.get(3).setPrefWidth(80);
        summaryTbColumns.get(4).setPrefWidth(100);
        summaryTbColumns.get(5).setPrefWidth(110);
        summaryTbColumns.get(6).setPrefWidth(110);
        summaryTbColumns.get(6).setPrefWidth(120);
        summaryTbColumns.get(7).setPrefWidth(110);
        anovaTbColumns.get(0).setPrefWidth(100);
        anovaTbColumns.get(1).setPrefWidth(140);
        anovaTbColumns.get(2).setPrefWidth(140);
        anovaTbColumns.get(3).setPrefWidth(130);
        anovaTbColumns.get(4).setPrefWidth(130);
        anovaTbColumns.get(5).setPrefWidth(130);
        sourceTbColumns.get(0).setPrefWidth(127);
        sourceTbColumns.get(1).setPrefWidth(80);
        sourceTbColumns.get(2).setPrefWidth(110);
        sourceTbColumns.get(3).setPrefWidth(110);
        sourceTbColumns.get(4).setPrefWidth(120);
        sourceTbColumns.get(5).setPrefWidth(120);
        sourceTbColumns.get(6).setPrefWidth(120);
        xBarAppraiserChartBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_lines_normal.png")));
        rangeAppraiserChartBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_choose_lines_normal.png")));
        xBarAppraiserChartBtn.setListViewSize(80, 80);
        rangeAppraiserChartBtn.setListViewSize(80, 80);
        xBarAppraiserChartBtn.getStyleClass().add("btn-icon-b");
        rangeAppraiserChartBtn.getStyleClass().add("btn-icon-b");
        xBarAppraiserChartBtn.setDisable(true);
        rangeAppraiserChartBtn.setDisable(true);
    }

    private void initComponentEvents() {
        grrDataBtn.setOnAction(event -> fireDataBtnEvent());
        grrChartBtn.setOnAction(event -> fireChartBtnEvent());
        grrResultBtn.setOnAction(event -> fireResultBtnEvent());
        resultBasedCmb.setOnAction(event -> fireResultBasedCmbEvent());
        summaryItemTf.getTextField().textProperty().addListener(observable -> fireSummaryItemTfEvent());
        xBarAppraiserChartBtn.setSelectCallBack((name, selected, selectedNames) ->
                xBarAppraiserChart.toggleValueMarker(name, selected));
        rangeAppraiserChartBtn.setSelectCallBack((name, selected, selectedNames) ->
                rangeAppraiserChart.toggleValueMarker(name, selected));
    }

    private void initData() {
        summaryTb.setItems(filteredList);
        anovaTb.setItems(anovaModel.getAnovas());
        sourceTb.setItems(sourceModel.getSources());
        xBarAppraiserChartBtn.setListViewData(Lists.newArrayList(UIConstant.CHART_OPERATE_NAME));
        rangeAppraiserChartBtn.setListViewData(Lists.newArrayList(UIConstant.CHART_OPERATE_NAME));
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
        summaryTb.refresh();
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
            public boolean execute(TableCell cell, int columnIndex) {
                int rowIndex = cell.getTableRow().getIndex();
                GrrSingleSummary singleSummary = (GrrSingleSummary) cell.getTableView().getItems().get(rowIndex);
                boolean valid = false;
                boolean hasEdit = false;
                if (columnIndex == 2) {
                    valid = validLsl(singleSummary, cell.getText());
                    if (!singleSummary.getOriginalLsl().equals(cell.getText())) {
                        hasEdit = true;
                    }
                }
                if (columnIndex == 3) {
                    valid = validUsl(singleSummary, cell.getText());
                    if (!singleSummary.getOriginalUsl().equals(cell.getText())) {
                        hasEdit = true;
                    }
                }
                if (valid && hasEdit) {
                    TestItemWithTypeDto testItemWithTypeDto = new TestItemWithTypeDto();
                    testItemWithTypeDto.setTestItemName(singleSummary.getItemName());
                    if (columnIndex == 2) {
                        testItemWithTypeDto.setLsl(cell.getText());
                        testItemWithTypeDto.setUsl(singleSummary.getUsl());
                    }

                    if (columnIndex == 3) {
                        testItemWithTypeDto.setLsl(singleSummary.getLsl());
                        testItemWithTypeDto.setUsl(cell.getText());
                    }
                    boolean hasItemName = false;
                    for (int i = 0; i < summaryModel.getEditTestItem().size(); i++) {
                        if (singleSummary.getItemName().equals(summaryModel.getEditTestItem().get(i).getTestItemName())) {
                            summaryModel.getEditTestItem().set(i, testItemWithTypeDto);
                            hasItemName = true;
                        }
                    }
                    if (!hasItemName) {
                        summaryModel.addEditTestItem(testItemWithTypeDto);
                    }
                } else {
                    //输入框变红
                    ((EditTableCell) cell).getTextField().setStyle("-fx-border-color: red; -fx-border-width: 1px");
                    return false;
                }
//                有修改文本变黄
                if (hasEdit) {
                    cell.setStyle("-fx-text-fill: " + ColorUtils.toHexFromFXColor(UIConstant.COLOR_EDIT_CHANGE));
                    editCells.add(cell);
                }
                return true;
            }
        };
    }

    private boolean validUsl(GrrSingleSummary singleSummary, String textValue) {
        boolean valid = DAPStringUtils.isNumeric(textValue);
        valid = valid && Double.valueOf(singleSummary.getLsl()) < Double.valueOf(textValue);
        return valid;
    }

    private boolean validLsl(GrrSingleSummary singleSummary, String textValue) {
        boolean valid = DAPStringUtils.isNumeric(textValue);
        valid = valid && Double.valueOf(textValue) < Double.valueOf(singleSummary.getUsl());
        return valid;
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
                summaryModel.setSelectedItemName(singleSummary.getItemName());
                summaryTb.refresh();
                setToleranceValue(singleSummary.getTolerance());
                String itemName = singleSummary.getItemName();
                grrMainController.getSearchConditionDto().getSelectedTestItemDtos().forEach(testItemWithTypeDto -> {
                    if (itemName.equals(testItemWithTypeDto.getTestItemName())) {
                        analyzeGrrSubResult(testItemWithTypeDto);
                    }
                });
            }
        };
    }

    private TableCellCallBack buildGrrCallBack() {
        return new TableCellCallBack() {
            @Override
            public void execute(TableCell cell, Object item) {
                if (item == null && !(item instanceof String)) {
                    return;
                }
                String grrStr = (String) item;
                grrStr = grrStr.substring(0, grrStr.length() - 1);
                if (DAPStringUtils.isBlankWithSpecialNumber(grrStr)) {
                    cell.setStyle("-fx-background-color: " + ColorUtils.toHexFromFXColor(UIConstant.COLOR_EXCELLENT));
                } else {
                    double grr = Double.valueOf(grrStr);
                    List<Double> rules = grrMainController.getGrrConfigDto().getAlarmSetting();
                    int size = rules.size();
                    if (size >= 1 && grr <= rules.get(0)) {
                        cell.setStyle("-fx-background-color: " + ColorUtils.toHexFromFXColor(UIConstant.COLOR_EXCELLENT));
                    } else if (size >= 2 && grr > rules.get(0) && grr < rules.get(1)) {
                        cell.setStyle("-fx-background-color: " + ColorUtils.toHexFromFXColor(UIConstant.COLOR_GOOD));
                    } else if (size >= 3 && grr >= rules.get(1) && grr < rules.get(2)) {
                        cell.setStyle("-fx-background-color: " + ColorUtils.toHexFromFXColor(UIConstant.COLOR_ACCEPTABLE));
                    } else {
                        cell.setStyle("-fx-background-color: " + ColorUtils.toHexFromFXColor(UIConstant.COLOR_RECTIFICATION));
                    }

                }
            }
        };
    }

    private void clearCellStyle() {
        editCells.forEach(cell -> cell.setStyle("-fx-text-fill: " + ColorUtils.toHexFromFXColor(UIConstant.COLOR_EDIT_RESET)));
    }

    public void setToleranceValue(String toleranceText) {
        this.toleranceLbl.setText(toleranceText);
    }

    /****** Summary *****/
    @FXML
    private HBox itemFilterHBox;
    @FXML
    private ComboBox resultBasedCmb;
    @FXML
    private TableView<GrrSingleSummary> summaryTb;
    @FXML
    private TableView itemDetailTb;
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
    private TextFieldFilter summaryItemTf;
    /****** Chart ******/
    @FXML
    private VBox xBarAppraiserVBox;
    @FXML
    private VBox rangeAppraiserVBox;
    @FXML
    private VBox rrByAppraiserVBox;
    @FXML
    private VBox rrbyPartVBox;
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
    private LinearChart rangeAppraiserChart;
    private LineChart rrByAppraiserChart;
    private LineChart rrbyPartChart;
    private ChartOperateButton xBarAppraiserChartBtn;
    private ChartOperateButton rangeAppraiserChartBtn;
    @FXML
    private Label toleranceLbl;
}
