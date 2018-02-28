package com.dmsoft.firefly.plugin.spc.service;

import com.dmsoft.firefly.plugin.spc.dto.SpcAnalysisConfigDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.*;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcAnalysisService;
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
    @Override
    public SpcStatsResultDto analyzeStatsResult(AnalysisDataDto dataDto, SpcAnalysisConfigDto configDto) {
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
