package com.dmsoft.firefly.plugin.spc.service;

import com.dmsoft.firefly.plugin.spc.dto.SpcAnalysisConfigDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.*;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcAnalysisService;
import com.dmsoft.firefly.plugin.spc.utils.REnConnector;
import com.dmsoft.firefly.plugin.spc.utils.SpcChartType;
import com.dmsoft.firefly.sdk.plugin.annotation.Analysis;
import com.dmsoft.firefly.sdk.plugin.apis.IAnalysis;

import java.util.List;

/**
 * impl class for spc analysis
 *
 * @author Can Guan
 */
@Analysis
public class SpcAnalysisServiceImpl implements SpcAnalysisService, IAnalysis {
    private String spcPathName = "rscript/spc.R";

    @Override
    public SpcStatsResultDto analyzeStatsResult(AnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
//        REnConnector connector = new REnConnector();
//        connector.connect();
//        String scriptPath = null;
//        if (getClass().getResource(spcPathName) != null) {
//            scriptPath = getClass().getResource(spcPathName).getPath();
//        }
//        connector.execEval();
        return null;
    }

    @Override
    public NDCResultDto analyzeNDCResult(AnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
        return null;
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
}
