package com.dmsoft.firefly.plugin.spc.service.impl;

import com.dmsoft.firefly.plugin.spc.dto.SpcAnalysisConfigDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcAnalysisStatsResultDto;
import com.dmsoft.firefly.sdk.plugin.annotation.Analysis;
import com.dmsoft.firefly.sdk.plugin.apis.IAnalysis;

import java.util.List;

/**
 * spc analysis interface for service
 *
 * @author Can Guan
 */
@Analysis
public interface SpcAnalysisService extends IAnalysis {
    /**
     * method analysis spc stats result
     *
     * @param data      list of data
     * @param configDto spc analysis config dto
     * @return spc analysis stats result dto
     */
    SpcAnalysisStatsResultDto analysisStatsResult(List<String> data, SpcAnalysisConfigDto configDto);
}
