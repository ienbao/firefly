package com.dmsoft.firefly.plugin.spc.service.impl;

import com.dmsoft.firefly.plugin.spc.dto.SpcAnalysisConfigDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.*;
import com.dmsoft.firefly.plugin.spc.utils.SpcChartType;

import java.util.List;

/**
 * spc analysis interface for service
 *
 * @author Can Guan
 */
public interface SpcAnalysisService {
    /**
     * method analysis spc stats result
     *
     * @param dataDto   data dto
     * @param configDto spc analysis config dto
     * @return spc analysis stats result dto
     */
    SpcStatsResultDto analyzeStatsResult(AnalysisDataDto dataDto, SpcAnalysisConfigDto configDto);

    /**
     * method to analysis nd chart result
     *
     * @param dataDto   data dto
     * @param configDto spc analysis config dto
     * @return ndc chart result dto
     */
    NDCResultDto analyzeNDCResult(AnalysisDataDto dataDto, SpcAnalysisConfigDto configDto);

    /**
     * method to analysis run chart result
     *
     * @param dataDto   data dto
     * @param configDto spc analysis config dto
     * @return run chart result dto
     */
    RunCResultDto analyzeRunCResult(AnalysisDataDto dataDto, SpcAnalysisConfigDto configDto);

    /**
     * method to analysis xbar chart result
     *
     * @param dataDto   data dto
     * @param configDto spc analysis config dto
     * @return run chart result dto
     */
    ControlChartDto analyzeXbarCResult(AnalysisDataDto dataDto, SpcAnalysisConfigDto configDto);

    /**
     * method to analysis range chart result
     *
     * @param dataDto   data dto
     * @param configDto spc analysis config dto
     * @return range chart result dto
     */
    ControlChartDto analyzeRangeCResult(AnalysisDataDto dataDto, SpcAnalysisConfigDto configDto);

    /**
     * method to analysis sd chart result
     *
     * @param dataDto   data dto
     * @param configDto spc analysis config dto
     * @return sd chart result dto
     */
    ControlChartDto analyzeSdCResult(AnalysisDataDto dataDto, SpcAnalysisConfigDto configDto);

    /**
     * method to analysis me chart result
     *
     * @param dataDto   data dto
     * @param configDto spc analysis config dto
     * @return me chart reuslt dto
     */
    ControlChartDto analyzeMeCResult(AnalysisDataDto dataDto, SpcAnalysisConfigDto configDto);

    /**
     * method to analysis mr chart result
     *
     * @param dataDto   data dto
     * @param configDto spc analysis config dto
     * @return mr chart result dto
     */
    ControlChartDto analyzeMrCResult(AnalysisDataDto dataDto, SpcAnalysisConfigDto configDto);

    /**
     * method to analysis box chart result
     *
     * @param dataDto   data dto
     * @param configDto spc analysis config dto
     * @return box chart result dto
     */
    BoxCResultDto analyzeBoxCResult(AnalysisDataDto dataDto, SpcAnalysisConfigDto configDto);

    /**
     * method to analysis all spc chart result
     *
     * @param dataDto   data dto
     * @param configDto spc analysis config dto
     * @return spc chart result dto
     */
    SpcChartResultDto analyzeSpcChartResult(AnalysisDataDto dataDto, SpcAnalysisConfigDto configDto);

    /**
     * method to analysis spc chart result depend on require chars
     *
     * @param dataDto        data dto
     * @param requiredCharts list of spc chart type
     * @param configDto      spc analysis config dto
     * @return spc chart result dto
     */
    SpcChartResultDto analyzeSpcChartResult(AnalysisDataDto dataDto, List<SpcChartType> requiredCharts, SpcAnalysisConfigDto configDto);
}
