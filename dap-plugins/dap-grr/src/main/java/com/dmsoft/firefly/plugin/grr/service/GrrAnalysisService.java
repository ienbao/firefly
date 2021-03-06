package com.dmsoft.firefly.plugin.grr.service;

import com.dmsoft.firefly.plugin.grr.dto.analysis.*;

/**
 * interface class for grr analysis
 *
 * @author Can Guan
 */
public interface GrrAnalysisService {
    /**
     * method to analyze grr summary result
     *
     * @param analysisDataDto grr analysis data dto
     * @param configDto       grr config dto
     * @return grr summary result dto
     */
    GrrSummaryResultDto analyzeSummaryResult(GrrAnalysisDataDto analysisDataDto, GrrAnalysisConfigDto configDto);

    /**
     * method to analyze grr detail result
     *
     * @param analysisDataDto grr analysis data dto
     * @param configDto       grr config dto
     * @return grr detail result dto
     */
    GrrDetailResultDto analyzeDetailResult(GrrAnalysisDataDto analysisDataDto, GrrAnalysisConfigDto configDto);

    /**
     * method to analyze grr detail result for export
     *
     * @param analysisDataDto grr analysis data dto
     * @param configDto grr config dto
     * @return grr detail result dto
     */
    GrrExportDetailResultDto analyzeExportDetailResult(GrrAnalysisDataDto analysisDataDto, GrrAnalysisConfigDto configDto);
}
