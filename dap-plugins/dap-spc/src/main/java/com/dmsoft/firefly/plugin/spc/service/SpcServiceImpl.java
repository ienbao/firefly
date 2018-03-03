package com.dmsoft.firefly.plugin.spc.service;

import com.dmsoft.firefly.plugin.spc.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcAnalysisConfigDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcChartDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcStatsDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.AnalysisDataDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcChartResultDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcStatsResultDto;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcAnalysisService;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcService;
import com.dmsoft.firefly.plugin.spc.utils.SpcExceptionCode;
import com.dmsoft.firefly.plugin.spc.utils.SpcExceptionParser;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
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
    public List<SpcStatsDto> getStatisticalResult(SearchDataFrame searchDataFrame, List<SearchConditionDto> searchConditions, SpcAnalysisConfigDto configDto) {
        /*
        1.Verify the validity of the parameters
        2.Get analysis statistical result from R
         */
        if (searchDataFrame == null || searchConditions == null || configDto == null) {
            throw new ApplicationException(SpcExceptionParser.parser(SpcExceptionCode.ERR_11001));
        }
        List<SpcStatsDto> result = Lists.newArrayList();
        List<AnalysisDataDto> analysisDataDtoList = Lists.newArrayList();
        for (SearchConditionDto searchConditionDto : searchConditions) {
            AnalysisDataDto analysisDataDto = new AnalysisDataDto();
            List<String> searchRowKeys = searchDataFrame.getSearchRowKey(searchConditionDto.getCondition());
            List<String> datas = searchDataFrame.getDataValue(searchConditionDto.getItemName(), searchRowKeys);
            List<Double> doubleList = Lists.newArrayList();
            for (String s : datas) {
                if (DAPStringUtils.isNumeric(s)) {
                    doubleList.add(Double.valueOf(s));
                }
            }
            analysisDataDto.setLsl(searchConditionDto.getCusLsl());
            analysisDataDto.setUsl(searchConditionDto.getCusUsl());
            analysisDataDto.setDataList(doubleList);
            analysisDataDtoList.add(analysisDataDto);
        }
        for (int i = 0; i < analysisDataDtoList.size(); i++) {
            SpcStatsResultDto resultDto = getAnalysisService().analyzeStatsResult(analysisDataDtoList.get(i), configDto);
            SpcStatsDto statsDto = new SpcStatsDto();
            statsDto.setStatsResultDto(resultDto);
            statsDto.setKey(searchConditions.get(i).getKey());
            statsDto.setItemName(searchConditions.get(i).getItemName());
            statsDto.setCondition(searchConditions.get(i).getCondition());
            result.add(statsDto);
        }
        return result;
    }

    @Override
    public List<SpcChartDto> getChartResult(SearchDataFrame searchDataFrame, List<SearchConditionDto> searchConditions, SpcAnalysisConfigDto configDto) {
        /*
        1.Verify the validity of the parameters
        2.Get analysis chart result from R
         */
        if (searchDataFrame == null || searchConditions == null || configDto == null) {
            throw new ApplicationException(SpcExceptionParser.parser(SpcExceptionCode.ERR_11001));
        }
        List<SpcChartDto> result = Lists.newArrayList();
        List<AnalysisDataDto> analysisDataDtoList = Lists.newArrayList();
        List<List<String>> analyzedRowKeys = Lists.newArrayList();
        Double ndcMax = Double.NEGATIVE_INFINITY;
        Double ndcMin = Double.POSITIVE_INFINITY;
        for (SearchConditionDto searchConditionDto : searchConditions) {
            AnalysisDataDto analysisDataDto = new AnalysisDataDto();
            List<String> searchRowKeys = searchDataFrame.getSearchRowKey(searchConditionDto.getCondition());
            List<String> datas = searchDataFrame.getDataValue(searchConditionDto.getItemName(), searchRowKeys);
            List<String> rowKeys = Lists.newArrayList();
            List<Double> doubleList = Lists.newArrayList();
            for (String s : datas) {
                if (DAPStringUtils.isNumeric(s)) {
                    Double value = Double.valueOf(s);
                    if (value > ndcMax) {
                        ndcMax = value;
                    }
                    if (value < ndcMin) {
                        ndcMin = value;
                    }
                    rowKeys.add(s);
                    doubleList.add(value);
                }
            }
            analyzedRowKeys.add(rowKeys);
            analysisDataDto.setLsl(searchConditionDto.getCusLsl());
            analysisDataDto.setUsl(searchConditionDto.getCusUsl());
            analysisDataDto.setDataList(doubleList);
            analysisDataDtoList.add(analysisDataDto);
        }
        for (int i = 0; i < analysisDataDtoList.size(); i++) {
            AnalysisDataDto analysisDataDto = analysisDataDtoList.get(i);
            if (ndcMax != Double.NEGATIVE_INFINITY) {
                analysisDataDto.setNdcMax(ndcMax);
            }
            if (ndcMin != Double.POSITIVE_INFINITY) {
                analysisDataDto.setNdcMin(ndcMin);
            }
            SpcChartResultDto chartResultDto = getAnalysisService().analyzeSpcChartResult(analysisDataDto, configDto);
            SpcChartDto chartDto = new SpcChartDto();
            chartDto.setResultDto(chartResultDto);
            chartDto.setKey(searchConditions.get(i).getKey());
            chartDto.setItemName(searchConditions.get(i).getItemName());
            chartDto.setCondition(searchConditions.get(i).getCondition());
            chartDto.setAnalyzedRowKeys(analyzedRowKeys.get(i));
            result.add(chartDto);
        }
        return result;
    }

    private SpcAnalysisService getAnalysisService() {
        Validate.validState(this.analysisService != null, "Analysis Service is not injected");
        return this.analysisService;
    }

    public void setAnalysisService(SpcAnalysisService analysisService) {
        this.analysisService = analysisService;
    }
}
