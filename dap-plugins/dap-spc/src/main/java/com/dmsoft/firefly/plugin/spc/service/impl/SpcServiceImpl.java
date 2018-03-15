package com.dmsoft.firefly.plugin.spc.service.impl;

import com.dmsoft.firefly.plugin.spc.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcAnalysisConfigDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcChartDto;
import com.dmsoft.firefly.plugin.spc.dto.SpcStatsDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcAnalysisDataDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcChartResultDto;
import com.dmsoft.firefly.plugin.spc.dto.analysis.SpcStatsResultDto;
import com.dmsoft.firefly.plugin.spc.service.SpcAnalysisService;
import com.dmsoft.firefly.plugin.spc.service.SpcService;
import com.dmsoft.firefly.plugin.spc.utils.SpcExceptionCode;
import com.dmsoft.firefly.plugin.spc.utils.SpcFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.spc.utils.StringUtils;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.ProcessMonitorAuto;
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
            throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11002));
        }
        List<SpcStatsDto> result = Lists.newArrayList();
        List<SpcAnalysisDataDto> spcAnalysisDataDtoList = Lists.newArrayList();
        int n = 0;
        int len = searchConditions.size() * 3;
        for (SearchConditionDto searchConditionDto : searchConditions) {
            SpcAnalysisDataDto spcAnalysisDataDto = new SpcAnalysisDataDto();
            List<String> searchRowKeys = searchDataFrame.getSearchRowKey(searchConditionDto.getCondition());
            List<String> datas = searchDataFrame.getDataValue(searchConditionDto.getItemName(), searchRowKeys);
            List<Double> doubleList = Lists.newArrayList();
            for (String s : datas) {
                if (DAPStringUtils.isNumeric(s)) {
                    doubleList.add(Double.valueOf(s));
                }
            }
            if (!StringUtils.isSpecialBlank(searchConditionDto.getCusLsl())) {
                spcAnalysisDataDto.setLsl(searchConditionDto.getCusLsl());
            } else {
                spcAnalysisDataDto.setLsl(searchDataFrame.getTestItemWithTypeDto(searchConditionDto.getItemName()).getLsl());
            }
            if (!StringUtils.isSpecialBlank(searchConditionDto.getCusUsl())) {
                spcAnalysisDataDto.setUsl(searchConditionDto.getCusUsl());
            } else {
                spcAnalysisDataDto.setUsl(searchDataFrame.getTestItemWithTypeDto(searchConditionDto.getItemName()).getUsl());
            }
            spcAnalysisDataDto.setDataList(doubleList);
            spcAnalysisDataDtoList.add(spcAnalysisDataDto);
            n++;
            pushProgress(n / len);
        }
        for (int i = 0; i < spcAnalysisDataDtoList.size(); i++) {
            SpcStatsResultDto resultDto = getAnalysisService().analyzeStatsResult(spcAnalysisDataDtoList.get(i), configDto);
            SpcStatsDto statsDto = new SpcStatsDto();
            statsDto.setStatsResultDto(resultDto);
            statsDto.setKey(searchConditions.get(i).getKey());
            statsDto.setItemName(searchConditions.get(i).getItemName());
            statsDto.setCondition(searchConditions.get(i).getCondition());
            result.add(statsDto);
            n = n + 2;
            pushProgress(n / len);
        }
        pushProgress(100);
        return result;
    }

    @Override
    public List<SpcChartDto> getChartResult(SearchDataFrame searchDataFrame, List<SearchConditionDto> searchConditions, SpcAnalysisConfigDto configDto) {
        /*
        1.Verify the validity of the parameters
        2.Get analysis chart result from R
         */
        if (searchDataFrame == null || searchConditions == null || configDto == null) {
            throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11002));
        }
        List<SpcChartDto> result = Lists.newArrayList();
        List<SpcAnalysisDataDto> spcAnalysisDataDtoList = Lists.newArrayList();
        List<List<String>> analyzedRowKeys = Lists.newArrayList();
        Double ndcMax = Double.NEGATIVE_INFINITY;
        Double ndcMin = Double.POSITIVE_INFINITY;
        int n = 0;
        int len = searchConditions.size() * 3;
        for (SearchConditionDto searchConditionDto : searchConditions) {
            SpcAnalysisDataDto spcAnalysisDataDto = new SpcAnalysisDataDto();
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
            if (searchConditionDto.getCusLsl() != null) {
                spcAnalysisDataDto.setLsl(searchConditionDto.getCusLsl());
            } else {
                spcAnalysisDataDto.setLsl(searchDataFrame.getTestItemWithTypeDto(searchConditionDto.getItemName()).getLsl());
            }
            if (searchConditionDto.getCusUsl() != null) {
                spcAnalysisDataDto.setUsl(searchConditionDto.getCusUsl());
            } else {
                spcAnalysisDataDto.setUsl(searchDataFrame.getTestItemWithTypeDto(searchConditionDto.getItemName()).getUsl());
            }
            spcAnalysisDataDto.setDataList(doubleList);
            spcAnalysisDataDtoList.add(spcAnalysisDataDto);
            n++;
            pushProgress(n / len);
        }
        for (int i = 0; i < spcAnalysisDataDtoList.size(); i++) {
            SpcAnalysisDataDto spcAnalysisDataDto = spcAnalysisDataDtoList.get(i);
            if (ndcMax != Double.NEGATIVE_INFINITY) {
                spcAnalysisDataDto.setNdcMax(ndcMax);
            }
            if (ndcMin != Double.POSITIVE_INFINITY) {
                spcAnalysisDataDto.setNdcMin(ndcMin);
            }
            SpcChartResultDto chartResultDto = getAnalysisService().analyzeSpcChartResult(spcAnalysisDataDto, configDto);
            SpcChartDto chartDto = new SpcChartDto();
            chartDto.setResultDto(chartResultDto);
            chartDto.setKey(searchConditions.get(i).getKey());
            chartDto.setItemName(searchConditions.get(i).getItemName());
            chartDto.setCondition(searchConditions.get(i).getCondition());
            chartDto.setAnalyzedRowKeys(analyzedRowKeys.get(i));
            result.add(chartDto);
            n = n + 2;
            pushProgress(n / len);
        }
        pushProgress(100);
        return result;
    }

    private void pushProgress(int progress) {
        if (Thread.currentThread() instanceof ProcessMonitorAuto) {
            ((ProcessMonitorAuto) Thread.currentThread()).push(progress);
        }
    }

    private SpcAnalysisService getAnalysisService() {
        Validate.validState(this.analysisService != null, "Analysis Service is not injected");
        return this.analysisService;
    }

    public void setAnalysisService(SpcAnalysisService analysisService) {
        this.analysisService = analysisService;
    }
}
