package com.dmsoft.firefly.plugin.spc.service;

import com.dmsoft.firefly.plugin.spc.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcAnalysisConfigDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcChartDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcStatsDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.AnalysisDataDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcChartResultDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcStatsResultDto;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcService;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcAnalysisService;
import com.dmsoft.firefly.plugin.spc.utils.SpcExceptionCode;
import com.dmsoft.firefly.plugin.spc.utils.SpcExceptionParser;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.Validate;

import java.util.List;

/**
 * impl class for spc service
 *
 * @author Can Guan, Ethan Yang
 */
public class SpcServiceImpl implements SpcService {
    private SpcAnalysisService analysisService;

    @Override
    public List<SpcStatsDto> getStatisticalResult(SearchDataFrame searchDataFrame, List<String> testItemNames, List<SearchConditionDto> searchConditions, SpcAnalysisConfigDto configDto) {
        /*
        1.Verify the validity of the parameters
        2.Get analysis statistical result from R
         */
        if (searchDataFrame == null || testItemNames == null || searchConditions == null || configDto == null) {
            throw new ApplicationException(SpcExceptionParser.parser(SpcExceptionCode.ERR_11001));
        }
        List<SpcStatsDto> result = Lists.newArrayList();
        for (String testItem : testItemNames) {
            for (SearchConditionDto searchConditionDto : searchConditions) {
                List<String> searchRowKeys = searchDataFrame.getSearchRowKey(searchConditionDto.getCondition());
                List<String> datas = searchDataFrame.getDataValue(testItem, searchRowKeys);
                AnalysisDataDto analysisDataDto = new AnalysisDataDto();
                analysisDataDto.setDataList(datas);
                analysisDataDto.setUsl(searchConditionDto.getCusUsl());
                analysisDataDto.setLsl(searchConditionDto.getCusLsl());
                SpcStatsResultDto resultDto = getAnalysisService().analyzeStatsResult(analysisDataDto, configDto);
                SpcStatsDto statsDto = new SpcStatsDto();
                statsDto.setStatsResultDto(resultDto);
                statsDto.setKey(searchConditionDto.getKey());
                statsDto.setItemName(testItem);
                statsDto.setCondition(searchConditionDto.getCondition());
                result.add(statsDto);
            }
        }
        return result;
    }

    @Override
    public List<SpcChartDto> getChartResult(SearchDataFrame searchDataFrame, List<String> testItemNames, List<SearchConditionDto> searchConditions, SpcAnalysisConfigDto configDto) {
        /*
        1.Verify the validity of the parameters
        2.Get analysis chart result from R
         */
        if (searchDataFrame == null || testItemNames == null || searchConditions == null || configDto == null) {
            throw new ApplicationException(SpcExceptionParser.parser(SpcExceptionCode.ERR_11001));
        }
        List<SpcChartDto> result = Lists.newArrayList();
        for (String testItem : testItemNames) {
            for (SearchConditionDto searchConditionDto : searchConditions) {
                List<String> searchRowKeys = searchDataFrame.getSearchRowKey(searchConditionDto.getCondition());
                List<String> datas = searchDataFrame.getDataValue(testItem, searchRowKeys);
                AnalysisDataDto analysisDataDto = new AnalysisDataDto();
                analysisDataDto.setDataList(datas);
                analysisDataDto.setUsl(searchConditionDto.getCusUsl());
                analysisDataDto.setLsl(searchConditionDto.getCusLsl());
                SpcChartResultDto resultDto = getAnalysisService().analyzeSpcChartResult(analysisDataDto, configDto);
                SpcChartDto chartDto = new SpcChartDto();
                chartDto.setResultDto(resultDto);
                chartDto.setKey(searchConditionDto.getKey());
                chartDto.setItemName(testItem);
                chartDto.setCondition(searchConditionDto.getCondition());
                List<String> analyzedRowKeys = Lists.newArrayList();
                for (int i = 0; i < chartDto.getResultDto().getRunCResult().getIsAnalyzed().length; i++) {
                    if (chartDto.getResultDto().getRunCResult().getIsAnalyzed()[i]) {
                        analyzedRowKeys.add(searchRowKeys.get(i));
                    }
                }
                result.add(chartDto);
            }
        }
        return null;
    }

    private SpcAnalysisService getAnalysisService() {
        Validate.validState(this.analysisService != null, "Analysis Service is not injected");
        return this.analysisService;
    }

    public void setAnalysisService(SpcAnalysisService analysisService) {
        this.analysisService = analysisService;
    }
}
