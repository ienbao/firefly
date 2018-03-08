package com.dmsoft.firefly.plugin.spc.service.impl;

import com.dmsoft.firefly.plugin.spc.SpcPlugin;
import com.dmsoft.firefly.plugin.spc.dto.SpcAnalysisConfigDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.*;
import com.dmsoft.firefly.plugin.spc.service.SpcAnalysisService;
import com.dmsoft.firefly.plugin.spc.utils.REnConnector;
import com.dmsoft.firefly.plugin.spc.utils.RUtils;
import com.dmsoft.firefly.plugin.spc.utils.SpcChartType;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.plugin.apis.IAnalysis;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.Analysis;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * impl class for spc analysis
 *
 * @author Can Guan
 */
@Analysis
public class SpcAnalysisServiceImpl implements SpcAnalysisService, IAnalysis {

    @Override
    public SpcStatsResultDto analyzeStatsResult(SpcAnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        REnConnector connector = prepareConnect(dataDto.getDataList(), configDto);
        SpcStatsResultDto resultDto = new SpcStatsResultDto();
        if (DAPStringUtils.isNumeric(dataDto.getLsl())) {
            connector.setInput("lsl", Double.valueOf(dataDto.getLsl()));
            resultDto.setLsl(Double.valueOf(dataDto.getLsl()));
        }
        if (DAPStringUtils.isNumeric(dataDto.getUsl())) {
            connector.setInput("usl", Double.valueOf(dataDto.getUsl()));
            resultDto.setUsl(Double.valueOf(dataDto.getUsl()));
        }
        connector.execEval("DSResult <- intspc.normal.getDSResult(x)");

        double samples = connector.getOutputInt("DSResult$Samples");
        if (!Double.isNaN(samples)) {
            resultDto.setSamples(samples);
        }
        double mean = connector.getOutputDouble("DSResult$Mean");
        if (!Double.isNaN(mean)) {
            resultDto.setAvg(mean);
        }
        double sd = connector.getOutputDouble("DSResult$SD");
        if (!Double.isNaN(sd)) {
            resultDto.setStDev(sd);
        }
        double max = connector.getOutputDouble("DSResult$Max");
        if (!Double.isNaN(max)) {
            resultDto.setMax(max);
        }
        double min = connector.getOutputDouble("DSResult$Min");
        if (!Double.isNaN(min)) {
            resultDto.setMin(min);
        }
        double range = connector.getOutputDouble("DSResult$Range");
        if (!Double.isNaN(range)) {
            resultDto.setRange(range);
        }
        double plus3sd = connector.getOutputDouble("DSResult$Plus3SD");
        if (!Double.isNaN(plus3sd)) {
            resultDto.setUcl(plus3sd);
        }
        double min3sd = connector.getOutputDouble("DSResult$Minus3SD");
        if (!Double.isNaN(min3sd)) {
            resultDto.setLcl(min3sd);
        }
        double kurtosis = connector.getOutputDouble("DSResult$Kurtosis");
        if (!Double.isNaN(kurtosis)) {
            resultDto.setKurtosis(kurtosis);
        }
        double skewness = connector.getOutputDouble("DSResult$Skewness");
        if (!Double.isNaN(skewness)) {
            resultDto.setSkewness(skewness);
        }

        connector.execEval("PPResult <- intspc.normal.getPPResult(x, usl = usl, lsl = lsl, SD = " + sd + ", Mean = " + mean + ")");
        double pp = connector.getOutputDouble("PPResult$Pp");
        if (!Double.isNaN(pp)) {
            resultDto.setPp(pp);
        }
        double ppl = connector.getOutputDouble("PPResult$Ppl");
        if (!Double.isNaN(ppl)) {
            resultDto.setPpl(ppl);
        }
        double ppu = connector.getOutputDouble("PPResult$Ppu");
        if (!Double.isNaN(ppu)) {
            resultDto.setPpu(ppu);
        }
        double ppk = connector.getOutputDouble("PPResult$Ppk");
        if (!Double.isNaN(ppk)) {
            resultDto.setPpk(ppk);
        }
        double oPPM = connector.getOutputDouble("PPResult$OverallPPM");
        if (!Double.isNaN(oPPM)) {
            resultDto.setOverallPPM(oPPM);
        }

        connector.execEval("PCResult <- intspc.normal.getPCResult(x, SubGroupSize = subgroupSize, usl = usl, lsl = lsl)");
        double ca = connector.getOutputDouble("PCResult$Ca");
        if (!Double.isNaN(ca)) {
            resultDto.setCa(ca);
        }
        double cp = connector.getOutputDouble("PCResult$Cp");
        if (!Double.isNaN(cp)) {
            resultDto.setCp(cp);
        }
        double cpl = connector.getOutputDouble("PCResult$Cpl");
        if (!Double.isNaN(cpl)) {
            resultDto.setCpl(cpl);
        }
        double cpu = connector.getOutputDouble("PCResult$Cpu");
        if (!Double.isNaN(cpu)) {
            resultDto.setCpu(cpu);
        }
        double cpk = connector.getOutputDouble("PCResult$Cpk");
        if (!Double.isNaN(cpk)) {
            resultDto.setCpk(cpk);
        }
        double wPPM = connector.getOutputDouble("PCResult$WithinPPM");
        if (!Double.isNaN(wPPM)) {
            resultDto.setWithinPPM(wPPM);
        }
        while (connector.isActive()) {
            connector.disconnect();
        }
        RUtils.getSemaphore().release();
        return resultDto;
    }

    @Override
    public NDCResultDto analyzeNDCResult(SpcAnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        REnConnector connector = prepareConnect(dataDto.getDataList(), configDto);
        if (dataDto.getNdcMax() != null) {
            connector.setInput("ndcMax", dataDto.getNdcMax());
        } else {
            connector.setInput("ndcMax", Double.NaN);
        }
        if (dataDto.getNdcMin() != null) {
            connector.setInput("ndcMin", dataDto.getNdcMin());
        } else {
            connector.setInput("ndcMin", Double.NaN);
        }
        NDCResultDto resultDto = getNDCResult(connector, dataDto);
        while (connector.isActive()) {
            connector.disconnect();
        }
        RUtils.getSemaphore().release();
        return resultDto;

    }

    @Override
    public RunCResultDto analyzeRunCResult(SpcAnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        REnConnector connector = prepareConnect(dataDto.getDataList(), configDto);
        RunCResultDto resultDto = getRunCResult(connector, dataDto);
        while (connector.isActive()) {
            connector.disconnect();
        }
        RUtils.getSemaphore().release();
        return resultDto;
    }

    @Override
    public SpcControlChartDto analyzeXbarCResult(SpcAnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        REnConnector connector = prepareConnect(dataDto.getDataList(), configDto);
        SpcControlChartDto result = getXbarCResult(connector);
        while (connector.isActive()) {
            connector.disconnect();
        }
        RUtils.getSemaphore().release();
        return result;
    }

    @Override
    public SpcControlChartDto analyzeRangeCResult(SpcAnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        REnConnector connector = prepareConnect(dataDto.getDataList(), configDto);
        SpcControlChartDto result = getRangeCResult(connector);
        while (connector.isActive()) {
            connector.disconnect();
        }
        RUtils.getSemaphore().release();
        return result;
    }

    @Override
    public SpcControlChartDto analyzeSdCResult(SpcAnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        REnConnector connector = prepareConnect(dataDto.getDataList(), configDto);
        SpcControlChartDto result = getSdCResult(connector);
        while (connector.isActive()) {
            connector.disconnect();
        }
        RUtils.getSemaphore().release();
        return result;
    }

    @Override
    public SpcControlChartDto analyzeMeCResult(SpcAnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        REnConnector connector = prepareConnect(dataDto.getDataList(), configDto);
        SpcControlChartDto result = getMeCResult(connector);
        while (connector.isActive()) {
            connector.disconnect();
        }
        RUtils.getSemaphore().release();
        return result;
    }

    @Override
    public SpcControlChartDto analyzeMrCResult(SpcAnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        REnConnector connector = prepareConnect(dataDto.getDataList(), configDto);
        SpcControlChartDto result = getMrCResult(connector);
        while (connector.isActive()) {
            connector.disconnect();
        }
        RUtils.getSemaphore().release();
        return result;
    }

    @Override
    public BoxCResultDto analyzeBoxCResult(SpcAnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        REnConnector connector = prepareConnect(dataDto.getDataList(), configDto);
        BoxCResultDto result = getBoxCResult(connector);
        while (connector.isActive()) {
            connector.disconnect();
        }
        RUtils.getSemaphore().release();
        return result;
    }

    @Override
    public SpcChartResultDto analyzeSpcChartResult(SpcAnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        SpcChartResultDto result = new SpcChartResultDto();
        REnConnector connector = prepareConnect(dataDto.getDataList(), configDto);
        result.setNdcResult(getNDCResult(connector, dataDto));
        result.setRunCResult(getRunCResult(connector, dataDto));
        result.setXbarCResult(getXbarCResult(connector));
        result.setRangeCResult(getRangeCResult(connector));
        result.setSdCResult(getSdCResult(connector));
        result.setMedianCResult(getMeCResult(connector));
        result.setBoxCResult(getBoxCResult(connector));
        result.setMrCResult(getMrCResult(connector));
        while (connector.isActive()) {
            connector.disconnect();
        }
        RUtils.getSemaphore().release();
        return result;
    }

    @Override
    public SpcChartResultDto analyzeSpcChartResult(SpcAnalysisDataDto dataDto, List<SpcChartType> requiredCharts, SpcAnalysisConfigDto configDto) {
        SpcChartResultDto result = new SpcChartResultDto();
        REnConnector connector = prepareConnect(dataDto.getDataList(), configDto);
        for (SpcChartType chartType : requiredCharts) {
            switch (chartType) {
                case NDC:
                    result.setNdcResult(getNDCResult(connector, dataDto));
                    break;
                case RunC:
                    result.setRunCResult(getRunCResult(connector, dataDto));
                    break;
                case XbarC:
                    result.setXbarCResult(getXbarCResult(connector));
                    break;
                case SDC:
                    result.setSdCResult(getSdCResult(connector));
                    break;
                case RangeC:
                    result.setRangeCResult(getRangeCResult(connector));
                    break;
                case MeC:
                    result.setMedianCResult(getMeCResult(connector));
                    break;
                case MRC:
                    result.setMrCResult(getMrCResult(connector));
                    break;
                case BoxC:
                    result.setBoxCResult(getBoxCResult(connector));
                    break;
                default:
                    break;
            }
        }
        while (connector.isActive()) {
            connector.disconnect();
        }
        RUtils.getSemaphore().release();
        return result;
    }

    private REnConnector prepareConnect(List<Double> dataList, SpcAnalysisConfigDto configDto) {
        REnConnector connector = RUtils.getInstance().getConnector();
        connector.connect();
        connector.execEval("rm(list=ls(all=TRUE))");
        String spcPathName = "rscripts/spc.R";
        String scriptPath = RuntimeContext.getBean(PluginContext.class).getEnabledPluginInfo(SpcPlugin.SPC_PLUGIN_NAME).getFolderPath() + "/" + spcPathName;
        connector.execEval("source(\"" + scriptPath + "\")");
        double[] dataArray = new double[dataList.size()];
        for (int i = 0; i < dataList.size(); i++) {
            dataArray[i] = dataList.get(i);
        }
        connector.setInput("x", dataArray);
        connector.setInput("subgroupSize", configDto.getSubgroupSize());
        connector.setInput("intervalNum", configDto.getIntervalNumber());
        return connector;
    }

    private NDCResultDto getNDCResult(REnConnector connector, SpcAnalysisDataDto dataDto) {
        connector.execEval("NDCResult <- intspc.normal.getNDCResult(x, intervalNum, Max = ndcMax, Min = ndcMin)");
        NDCResultDto result = new NDCResultDto();
        double[] ndx = connector.getOutputDoubleArray("NDCResult$Breaks");
        double[] ndy = connector.getOutputDoubleArray("NDCResult$Counts");
        double[] ndcls = connector.getOutputDoubleArray("NDCResult$CLs");
        double[] ndccx = connector.getOutputDoubleArray("NDCResult$Normal.X");
        double[] ndccy = connector.getOutputDoubleArray("NDCResult$Normal.Y");
        result.setCls(convert(ndcls));
        result.setCurveX(convert(ndccx));
        result.setCurveY(convert(ndccy));
        result.setHistX(convert(ndx));
        result.setHistY(convert(ndy));
        if (DAPStringUtils.isNumeric(dataDto.getLsl())) {
            result.setLsl(Double.valueOf(dataDto.getLsl()));
        }
        if (DAPStringUtils.isNumeric(dataDto.getUsl())) {
            result.setUsl(Double.valueOf(dataDto.getUsl()));
        }
        return result;
    }

    private RunCResultDto getRunCResult(REnConnector connector, SpcAnalysisDataDto dataDto) {
        connector.execEval("RunCResult <- intspc.normal.getRunCResult(x)");
        RunCResultDto result = new RunCResultDto();
        double[] runy = connector.getOutputDoubleArray("RunCResult$Values");
        double[] runcls = connector.getOutputDoubleArray("RunCResult$CLs");
        result.setY(convert(runy));
        result.setX(generate(runy.length));
        result.setCls(convert(runcls));
        if (DAPStringUtils.isNumeric(dataDto.getLsl())) {
            result.setLsl(Double.valueOf(dataDto.getLsl()));
        }
        if (DAPStringUtils.isNumeric(dataDto.getUsl())) {
            result.setUsl(Double.valueOf(dataDto.getUsl()));
        }
        return result;
    }

    private SpcControlChartDto getXbarCResult(REnConnector connector) {
        connector.execEval("XBCResult <- intspc.normal.getXBCResult(x, subgroupSize)");
        SpcControlChartDto result = new SpcControlChartDto();
        double[] xbary = connector.getOutputDoubleArray("XBCResult$Values");
        double xbarcl = connector.getOutputDouble("XBCResult$CenterLine");
        double[] xbarucl = connector.getOutputDoubleArray("XBCResult$UCL");
        double[] xbarlcl = connector.getOutputDoubleArray("XBCResult$LCL");
        result.setY(convert(xbary));
        result.setX(generate(xbary.length));
        result.setCl(xbarcl);
        result.setUcl(convert(xbarucl));
        result.setLcl(convert(xbarlcl));
        return result;
    }

    private SpcControlChartDto getRangeCResult(REnConnector connector) {
        connector.execEval("RangeCResult <- intspc.normal.getRangeCResult(x, subgroupSize)");
        SpcControlChartDto result = new SpcControlChartDto();
        double[] rangey = connector.getOutputDoubleArray("RangeCResult$Values");
        double rangecl = connector.getOutputDouble("RangeCResult$CenterLine");
        double[] rangeucl = connector.getOutputDoubleArray("RangeCResult$UCL");
        double[] rangelcl = connector.getOutputDoubleArray("RangeCResult$LCL");
        result.setY(convert(rangey));
        result.setX(generate(rangey.length));
        result.setCl(rangecl);
        result.setUcl(convert(rangeucl));
        result.setLcl(convert(rangelcl));
        return result;
    }

    private SpcControlChartDto getSdCResult(REnConnector connector) {
        connector.execEval("SDCResult <- intspc.normal.getSDCResult(x, subgroupSize)");
        SpcControlChartDto result = new SpcControlChartDto();
        double[] sdy = connector.getOutputDoubleArray("SDCResult$Values");
        double sdcl = connector.getOutputDouble("SDCResult$CenterLine");
        double[] sducl = connector.getOutputDoubleArray("SDCResult$UCL");
        double[] sdlcl = connector.getOutputDoubleArray("SDCResult$LCL");
        result.setY(convert(sdy));
        result.setX(generate(sdy.length));
        result.setLcl(convert(sdlcl));
        result.setUcl(convert(sducl));
        result.setCl(sdcl);
        return result;
    }

    private SpcControlChartDto getMeCResult(REnConnector connector) {
        connector.execEval("MECResult <- intspc.normal.getMECResult(x, subgroupSize)");
        SpcControlChartDto result = new SpcControlChartDto();
        double[] mey = connector.getOutputDoubleArray("MECResult$Values");
        double mecl = connector.getOutputDouble("MECResult$CenterLine");
        double[] melcl = connector.getOutputDoubleArray("MECResult$LCL");
        double[] meucl = connector.getOutputDoubleArray("MECResult$UCL");
        result.setY(convert(mey));
        result.setX(generate(mey.length));
        result.setCl(mecl);
        result.setUcl(convert(meucl));
        result.setLcl(convert(melcl));
        return result;
    }

    private SpcControlChartDto getMrCResult(REnConnector connector) {
        connector.execEval("MECResult <- intspc.normal.getMECResult(x, subgroupSize)");
        SpcControlChartDto result = new SpcControlChartDto();
        double[] mey = connector.getOutputDoubleArray("MECResult$Values");
        double mecl = connector.getOutputDouble("MECResult$CenterLine");
        double[] melcl = connector.getOutputDoubleArray("MECResult$LCL");
        double[] meucl = connector.getOutputDoubleArray("MECResult$UCL");
        result.setY(convert(mey));
        result.setX(generate(mey.length));
        result.setUcl(convert(meucl));
        result.setLcl(convert(melcl));
        result.setCl(mecl);
        return result;
    }

    private BoxCResultDto getBoxCResult(REnConnector connector) {
        connector.execEval("BoxCResult <- intspc.normal.getBoxCResult(x, subgroupSize)");
        double[] boxy = connector.getOutputDoubleArray("BoxCResult$Values");
        double[] outs = connector.getOutputDoubleArray("BoxCResult$Outs");
        if (outs.length == 0 || outs.length % 2 == 1) {
            outs = null;
        }
        int length = boxy.length / 5;
        int j = 0;
        List<SingleBoxDataDto> boxDataDtoList = Lists.newArrayList();
        for (int i = 0; i < length; i++) {
            SingleBoxDataDto singleBoxDataDto = new SingleBoxDataDto();
            singleBoxDataDto.setX(i + 1.0);
            singleBoxDataDto.setLowerWhisker(boxy[i * 5]);
            singleBoxDataDto.setQ1(boxy[i * 5 + 1]);
            singleBoxDataDto.setMedian(boxy[i * 5 + 2]);
            singleBoxDataDto.setQ3(boxy[i * 5 + 3]);
            singleBoxDataDto.setUpperWhisker(boxy[i * 5 + 4]);
            List<Double> outList = Lists.newArrayList();
            if (outs != null) {
                while (outs[j] == i) {
                    outList.add(outs[j + 1]);
                    j = j + 2;
                }
            }
            if (!outList.isEmpty()) {
                singleBoxDataDto.setAbnormalPoints(convert(outList));
            }
            boxDataDtoList.add(singleBoxDataDto);
        }
        BoxCResultDto resultDto = new BoxCResultDto();
        resultDto.setBoxData(boxDataDtoList);
        return resultDto;
    }

    private Double[] convert(double[] array) {
        Double[] result = new Double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
    }

    private Double[] generate(int length) {
        Double[] result = new Double[length];
        for (int i = 0; i < length; i++) {
            result[i] = i + 1.0;
        }
        return result;
    }

    private Double[] convert(List<Double> list) {
        if (list != null) {
            Double[] result = new Double[list.size()];
            for (int i = 0; i < list.size(); i++) {
                result[i] = list.get(i);
            }
            return result;
        } else {
            return null;
        }
    }
}
