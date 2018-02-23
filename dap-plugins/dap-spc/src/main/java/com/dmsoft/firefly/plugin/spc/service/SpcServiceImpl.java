/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.service;

import com.dmsoft.firefly.plugin.spc.dto.*;
import com.dmsoft.firefly.plugin.spc.service.impl.SpcService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.dto.TestDataDto;
import com.dmsoft.firefly.sdk.dai.entity.CellData;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dai.service.TemplateService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Created by Ethan.Yang on 2018/2/5.
 */
public class SpcServiceImpl implements SpcService {
    private TemplateService templateService = RuntimeContext.getBean(TemplateService.class);
    private SourceDataService sourceDataService = RuntimeContext.getBean(SourceDataService.class);

    @Override
    public List<SpcStatisticalResultDto> findStatisticalResult(List<String> testItemNames, List<String> conditions, SpcSearchConfigDto spcSearchConfigDto) {
        /*
        1.Verify the validity of the parameters
        2.Find template setting and global setting, change config dto
        3.Get analysis data from SourceDataService
        4.Get analysis statistical result from R
        5.build all warning rules
         */
        //1.Verify the validity of the parameters
        if (spcSearchConfigDto == null || testItemNames == null || conditions == null) {
            //todo throw Exception throw new ApplicationException();
        }
        //2.Find template and global setting
        String templateName = spcSearchConfigDto.getTemplateName();
        TemplateSettingDto templateSettingDto = templateService.findAnalysisTemplate(templateName);
        //3.Get analysis data from SourceDataService
        List<String> projectNames = spcSearchConfigDto.getProjectNames();
        //todo get testData from SourceDataService
        List<TestDataDto> testDataDtoList = sourceDataService.findDataByCondition(projectNames, testItemNames, conditions, templateName, true);
//        Map<String,List<TestDataDto>> testItemConditionMap = Maps.newHashMap();

//        Map<String,Map<String,TestDataDto>> dataMap = Maps.newHashMap();
//        for(String testItemName : testItemNames){
//            Map<String,TestDataDto> condtionMap = Maps.newHashMap();
//            for(String condition : conditions){
//                List<TestDataDto> testDataDtoList = testItemConditionMap.get(condition);
//                if(testDataDtoList != null){
//
//                }
//            }
//        }

        //4.Get analysis statistical result from R
        // todo from R
        List<SpcStatisticalResultDto> spcStatisticalResultDtoList = Lists.newArrayList();
        return spcStatisticalResultDtoList;
    }

    @Override
    public SpcDetailResultDto findChartDataAndViewData(List<SearchConditionDto> searchConditionDtoList, SpcSearchConfigDto spcSearchConfigDto) {
        if (spcSearchConfigDto == null || searchConditionDtoList == null) {
            //todo throw Exception throw new ApplicationException();
        }

//        List<String> testItemNames = Lists.newArrayList();
//        List<String> conditions = Lists.newArrayList();
//        searchConditionDtoList.forEach(v -> {
//            if (!testItemNames.contains(v.getItemName())) {
//                testItemNames.add(v.getItemName());
//            }
//            if (!conditions.contains(v.getCondition())) {
//                conditions.add(v.getCondition());
//            }
//        });
        String templateName = spcSearchConfigDto.getTemplateName();
        List<String> projectNames = spcSearchConfigDto.getProjectNames();
        //todo get testData from SourceDataService
        Map<String, TestDataDto> testDataDtoMap = Maps.newHashMap();

        searchConditionDtoList.forEach(v -> {
            List<TestDataDto> testDataDtoList = sourceDataService.findDataByCondition(projectNames, Lists.newArrayList(v.getItemName()), Lists.newArrayList(v.getCondition()), templateName, true);
            testDataDtoMap.put(v.getKey(),testDataDtoList.get(0));
        });
        // todo from R

        //todo get view data
        List<SpcViewDataDto> spcViewDataDtoList = Lists.newArrayList();
        for(Map.Entry<String,TestDataDto> entry : testDataDtoMap.entrySet()){
            TestDataDto testDataDto = entry.getValue();
            List<CellData> data = testDataDto.getData();
            data.forEach(d ->{
                SpcViewDataDto spcViewDataDto = new SpcViewDataDto();
                Map<String,Object> testData = Maps.newHashMap();
                spcViewDataDto.setLineKey(d.getRowKey());

                testData.put(testDataDto.getItemName(),d.getValue());
                spcViewDataDto.setTestData(testData);
                spcViewDataDtoList.add(spcViewDataDto);

            });
        }

        return null;
    }

    @Override
    public List<SpcStatisticalResultDto> refreshStatisticalResult(List<SearchConditionDto> searchConditionDtoList, SpcSearchConfigDto spcSearchConfigDto) {
        if (spcSearchConfigDto == null || searchConditionDtoList == null) {
            //todo throw Exception throw new ApplicationException();
        }

        List<String> projectNames = spcSearchConfigDto.getProjectNames();
        String templateName = spcSearchConfigDto.getTemplateName();
        //todo get testData from SourceDataService
        Map<String, TestDataDto> testDataDtoMap = Maps.newHashMap();
        searchConditionDtoList.forEach(v -> {
            List<TestDataDto> testDataDtoList = sourceDataService.findDataByCondition(projectNames, Lists.newArrayList(v.getItemName()), Lists.newArrayList(v.getCondition()), templateName, true);
            TestDataDto testDataDto = testDataDtoList.get(0);
            if(testDataDto != null) {
                //set fix usl and lsl
                testDataDto.setLsl(v.getCusLsl());
                testDataDto.setUsl(v.getCusUsl());
                testDataDtoMap.put(v.getKey(), testDataDto);
            }
        });
        // todo from R
        List<SpcStatisticalResultDto> spcStatisticalResultDtoList = Lists.newArrayList();
        return spcStatisticalResultDtoList;
    }

    @Override
    public SpcAnalysisResultDto refreshAllAnalysisResult(List<SearchConditionDto> searchConditionDtoList, SpcSearchConfigDto spcSearchConfigDto, Map<String, List<Long>> includeLineNo) {
        return null;
    }

    @Override
    public SpcViewDataDto updateViewData(List<SearchConditionDto> searchConditionDtoList, SpcSearchConfigDto spcSearchConfigDto, List<String> testItems) {
        return null;
    }
}
