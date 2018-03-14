package com.dmsoft.firefly.plugin.spc.handler;

/**
 * param keys for spc handler
 *
 * @author Can Guan
 */
public class ParamKeys {
    public static final String PROJECT_NAME_LIST = "projectNameList";
    public static final String TEST_ITEM_WITH_TYPE_DTO_LIST = "testItemWithTypeDtoList";
    public static final String ROW_DATA_DTO_LIST = "rowDataDtoList";
    public static final String SEARCH_DATA_FRAME = "searchDataFrame";
    public static final String SEARCH_CONDITION_DTO_LIST = "searchConditionDtoList";
    public static final String SPC_ANALYSIS_CONFIG_DTO = "spcAnalysisConfigDto";

    //handler
    public static final String FIND_TEST_DATA_HANDLER = "findTestDataHandler";
    public static final String DATA_FRAME_HANDLER = "dataFrameHandler";
    public static final String SPC_STATS_RESULT_HANDLER = "spcStatsResultHandler";
    public static final String SPC_CHART_RESULT_HANDLER = "spcChartResultHandler";
    public static final String FIND_SPC_SETTING_HANDLER = "findSpcSettingHandler";

    //pipeline
    public static final String SPC_ANALYSIS_JOB_PIPELINE = "spcAnalysisJobPipeline";
    public static final String SPC_REFRESH_JOB_PIPELINE = "spcRefreshJobPipeline";
    public static final String FIND_SPC_SETTING_DATA_JOP_PIPELINE = "findSpcSettingDataJobPipeline";

    public static final String SPC_ANALYSIS_CONDITION_KEY = "analysisKey";

}
