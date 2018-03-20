package com.dmsoft.firefly.plugin.grr.service.impl;

import com.dmsoft.firefly.plugin.grr.dto.*;
import com.dmsoft.firefly.plugin.grr.service.GrrFilterService;
import com.dmsoft.firefly.plugin.grr.utils.GrrExceptionCode;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.grr.utils.UIConstant;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.OpenService;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
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
@OpenService
public class GrrFilterServiceImpl implements GrrFilterService {
    private static String SORT_MEHODE_APPRAISER = "Appraisers";
    private static String SORT_MEHODE_TRIAL = "default";
    private final Logger logger = LoggerFactory.getLogger(GrrFilterServiceImpl.class);

    @Override
    public GrrParamDto validateGrrParam(SearchDataFrame dataFrame, SearchConditionDto searchConditionDto) {
        if (dataFrame == null || searchConditionDto == null) {
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12001));
        }
        GrrParamDto grrParamDto;
        String appraiserName = searchConditionDto.getAppraiser();
        int partInt = searchConditionDto.getPartInt();
        Set<String> parts = getParts(dataFrame, searchConditionDto);
        if (parts == null || parts.isEmpty()) {
            logger.error("Part value is empty!");
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12002));
        }

        if (parts.size() < partInt) {
            logger.error("Please check your configuration of part numbers!");
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12007));
        }
        if (StringUtils.isNotBlank(appraiserName)) {
            grrParamDto = validateSlot(dataFrame, searchConditionDto, parts);
        } else {
            grrParamDto = validateNormal(dataFrame, searchConditionDto, parts);
        }

        return grrParamDto;
    }

    @Override
    public GrrDataFrameDto getGrrViewData(SearchDataFrame dataFrame, GrrConfigDto configDto, TemplateSettingDto templateSettingDto, SearchConditionDto searchConditionDto) {
        if (dataFrame == null || configDto == null || searchConditionDto == null) {
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12001));
        }
        String appraiserName = searchConditionDto.getAppraiser();
        if (DAPStringUtils.isNotBlank(appraiserName)) {
            return getGrrSlot(dataFrame, searchConditionDto);
        } else {
            if (SORT_MEHODE_APPRAISER.equals(configDto.getSortMethod())) {
                return getGrrNormalForAppraiser(dataFrame, searchConditionDto);
            } else if (SORT_MEHODE_TRIAL.equals(configDto.getSortMethod())) {
                return getGrrNormalForTrial(dataFrame, searchConditionDto);
            } else {
                logger.error("Sort method is empty.");
                throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12011));
            }
        }
    }

    private GrrDataFrameDto getGrrSlot(SearchDataFrame dataFrame, SearchConditionDto searchConditionDto) {
        GrrDataFrameDto grrDataFrameDto = new GrrDataFrameDto();
        List<GrrViewDataDto> grrIncludeDataDtos = Lists.newLinkedList();
        List<GrrViewDataDto> grrBackupDataDtos = Lists.newLinkedList();

        String partName = searchConditionDto.getPart();
        String appraiserName = searchConditionDto.getAppraiser();
        int trialInt = searchConditionDto.getTrialInt();

        List<String> parts = searchConditionDto.getParts();
        List<String> appraisers = searchConditionDto.getAppraisers();
        parts.forEach(partValue -> {
            appraisers.forEach(appraiserValue -> {
                StringBuffer search = new StringBuffer();
                search.append("\"" + partName + "\"").append("=").append("\"" + partValue + "\"").append("&").append("\"" + appraiserName + "\"").append("=").append("\"" + appraiserValue + "\"");
                List<String> rowKeys = dataFrame.getSearchRowKey(search.toString());
                if (rowKeys != null && !rowKeys.isEmpty()) {
                    AtomicInteger index = new AtomicInteger(1);
                    AtomicInteger count = new AtomicInteger(0);
                    rowKeys.forEach(rowKey -> {
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

    private GrrDataFrameDto getGrrNormalForTrial(SearchDataFrame dataFrame, SearchConditionDto searchConditionDto) {
        GrrDataFrameDto grrDataFrameDto = new GrrDataFrameDto();
        List<GrrViewDataDto> grrIncludeDataDtos = Lists.newLinkedList();
        List<GrrViewDataDto> grrBackupDataDtos = Lists.newLinkedList();

        String partName = searchConditionDto.getPart();
        int trialInt = searchConditionDto.getTrialInt();
        int appraiserInt = searchConditionDto.getAppraiserInt();

        List<String> parts = searchConditionDto.getParts();
        parts.forEach(partValue -> {
            StringBuffer search = new StringBuffer();
            search.append("\"" + partName + "\"").append("=").append("\"" + partValue + "\"");
            List<String> rowKeys = dataFrame.getSearchRowKey(search.toString());
            AtomicInteger index = new AtomicInteger(1);
            AtomicInteger appraiserIndex = new AtomicInteger(1);
            AtomicInteger trialIndex = new AtomicInteger(1);
            rowKeys.forEach(rowKey -> {
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

    private GrrDataFrameDto getGrrNormalForAppraiser(SearchDataFrame dataFrame, SearchConditionDto searchConditionDto) {
        GrrDataFrameDto grrDataFrameDto = new GrrDataFrameDto();
        List<GrrViewDataDto> grrIncludeDataDtos = Lists.newLinkedList();
        List<GrrViewDataDto> grrBackupDataDtos = Lists.newLinkedList();
        String partName = searchConditionDto.getPart();
        int trialInt = searchConditionDto.getTrialInt();
        int appraiserInt = searchConditionDto.getAppraiserInt();

        List<String> parts = searchConditionDto.getParts();
        parts.forEach(partValue -> {
            StringBuffer search = new StringBuffer();
            search.append("\"" + partName + "\"").append("=").append("\"" + partValue + "\"");
            List<String> rowKeys = dataFrame.getSearchRowKey(search.toString());
            AtomicInteger index = new AtomicInteger(1);
            AtomicInteger appraiserIndex = new AtomicInteger(1);
            AtomicInteger trialIndex = new AtomicInteger(1);
            AtomicInteger backupTrialIndex = new AtomicInteger(1);

            rowKeys.forEach(rowKey -> {
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

    private Set<String> getParts(SearchDataFrame dataFrame, SearchConditionDto searchConditionDto) {
        String partName = searchConditionDto.getPart();
        if (StringUtils.isNotBlank(partName)) {
            if (searchConditionDto.getParts() != null && !searchConditionDto.getParts().isEmpty()) {
                return new LinkedHashSet(searchConditionDto.getParts());
            } else {
                return dataFrame.getValueSet(partName);
            }
        }
        return null;
    }

    private Set<String> getAppraisers(SearchDataFrame dataFrame, SearchConditionDto searchConditionDto) {
        String appraiser = searchConditionDto.getAppraiser();
        if (StringUtils.isNotBlank(appraiser)) {
            if (searchConditionDto.getAppraisers() != null && !searchConditionDto.getAppraisers().isEmpty()) {
                return new LinkedHashSet(searchConditionDto.getAppraisers());
            } else {
                return dataFrame.getValueSet(appraiser);
            }
        }
        return null;
    }

    private GrrParamDto validateSlot(SearchDataFrame dataFrame, SearchConditionDto searchConditionDto, Set<String> parts) {
        GrrParamDto grrParamDto = new GrrParamDto();
        Map<String, String> errorMap = new LinkedHashMap<>();
        Set<String> rightParts = new LinkedHashSet<>();
        Set<String> rightAppraisers = new LinkedHashSet<>();
        AtomicInteger rights = new AtomicInteger(0);

        int partInt = searchConditionDto.getPartInt();
        int appraiserInt = searchConditionDto.getAppraiserInt();
        int trialInt = searchConditionDto.getTrialInt();

        String partName = searchConditionDto.getPart();
        String appraiserName = searchConditionDto.getAppraiser();

        if (StringUtils.isNotBlank(appraiserName)) {
            //slot validate
            Set<String> appraisers = getAppraisers(dataFrame, searchConditionDto);
            if (appraisers == null || appraisers.isEmpty()) {
                logger.error("Appraiser value is empty.");
                throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12004));
            }
            if (appraisers.size() < appraiserInt) {
                logger.error("Please check your configuration of appraiser numbers!");
                throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12006));
            } else {
                int partIndex = 1;
                for (String partValue : parts) {
                    System.out.println("isContinue");
                    for (String appraiserValue : appraisers) {
                        StringBuffer search = new StringBuffer();
                        search.append("\"" + partName + "\"").append("=").append("\"" + partValue + "\"").append("&").append("\"" + appraiserName + "\"").append("=").append("\"" + appraiserValue + "\"");
                        List<String> rowKeys = dataFrame.getSearchRowKey(search.toString());
                        String[] errorParams;
                        if (rowKeys != null && !rowKeys.isEmpty()) {
                            AtomicInteger index = new AtomicInteger(1);
                            AtomicInteger count = new AtomicInteger(0);
                            rowKeys.forEach(rowKey -> {
                                if (index.get() <= trialInt) {
                                    count.getAndIncrement();
                                }
                                index.getAndIncrement();
                            });
                            if (count.getAndIncrement() < trialInt) {
                                errorParams = new String[]{partValue + " * " + appraiserValue, String.valueOf(trialInt), String.valueOf(count.getAndIncrement())};
                                errorMap.put(partValue + UIConstant.SPLIT_FLAG + appraiserValue, GrrFxmlAndLanguageUtils.getString(UIConstant.EXCEPTION_GRR_MODEL, errorParams));
                            } else {
                                rightParts.add(partValue);
                                rightAppraisers.add(appraiserValue);
                                rights.getAndIncrement();
                                if (rights.get() == appraiserInt * partIndex) {
                                    partIndex++;
                                    break;
                                }
                            }
                        } else {
                            errorParams = new String[]{partValue + " * " + appraiserValue, String.valueOf(trialInt), "0"};
                            errorMap.put(partValue + UIConstant.SPLIT_FLAG + appraiserValue, GrrFxmlAndLanguageUtils.getString(UIConstant.EXCEPTION_GRR_MODEL, errorParams));
                        }
                    }
                    if (partIndex == partInt + 1) {
                        break;
                    }
                }
            }
            grrParamDto.setParts(rightParts);
            grrParamDto.setAppraisers(rightAppraisers);
            if (rights.get() != partInt * appraiserInt) {
                grrParamDto.setErrors(errorMap);
            }
        }

        return grrParamDto;
    }

    private GrrParamDto validateNormal(SearchDataFrame dataFrame, SearchConditionDto searchConditionDto, Set<String> parts) {
        GrrParamDto grrParamDto = new GrrParamDto();
        Map<String, String> errorMap = new LinkedHashMap<>();
        Set<String> rightParts = new LinkedHashSet<>();
        AtomicInteger rights = new AtomicInteger(0);

        int partInt = searchConditionDto.getPartInt();
        int appraiserInt = searchConditionDto.getAppraiserInt();
        int trialInt = searchConditionDto.getTrialInt();

        String partName = searchConditionDto.getPart();

        //normal validate
        for (String partValue : parts) {
            StringBuffer search = new StringBuffer();
            search.append("\"" + partName + "\"").append("=").append("\"" + partValue + "\"");
            List<String> rowKeys = dataFrame.getSearchRowKey(search.toString());
            String[] errorParams;
            if (rowKeys != null && !rowKeys.isEmpty()) {
                AtomicInteger index = new AtomicInteger(1);
                AtomicInteger count = new AtomicInteger(0);
                rowKeys.forEach(rowKey -> {
                    if (index.get() <= (appraiserInt * trialInt)) {
                        count.getAndIncrement();
                    }
                    index.getAndIncrement();
                });
                if (count.getAndIncrement() < (appraiserInt * trialInt)) {
                    errorParams = new String[]{partValue, String.valueOf(appraiserInt * trialInt), String.valueOf(count.getAndIncrement())};
                    errorMap.put(partValue, GrrFxmlAndLanguageUtils.getString(UIConstant.EXCEPTION_GRR_MODEL, errorParams));

                } else {
                    rights.getAndIncrement();
                    rightParts.add(partValue);
                    if (rights.get() == partInt) {
                        break;
                    }
                }
            } else {
                errorParams = new String[]{partValue, String.valueOf(appraiserInt * trialInt), "0"};
                errorMap.put(partValue, GrrFxmlAndLanguageUtils.getString(UIConstant.EXCEPTION_GRR_MODEL, errorParams));
            }
        }
        grrParamDto.setParts(rightParts);
        if (rights.get() != partInt) {
            grrParamDto.setErrors(errorMap);
        }

        return grrParamDto;
    }


    private GrrDataFrameDto getGrrSlot1(SearchDataFrame dataFrame, SearchConditionDto searchConditionDto) {
        GrrDataFrameDto grrDataFrameDto = new GrrDataFrameDto();
        List<GrrViewDataDto> grrIncludeDataDtos = Lists.newLinkedList();
        List<GrrViewDataDto> grrBackupDataDtos = Lists.newLinkedList();

        List<RowDataDto> allRowDataDtos = dataFrame.getAllDataRow();

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

    private GrrDataFrameDto getGrrNormalForTrial1(SearchDataFrame dataFrame, SearchConditionDto searchConditionDto) {
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
