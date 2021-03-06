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
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import com.dmsoft.firefly.sdk.job.core.JobEvent;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.OpenService;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import org.springframework.stereotype.Component;

/**
 * impl class for spc service
 *
 * @author Can Guan, Ethan Yang
 */
@Component
public class SpcServiceImpl implements SpcService {
    private static Logger logger = LoggerFactory.getLogger(SpcServiceImpl.class);
    private SpcAnalysisService analysisService;

    @Override
    public List<SpcStatsDto> getStatisticalResult(SearchDataFrame searchDataFrame, List<SearchConditionDto> searchConditions, SpcAnalysisConfigDto configDto) {
        /*
        1.Verify the validity of the parameters
        2.Get analysis statistical result from R
         */
        logger.debug("Getting SPC stats result...");
        //TODO yuanwen 优化此处逻辑，特别关注内存使用情况
        if (searchDataFrame == null || searchConditions == null || configDto == null) {
            pushProgress(100);
            throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11002));
        }
        List<SpcStatsDto> result = Lists.newArrayList();
        int n = 0;
        double len = searchConditions.size();
        //TODO yuanwen 第一步：这里对数据处理占用太多内存，需要抽取公共资管理类；第二步：分级分析对象
        Long start = System.currentTimeMillis();
        logger.debug("开始分析每个测试项的数据。。");
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

            spcAnalysisDataDto.setLsl(searchConditionDto.getCusLsl());
            spcAnalysisDataDto.setUsl(searchConditionDto.getCusUsl());
            spcAnalysisDataDto.setDataList(doubleList);

            SpcStatsResultDto resultDto = getAnalysisService().analyzeStatsResult(spcAnalysisDataDto, configDto);
            SpcStatsDto statsDto = new SpcStatsDto();
            statsDto.setStatsResultDto(resultDto);
            statsDto.setKey(searchConditionDto.getKey());
            statsDto.setItemName(searchConditionDto.getItemName());
            statsDto.setCondition(searchConditionDto.getCondition());
            result.add(statsDto);

            spcAnalysisDataDto = null;
            resultDto = null;
            statsDto = null;

            n++;
            pushProgress((int) (n / len * 100));
        }

        logger.info("Get SPC stats result done.");
        System.out.println(System.currentTimeMillis() - start);
        return result;
    }

    @Override
    public List<SpcChartDto> getChartResult(SearchDataFrame searchDataFrame, List<SearchConditionDto> searchConditions, SpcAnalysisConfigDto configDto) {
        /*
        1.Verify the validity of the parameters
        2.Get analysis chart result from R
         */
        logger.debug("Getting SPC chart result...");
        if (searchDataFrame == null || searchConditions == null || configDto == null) {
            pushProgress(100);
            throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_11002));
        }
        List<SpcChartDto> result = Lists.newArrayList();
        Double ndcMax = Double.NEGATIVE_INFINITY;
        Double ndcMin = Double.POSITIVE_INFINITY;
        int n = 0;
        double len = searchConditions.size();
        Long start = System.currentTimeMillis();
        for (SearchConditionDto searchConditionDto : searchConditions) {
            SpcAnalysisDataDto spcAnalysisDataDto = new SpcAnalysisDataDto();
            List<String> searchRowKeys = searchDataFrame.getSearchRowKey(searchConditionDto.getCondition());
            List<String> datas = searchDataFrame.getDataValue(searchConditionDto.getItemName(), searchRowKeys);
            List<String> rowKeys = Lists.newArrayList();
            List<Double> doubleList = Lists.newArrayList();
            boolean flag = false;
            for (int i = 0; i < datas.size(); i++) {
                if (DAPStringUtils.isNumeric(datas.get(i))) {
                    Double value = Double.valueOf(datas.get(i));
                    flag = true;
                    if (value > ndcMax) {
                        ndcMax = value;
                    }
                    if (value < ndcMin) {
                        ndcMin = value;
                    }
                    rowKeys.add(searchRowKeys.get(i));
                    doubleList.add(value);
                }
            }
            if (!flag) {
                continue;
            }

            if (ndcMax != Double.NEGATIVE_INFINITY) {
                spcAnalysisDataDto.setNdcMax(ndcMax);
            }
            if (ndcMin != Double.POSITIVE_INFINITY) {
                spcAnalysisDataDto.setNdcMin(ndcMin);
            }

            spcAnalysisDataDto.setLsl(searchConditionDto.getCusLsl());
            spcAnalysisDataDto.setUsl(searchConditionDto.getCusUsl());
            spcAnalysisDataDto.setDataList(doubleList);

            SpcChartResultDto chartResultDto = getAnalysisService().analyzeSpcChartResult(spcAnalysisDataDto, configDto);
            SpcChartDto chartDto = new SpcChartDto();
            chartDto.setResultDto(chartResultDto);
            chartDto.setKey(searchConditionDto.getKey());
            chartDto.setItemName(searchConditionDto.getItemName());
            chartDto.setCondition(searchConditionDto.getCondition());
            chartDto.setAnalyzedRowKeys(rowKeys);
            result.add(chartDto);

            spcAnalysisDataDto = null;
            chartResultDto = null;
            chartDto = null;

            n++;
            pushProgress((int) (n / len * 100));
        }
        System.out.println(System.currentTimeMillis() - start);
        logger.info("Get SPC chart result done.");
        return result;
    }

    private void pushProgress(int progress) {
        JobContext context = RuntimeContext.getBean(JobManager.class).findJobContext(Thread.currentThread());
        if (context != null) {
            context.pushEvent(new JobEvent("SpcService", progress + 0.0, null));
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
