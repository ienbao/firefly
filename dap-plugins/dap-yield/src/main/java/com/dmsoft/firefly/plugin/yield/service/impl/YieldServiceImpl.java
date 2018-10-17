package com.dmsoft.firefly.plugin.yield.service.impl;

import com.dmsoft.firefly.plugin.yield.dto.*;
import com.dmsoft.firefly.plugin.yield.service.YieldService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import com.dmsoft.firefly.sdk.job.core.JobEvent;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class YieldServiceImpl implements YieldService {

    private static Logger logger = LoggerFactory.getLogger(YieldServiceImpl.class);

    @Override
    public List<YieldResultDto> getResult(SearchDataFrame searchDataFrame, List<SearchConditionDto> searchConditions, YieldAnalysisConfigDto configDto) {
        return null;
    }

    @Override
    public List<YieldViewDataDto> getViewData(SearchDataFrame searchDataFrame, SearchViewDataConditionDto searchViewDataConditionDto) {

        logger.debug("Getting Yield result...");
//        if (searchDataFrame == null || searchViewDataConditionDto == null ) {
//            throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11002));
//        }
        return null;
    }

    private List<YieldOverviewDto> getOverviewResult(SearchDataFrame searchDataFrame, List<SearchConditionDto> searchConditions,
                                             YieldAnalysisConfigDto configDto){
        return null;
    }

    private List<YieldTotalProcessesDto> getTotalProcessesResult(SearchDataFrame searchDataFrame, List<SearchConditionDto> searchConditions,
                                                                 YieldAnalysisConfigDto configDto){
        logger.debug("Getting Yield totalProcesses result...");
        if (searchDataFrame == null || searchConditions == null || configDto == null) {
            pushProgress(100);
            throw new ApplicationException();
        }
        //TODO td SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11002)
        List<YieldTotalProcessesDto> result = Lists.newArrayList();
        List<YieldAnalysisDataDto> yieldAnalysisDataDtoList = Lists.newArrayList();
        for (SearchConditionDto searchConditionDto : searchConditions) {
            YieldAnalysisDataDto yieldAnalysisDataDto = new YieldAnalysisDataDto();
            
        }




        return null;
    }

    private List<YieldNTFChartDto> getNTFChartResult(SearchDataFrame searchDataFrame, List<SearchConditionDto> searchConditions,
                                             YieldAnalysisConfigDto configDto){
        return null;
    }

    private void pushProgress(int progress) {
        JobContext context = RuntimeContext.getBean(JobManager.class).findJobContext(Thread.currentThread());
        if (context != null) {
            context.pushEvent(new JobEvent("YieldService", progress + 0.0, null));
        }
    }
}
