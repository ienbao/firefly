package com.dmsoft.firefly.plugin.grr.service.impl;

import com.dmsoft.firefly.plugin.grr.dto.*;
import com.dmsoft.firefly.plugin.grr.service.GrrFilterService;
import com.dmsoft.firefly.plugin.grr.utils.GrrExceptionCode;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * impl class for grr filter service
 *
 * @author Julia
 */
public class GrrFilterServiceImpl implements GrrFilterService {
    private final Logger logger = LoggerFactory.getLogger(GrrFilterServiceImpl.class);
    private static String SORT_MEHODE_APPRAISER = "Appraisers";
    private static String SORT_MEHODE_TRIAL = "Trial";


    @Override
    public GrrDataFrameDto getGrrViewData(SearchDataFrame dataFrame, GrrConfigDto configDto, TemplateSettingDto templateSettingDto, SearchConditionDto searchConditionDto) {
        if (dataFrame == null || configDto == null || searchConditionDto == null) {
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12001));
        }
        String appraiserName = searchConditionDto.getAppraiser();
        if (DAPStringUtils.isNotBlank(appraiserName)) {
            return getGrrSlot1(dataFrame, searchConditionDto);
        } else {
            if (SORT_MEHODE_APPRAISER.equals(configDto.getSortMethod())) {
                return getGrrNormalForAppraiser1(dataFrame, searchConditionDto);
            } else if (SORT_MEHODE_TRIAL.equals(configDto.getSortMethod())) {
                return getGrrNormalForTrial1(dataFrame, searchConditionDto);
            } else {
                logger.error("Sort method is empty.");
                throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12011));
            }
        }
    }

    private GrrDataFrameDto getGrrSlot1(SearchDataFrame dataFrame, SearchConditionDto searchConditionDto) {
        GrrDataFrameDto grrDataFrameDto = new GrrDataFrameDto();
        List<GrrViewDataDto> grrIncludeDataDtos = Lists. newLinkedList();
        List<GrrViewDataDto> grrBackupDataDtos = Lists.newLinkedList();

        String partName = searchConditionDto.getPart();
        String appraiserName = searchConditionDto.getAppraiser();
        int trialInt = searchConditionDto.getTrialInt();

        List<String> parts = searchConditionDto.getParts();
        List<String> appraisers = searchConditionDto.getAppraisers();
        parts.forEach(partValue->{
            appraisers.forEach(appraiserValue->{
                StringBuffer search = new StringBuffer();
                search.append("\"" + partName + "\"").append("=").append("\"" + partValue + "\"").append("&").append("\"" + appraiserName + "\"").append("=").append("\"" + appraiserValue + "\"");
                List<String> rowKeys = dataFrame.getSearchRowKey(search.toString());
                if (rowKeys != null && !rowKeys.isEmpty()) {
                    AtomicInteger index = new AtomicInteger(1);
                    AtomicInteger count = new AtomicInteger(0);
                    rowKeys.forEach(rowKey->{
                        GrrViewDataDto grrViewDataDto = new GrrViewDataDto();
                        grrViewDataDto.setPart(partValue);
                        grrViewDataDto.setOperator(appraiserValue);
                        grrViewDataDto.setRowKey(rowKey);
                        if (index.get() > trialInt) {
                            grrViewDataDto.setTrial("");
                            grrBackupDataDtos.add(grrViewDataDto);
                        } else {
                            grrViewDataDto.setTrial(String.valueOf(index.get()));
                            count.getAndIncrement();
                            grrIncludeDataDtos.add(grrViewDataDto);
                        }
                        index.getAndIncrement();
                    });
                }
            });
        });

        grrDataFrameDto.setDataFrame(dataFrame);
        grrDataFrameDto.setIncludeDatas(grrIncludeDataDtos);
        grrDataFrameDto.setBackupDatas(grrBackupDataDtos);
        return grrDataFrameDto;
    }

    private GrrDataFrameDto getGrrNormalForTrial1(SearchDataFrame dataFrame, SearchConditionDto searchConditionDto) {
        GrrDataFrameDto grrDataFrameDto = new GrrDataFrameDto();
        List<GrrViewDataDto> grrIncludeDataDtos = Lists. newLinkedList();
        List<GrrViewDataDto> grrBackupDataDtos = Lists.newLinkedList();

        String partName = searchConditionDto.getPart();
        int trialInt = searchConditionDto.getTrialInt();
        int appraiserInt = searchConditionDto.getAppraiserInt();

        List<String> parts = searchConditionDto.getParts();
        parts.forEach(partValue->{
            StringBuffer search = new StringBuffer();
            search.append("\"" + partName + "\"").append("=").append("\"" + partValue + "\"");
            List<String> rowKeys = dataFrame.getSearchRowKey(search.toString());
            AtomicInteger index = new AtomicInteger(1);
            AtomicInteger appraiserIndex = new AtomicInteger(1);
            AtomicInteger trialIndex = new AtomicInteger(1);
            rowKeys.forEach(rowKey->{
                if (trialIndex.get() == trialInt + 1) {
                    appraiserIndex.set(appraiserIndex.get() + 1);
                    trialIndex.set(1);
                }
                GrrViewDataDto grrViewDataDto = new GrrViewDataDto();
                grrViewDataDto.setPart(partValue);
                grrViewDataDto.setRowKey(rowKey);
                if (index.get() > (appraiserInt * trialInt)) {
                    grrViewDataDto.setOperator("");
                    grrViewDataDto.setTrial("");
                    grrBackupDataDtos.add(grrViewDataDto);
                } else {
                    grrViewDataDto.setOperator(String.valueOf(appraiserIndex.get()));
                    grrViewDataDto.setTrial(String.valueOf(trialIndex.get()));
                    grrIncludeDataDtos.add(grrViewDataDto);
                    trialIndex.getAndIncrement();
                }
                index.getAndIncrement();
            });
        });
        grrDataFrameDto.setDataFrame(dataFrame);
        grrDataFrameDto.setIncludeDatas(grrIncludeDataDtos);
        grrDataFrameDto.setBackupDatas(grrBackupDataDtos);
        return grrDataFrameDto;
    }

    private GrrDataFrameDto getGrrNormalForAppraiser1(SearchDataFrame dataFrame, SearchConditionDto searchConditionDto) {
        GrrDataFrameDto grrDataFrameDto = new GrrDataFrameDto();
        List<GrrViewDataDto> grrIncludeDataDtos = Lists. newLinkedList();
        List<GrrViewDataDto> grrBackupDataDtos = Lists.newLinkedList();
        String partName = searchConditionDto.getPart();
        int trialInt = searchConditionDto.getTrialInt();
        int appraiserInt = searchConditionDto.getAppraiserInt();

        List<String> parts = searchConditionDto.getParts();
        parts.forEach(partValue->{
            StringBuffer search = new StringBuffer();
            search.append("\"" + partName + "\"").append("=").append("\"" + partValue + "\"");
            List<String> rowKeys = dataFrame.getSearchRowKey(search.toString());
            AtomicInteger index = new AtomicInteger(1);
            AtomicInteger appraiserIndex = new AtomicInteger(1);
            AtomicInteger trialIndex = new AtomicInteger(1);
            AtomicInteger backupTrialIndex = new AtomicInteger(1);

            rowKeys.forEach(rowKey->{
                if (trialIndex.get() == trialInt + 1) {
                    appraiserIndex.set(appraiserIndex.get() + 1);
                    trialIndex.set(1);
                }
                GrrViewDataDto grrViewDataDto = new GrrViewDataDto();
                grrViewDataDto.setPart(partValue);
                grrViewDataDto.setRowKey(rowKey);
                if (index.get() > (appraiserInt * trialInt)) {
                    if (backupTrialIndex.get() == trialInt + 1) {
                        backupTrialIndex.set(1);
                    }
                    grrViewDataDto.setOperator("");
                    grrViewDataDto.setTrial(String.valueOf(backupTrialIndex.get()));
                    grrBackupDataDtos.add(grrViewDataDto);
                    backupTrialIndex.getAndIncrement();
                } else {
                    grrViewDataDto.setOperator(String.valueOf(appraiserIndex.get()));
                    grrViewDataDto.setTrial(String.valueOf(trialIndex.get()));
                    grrIncludeDataDtos.add(grrViewDataDto);
                    trialIndex.getAndIncrement();
                }
                index.getAndIncrement();
            });
        });
        Collections.sort(grrIncludeDataDtos, new Comparator<GrrViewDataDto>() {
            @Override
            public int compare(GrrViewDataDto s1, GrrViewDataDto s2) {
                return s1.getTrial().compareTo(s2.getTrial());
            }
        });

        Collections.sort(grrBackupDataDtos, new Comparator<GrrViewDataDto>() {
            @Override
            public int compare(GrrViewDataDto s1, GrrViewDataDto s2) {
                return s1.getTrial().compareTo(s2.getTrial());
            }
        });
        grrDataFrameDto.setDataFrame(dataFrame);
        grrDataFrameDto.setIncludeDatas(grrIncludeDataDtos);
        grrDataFrameDto.setBackupDatas(grrBackupDataDtos);
        return grrDataFrameDto;
    }


    private GrrDataFrameDto getGrrSlot(SearchDataFrame dataFrame, SearchConditionDto searchConditionDto) {
        GrrDataFrameDto grrDataFrameDto = new GrrDataFrameDto();
        List<GrrViewDataDto> grrIncludeDataDtos = Lists. newLinkedList();
        List<GrrViewDataDto> grrBackupDataDtos = Lists.newLinkedList();

        List<RowDataDto> allRowDataDtos = dataFrame.getAllDataRow();

/*
        validateSlot(allRowDataDtos, searchConditionDtos);
*/

        String partName = searchConditionDto.getPart();
        String appraiserName = searchConditionDto.getAppraiser();
        int trialInt = searchConditionDto.getTrialInt();

        Map<String, Integer> everyPartMap = new LinkedHashMap<>();
        allRowDataDtos.forEach(rowDataDto -> {
            Map<String, String> rowData = rowDataDto.getData();
            String partValue = rowData.get(partName);
            String appraiserValue = rowData.get(appraiserName);
            String groupKey = partValue + appraiserValue;
            if (!everyPartMap.containsKey(groupKey)) {
                everyPartMap.put(groupKey, 1);
                GrrViewDataDto grrViewDataDto = new GrrViewDataDto();
                grrViewDataDto.setPart(partValue);
                grrViewDataDto.setOperator(appraiserValue);
                grrViewDataDto.setTrial("1");
                grrViewDataDto.setRowKey(rowDataDto.getRowKey());
                grrIncludeDataDtos.add(grrViewDataDto);
            } else {
                everyPartMap.put(groupKey, everyPartMap.get(groupKey) + 1);
                GrrViewDataDto grrViewDataDto = new GrrViewDataDto();
                grrViewDataDto.setPart(partValue);
                grrViewDataDto.setOperator(appraiserValue);
                grrViewDataDto.setRowKey(rowDataDto.getRowKey());
                if (everyPartMap.get(groupKey) > trialInt) {
                    grrViewDataDto.setTrial("");
                    grrBackupDataDtos.add(grrViewDataDto);
                } else {
                    grrViewDataDto.setTrial(everyPartMap.get(groupKey).toString());
                    grrIncludeDataDtos.add(grrViewDataDto);
                }
            }
        });
        grrDataFrameDto.setDataFrame(dataFrame);
        grrDataFrameDto.setIncludeDatas(grrIncludeDataDtos);
        grrDataFrameDto.setBackupDatas(grrBackupDataDtos);
        return grrDataFrameDto;
    }

    private void validateSlot(List<RowDataDto> allRowDataDtos, List<SearchConditionDto> searchConditionDtos) {
        String partName = searchConditionDtos.get(0).getPart();
        String appraiserName = searchConditionDtos.get(0).getAppraiser();
        int partNumber = searchConditionDtos.get(0).getPartInt();
        int appraiserInt = searchConditionDtos.get(0).getAppraiserInt();
        int trialInt = searchConditionDtos.get(0).getTrialInt();

        if (DAPStringUtils.isNotBlank(appraiserName)) {
            Map<String, Integer> everyPartMap = new LinkedHashMap<>();
            allRowDataDtos.forEach(rowDataDto -> {
                Map<String, String> rowData = rowDataDto.getData();
                String partValue = rowData.get(partName);
                String appraiserValue = rowData.get(appraiserName);
                String groupKey = partValue + appraiserValue;
                if (!everyPartMap.containsKey(groupKey)) {
                    everyPartMap.put(groupKey, 1);
                } else {
                    everyPartMap.put(groupKey, everyPartMap.get(groupKey) + 1);
                }
            });
            if (everyPartMap.keySet().size() <= (partNumber * appraiserInt)) {
                logger.error("Strategy value does not match operator number.");
            }
            everyPartMap.values().forEach(num->{
                if (num <= trialInt) {
                    logger.error("Strategy value does not match operator number.");
                }
            });
        }
    }

    public SearchDataFrame getFilterData(SearchDataFrame selectedItemsDataFrame, List<String> selectedItems, String condition) {
        if (selectedItemsDataFrame == null || selectedItems == null) {
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12001));
        }
        SearchDataFrame filterDatas = null;
        if (DAPStringUtils.isBlank(condition)) {
            filterDatas = selectedItemsDataFrame;
        } else {
            List<String> searchRowKeys = selectedItemsDataFrame.getSearchRowKey(condition);
            filterDatas = selectedItemsDataFrame.subDataFrame(searchRowKeys, selectedItems);
        }
        return filterDatas;
         /*Collections.sort(allRowDataDtos, new Comparator<RowDataDto>() {
            @Override
            public int compare(RowDataDto s1, RowDataDto s2) {
                int flag=s1.getData().get(partName).compareTo(s2.getData().get(partName));
                if(flag == 0){
                    if (StringUtils.isNotBlank(s1.getData().get(appraiserName))) {
                        return s1.getData().get(appraiserName).compareTo(s2.getData().get(appraiserName));
                    }
                    return flag;
                }else{
                    return flag;
                }
            }
        });*/
    }

    private GrrDataFrameDto getGrrNormalForTrial(SearchDataFrame dataFrame, SearchConditionDto searchConditionDto) {
        GrrDataFrameDto grrDataFrameDto = new GrrDataFrameDto();
        List<GrrViewDataDto> grrIncludeDataDtos = Lists.newLinkedList();
        List<GrrViewDataDto> grrBackupDataDtos = Lists.newLinkedList();

        List<RowDataDto> allRowDataDtos = dataFrame.getAllDataRow();

        String partName = searchConditionDto.getPart();
        int trialInt = searchConditionDto.getTrialInt();
        int appraiserInt = searchConditionDto.getAppraiserInt();

        AtomicReference<Integer> appraiserValue = new AtomicReference<>(1);
        AtomicReference<Integer> trialValue = new AtomicReference<>(1);
        Map<String, Integer> everyPartMap = new LinkedHashMap<>();
        allRowDataDtos.forEach(rowDataDto -> {
            Map<String, String> rowData = rowDataDto.getData();
            String partValue = rowData.get(partName);
            if (!everyPartMap.containsKey(partValue)) {
                everyPartMap.put(partValue, 1);
                GrrViewDataDto grrViewDataDto = new GrrViewDataDto();
                grrViewDataDto.setPart(partValue);
                grrViewDataDto.setOperator(appraiserValue.get().toString());
                grrViewDataDto.setTrial(trialValue.get().toString());
                grrViewDataDto.setRowKey(rowDataDto.getRowKey());
                grrIncludeDataDtos.add(grrViewDataDto);
            } else {
                everyPartMap.put(partValue, everyPartMap.get(partValue) + 1);
                trialValue.set(everyPartMap.get(partValue));
                if (trialValue.get() == trialInt + 1) {
                    appraiserValue.set(appraiserValue.get() + 1);
                    trialValue.set(1);
                }
                GrrViewDataDto grrViewDataDto = new GrrViewDataDto();
                grrViewDataDto.setPart(partValue);
                grrViewDataDto.setRowKey(rowDataDto.getRowKey());
                if (everyPartMap.get(partValue) > (appraiserInt * trialInt)) {
                    grrViewDataDto.setOperator("");
                    grrViewDataDto.setTrial("");
                    grrBackupDataDtos.add(grrViewDataDto);
                } else {
                    grrViewDataDto.setOperator(appraiserValue.toString());
                    grrViewDataDto.setTrial(trialValue.get().toString());
                    grrIncludeDataDtos.add(grrViewDataDto);
                }
            }
        });
        grrDataFrameDto.setDataFrame(dataFrame);
        grrDataFrameDto.setIncludeDatas(grrIncludeDataDtos);
        grrDataFrameDto.setBackupDatas(grrBackupDataDtos);
        return grrDataFrameDto;
    }
}
