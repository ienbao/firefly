package com.dmsoft.firefly.plugin.spc.service;

import com.dmsoft.firefly.plugin.spc.SpcPlugin;
import com.dmsoft.firefly.plugin.spc.dto.SpcAnalysisConfigDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.*;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcAnalysisService;
import com.dmsoft.firefly.plugin.spc.utils.REnConnector;
import com.dmsoft.firefly.plugin.spc.utils.SpcChartType;
import com.dmsoft.firefly.plugin.spc.utils.SpcExceptionCode;
import com.dmsoft.firefly.plugin.spc.utils.SpcExceptionParser;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.plugin.annotation.Analysis;
import com.dmsoft.firefly.sdk.plugin.apis.IAnalysis;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;

import java.util.List;

/**
 * impl class for spc analysis
 *
 * @author Can Guan
 */
@Analysis
public class SpcAnalysisServiceImpl implements SpcAnalysisService, IAnalysis {

    @Override
    public SpcStatsResultDto analyzeStatsResult(AnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        REnConnector connector = prepareConnect(dataDto.getDataList(), configDto);
        try {
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
            connector.disconnect();
            return resultDto;
        } catch (Exception e) {
            throw new ApplicationException(SpcExceptionParser.parser(SpcExceptionCode.ERR_20001));
        } finally {
            if (connector != null) {
                connector.disconnect();
            }
        }
    }

    @Override
    public NDCResultDto analyzeNDCResult(AnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        REnConnector connector = prepareConnect(dataDto.getDataList(), configDto);
        try {
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
        } catch (Exception e) {
            throw new ApplicationException(SpcExceptionParser.parser(SpcExceptionCode.ERR_20001));
        } finally {
            if (connector != null) {
                connector.disconnect();
            }
        }
    }

    @Override
    public RunCResultDto analyzeRunCResult(AnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        return null;
    }

    @Override
    public ControlChartDto analyzeXbarCResult(AnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        return null;
    }

    @Override
    public ControlChartDto analyzeRangeCResult(AnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        return null;
    }

    @Override
    public ControlChartDto analyzeSdCResult(AnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        return null;
    }

    @Override
    public ControlChartDto analyzeMeCResult(AnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        return null;
    }

    @Override
    public ControlChartDto analyzeMrCResult(AnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        return null;
    }

    @Override
    public BoxCResultDto analyzeBoxCResult(AnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        return null;
    }

    @Override
    public SpcChartResultDto analyzeSpcChartResult(AnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        return null;
    }

    @Override
    public SpcChartResultDto analyzeSpcChartResult(AnalysisDataDto dataDto, List<SpcChartType> requiredCharts, SpcAnalysisConfigDto configDto) {
        return null;
    }

    private REnConnector prepareConnect(List<Double> dataList, SpcAnalysisConfigDto configDto) {
        REnConnector connector = new REnConnector();
        connector.connect();
        String spcPathName = "rscript/spc.R";
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

    private Double[] convert(double[] array) {
        Double[] result = new Double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
    }
}
