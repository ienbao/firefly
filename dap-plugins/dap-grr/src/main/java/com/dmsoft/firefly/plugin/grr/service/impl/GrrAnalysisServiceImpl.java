package com.dmsoft.firefly.plugin.grr.service.impl;

import com.dmsoft.firefly.plugin.grr.GrrPlugin;
import com.dmsoft.firefly.plugin.grr.dto.GrrExportDetailDto;
import com.dmsoft.firefly.plugin.grr.dto.analysis.*;
import com.dmsoft.firefly.plugin.grr.service.GrrAnalysisService;
import com.dmsoft.firefly.plugin.grr.utils.GrrExceptionCode;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.grr.utils.enums.GrrAnalysisMethod;
import com.dmsoft.firefly.plugin.grr.utils.enums.GrrResultName;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.plugin.apis.IAnalysis;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.Analysis;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.dmsoft.firefly.sdk.utils.SemaphoreUtils;
import com.google.common.collect.Lists;
import org.rosuda.JRI.Rengine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * impl class for grr analysis
 *
 * @author Can Guan
 */
@Analysis
public class GrrAnalysisServiceImpl implements IAnalysis, GrrAnalysisService {
    private static final double NDC_RATE = 1.4;
    private static Logger logger = LoggerFactory.getLogger(GrrAnalysisServiceImpl.class);
    private Rengine privateEngine = null;

    @Override
    public GrrSummaryResultDto analyzeSummaryResult(GrrAnalysisDataDto analysisDataDto, GrrAnalysisConfigDto configDto) {
        logger.debug("Analyzing GRR summary result ...");
        GrrSummaryResultDto result;
        try {
            Rengine engine = prepareEngine(analysisDataDto, configDto);
            result = getSummaryResult(engine, configDto.getMethod());
            if (DAPStringUtils.isNumeric(analysisDataDto.getUsl())) {
                result.setUsl(Double.valueOf(analysisDataDto.getUsl()));
            }
            if (DAPStringUtils.isNumeric(analysisDataDto.getLsl())) {
                result.setLsl(Double.valueOf(analysisDataDto.getLsl()));
            }
            if (result.getLsl() != null && result.getUsl() != null) {
                result.setTolerance(result.getUsl() - result.getLsl());
            }
            SemaphoreUtils.releaseSemaphore(engine);
            logger.info("Analyze GRR summary result done");
        } catch (Exception e) {
            SemaphoreUtils.releaseSemaphore(privateEngine);
            logger.error("Analyze GRR summary result error, exception message = {}", e.getMessage());
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12013));
        }
        return result;
    }

    @Override
    public GrrDetailResultDto analyzeDetailResult(GrrAnalysisDataDto analysisDataDto, GrrAnalysisConfigDto configDto) {
        logger.debug("Analyzing GRR detail result ...");
        GrrDetailResultDto result;
        try {
            Rengine engine = prepareEngine(analysisDataDto, configDto);
            result = getGrrDetailResult(engine, configDto);
            SemaphoreUtils.releaseSemaphore(engine);
            logger.info("Analyze GRR detail result done.");
        } catch (Exception e) {
            SemaphoreUtils.releaseSemaphore(privateEngine);
            logger.error("Analyze Grr detail result error, exception message = {}", e.getMessage());
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12013));
        }
        return result;
    }

    @Override
    public GrrExportDetailResultDto analyzeExportDetailResult(GrrAnalysisDataDto analysisDataDto, GrrAnalysisConfigDto configDto) {
        logger.debug("Analyzing GRR export detail result ...");
        GrrExportDetailResultDto result = new GrrExportDetailResultDto();
        try {
            Rengine engine = prepareEngine(analysisDataDto, configDto);
            GrrDetailResultDto detailResultDto = getGrrDetailResult(engine, configDto);
            result.setComponentChartDto(detailResultDto.getComponentChartDto());
            result.setPartAppraiserChartDto(detailResultDto.getPartAppraiserChartDto());
            result.setXbarAppraiserChartDto(detailResultDto.getXbarAppraiserChartDto());
            result.setRangeAppraiserChartDto(detailResultDto.getRangeAppraiserChartDto());
            result.setRrbyAppraiserChartDto(detailResultDto.getRrbyAppraiserChartDto());
            result.setRrbyPartChartDto(detailResultDto.getRrbyPartChartDto());
            result.setAnovaAndSourceResultDto(detailResultDto.getAnovaAndSourceResultDto());
            //TODO
            SemaphoreUtils.releaseSemaphore(engine);
            logger.info("Analyze GRR export detail result done.");
        } catch (Exception e) {
            SemaphoreUtils.releaseSemaphore(privateEngine);
            logger.error("Analyze Grr export detail result error, exception message = {}", e.getMessage());
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12013));
        }
        return result;
    }

    private Rengine prepareEngine(GrrAnalysisDataDto dataDto, GrrAnalysisConfigDto configDto) {
        if (this.privateEngine == null) {
            privateEngine = RuntimeContext.getBean(Rengine.class);
            if (privateEngine == null) {
                privateEngine = new Rengine(new String[]{"--no-save"}, false, null);
                privateEngine.end();
                RuntimeContext.registerBean(Rengine.class, privateEngine);
            }
            String anovaPathName = "rscripts/intgrr_anova.R";
            String xrPathName = "rscripts/intgrr_xr.R";
            String anovaScriptPath = RuntimeContext.getBean(PluginContext.class).getEnabledPluginInfo(GrrPlugin.GRR_PLUGIN_ID).getFolderPath() + "/" + anovaPathName;
            privateEngine.eval("source(\"" + anovaScriptPath + "\")");
            String xrScriptPath = RuntimeContext.getBean(PluginContext.class).getEnabledPluginInfo(GrrPlugin.GRR_PLUGIN_ID).getFolderPath() + "/" + xrPathName;
            privateEngine.eval("source(\"" + xrScriptPath + "\")");
        }
        SemaphoreUtils.lockSemaphore(privateEngine);
        privateEngine.eval("rm(list=setdiff(ls(), ls()[grep(\"^int\", ls())]))");
        double[] dataArray = new double[dataDto.getDataList().size()];
        for (int i = 0; i < dataDto.getDataList().size(); i++) {
            dataArray[i] = dataDto.getDataList().get(i);
        }
        privateEngine.assign("x", dataArray);
        privateEngine.assign("k", new double[]{configDto.getAppraiser()});
        privateEngine.assign("n", new double[]{configDto.getPart()});
        privateEngine.assign("r", new double[]{configDto.getTrial()});
        privateEngine.assign("sig", new double[]{configDto.getCoverage()});
        privateEngine.assign("pap", new double[]{configDto.getSignificance()});
        if (DAPStringUtils.isNumeric(dataDto.getUsl()) && DAPStringUtils.isNumeric(dataDto.getLsl())) {
            double tole = Double.valueOf(dataDto.getUsl()) - Double.valueOf(dataDto.getLsl());
            privateEngine.assign("tole", new double[]{tole});
        } else {
            privateEngine.assign("tole", new double[]{Double.NaN});
        }
        return privateEngine;
    }

    private GrrSummaryResultDto getSummaryResult(Rengine engine, GrrAnalysisMethod method) {
        String methodKey = "anova";
        if (GrrAnalysisMethod.XbarAndRange.equals(method)) {
            methodKey = "xr";
        }
        engine.eval("sourceResult <- intgrr." + methodKey + ".getSourceResult(x, k, n, r, sig, tole, pap)");
        GrrSummaryResultDto result = new GrrSummaryResultDto();
        double rep = engine.eval("sourceResult[1, 5]").asDouble();
        double reprod = engine.eval("sourceResult[2, 5]").asDouble();
        Double grr = null;
        if ("xr".equals(methodKey)) {
            grr = engine.eval("sourceResult[3, 5]").asDouble();
        } else {
            grr = engine.eval("sourceResult[5, 5]").asDouble();
        }

        result.setRepeatabilityOnContribution(rep);
        result.setReproducibilityOnContribution(reprod);
        result.setGrrOnContribution(grr);

        rep = engine.eval("sourceResult[1, 6]").asDouble();
        reprod = engine.eval("sourceResult[2, 6]").asDouble();
        if ("xr".equals(methodKey)) {
            grr = engine.eval("sourceResult[3, 6]").asDouble();
        } else {
            grr = engine.eval("sourceResult[5, 6]").asDouble();
        }

        result.setRepeatabilityOnTolerance(rep);
        result.setReproducibilityOnTolerance(reprod);
        result.setGrrOnTolerance(grr);
        return result;
    }

    private GrrDetailResultDto getGrrDetailResult(Rengine engine, GrrAnalysisConfigDto configDto) {
        String methodKey = "anova";
        if (GrrAnalysisMethod.XbarAndRange.equals(configDto.getMethod())) {
            methodKey = "xr";
        }

        GrrDetailResultDto result = new GrrDetailResultDto();

        result.setComponentChartDto(getComponentChartResult(engine, methodKey));
        result.setPartAppraiserChartDto(getPartAppraiserChartResult(engine, methodKey));
        result.setXbarAppraiserChartDto(getXbarByApprariserChartResult(engine, methodKey));
        result.setRangeAppraiserChartDto(getRangeByAppraiserChartResult(engine, methodKey));
        result.setRrbyAppraiserChartDto(getRRByAppraiserChartResult(engine, methodKey, configDto));
        result.setRrbyPartChartDto(getRRByPartChartResult(engine, methodKey, configDto));
        result.setAnovaAndSourceResultDto(getAnovaAndSourceResult(engine, methodKey, configDto));
        return result;
    }

    private GrrComponentCResultDto getComponentChartResult(Rengine engine, String methodKey) {
        engine.eval("ComponentCResult <- intgrr." + methodKey + ".getComponentCResult(x, k, n, r, sig, tole, pap)");
        GrrComponentCResultDto componentCResultDto = new GrrComponentCResultDto();
        componentCResultDto.setGrrContri(engine.eval("ComponentCResult[1, 3]").asDouble());
        componentCResultDto.setGrrVar(engine.eval("ComponentCResult[2, 3]").asDouble());
        componentCResultDto.setGrrTol(engine.eval("ComponentCResult[3, 3]").asDouble());
        componentCResultDto.setRepeatContri(engine.eval("ComponentCResult[1, 1]").asDouble());
        componentCResultDto.setRepeatVar(engine.eval("ComponentCResult[2, 1]").asDouble());
        componentCResultDto.setRepeatTol(engine.eval("ComponentCResult[3, 1]").asDouble());
        componentCResultDto.setReprodContri(engine.eval("ComponentCResult[1, 2]").asDouble());
        componentCResultDto.setReprodVar(engine.eval("ComponentCResult[2, 2]").asDouble());
        componentCResultDto.setReprodTol(engine.eval("ComponentCResult[3, 2]").asDouble());
        componentCResultDto.setPartContri(engine.eval("ComponentCResult[1, 4]").asDouble());
        componentCResultDto.setPartVar(engine.eval("ComponentCResult[1, 4]").asDouble());
        componentCResultDto.setPartTol(engine.eval("ComponentCResult[3, 4]").asDouble());
        return componentCResultDto;
    }

    private GrrControlChartDto getXbarByApprariserChartResult(Rengine connector, String methodKey) {
        GrrControlChartDto xbarByAppChartDto = new GrrControlChartDto();
        connector.eval("XBBACResult <- intgrr." + methodKey + ".getXBBACResult(x, k, n, r)");
        double[] xbbay = connector.eval("XBBACResult$Values").asDoubleArray();
        double xbbaucl = connector.eval("XBBACResult$UCL").asDouble();
        double xbbalcl = connector.eval("XBBACResult$LCL").asDouble();
        double xbbacl = connector.eval("XBBACResult$CenterLine").asDouble();
        xbarByAppChartDto.setY(convert(xbbay));
        xbarByAppChartDto.setX(generate(xbbay.length));
        xbarByAppChartDto.setUcl(xbbaucl);
        xbarByAppChartDto.setLcl(xbbalcl);
        xbarByAppChartDto.setCl(xbbacl);
        return xbarByAppChartDto;
    }

    private GrrPACResultDto getPartAppraiserChartResult(Rengine engine, String methodKey) {
        engine.eval("APCResult <- intgrr." + methodKey + ".getAPCResult(x, k, n, r)");
        GrrPACResultDto paicResultDto = new GrrPACResultDto();
        paicResultDto.setDatas(engine.eval("APCResult").asDoubleMatrix());
        return paicResultDto;
    }

    private GrrControlChartDto getRangeByAppraiserChartResult(Rengine engine, String methodKey) {
        GrrControlChartDto rangeByAppChartDto = new GrrControlChartDto();
        engine.eval("RangeBACResult <- intgrr." + methodKey + ".getRangeBACResult(x, k, n, r)");
        double[] rcy = engine.eval("RangeBACResult$Values").asDoubleArray();
        double rcucl = engine.eval("RangeBACResult$UCL").asDouble();
        double rclcl = engine.eval("RangeBACResult$LCL").asDouble();
        double rccl = engine.eval("RangeBACResult$CenterLine").asDouble();
        rangeByAppChartDto.setY(convert(rcy));
        rangeByAppChartDto.setX(generate(rcy.length));
        rangeByAppChartDto.setCl(rccl);
        rangeByAppChartDto.setLcl(rclcl);
        rangeByAppChartDto.setUcl(rcucl);
        return rangeByAppChartDto;
    }

    private GrrScatterChartDto getRRByAppraiserChartResult(Rengine engine, String methodKey, GrrAnalysisConfigDto configDto) {
        GrrScatterChartDto rrbaPlotChartDto = new GrrScatterChartDto();
        engine.eval("RRBACResult <- intgrr." + methodKey + ".getRRBACResult(x, k, n, r)");
        double[][] rrbaValue = engine.eval("RRBACResult$Values").asDoubleMatrix();
        double[] rrbacl = engine.eval("RRBACResult$CL").asDoubleArray();
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

    private GrrScatterChartDto getRRByPartChartResult(Rengine engine, String methodKey, GrrAnalysisConfigDto configDto) {
        GrrScatterChartDto rrbpPlotChartDto = new GrrScatterChartDto();
        engine.eval("RRBPCResult <- intgrr." + methodKey + ".getRRBPCResult(x, k, n, r)");
        double[][] rrbpValue = engine.eval("RRBPCResult$Values").asDoubleMatrix();
        double[] rrbpx = new double[configDto.getPart() * configDto.getAppraiser() * configDto.getTrial()];
        double[] rrbpy = new double[configDto.getPart() * configDto.getAppraiser() * configDto.getTrial()];
        for (int i = 0; i < rrbpValue.length; i++) {
            for (int j = 0; j < rrbpValue[0].length; j++) {
                rrbpx[i * configDto.getTrial() * configDto.getAppraiser() + j] = i + 1;
                rrbpy[i * configDto.getTrial() * configDto.getAppraiser() + j] = rrbpValue[i][j];
            }
        }
        double[] rrbpcl = engine.eval("RRBPCResult$CL").asDoubleArray();
        rrbpPlotChartDto.setClY(convert(rrbpcl));
        rrbpPlotChartDto.setClX(generate(rrbpcl.length));
        rrbpPlotChartDto.setY(convert(rrbpy));
        rrbpPlotChartDto.setX(convert(rrbpx));
        return rrbpPlotChartDto;
    }

    private GrrAnovaAndSourceResultDto getAnovaAndSourceResult(Rengine engine, String methodKey, GrrAnalysisConfigDto configDto) {
        GrrAnovaAndSourceResultDto result = new GrrAnovaAndSourceResultDto();
        if ("anova".equals(methodKey)) {
            List<GrrAnovaDto> anovaDtoList = Lists.newArrayList();
            engine.eval("anovaResult <- intgrr." + methodKey + ".getAnovaResult(x, k, n, r, pap)");

            GrrAnovaDto aAnova = new GrrAnovaDto();
            aAnova.setName(GrrResultName.Appraisers);
            aAnova.setDf(engine.eval("anovaResult[1, 1]").asDouble());
            aAnova.setSs(engine.eval("anovaResult[1, 2]").asDouble());
            aAnova.setMs(engine.eval("anovaResult[1, 3]").asDouble());
            aAnova.setF(engine.eval("anovaResult[1, 4]").asDouble());
            aAnova.setProbF(engine.eval("anovaResult[1, 5]").asDouble());
            anovaDtoList.add(aAnova);

            GrrAnovaDto pAnova = new GrrAnovaDto();
            pAnova.setName(GrrResultName.Parts);
            pAnova.setDf(engine.eval("anovaResult[2, 1]").asDouble());
            pAnova.setSs(engine.eval("anovaResult[2, 2]").asDouble());
            pAnova.setMs(engine.eval("anovaResult[2, 3]").asDouble());
            pAnova.setF(engine.eval("anovaResult[2, 4]").asDouble());
            pAnova.setProbF(engine.eval("anovaResult[2, 5]").asDouble());
            anovaDtoList.add(pAnova);

            GrrAnovaDto apAnova = new GrrAnovaDto();
            apAnova.setName(GrrResultName.AppraisersAndParts);
            apAnova.setDf(engine.eval("anovaResult[3, 1]").asDouble());
            apAnova.setSs(engine.eval("anovaResult[3, 2]").asDouble());
            apAnova.setMs(engine.eval("anovaResult[3, 3]").asDouble());
            apAnova.setF(engine.eval("anovaResult[3, 4]").asDouble());
            apAnova.setProbF(engine.eval("anovaResult[3, 5]").asDouble());
            if (configDto.getSignificance() != null && apAnova.getProbF() < configDto.getSignificance()) {
                anovaDtoList.add(apAnova);
            }


            GrrAnovaDto repAnova = new GrrAnovaDto();
            repAnova.setName(GrrResultName.Repeatability);
            repAnova.setDf(engine.eval("anovaResult[4, 1]").asDouble());
            repAnova.setSs(engine.eval("anovaResult[4, 2]").asDouble());
            repAnova.setMs(engine.eval("anovaResult[4, 3]").asDouble());
            anovaDtoList.add(repAnova);

            GrrAnovaDto tolAnova = new GrrAnovaDto();
            tolAnova.setName(GrrResultName.Total);
            tolAnova.setDf(engine.eval("anovaResult[5, 1]").asDouble());
            tolAnova.setSs(engine.eval("anovaResult[5, 2]").asDouble());
            anovaDtoList.add(tolAnova);

            result.setGrrAnovaDtos(anovaDtoList);
        }
        List<GrrSourceDto> sourceDtoList = Lists.newArrayList();
        engine.eval("sourceResult <- intgrr." + methodKey + ".getSourceResult(x, k, n, r, sig, tole, pap)");
        GrrSourceDto repSource = new GrrSourceDto();
        repSource.setName(GrrResultName.Repeatability);
        repSource.setVariation(engine.eval("sourceResult[1, 1]").asDouble());
        repSource.setSigma(engine.eval("sourceResult[1, 2]").asDouble());
        repSource.setStudyVar(engine.eval("sourceResult[1, 3]").asDouble());
        repSource.setContribution(engine.eval("sourceResult[1, 4]").asDouble());
        repSource.setTotalVariation(engine.eval("sourceResult[1, 5]").asDouble());
        repSource.setTotalTolerance(engine.eval("sourceResult[1, 6]").asDouble());
        sourceDtoList.add(repSource);

        GrrSourceDto reprodSource = new GrrSourceDto();
        reprodSource.setName(GrrResultName.Reproducibility);
        reprodSource.setVariation(engine.eval("sourceResult[2, 1]").asDouble());
        reprodSource.setSigma(engine.eval("sourceResult[2, 2]").asDouble());
        reprodSource.setStudyVar(engine.eval("sourceResult[2, 3]").asDouble());
        reprodSource.setContribution(engine.eval("sourceResult[2, 4]").asDouble());
        reprodSource.setTotalVariation(engine.eval("sourceResult[2, 5]").asDouble());
        reprodSource.setTotalTolerance(engine.eval("sourceResult[2, 6]").asDouble());
        sourceDtoList.add(reprodSource);

        if ("anova".equals(methodKey)) {
            GrrSourceDto aSource = new GrrSourceDto();
            aSource.setName(GrrResultName.Appraisers);
            aSource.setVariation(engine.eval("sourceResult[3, 1]").asDouble());
            aSource.setSigma(engine.eval("sourceResult[3, 2]").asDouble());
            aSource.setStudyVar(engine.eval("sourceResult[3, 3]").asDouble());
            aSource.setContribution(engine.eval("sourceResult[3, 4]").asDouble());
            aSource.setTotalVariation(engine.eval("sourceResult[3, 5]").asDouble());
            aSource.setTotalTolerance(engine.eval("sourceResult[3, 6]").asDouble());
            sourceDtoList.add(aSource);

            if (result.getGrrAnovaDtos() != null && result.getGrrAnovaDtos().size() == 5) {
                GrrSourceDto apSource = new GrrSourceDto();
                apSource.setName(GrrResultName.AppraisersAndParts);
                apSource.setVariation(engine.eval("sourceResult[4, 1]").asDouble());
                apSource.setSigma(engine.eval("sourceResult[4, 2]").asDouble());
                apSource.setStudyVar(engine.eval("sourceResult[4, 3]").asDouble());
                apSource.setContribution(engine.eval("sourceResult[4, 4]").asDouble());
                apSource.setTotalVariation(engine.eval("sourceResult[4, 5]").asDouble());
                apSource.setTotalTolerance(engine.eval("sourceResult[4, 6]").asDouble());
                sourceDtoList.add(apSource);
            }

            GrrSourceDto gSource = new GrrSourceDto();
            gSource.setName(GrrResultName.Gauge);
            gSource.setVariation(engine.eval("sourceResult[5, 1]").asDouble());
            gSource.setSigma(engine.eval("sourceResult[5, 2]").asDouble());
            gSource.setStudyVar(engine.eval("sourceResult[5, 3]").asDouble());
            gSource.setContribution(engine.eval("sourceResult[5, 4]").asDouble());
            gSource.setTotalVariation(engine.eval("sourceResult[5, 5]").asDouble());
            gSource.setTotalTolerance(engine.eval("sourceResult[5, 6]").asDouble());
            sourceDtoList.add(gSource);


            GrrSourceDto pSource = new GrrSourceDto();
            pSource.setName(GrrResultName.Parts);
            pSource.setVariation(engine.eval("sourceResult[6, 1]").asDouble());
            pSource.setSigma(engine.eval("sourceResult[6, 2]").asDouble());
            pSource.setStudyVar(engine.eval("sourceResult[6, 3]").asDouble());
            pSource.setContribution(engine.eval("sourceResult[6, 4]").asDouble());
            pSource.setTotalVariation(engine.eval("sourceResult[6, 5]").asDouble());
            pSource.setTotalTolerance(engine.eval("sourceResult[6, 6]").asDouble());
            sourceDtoList.add(pSource);


            GrrSourceDto tSource = new GrrSourceDto();
            tSource.setName(GrrResultName.Total);
            tSource.setVariation(engine.eval("sourceResult[7, 1]").asDouble());
            tSource.setSigma(engine.eval("sourceResult[7, 2]").asDouble());
            tSource.setStudyVar(engine.eval("sourceResult[7, 3]").asDouble());
            tSource.setContribution(engine.eval("sourceResult[7, 4]").asDouble());
            tSource.setTotalVariation(engine.eval("sourceResult[7, 5]").asDouble());
            tSource.setTotalTolerance(engine.eval("sourceResult[7, 6]").asDouble());
            sourceDtoList.add(tSource);

            Double ndc = NDC_RATE * pSource.getSigma() / gSource.getSigma();
            result.setNumberOfDc(ndc);
        } else {
            GrrSourceDto gSource = new GrrSourceDto();
            gSource.setName(GrrResultName.Gauge);
            gSource.setVariation(engine.eval("sourceResult[3, 1]").asDouble());
            gSource.setSigma(engine.eval("sourceResult[3, 2]").asDouble());
            gSource.setStudyVar(engine.eval("sourceResult[3, 3]").asDouble());
            gSource.setContribution(engine.eval("sourceResult[3, 4]").asDouble());
            gSource.setTotalVariation(engine.eval("sourceResult[3, 5]").asDouble());
            gSource.setTotalTolerance(engine.eval("sourceResult[3, 6]").asDouble());
            sourceDtoList.add(gSource);


            GrrSourceDto pSource = new GrrSourceDto();
            pSource.setName(GrrResultName.Parts);
            pSource.setVariation(engine.eval("sourceResult[4, 1]").asDouble());
            pSource.setSigma(engine.eval("sourceResult[4, 2]").asDouble());
            pSource.setStudyVar(engine.eval("sourceResult[4, 3]").asDouble());
            pSource.setContribution(engine.eval("sourceResult[4, 4]").asDouble());
            pSource.setTotalVariation(engine.eval("sourceResult[4, 5]").asDouble());
            pSource.setTotalTolerance(engine.eval("sourceResult[4, 6]").asDouble());
            sourceDtoList.add(pSource);


            GrrSourceDto tSource = new GrrSourceDto();
            tSource.setName(GrrResultName.Total);
            tSource.setVariation(engine.eval("sourceResult[5, 1]").asDouble());
            tSource.setSigma(engine.eval("sourceResult[5, 2]").asDouble());
            tSource.setStudyVar(engine.eval("sourceResult[5, 3]").asDouble());
            tSource.setContribution(engine.eval("sourceResult[5, 4]").asDouble());
            tSource.setTotalVariation(engine.eval("sourceResult[5, 5]").asDouble());
            tSource.setTotalTolerance(engine.eval("sourceResult[5, 6]").asDouble());
            sourceDtoList.add(tSource);

            Double ndc = NDC_RATE * pSource.getSigma() / gSource.getSigma();
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
}
