
/*
 * Copyright (C) 2018. For Intelligent Group.
 *
 */

package com.dmsoft.firefly.core.utils;

import com.dmsoft.firefly.core.utils.parser.FilterExpressionParser;
import com.dmsoft.firefly.core.utils.parser.ParserToken;
import com.dmsoft.firefly.core.utils.parser.SEPResult;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestDataDto;
import com.dmsoft.firefly.sdk.dai.entity.CellData;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Lucien.Chen on 2018/1/24.
 */
public class FilterConditionUtil {

    private Logger logger = LoggerFactory.getLogger(FilterConditionUtil.class);
    private SourceDataService sourceDataService = RuntimeContext.getBean(SourceDataService.class);

    private List<String> timeKeys = Lists.newArrayList();
    private FastDateFormat fastDateFormat;

    public FilterConditionUtil() {
    }

    public FilterConditionUtil(List<String> timeKeys) {
        this.timeKeys = timeKeys;
    }

    /**
     * Parse the item names from conditions.
     *
     * @param condition search condition
     * @return the String set of test item names
     */
    public Set<String> parseItemNameFromConditions(String condition) {
        Set<String> resultList = new LinkedHashSet<>();
        if (StringUtils.isBlank(condition)) {
            return resultList;
        }
        int i = 0;
        try {
            FilterExpressionParser fep = new FilterExpressionParser();
            SEPResult result = fep.doParser(condition);
            if (result.getLeftExpr() instanceof SEPResult) {
                List<Object> columns = parseItemNameFromConditions((SEPResult) result.getLeftExpr());
                if (columns != null && !columns.isEmpty()) {
                    for (Object column : columns) {
                        resultList.add((String) column);
                    }
                }
            }
            if (result.getRightExpr() instanceof SEPResult) {
                List<Object> columns = parseItemNameFromConditions((SEPResult) result.getRightExpr());
                if (columns != null && !columns.isEmpty()) {
                    for (Object column : columns) {
                        resultList.add((String) column);
                    }
                }
            }
            if (StringUtils.isBlank(result.getLogicalToken())) {
                resultList.add((String) result.getLeftExpr());
            }
        } catch (Exception e) {
            logger.debug("Search condition parse error! condition = {}", condition);
        }
        return resultList;
    }


    // parse item names from spe result
    private List<Object> parseItemNameFromConditions(SEPResult result) {
        List<Object> resultList = Lists.newLinkedList();
        List<Object> leftList = Lists.newLinkedList();
        List<Object> rightList = Lists.newLinkedList();
        if (result.getLeftExpr() instanceof SEPResult) {
            List left = parseItemNameFromConditions((SEPResult) result.getLeftExpr());
            leftList.addAll(left);
            resultList.addAll(left);
        }
        if (result.getRightExpr() instanceof SEPResult) {
            List right = parseItemNameFromConditions((SEPResult) result.getRightExpr());
            rightList.addAll(right);
            resultList.addAll(right);
        }
        if (StringUtils.isBlank(result.getLogicalToken())) {
            resultList.add(result.getLeftExpr());
        }
        return resultList;
    }

    /**
     * Filter the data.
     *
     * @param condition search condition
     * @param testDataDtoList testDataDtoList
     * @return true, exist; false, not exist
     */
    public List<String> filterCondition(String condition, List<TestDataDto> testDataDtoList) {
        if (StringUtils.isBlank(condition)) {
            return Lists.newArrayList();
        }
        try {
            FilterExpressionParser fep = new FilterExpressionParser();
            SEPResult result = fep.doParser(condition);
            List<String> rowKeys = parseSepResult(result, testDataDtoList);
//            if (validateResult == null || validateResult.isEmpty()) {
//                isExist = false;
//            }
            return rowKeys;
        } catch (Exception e) {
            logger.debug("Search condition parse error! condition = {}", condition);
            return Lists.newArrayList();
        }
    }

    private List<String> parseSepResult(SEPResult result, List<TestDataDto> testDataDtoList) {
        List<String> resultObject = null;
        List<String> leftObject = null;
        List<String> rightObject = null;

        if (result.getLeftExpr() instanceof SEPResult) {
            leftObject = parseSepResult((SEPResult) result.getLeftExpr(), testDataDtoList);
        }
        if (result.getRightExpr() instanceof SEPResult) {
            rightObject = parseSepResult((SEPResult) result.getRightExpr(), testDataDtoList);
        }
        if (StringUtils.isBlank(result.getLogicalToken())) {
            resultObject = parseBaseData(result, testDataDtoList);
        } else {
            // Logical Token : |
            if (result.getLogicalToken().equals(ParserToken.OR.getCode()) && leftObject != null && rightObject != null) {
                rightObject.removeAll(leftObject);
                leftObject.addAll(rightObject);
                resultObject = leftObject;
            } else if (result.getLogicalToken().equals(ParserToken.AND.getCode()) && leftObject != null && rightObject != null) {
                leftObject.retainAll(rightObject);
                resultObject = leftObject;
            } else if (leftObject != null && rightObject == null) {
                resultObject = leftObject;
            } else if (leftObject == null && rightObject != null) {
                resultObject = rightObject;
            }
        }
        return resultObject;
    }

    private List<String> parseBaseData(SEPResult result, List<TestDataDto> testDataDtoList) {
        List<String> rowKeys = Lists.newArrayList();
        if (filterBaseData((String) result.getLeftExpr(), (String) result.getRightExpr(), result.getToken(), rowKeys, testDataDtoList)) {
            return rowKeys;
        } else {
            return null;
        }
    }

    private boolean filterBaseData(String name, String value, String token, List<String> rowKeys, List<TestDataDto> testDataDtoList) {
        boolean isExist = true;
        TestDataDto itemData = null;
        for (TestDataDto testDataDto : testDataDtoList) {
            if (testDataDto.getItemName().equals(name)) {
                itemData = testDataDto;
                break;
            }
        }

        if (itemData == null || StringUtils.isBlank(value)) {
            isExist = false;
        } else {
            List<CellData> itemNames = itemData.getData();
            switch (ParserToken.getValueOf(token)) {
                case EQ:
                    if (StringUtils.isNumeric(value)) {
                        Double num = Double.valueOf(value);
                        itemNames.forEach(cellData -> {
                            if (StringUtils.isNumeric(cellData.getValue().toString())) {
                                if (num == Double.valueOf(cellData.getValue().toString())) {
                                    rowKeys.add(cellData.getRowKey());
                                }
                            }
                        });
                    } else {
                        itemNames.forEach(cellData -> {
                            if (value.equals(cellData.getValue())) {
                                rowKeys.add(cellData.getRowKey());
                            }
                        });
                    }
                    break;
                case LIKE:
                    itemNames.forEach(cellData -> {
                        if (cellData.getValue().toString().contains(value)) {
                            rowKeys.add(cellData.getRowKey());
                        }
                    });
                    break;
                case GT:
                    if (StringUtils.isNumeric(value)) {
                        Double num = Double.valueOf(value);
                        itemNames.forEach(cellData -> {
                            if (StringUtils.isNumeric(cellData.getValue().toString())) {
                                if (Double.valueOf(cellData.getValue().toString()) > num) {
                                    rowKeys.add(cellData.getRowKey());
                                }
                            }
                        });
                    } else if (timeKeys != null && !timeKeys.isEmpty() && timeKeys.contains(name)) {
                        Long timeValue = getTime(value);
                        itemNames.forEach(cellData -> {
                            Long realTimeValue = getTime(cellData.getValue().toString());
                            if (timeValue != null && realTimeValue != null && realTimeValue > timeValue) {
                                rowKeys.add(cellData.getRowKey());
                            }
                        });
                    } else {
                        isExist = false;
                    }
                    break;
                case GET:
                    if (StringUtils.isNumeric(value)) {
                        Double num = Double.valueOf(value);
                        itemNames.forEach(cellData -> {
                            if (StringUtils.isNumeric(cellData.getValue().toString())) {
                                if (Double.valueOf(cellData.getValue().toString()) >= num) {
                                    rowKeys.add(cellData.getRowKey());
                                }
                            }
                        });
                    } else if (timeKeys != null && !timeKeys.isEmpty() && timeKeys.contains(name)) {
                        Long timeValue = getTime(value);
                        itemNames.forEach(cellData -> {
                            Long realTimeValue = getTime(cellData.getValue().toString());
                            if (timeValue != null && realTimeValue != null && realTimeValue >= timeValue) {
                                rowKeys.add(cellData.getRowKey());
                            }
                        });
                    } else {
                        isExist = false;
                    }
                    break;
                case LT:
                    if (StringUtils.isNumeric(value)) {
                        Double num = Double.valueOf(value);
                        itemNames.forEach(cellData -> {
                            if (StringUtils.isNumeric(cellData.getValue().toString())) {
                                if (Double.valueOf(cellData.getValue().toString()) < num) {
                                    rowKeys.add(cellData.getRowKey());
                                }
                            }
                        });
                    } else if (timeKeys != null && !timeKeys.isEmpty() && timeKeys.contains(name)) {
                        Long timeValue = getTime(value);
                        itemNames.forEach(cellData -> {
                            Long realTimeValue = getTime(cellData.getValue().toString());
                            if (timeValue != null && realTimeValue != null && realTimeValue < timeValue) {
                                rowKeys.add(cellData.getRowKey());
                            }
                        });
                    } else {
                        isExist = false;
                    }
                    break;
                case LET:
                    if (StringUtils.isNumeric(value)) {
                        Double num = Double.valueOf(value);
                        itemNames.forEach(cellData -> {
                            if (StringUtils.isNumeric(cellData.getValue().toString())) {
                                if (Double.valueOf(cellData.getValue().toString()) <= num) {
                                    rowKeys.add(cellData.getRowKey());
                                }
                            }
                        });
                    } else if (timeKeys != null && !timeKeys.isEmpty() && timeKeys.contains(name)) {
                        Long timeValue = getTime(value);
                        itemNames.forEach(cellData -> {
                            Long realTimeValue = getTime(cellData.getValue().toString());
                            if (timeValue != null && realTimeValue != null && realTimeValue <= timeValue) {
                                rowKeys.add(cellData.getRowKey());
                            }
                        });
                    } else {
                        isExist = false;
                    }
                    break;
                case NE:
                    if (StringUtils.isNumeric(value)) {
                        Double num = Double.valueOf(value);
                        itemNames.forEach(cellData -> {
                            if (StringUtils.isNumeric(cellData.getValue().toString())) {
                                if (num != Double.valueOf(cellData.getValue().toString())) {
                                    rowKeys.add(cellData.getRowKey());
                                }
                            }
                        });
                    } else {
                        itemNames.forEach(cellData -> {
                            if (!value.equals(cellData.getValue())) {
                                rowKeys.add(cellData.getRowKey());
                            }
                        });
                    }
                    break;
                default:
                    break;
            }
        }
        return isExist;
    }
//
//    /**
//     * Filter the data.
//     *
//     * @param condition search condition
//     * @return true, exist; false, not exist
//     */
//    public Boolean filterTimeformCondition(String condition, Map data) {
//        if (StringUtils.isBlank(condition)) {
//            return null;
//        }
//        try {
//            FilterExpressionParser fep = new FilterExpressionParser();
//            SEPResult result = fep.doParser(condition);
//            Boolean isExist = parseSepResultFromTimeKey(result, data);
////            if (validateResult == null || validateResult.isEmpty()) {
////                isExist = false;
////            }
//            return isExist;
//        } catch (Exception e) {
//            logger.debug("Search condition parse error! condition = {}", condition);
//            return false;
//        }
//    }
//
//
//    private Boolean parseSepResultFromTimeKey(SEPResult result, Map<String, Object> data) {
//
//        Boolean resultObject = null;
//        Boolean leftObject = null;
//        Boolean rightObject = null;
//
//        if (result.getLeftExpr() instanceof SEPResult) {
//            leftObject = parseSepResultFromTimeKey((SEPResult) result.getLeftExpr(), data);
//        }
//        if (result.getRightExpr() instanceof SEPResult) {
//            rightObject = parseSepResultFromTimeKey((SEPResult) result.getRightExpr(), data);
//        }
//        if (StringUtils.isBlank(result.getLogicalToken())) {
//            resultObject = parseBaseDataFromTimekey((String) result.getLeftExpr(), (String) result.getRightExpr(), result.getToken(), data);
//        } else {
//            // Logical Token : |
//            if (result.getLogicalToken().equals(ParserToken.OR.getCode())) {
//                resultObject = leftObject || rightObject;
//            }
//            // Logical Token : &
//            if (result.getLogicalToken().equals(ParserToken.AND.getCode()) && leftObject != null && rightObject != null) {
//                resultObject = leftObject && rightObject;
//            }
//        }
//        return resultObject;
//    }
//
//    private Boolean parseBaseDataFromTimekey(String name, String value, String token, Map<String, Object> data) {
//        boolean isExist = true;
//        if (data != null && data.containsKey(name) && StringUtils.isNotBlank(token) && StringUtils.isNotBlank(value)) {
//            String realValue = (String) data.get(name);
//            if (StringUtils.isBlank(realValue)) {
//                isExist = false;
//            } else {
//                switch (ParserToken.getValueOf(token)) {
//                    case EQ:
//                        if (timeKeys != null && !timeKeys.isEmpty() && timeKeys.contains(name)) {
//                            Long timeValue = getTime(value);
//                            Long realTimeValue = getTime(realValue);
//                            if (null == timeValue || null == realTimeValue || realTimeValue != timeValue) {
//                                isExist = false;
//                            }
//                        }
//                        break;
//                    case LIKE:
//                        break;
//                    case GT:
//                        if (timeKeys != null && !timeKeys.isEmpty() && timeKeys.contains(name)) {
//                            Long timeValue = getTime(value);
//                            Long realTimeValue = getTime(realValue);
//                            if (null == timeValue || null == realTimeValue || realTimeValue <= timeValue) {
//                                isExist = false;
//                            }
//                        }
//                        break;
//                    case GET:
//                        if (timeKeys != null && !timeKeys.isEmpty() && timeKeys.contains(name)) {
//                            Long timeValue = getTime(value);
//                            Long realTimeValue = getTime(realValue);
//                            if (null == timeValue || null == realTimeValue || realTimeValue < timeValue) {
//                                isExist = false;
//                            }
//                        }
//                        break;
//                    case LT:
//                        if (timeKeys != null && !timeKeys.isEmpty() && timeKeys.contains(name)) {
//                            Long timeValue = getTime(value);
//                            Long realTimeValue = getTime(realValue);
//                            if (null == timeValue || null == realTimeValue || realTimeValue >= timeValue) {
//                                isExist = false;
//                            }
//                        }
//                        break;
//                    case LET:
//                        if (timeKeys != null && !timeKeys.isEmpty() && timeKeys.contains(name)) {
//                            Long timeValue = getTime(value);
//                            Long realTimeValue = getTime(realValue);
//                            if (null == timeValue || null == realTimeValue || realTimeValue > timeValue) {
//                                isExist = false;
//                            }
//                        }
//                        break;
//                    case NE:
//                        if (timeKeys != null && !timeKeys.isEmpty() && timeKeys.contains(name)) {
//                            Long timeValue = getTime(value);
//                            Long realTimeValue = getTime(realValue);
//                            if (null == timeValue || null == realTimeValue || realTimeValue == timeValue) {
//                                isExist = false;
//                            }
//                        }
//                        break;
//                    default:
//                        break;
//                }
//            }
//        }
//        return isExist;
//    }

    private Long getTime(String value) {
        try {
            return fastDateFormat.parse(value).getTime();
        } catch (Exception e) {
            return null;
        }
    }

    public List<String> getTimeKeys() {
        return timeKeys;
    }

    public void setTimeKeys(List<String> timeKeys) {
        this.timeKeys = timeKeys;
    }

    public void setTimePattern(String timePattern) {
        fastDateFormat = FastDateFormat.getInstance(timePattern);
    }
}
