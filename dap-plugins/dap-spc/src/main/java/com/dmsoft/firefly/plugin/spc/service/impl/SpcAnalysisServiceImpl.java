package com.dmsoft.firefly.plugin.spc.service.impl;

import com.dmsoft.firefly.plugin.spc.SpcPlugin;
import com.dmsoft.firefly.plugin.spc.dto.SpcAnalysisConfigDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.*;
import com.dmsoft.firefly.plugin.spc.service.SpcAnalysisService;
import com.dmsoft.firefly.plugin.spc.utils.SpcChartType;
import com.dmsoft.firefly.plugin.spc.utils.SpcExceptionCode;
import com.dmsoft.firefly.plugin.spc.utils.SpcFxmlAndLanguageUtils;
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
 * impl class for spc analysis
 *
 * @author Can Guan
 */
@Analysis
public class SpcAnalysisServiceImpl implements SpcAnalysisService, IAnalysis {
    private static Logger logger = LoggerFactory.getLogger(SpcAnalysisServiceImpl.class);
    private Rengine privateEngine = null;

    @Override
    public SpcStatsResultDto analyzeStatsResult(SpcAnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        logger.debug("Analyzing SPC stats result ...");
        SpcStatsResultDto resultDto = new SpcStatsResultDto();
        try {
            if (dataDto.getDataList() == null || dataDto.getDataList().isEmpty()) {
                if (DAPStringUtils.isNumeric(dataDto.getLsl())) {
                    resultDto.setLsl(Double.parseDouble(dataDto.getLsl()));
                }
                if (DAPStringUtils.isNumeric(dataDto.getUsl())) {
                    resultDto.setUsl(Double.parseDouble(dataDto.getUsl()));
                }
                return resultDto;
            }
            Rengine engine = prepareEngine(dataDto.getDataList(), configDto);
            if (DAPStringUtils.isNumeric(dataDto.getLsl())) {
                engine.assign("lsl", new double[]{Double.valueOf(dataDto.getLsl())});
                resultDto.setLsl(Double.valueOf(dataDto.getLsl()));
            } else {
                engine.assign("lsl", new double[]{Double.NaN});
            }
            if (DAPStringUtils.isNumeric(dataDto.getUsl())) {
                engine.assign("usl", new double[]{Double.valueOf(dataDto.getUsl())});
                resultDto.setUsl(Double.valueOf(dataDto.getUsl()));
            } else {
                engine.assign("usl", new double[]{Double.NaN});
            }
            engine.eval("DSResult <- intspc.normal.getDSResult(x)");

            double samples = engine.eval("DSResult$Samples").asInt();
            if (!Double.isNaN(samples)) {
                resultDto.setSamples(samples);
            }
            double mean = engine.eval("DSResult$Mean").asDouble();
            if (!Double.isNaN(mean)) {
                resultDto.setAvg(mean);
            }
            double sd = engine.eval("DSResult$SD").asDouble();
            if (!Double.isNaN(sd)) {
                resultDto.setStDev(sd);
            }
            double max = engine.eval("DSResult$Max").asDouble();
            if (!Double.isNaN(max)) {
                resultDto.setMax(max);
            }
            double min = engine.eval("DSResult$Min").asDouble();
            if (!Double.isNaN(min)) {
                resultDto.setMin(min);
            }
            double range = engine.eval("DSResult$Range").asDouble();
            if (!Double.isNaN(range)) {
                resultDto.setRange(range);
            }
            double plus3sd = engine.eval("DSResult$Plus3SD").asDouble();
            if (!Double.isNaN(plus3sd)) {
                resultDto.setUcl(plus3sd);
            }
            double min3sd = engine.eval("DSResult$Minus3SD").asDouble();
            if (!Double.isNaN(min3sd)) {
                resultDto.setLcl(min3sd);
            }
            double kurtosis = engine.eval("DSResult$Kurtosis").asDouble();
            if (!Double.isNaN(kurtosis)) {
                resultDto.setKurtosis(kurtosis);
            }
            double skewness = engine.eval("DSResult$Skewness").asDouble();
            if (!Double.isNaN(skewness)) {
                resultDto.setSkewness(skewness);
            }

            engine.eval("PPResult <- intspc.normal.getPPResult(x, usl = usl, lsl = lsl, SD = " + sd + ", Mean = " + mean + ")");
            double pp = engine.eval("PPResult$Pp").asDouble();
            if (!Double.isNaN(pp)) {
                resultDto.setPp(pp);
            }
            double ppl = engine.eval("PPResult$Ppl").asDouble();
            if (!Double.isNaN(ppl)) {
                resultDto.setPpl(ppl);
            }
            double ppu = engine.eval("PPResult$Ppu").asDouble();
            if (!Double.isNaN(ppu)) {
                resultDto.setPpu(ppu);
            }
            double ppk = engine.eval("PPResult$Ppk").asDouble();
            if (!Double.isNaN(ppk)) {
                resultDto.setPpk(ppk);
            }
            double oPPM = engine.eval("PPResult$OverallPPM").asDouble();
            if (!Double.isNaN(oPPM)) {
                resultDto.setOverallPPM(oPPM);
            }

            engine.eval("PCResult <- intspc.normal.getPCResult(x, SubGroupSize = subgroupSize, usl = usl, lsl = lsl)");
            double ca = engine.eval("PCResult$Ca").asDouble();
            if (!Double.isNaN(ca)) {
                resultDto.setCa(ca);
            }
            double cp = engine.eval("PCResult$Cp").asDouble();
            if (!Double.isNaN(cp)) {
                resultDto.setCp(cp);
            }
            double cpl = engine.eval("PCResult$Cpl").asDouble();
            if (!Double.isNaN(cpl)) {
                resultDto.setCpl(cpl);
            }
            double cpu = engine.eval("PCResult$Cpu").asDouble();
            if (!Double.isNaN(cpu)) {
                resultDto.setCpu(cpu);
            }
            double cpk = engine.eval("PCResult$Cpk").asDouble();
            if (!Double.isNaN(cpk)) {
                resultDto.setCpk(cpk);
            }
            double wPPM = engine.eval("PCResult$WithinPPM").asDouble();
            if (!Double.isNaN(wPPM)) {
                resultDto.setWithinPPM(wPPM);
            }
            if (resultDto.getUsl() != null && resultDto.getLsl() != null) {
                resultDto.setCenter((resultDto.getUsl() + resultDto.getLsl()) / 2);
            }
            SemaphoreUtils.releaseSemaphore(engine);
        } catch (Exception e) {
            SemaphoreUtils.releaseSemaphore(privateEngine);
            logger.error("Analyze SPC stats result error, exception message = {}", e.getMessage());
            throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11005));
        }
        logger.info("Analyze SPC stats result done ...");
        return resultDto;
    }

    @Override
    public NDCResultDto analyzeNDCResult(SpcAnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        logger.debug("Analyzing SPC ND chart result ...");
        NDCResultDto resultDto = new NDCResultDto();
        try {
            if (dataDto.getDataList() == null || dataDto.getDataList().isEmpty()) {
                return resultDto;
            }
            Rengine engine = prepareEngine(dataDto.getDataList(), configDto);
            if (dataDto.getNdcMax() != null) {
                engine.assign("ndcMax", new double[]{dataDto.getNdcMax()});
            } else {
                engine.assign("ndcMax", new double[]{Double.NaN});
            }
            if (dataDto.getNdcMin() != null) {
                engine.assign("ndcMin", new double[]{dataDto.getNdcMin()});
            } else {
                engine.assign("ndcMin", new double[]{Double.NaN});
            }
            resultDto = getNDCResult(engine, dataDto);
            SemaphoreUtils.releaseSemaphore(engine);
            logger.info("Analyze SPC ND chart result done.");
        } catch (Exception e) {
            SemaphoreUtils.releaseSemaphore(privateEngine);
            logger.error("Analyze SPC ND chart result error, exception message = {}", e.getMessage());
            throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11005));
        }
        return resultDto;

    }

    @Override
    public RunCResultDto analyzeRunCResult(SpcAnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        logger.debug("Analyzing SPC RUN chart result ...");
        RunCResultDto resultDto = new RunCResultDto();
        try {
            if (dataDto.getDataList() == null || dataDto.getDataList().isEmpty()) {
                return resultDto;
            }
            Rengine engine = prepareEngine(dataDto.getDataList(), configDto);
            resultDto = getRunCResult(engine, dataDto);
            SemaphoreUtils.releaseSemaphore(engine);
            logger.info("Analyze SPC RUN chart result done.");
        } catch (Exception e) {
            SemaphoreUtils.releaseSemaphore(privateEngine);
            logger.error("Analyze SPC RUN chart result error, exception message = {}", e.getMessage());
            throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11005));
        }
        return resultDto;
    }

    @Override
    public SpcControlChartDto analyzeXbarCResult(SpcAnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        logger.debug("Analyzing SPC Xbar chart result ...");
        SpcControlChartDto resultDto = new SpcControlChartDto();
        try {
            if (dataDto.getDataList() == null || dataDto.getDataList().isEmpty()) {
                return resultDto;
            }
            Rengine engine = prepareEngine(dataDto.getDataList(), configDto);
            resultDto = getXbarCResult(engine);
            SemaphoreUtils.releaseSemaphore(engine);
            logger.info("Analyze SPC Xbar chart result done.");
        } catch (Exception e) {
            SemaphoreUtils.releaseSemaphore(privateEngine);
            logger.error("Analyze SPC Xbar chart result error, exception message = {}", e.getMessage());
            throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11005));
        }
        return resultDto;
    }

    @Override
    public SpcControlChartDto analyzeRangeCResult(SpcAnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        logger.debug("Analyzing SPC Range chart result ...");
        SpcControlChartDto result = new SpcControlChartDto();
        try {
            if (dataDto.getDataList() == null || dataDto.getDataList().isEmpty()) {
                return result;
            }
            Rengine engine = prepareEngine(dataDto.getDataList(), configDto);
            result = getRangeCResult(engine);
            SemaphoreUtils.releaseSemaphore(engine);
            logger.info("Analyze SPC Range chart result done.");
        } catch (Exception e) {
            SemaphoreUtils.releaseSemaphore(privateEngine);
            logger.error("Analyze SPC Range chart result error, exception message = {}", e.getMessage());
            throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11005));
        }
        return result;
    }

    @Override
    public SpcControlChartDto analyzeSdCResult(SpcAnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        logger.debug("Analyzing SPC SD chart result ...");
        SpcControlChartDto result = new SpcControlChartDto();
        try {
            if (dataDto.getDataList() == null || dataDto.getDataList().isEmpty()) {
                return result;
            }
            Rengine engine = prepareEngine(dataDto.getDataList(), configDto);
            result = getSdCResult(engine);
            SemaphoreUtils.releaseSemaphore(engine);
            logger.info("Analyze SPC SD chart result done.");
        } catch (Exception e) {
            SemaphoreUtils.releaseSemaphore(privateEngine);
            logger.error("Analyze SPC SD error, exception message = {}", e.getMessage());
            throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11005));
        }
        return result;
    }

    @Override
    public SpcControlChartDto analyzeMeCResult(SpcAnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        logger.debug("Analyzing SPC ME chart result ...");
        SpcControlChartDto result = new SpcControlChartDto();
        try {
            if (dataDto.getDataList() == null || dataDto.getDataList().isEmpty()) {
                return result;
            }
            Rengine engine = prepareEngine(dataDto.getDataList(), configDto);
            result = getMeCResult(engine);
            SemaphoreUtils.releaseSemaphore(engine);
            logger.info("Analyze SPC ME chart result done.");
        } catch (Exception e) {
            SemaphoreUtils.releaseSemaphore(privateEngine);
            logger.error("Analyze SPC");
            throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11005));
        }
        return result;
    }

    @Override
    public SpcControlChartDto analyzeMrCResult(SpcAnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        logger.debug("Analyzing SPC MR chart result ...");
        SpcControlChartDto result = new SpcControlChartDto();
        try {
            if (dataDto.getDataList() == null || dataDto.getDataList().isEmpty()) {
                return result;
            }
            Rengine engine = prepareEngine(dataDto.getDataList(), configDto);
            result = getMrCResult(engine);
            SemaphoreUtils.releaseSemaphore(engine);
            logger.info("Analyzing SPC MR chart result done.");
        } catch (Exception e) {
            SemaphoreUtils.releaseSemaphore(privateEngine);
            logger.error("Analyze SPC MR chart result error, exception message = {}", e.getMessage());
            throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11005));
        }

        return result;
    }

    @Override
    public BoxCResultDto analyzeBoxCResult(SpcAnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        logger.debug("Analyzing SPC BOX chart result ...");
        BoxCResultDto result = new BoxCResultDto();
        try {
            if (dataDto.getDataList() == null || dataDto.getDataList().isEmpty()) {
                return result;
            }
            Rengine engine = prepareEngine(dataDto.getDataList(), configDto);
            result = getBoxCResult(engine);
            SemaphoreUtils.releaseSemaphore(engine);
            logger.info("Analyze SPC BOX chart result done.");
        } catch (Exception e) {
            SemaphoreUtils.releaseSemaphore(privateEngine);
            logger.error("Analyze SPC BOX chart result error, exception message = {}", e.getMessage());
            throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11005));
        }
        return result;
    }

    @Override
    public SpcChartResultDto analyzeSpcChartResult(SpcAnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        logger.debug("Analyzing SPC charts result ...");
        SpcChartResultDto result = new SpcChartResultDto();
        try {
            if (dataDto.getDataList() == null || dataDto.getDataList().isEmpty()) {
                return result;
            }
            Rengine engine = prepareEngine(dataDto.getDataList(), configDto);
            engine.assign("ndcMax", new double[]{dataDto.getNdcMax()});
            engine.assign("ndcMin", new double[]{dataDto.getNdcMin()});
            result.setNdcResult(getNDCResult(engine, dataDto));
            result.setRunCResult(getRunCResult(engine, dataDto));
            result.setXbarCResult(getXbarCResult(engine));
            result.setRangeCResult(getRangeCResult(engine));
            result.setSdCResult(getSdCResult(engine));
            result.setMedianCResult(getMeCResult(engine));
            result.setBoxCResult(getBoxCResult(engine));
            result.setMrCResult(getMrCResult(engine));
            SemaphoreUtils.releaseSemaphore(engine);
            logger.info("Analyze SPC charts result done.");
        } catch (Exception e) {
            SemaphoreUtils.releaseSemaphore(privateEngine);
            logger.error("Analyze SPC charts result error, exception message = {}", e.getMessage());
            throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11005));
        }
        return result;
    }

    @Override
    public SpcChartResultDto analyzeSpcChartResult(SpcAnalysisDataDto dataDto, List<SpcChartType> requiredCharts, SpcAnalysisConfigDto configDto) {
        logger.debug("Analyzing SPC charts result ...");
        SpcChartResultDto result = new SpcChartResultDto();
        try {
            if (dataDto.getDataList() == null || dataDto.getDataList().isEmpty()) {
                return result;
            }
            Rengine engine = prepareEngine(dataDto.getDataList(), configDto);
            for (SpcChartType chartType : requiredCharts) {
                switch (chartType) {
                    case NDC:
                        result.setNdcResult(getNDCResult(engine, dataDto));
                        break;
                    case RunC:
                        result.setRunCResult(getRunCResult(engine, dataDto));
                        break;
                    case XbarC:
                        result.setXbarCResult(getXbarCResult(engine));
                        break;
                    case SDC:
                        result.setSdCResult(getSdCResult(engine));
                        break;
                    case RangeC:
                        result.setRangeCResult(getRangeCResult(engine));
                        break;
                    case MeC:
                        result.setMedianCResult(getMeCResult(engine));
                        break;
                    case MRC:
                        result.setMrCResult(getMrCResult(engine));
                        break;
                    case BoxC:
                        result.setBoxCResult(getBoxCResult(engine));
                        break;
                    default:
                        break;
                }
            }
            SemaphoreUtils.releaseSemaphore(engine);
            logger.info("Analyze SPC charts result done.");
        } catch (Exception e) {
            SemaphoreUtils.releaseSemaphore(privateEngine);
            logger.error("Analyze SPC charts result error, exception message = {}", e.getMessage());
            throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11005));
        }
        return result;
    }

    private Rengine prepareEngine(List<Double> dataList, SpcAnalysisConfigDto configDto) {
        if (this.privateEngine == null) {
            privateEngine = RuntimeContext.getBean(Rengine.class);
            if (privateEngine == null) {
                privateEngine = new Rengine(new String[]{"--no-save"}, false, null);
                privateEngine.end();
                RuntimeContext.registerBean(Rengine.class, privateEngine);
            }
            String spcPathName = "rscripts/spc.R";
            String scriptPath = RuntimeContext.getBean(PluginContext.class).getEnabledPluginInfo(SpcPlugin.SPC_PLUGIN_NAME).getFolderPath() + "/" + spcPathName;
            scriptPath = scriptPath.replace('\\', '/');
            privateEngine.eval("source(\"" + scriptPath + "\")");
        }
        SemaphoreUtils.lockSemaphore(privateEngine);
        privateEngine.eval("rm(list=setdiff(ls(), ls()[grep(\"^int\", ls())]))");
        double[] dataArray = new double[dataList.size()];
        for (int i = 0; i < dataList.size(); i++) {
            dataArray[i] = dataList.get(i);
        }
        privateEngine.assign("x", dataArray);
        privateEngine.assign("subgroupSize", new double[]{configDto.getSubgroupSize()});
        privateEngine.assign("intervalNum", new double[]{configDto.getIntervalNumber()});
        return privateEngine;
    }

    private NDCResultDto getNDCResult(Rengine engine, SpcAnalysisDataDto dataDto) {
        engine.eval("NDCResult <- intspc.normal.getNDCResult(x, intervalNum, Max = ndcMax, Min = ndcMin)");
        NDCResultDto result = new NDCResultDto();
        double[] ndx = engine.eval("NDCResult$Breaks").asDoubleArray();
        double[] ndy = engine.eval("NDCResult$Counts").asDoubleArray();
        double[] ndcls = engine.eval("NDCResult$CLs").asDoubleArray();
        double[] ndccx = engine.eval("NDCResult$Normal.X").asDoubleArray();
        double[] ndccy = engine.eval("NDCResult$Normal.Y").asDoubleArray();
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

    private RunCResultDto getRunCResult(Rengine engine, SpcAnalysisDataDto dataDto) {
        engine.eval("RunCResult <- intspc.normal.getRunCResult(x)");
        RunCResultDto result = new RunCResultDto();
        double[] runy = engine.eval("RunCResult$Values").asDoubleArray();
        double[] runcls = engine.eval("RunCResult$CLs").asDoubleArray();

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

    private SpcControlChartDto getXbarCResult(Rengine engine) {
        engine.eval("XBCResult <- intspc.normal.getXBCResult(x, subgroupSize)");
        SpcControlChartDto result = new SpcControlChartDto();
        double[] xbary = engine.eval("XBCResult$Values").asDoubleArray();
        double xbarcl = engine.eval("XBCResult$CenterLine").asDouble();
        double[] xbarucl = engine.eval("XBCResult$UCL").asDoubleArray();
        double[] xbarlcl = engine.eval("XBCResult$LCL").asDoubleArray();
        result.setY(convert(xbary));
        result.setX(generate(xbary.length));
        result.setCl(xbarcl);
        result.setUcl(convert(xbarucl));
        result.setLcl(convert(xbarlcl));
        return result;
    }

    private SpcControlChartDto getRangeCResult(Rengine engine) {
        engine.eval("RangeCResult <- intspc.normal.getRangeCResult(x, subgroupSize)");
        SpcControlChartDto result = new SpcControlChartDto();
        double[] rangey = engine.eval("RangeCResult$Values").asDoubleArray();
        double rangecl = engine.eval("RangeCResult$CenterLine").asDouble();
        double[] rangeucl = engine.eval("RangeCResult$UCL").asDoubleArray();
        double[] rangelcl = engine.eval("RangeCResult$LCL").asDoubleArray();
        result.setY(convert(rangey));
        result.setX(generate(rangey.length));
        result.setCl(rangecl);
        result.setUcl(convert(rangeucl));
        result.setLcl(convert(rangelcl));
        return result;
    }

    private SpcControlChartDto getSdCResult(Rengine engine) {
        engine.eval("SDCResult <- intspc.normal.getSDCResult(x, subgroupSize)");
        SpcControlChartDto result = new SpcControlChartDto();
        double[] sdy = engine.eval("SDCResult$Values").asDoubleArray();
        double sdcl = engine.eval("SDCResult$CenterLine").asDouble();
        double[] sducl = engine.eval("SDCResult$UCL").asDoubleArray();
        double[] sdlcl = engine.eval("SDCResult$LCL").asDoubleArray();
        result.setY(convert(sdy));
        result.setX(generate(sdy.length));
        result.setLcl(convert(sdlcl));
        result.setUcl(convert(sducl));
        result.setCl(sdcl);
        return result;
    }

    private SpcControlChartDto getMeCResult(Rengine engine) {
        engine.eval("MECResult <- intspc.normal.getMECResult(x, subgroupSize)");
        SpcControlChartDto result = new SpcControlChartDto();
        double[] mey = engine.eval("MECResult$Values").asDoubleArray();
        double mecl = engine.eval("MECResult$CenterLine").asDouble();
        double[] melcl = engine.eval("MECResult$LCL").asDoubleArray();
        double[] meucl = engine.eval("MECResult$UCL").asDoubleArray();
        result.setY(convert(mey));
        result.setX(generate(mey.length));
        result.setCl(mecl);
        result.setUcl(convert(meucl));
        result.setLcl(convert(melcl));
        return result;
    }

    private SpcControlChartDto getMrCResult(Rengine engine) {
        engine.eval("MECResult <- intspc.normal.getMRCResult(x, subgroupSize)");
        SpcControlChartDto result = new SpcControlChartDto();
        double[] mey = engine.eval("MECResult$Values").asDoubleArray();
        double mecl = engine.eval("MECResult$CenterLine").asDouble();
        double[] melcl = engine.eval("MECResult$LCL").asDoubleArray();
        double[] meucl = engine.eval("MECResult$UCL").asDoubleArray();
        result.setY(convert(mey));
        result.setX(generate(mey.length));
        result.setUcl(convert(meucl));
        result.setLcl(convert(melcl));
        result.setCl(mecl);
        return result;
    }

    private BoxCResultDto getBoxCResult(Rengine engine) {
        engine.eval("BoxCResult <- intspc.normal.getBoxCResult(x, subgroupSize)");
        double[] boxy = engine.eval("BoxCResult$Values").asDoubleArray();
        double[] outs = engine.eval("BoxCResult$Outs").asDoubleArray();
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
                while (j < outs.length && outs[j] == i) {
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
