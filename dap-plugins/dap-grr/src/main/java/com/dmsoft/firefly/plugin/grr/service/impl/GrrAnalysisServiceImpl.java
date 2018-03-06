package com.dmsoft.firefly.plugin.grr.service.impl;

import com.dmsoft.firefly.plugin.grr.GrrPlugin;
import com.dmsoft.firefly.plugin.grr.dto.analysis.*;
import com.dmsoft.firefly.plugin.grr.service.GrrAnalysisService;
import com.dmsoft.firefly.plugin.utils.REnConnector;
import com.dmsoft.firefly.plugin.utils.enums.GrrAnalysisMethod;
import com.dmsoft.firefly.plugin.utils.enums.GrrResultName;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.plugin.annotation.Analysis;
import com.dmsoft.firefly.sdk.plugin.apis.IAnalysis;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * impl class for grr analysis
 *
 * @author Can Guan
 */
@Analysis
public class GrrAnalysisServiceImpl implements IAnalysis, GrrAnalysisService {
    @Override
    public GrrSummaryResultDto analyzeSummaryResult(GrrAnalysisDataDto analysisDataDto, GrrAnalysisConfigDto configDto) {
        REnConnector connector = prepareConnect(analysisDataDto, configDto);
        GrrSummaryResultDto result = getSummaryResult(connector, configDto.getMethod());
        connector.disconnect();
        if (DAPStringUtils.isNumeric(analysisDataDto.getUsl())) {
            result.setUsl(Double.valueOf(analysisDataDto.getUsl()));
        }
        if (DAPStringUtils.isNumeric(analysisDataDto.getLsl())) {
            result.setLsl(Double.valueOf(analysisDataDto.getLsl()));
        }
        if (result.getLsl() != null && result.getUsl() != null) {
            result.setTolerance(result.getUsl() - result.getLsl());
        }
        return result;
    }

    @Override
    public GrrDetailResultDto analyzeDetailResult(GrrAnalysisDataDto analysisDataDto, GrrAnalysisConfigDto configDto) {
        REnConnector connector = prepareConnect(analysisDataDto, configDto);
        GrrDetailResultDto result = getGrrDetailResult(connector, configDto);
        connector.disconnect();
        return result;
    }


    private REnConnector prepareConnect(GrrAnalysisDataDto dataDto, GrrAnalysisConfigDto configDto) {
        REnConnector connector = new REnConnector();
        connector.connect();
        String spcPathName = "rscript/intgrr_";
        if (GrrAnalysisMethod.XbarAndRange.equals(configDto.getMethod())) {
            spcPathName += "xr.R";
        } else {
            spcPathName += "anova.R";
        }
        String scriptPath = RuntimeContext.getBean(PluginContext.class).getEnabledPluginInfo(GrrPlugin.GRR_PLUGIN_ID).getFolderPath() + "/" + spcPathName;
        connector.execEval("source(\"" + scriptPath + "\")");
        double[] dataArray = new double[dataDto.getDataList().size()];
        for (int i = 0; i < dataDto.getDataList().size(); i++) {
            dataArray[i] = dataDto.getDataList().get(i);
        }
        connector.setInput("x", dataArray);
        connector.setInput("k", configDto.getAppraiser());
        connector.setInput("n", configDto.getPart());
        connector.setInput("r", configDto.getTrial());
        connector.setInput("sig", configDto.getCoverage());
        connector.setInput("pap", configDto.getSignificance());
        if (DAPStringUtils.isNumeric(dataDto.getUsl()) && DAPStringUtils.isNumeric(dataDto.getLsl())) {
            double tole = Double.valueOf(dataDto.getUsl()) - Double.valueOf(dataDto.getLsl());
            connector.setInput("tole", tole);
        } else {
            connector.setInput("tole", Double.NaN);
        }
        return connector;
    }

    private GrrSummaryResultDto getSummaryResult(REnConnector connector, GrrAnalysisMethod method) {
        String methodKey = "anova";
        if (GrrAnalysisMethod.XbarAndRange.equals(method)) {
            methodKey = "xr";
        }
        connector.execEval("sourceResult <- intgrr." + methodKey + ".getSourceResult(x, k, n, r, sig, tole, pap)");
        GrrSummaryResultDto result = new GrrSummaryResultDto();
        double rep = connector.getOutputDouble("sourceResult[1, 5]");
        double reprod = connector.getOutputDouble("sourceResult[2, 5]");
        Double grr = null;
        if ("xr".equals(methodKey)) {
            grr = connector.getOutputDouble("sourceResult[3, 5]");
        } else {
            grr = connector.getOutputDouble("sourceResult[5, 5]");
        }

        result.setRepeatabilityOnContribution(rep);
        result.setReproducibilityOnContribution(reprod);
        result.setGrrOnContribution(grr);

        rep = connector.getOutputDouble("sourceResult[1, 6]");
        reprod = connector.getOutputDouble("sourceResult[2, 6]");
        if ("xr".equals(methodKey)) {
            grr = connector.getOutputDouble("sourceResult[3, 6]");
        } else {
            grr = connector.getOutputDouble("sourceResult[5, 6]");
        }

        result.setRepeatabilityOnTolerance(rep);
        result.setReproducibilityOnTolerance(reprod);
        result.setGrrOnTolerance(grr);
        return result;
    }

    private GrrDetailResultDto getGrrDetailResult(REnConnector connector, GrrAnalysisConfigDto configDto) {
        String methodKey = "anova";
        if (GrrAnalysisMethod.XbarAndRange.equals(configDto.getMethod())) {
            methodKey = "xr";
        }

        GrrDetailResultDto result = new GrrDetailResultDto();

        result.setComponentChartDto(getComponentChartResult(connector, methodKey));
        result.setPartAppraiserChartDto(getPartAppraiserChartResult(connector, methodKey));
        result.setXbarAppraiserChartDto(getXbarByApprariserChartResult(connector, methodKey));
        result.setRangeAppraiserChartDto(getRangeByAppraiserChartResult(connector, methodKey));
        result.setRrbyAppraiserChartDto(getRRByAppraiserChartResult(connector, methodKey, configDto));
        result.setRrbyPartChartDto(getRRByPartChartResult(connector, methodKey, configDto));
        result.setAnovaAndSourceResultDto(getAnovaAndSourceResult(connector, methodKey, configDto));
        return result;
    }

    private GrrComponentCResultDto getComponentChartResult(REnConnector connector, String methodKey) {
        connector.execEval("ComponentCResult <- intgrr." + methodKey + ".getComponentCResult(x, k, n, r, sig, tole, pap)");
        GrrComponentCResultDto componentCResultDto = new GrrComponentCResultDto();
        componentCResultDto.setGrrContri(connector.getOutputDouble("ComponentCResult[1, 3]"));
        componentCResultDto.setGrrVar(connector.getOutputDouble("ComponentCResult[2, 3]"));
        componentCResultDto.setGrrTol(connector.getOutputDouble("ComponentCResult[3, 3]"));
        componentCResultDto.setRepeatContri(connector.getOutputDouble("ComponentCResult[1, 1]"));
        componentCResultDto.setRepeatVar(connector.getOutputDouble("ComponentCResult[2, 1]"));
        componentCResultDto.setRepeatTol(connector.getOutputDouble("ComponentCResult[3, 1]"));
        componentCResultDto.setReprodContri(connector.getOutputDouble("ComponentCResult[1, 2]"));
        componentCResultDto.setReprodVar(connector.getOutputDouble("ComponentCResult[2, 2]"));
        componentCResultDto.setReprodTol(connector.getOutputDouble("ComponentCResult[3, 2]"));
        componentCResultDto.setPartContri(connector.getOutputDouble("ComponentCResult[1, 4]"));
        componentCResultDto.setPartVar(connector.getOutputDouble("ComponentCResult[1, 4]"));
        componentCResultDto.setPartTol(connector.getOutputDouble("ComponentCResult[3, 4]"));
        return componentCResultDto;
    }

    private GrrControlChartDto getXbarByApprariserChartResult(REnConnector connector, String methodKey) {
        GrrControlChartDto xbarByAppChartDto = new GrrControlChartDto();
        connector.execEval("XBBACResult <- intgrr." + methodKey + ".getXBBACResult(x, k, n, r)");
        double[] xbbay = connector.getOutputDoubleArray("XBBACResult$Values");
        double xbbaucl = connector.getOutputDouble("XBBACResult$UCL");
        double xbbalcl = connector.getOutputDouble("XBBACResult$LCL");
        double xbbacl = connector.getOutputDouble("XBBACResult$CenterLine");
        xbarByAppChartDto.setY(convert(xbbay));
        xbarByAppChartDto.setX(generate(xbbay.length));
        xbarByAppChartDto.setUcl(xbbaucl);
        xbarByAppChartDto.setLcl(xbbalcl);
        xbarByAppChartDto.setCl(xbbacl);
        return xbarByAppChartDto;
    }

    private GrrPACResultDto getPartAppraiserChartResult(REnConnector connector, String methodKey) {
        connector.execEval("APCResult <- intgrr." + methodKey + ".getAPCResult(x, k, n, r)");
        GrrPACResultDto paicResultDto = new GrrPACResultDto();
        paicResultDto.setDatas(connector.getEval("APCResult").asDoubleMatrix());
        return paicResultDto;
    }

    private GrrControlChartDto getRangeByAppraiserChartResult(REnConnector connector, String methodKey) {
        GrrControlChartDto rangeByAppChartDto = new GrrControlChartDto();
        connector.execEval("RangeBACResult <- intgrr." + methodKey + ".getRangeBACResult(x, k, n, r)");
        double[] rcy = connector.getOutputDoubleArray("RangeBACResult$Values");
        double rcucl = connector.getOutputDouble("RangeBACResult$UCL");
        double rclcl = connector.getOutputDouble("RangeBACResult$LCL");
        double rccl = connector.getOutputDouble("RangeBACResult$CenterLine");
        rangeByAppChartDto.setY(convert(rcy));
        rangeByAppChartDto.setX(generate(rcy.length));
        rangeByAppChartDto.setCl(rccl);
        rangeByAppChartDto.setLcl(rclcl);
        rangeByAppChartDto.setUcl(rcucl);
        return rangeByAppChartDto;
    }

    private GrrScatterChartDto getRRByAppraiserChartResult(REnConnector connector, String methodKey, GrrAnalysisConfigDto configDto) {
        GrrScatterChartDto rrbaPlotChartDto = new GrrScatterChartDto();
        connector.execEval("RRBACResult <- intgrr." + methodKey + ".getRRBACResult(x, k, n, r)");
        double[][] rrbaValue = connector.getEval("RRBACResult$Values").asDoubleMatrix();
        double[] rrbacl = connector.getOutputDoubleArray("RRBACResult$CL");
        double[] rrbax = new double[configDto.getPart() * configDto.getAppraiser() * configDto.getTrial()];
        double[] rrbay = new double[configDto.getPart() * configDto.getAppraiser() * configDto.getTrial()];
        for (int i = 0; i < rrbaValue.length; i++) {
            for (int j = 0; j < rrbaValue[0].length; j++) {
                rrbax[i * configDto.getTrial() * configDto.getPart() + j] = i + 1;
                rrbay[i * configDto.getTrial() * configDto.getPart() + j] = rrbaValue[i][j];
            }
        }
        rrbaPlotChartDto.setX(convert(rrbax));
        rrbaPlotChartDto.setY(convert(rrbay));
        rrbaPlotChartDto.setClY(convert(rrbacl));
        rrbaPlotChartDto.setClX(generate(rrbacl.length));
        return rrbaPlotChartDto;
    }

    private GrrScatterChartDto getRRByPartChartResult(REnConnector connector, String methodKey, GrrAnalysisConfigDto configDto) {
        GrrScatterChartDto rrbpPlotChartDto = new GrrScatterChartDto();
        connector.execEval("RRBPCResult <- intgrr." + methodKey + ".getRRBPCResult(x, k, n, r)");
        double[][] rrbpValue = connector.getEval("RRBPCResult$Values").asDoubleMatrix();
        double[] rrbpx = new double[configDto.getPart() * configDto.getAppraiser() * configDto.getTrial()];
        double[] rrbpy = new double[configDto.getPart() * configDto.getAppraiser() * configDto.getTrial()];
        for (int i = 0; i < rrbpValue.length; i++) {
            for (int j = 0; j < rrbpValue[0].length; j++) {
                rrbpx[i * configDto.getTrial() * configDto.getAppraiser() + j] = i + 1;
                rrbpy[i * configDto.getTrial() * configDto.getAppraiser() + j] = rrbpValue[i][j];
            }
        }
        double[] rrbpcl = connector.getOutputDoubleArray("RRBPCResult$CL");
        rrbpPlotChartDto.setClY(convert(rrbpcl));
        rrbpPlotChartDto.setClX(generate(rrbpcl.length));
        rrbpPlotChartDto.setY(convert(rrbpy));
        rrbpPlotChartDto.setX(convert(rrbpx));
        return rrbpPlotChartDto;
    }

    private GrrAnovaAndSourceResultDto getAnovaAndSourceResult(REnConnector connector, String methodKey, GrrAnalysisConfigDto configDto) {
        GrrAnovaAndSourceResultDto result = new GrrAnovaAndSourceResultDto();
        if ("anova".equals(methodKey)) {
            List<GrrAnovaDto> anovaDtoList = Lists.newArrayList();
            connector.execEval("anovaResult <- intgrr." + methodKey + ".getAnovaResult(x, k, n, r, pap)");

            GrrAnovaDto aAnova = new GrrAnovaDto();
            aAnova.setName(GrrResultName.Appraisers);
            aAnova.setDf(connector.getOutputDouble("anovaResult[1, 1]"));
            aAnova.setSs(connector.getOutputDouble("anovaResult[1, 2]"));
            aAnova.setMs(connector.getOutputDouble("anovaResult[1, 3]"));
            aAnova.setF(connector.getOutputDouble("anovaResult[1, 4]"));
            aAnova.setProbF(connector.getOutputDouble("anovaResult[1, 5]"));
            anovaDtoList.add(aAnova);

            GrrAnovaDto pAnova = new GrrAnovaDto();
            pAnova.setName(GrrResultName.Parts);
            pAnova.setDf(connector.getOutputDouble("anovaResult[2, 1]"));
            pAnova.setSs(connector.getOutputDouble("anovaResult[2, 2]"));
            pAnova.setMs(connector.getOutputDouble("anovaResult[2, 3]"));
            pAnova.setF(connector.getOutputDouble("anovaResult[2, 4]"));
            pAnova.setProbF(connector.getOutputDouble("anovaResult[2, 5]"));
            anovaDtoList.add(pAnova);

            GrrAnovaDto apAnova = new GrrAnovaDto();
            apAnova.setName(GrrResultName.AppraisersAndParts);
            apAnova.setDf(connector.getOutputDouble("anovaResult[3, 1]"));
            apAnova.setSs(connector.getOutputDouble("anovaResult[3, 2]"));
            apAnova.setMs(connector.getOutputDouble("anovaResult[3, 3]"));
            apAnova.setF(connector.getOutputDouble("anovaResult[3, 4]"));
            apAnova.setProbF(connector.getOutputDouble("anovaResult[3, 5]"));
            if (configDto.getSignificance() != null && apAnova.getProbF() < configDto.getSignificance()) {
                anovaDtoList.add(apAnova);
            }


            GrrAnovaDto repAnova = new GrrAnovaDto();
            repAnova.setName(GrrResultName.Repeatability);
            repAnova.setDf(connector.getOutputDouble("anovaResult[4, 1]"));
            repAnova.setSs(connector.getOutputDouble("anovaResult[4, 2]"));
            repAnova.setMs(connector.getOutputDouble("anovaResult[4, 3]"));
            anovaDtoList.add(repAnova);

            GrrAnovaDto tolAnova = new GrrAnovaDto();
            tolAnova.setName(GrrResultName.Total);
            tolAnova.setDf(connector.getOutputDouble("anovaResult[5, 1]"));
            tolAnova.setSs(connector.getOutputDouble("anovaResult[5, 2]"));
            anovaDtoList.add(tolAnova);

            result.setGrrAnovaDtos(anovaDtoList);
        }
        List<GrrSourceDto> sourceDtoList = Lists.newArrayList();
        connector.execEval("sourceResult <- intgrr." + methodKey + ".getSourceResult(x, k, n, r, sig, tole, pap)");
        GrrSourceDto repSource = new GrrSourceDto();
        repSource.setName(GrrResultName.Repeatability);
        repSource.setVariation(connector.getOutputDouble("sourceResult[1, 1]"));
        repSource.setSigma(connector.getOutputDouble("sourceResult[1, 2]"));
        repSource.setStudyVar(connector.getOutputDouble("sourceResult[1, 3]"));
        repSource.setContribution(connector.getOutputDouble("sourceResult[1, 4]"));
        repSource.setTotalVariation(connector.getOutputDouble("sourceResult[1, 5]"));
        repSource.setTotalTolerance(connector.getOutputDouble("sourceResult[1, 6]"));
        sourceDtoList.add(repSource);

        GrrSourceDto reprodSource = new GrrSourceDto();
        reprodSource.setName(GrrResultName.Reproducibility);
        reprodSource.setVariation(connector.getOutputDouble("sourceResult[2, 1]"));
        reprodSource.setSigma(connector.getOutputDouble("sourceResult[2, 2]"));
        reprodSource.setStudyVar(connector.getOutputDouble("sourceResult[2, 3]"));
        reprodSource.setContribution(connector.getOutputDouble("sourceResult[2, 4]"));
        reprodSource.setTotalVariation(connector.getOutputDouble("sourceResult[2, 5]"));
        reprodSource.setTotalTolerance(connector.getOutputDouble("sourceResult[2, 6]"));
        sourceDtoList.add(reprodSource);

        if ("anova".equals(methodKey)) {
            GrrSourceDto aSource = new GrrSourceDto();
            aSource.setName(GrrResultName.Appraisers);
            aSource.setVariation(connector.getOutputDouble("sourceResult[3, 1]"));
            aSource.setSigma(connector.getOutputDouble("sourceResult[3, 2]"));
            aSource.setStudyVar(connector.getOutputDouble("sourceResult[3, 3]"));
            aSource.setContribution(connector.getOutputDouble("sourceResult[3, 4]"));
            aSource.setTotalVariation(connector.getOutputDouble("sourceResult[3, 5]"));
            aSource.setTotalTolerance(connector.getOutputDouble("sourceResult[3, 6]"));
            sourceDtoList.add(aSource);

            if (result.getGrrAnovaDtos() != null && result.getGrrAnovaDtos().size() == 5) {
                GrrSourceDto apSource = new GrrSourceDto();
                apSource.setName(GrrResultName.AppraisersAndParts);
                apSource.setVariation(connector.getOutputDouble("sourceResult[4, 1]"));
                apSource.setSigma(connector.getOutputDouble("sourceResult[4, 2]"));
                apSource.setStudyVar(connector.getOutputDouble("sourceResult[4, 3]"));
                apSource.setContribution(connector.getOutputDouble("sourceResult[4, 4]"));
                apSource.setTotalVariation(connector.getOutputDouble("sourceResult[4, 5]"));
                apSource.setTotalTolerance(connector.getOutputDouble("sourceResult[4, 6]"));
                sourceDtoList.add(apSource);
            }

            GrrSourceDto gSource = new GrrSourceDto();
            gSource.setName(GrrResultName.Gauge);
            gSource.setVariation(connector.getOutputDouble("sourceResult[5, 1]"));
            gSource.setSigma(connector.getOutputDouble("sourceResult[5, 2]"));
            gSource.setStudyVar(connector.getOutputDouble("sourceResult[5, 3]"));
            gSource.setContribution(connector.getOutputDouble("sourceResult[5, 4]"));
            gSource.setTotalVariation(connector.getOutputDouble("sourceResult[5, 5]"));
            gSource.setTotalTolerance(connector.getOutputDouble("sourceResult[5, 6]"));
            sourceDtoList.add(gSource);


            GrrSourceDto pSource = new GrrSourceDto();
            pSource.setName(GrrResultName.Parts);
            pSource.setVariation(connector.getOutputDouble("sourceResult[6, 1]"));
            pSource.setSigma(connector.getOutputDouble("sourceResult[6, 2]"));
            pSource.setStudyVar(connector.getOutputDouble("sourceResult[6, 3]"));
            pSource.setContribution(connector.getOutputDouble("sourceResult[6, 4]"));
            pSource.setTotalVariation(connector.getOutputDouble("sourceResult[6, 5]"));
            pSource.setTotalTolerance(connector.getOutputDouble("sourceResult[6, 6]"));
            sourceDtoList.add(pSource);


            GrrSourceDto tSource = new GrrSourceDto();
            tSource.setName(GrrResultName.Total);
            tSource.setVariation(connector.getOutputDouble("sourceResult[7, 1]"));
            tSource.setSigma(connector.getOutputDouble("sourceResult[7, 2]"));
            tSource.setStudyVar(connector.getOutputDouble("sourceResult[7, 3]"));
            tSource.setContribution(connector.getOutputDouble("sourceResult[7, 4]"));
            tSource.setTotalVariation(connector.getOutputDouble("sourceResult[7, 5]"));
            tSource.setTotalTolerance(connector.getOutputDouble("sourceResult[7, 6]"));
            sourceDtoList.add(tSource);

            Double ndc = 1.41 * pSource.getSigma() / gSource.getSigma();
            result.setNumberOfDc(ndc);
        } else {
            GrrSourceDto gSource = new GrrSourceDto();
            gSource.setName(GrrResultName.Gauge);
            gSource.setVariation(connector.getOutputDouble("sourceResult[3, 1]"));
            gSource.setSigma(connector.getOutputDouble("sourceResult[3, 2]"));
            gSource.setStudyVar(connector.getOutputDouble("sourceResult[3, 3]"));
            gSource.setContribution(connector.getOutputDouble("sourceResult[3, 4]"));
            gSource.setTotalVariation(connector.getOutputDouble("sourceResult[3, 5]"));
            gSource.setTotalTolerance(connector.getOutputDouble("sourceResult[3, 6]"));
            sourceDtoList.add(gSource);


            GrrSourceDto pSource = new GrrSourceDto();
            pSource.setName(GrrResultName.Parts);
            pSource.setVariation(connector.getOutputDouble("sourceResult[4, 1]"));
            pSource.setSigma(connector.getOutputDouble("sourceResult[4, 2]"));
            pSource.setStudyVar(connector.getOutputDouble("sourceResult[4, 3]"));
            pSource.setContribution(connector.getOutputDouble("sourceResult[4, 4]"));
            pSource.setTotalVariation(connector.getOutputDouble("sourceResult[4, 5]"));
            pSource.setTotalTolerance(connector.getOutputDouble("sourceResult[4, 6]"));
            sourceDtoList.add(pSource);


            GrrSourceDto tSource = new GrrSourceDto();
            tSource.setName(GrrResultName.Total);
            tSource.setVariation(connector.getOutputDouble("sourceResult[5, 1]"));
            tSource.setSigma(connector.getOutputDouble("sourceResult[5, 2]"));
            tSource.setStudyVar(connector.getOutputDouble("sourceResult[5, 3]"));
            tSource.setContribution(connector.getOutputDouble("sourceResult[5, 4]"));
            tSource.setTotalVariation(connector.getOutputDouble("sourceResult[5, 5]"));
            tSource.setTotalTolerance(connector.getOutputDouble("sourceResult[5, 6]"));
            sourceDtoList.add(tSource);

            Double ndc = 1.41 * pSource.getSigma() / gSource.getSigma();
            result.setNumberOfDc(ndc);
        }
        return result;
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
