package com.dmsoft.firefly.core.utils;

import com.dmsoft.firefly.core.utils.parser.FilterExpressionParser;
import com.dmsoft.firefly.core.utils.parser.ParserToken;
import com.dmsoft.firefly.core.utils.parser.SEPResult;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Can.Guan on 2017/2/22.
 */
public class FilterUtils {
    private Logger logger = LoggerFactory.getLogger(FilterUtils.class);

    private List<String> timeKeys = Lists.newArrayList();
    private String timePattern;


    /**
     * constructor
     */
    public FilterUtils() {
    }

    /**
     * constructor
     *
     * @param timeKeys    list of time key
     * @param timePattern time pattern
     */
    public FilterUtils(List<String> timeKeys, String timePattern) {
        this.timeKeys = timeKeys;
        this.timePattern = timePattern;
    }

    /**
     * Parse the item names from conditions.
     *
     * @param condition search condition
     * @return the String set of test item names
     */
    public Set<String> parseItemNameFromConditions(String condition) {
        Set<String> resultList = new LinkedHashSet<>();
        if (DAPStringUtils.isBlank(condition)) {
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
            if (DAPStringUtils.isBlank(result.getLogicalToken())) {
                resultList.add((String) result.getLeftExpr());
            }
        } catch (Exception e) {
            logger.debug("Search condition parse error! condition = {}", condition);
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_12001));
        }
        return resultList;
    }

    /**
     * Filter the data.
     *
     * @param condition search condition
     * @param row       the row of data
     * @return true, exist; false, not exist
     */
    public boolean filterData(String condition, Map<String, String> row) {
        boolean isExist = true;
        if (DAPStringUtils.isBlank(condition)) {
            return isExist;
        }
        try {
            FilterExpressionParser fep = new FilterExpressionParser();
            SEPResult result = fep.doParser(condition);
            List<Object> validateResult = parseSepResult(result, row);
            if (validateResult == null || validateResult.isEmpty()) {
                isExist = false;
            }
        } catch (Exception e) {
            logger.debug("Search condition parse error! condition = {}", condition);
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_12001));
        }
        return isExist;
    }

    /**
     * simple parse condition. [WARN] the format of this condition is "item name" operator value
     *
     * @param condition string of condition
     * @return filter dto or error format
     */
    public FilterDto simpleParseCondition(String condition) {
        try {
            FilterDto dto = new FilterDto();
            FilterExpressionParser fep = new FilterExpressionParser();
            SEPResult result = fep.doParser(condition);
            boolean flag = false;
            if (!(result.getLeftExpr() instanceof SEPResult)) {
                dto.setItemName(result.getLeftExpr().toString());
            } else {
                flag = true;
            }
            dto.setOperator(result.getToken());
            if (!(result.getRightExpr() instanceof SEPResult)) {
                dto.setValue(result.getRightExpr().toString());
            } else {
                flag = true;
            }
            if (flag) {
                return null;
            }
            return dto;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * judge condition is legal or not
     *
     * @param condition string of condition
     * @return true means legal
     */
    public boolean isLegal(String condition) {
        try {
            FilterExpressionParser fep = new FilterExpressionParser();
            fep.doParser(condition);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // parse sep result Object is List<Map>, Map key is String of test item, Map value is String of test item value
    // OR Object is
    private List<Object> parseSepResult(SEPResult result, Map<String, String> row) {
        List<Object> resultList = Lists.newLinkedList();
        List<Object> leftList = Lists.newLinkedList();
        List<Object> rightList = Lists.newLinkedList();
        if (result.getLeftExpr() instanceof SEPResult) {
            List left = parseSepResult((SEPResult) result.getLeftExpr(), row);
            leftList.addAll(left);
            resultList.addAll(left);
        }
        if (result.getRightExpr() instanceof SEPResult) {
            List right = parseSepResult((SEPResult) result.getRightExpr(), row);
            rightList.addAll(right);
            resultList.addAll(right);
        }
        if (DAPStringUtils.isBlank(result.getLogicalToken())) {
            resultList.addAll(parseBaseData(result, row));
        } else {
            // Logical Token : |
            if (result.getLogicalToken().equals(ParserToken.OR.getCode())) {
                return resultList;
            }
            // Logical Token : &
            if (result.getLogicalToken().equals(ParserToken.AND.getCode()) && leftList != null && !leftList.isEmpty() && rightList != null && !rightList.isEmpty()) {
                return resultList;
            } else {
                resultList.clear();
                return resultList;
            }
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
        if (DAPStringUtils.isBlank(result.getLogicalToken())) {
            resultList.add(result.getLeftExpr());
        }
        return resultList;
    }

    // parse data from spe result and return single row data
    private List<Object> parseBaseData(SEPResult result, Map<String, String> row) {
        List<Object> resultList = Lists.newLinkedList();
        if (DAPStringUtils.isBlank(result.getLogicalToken())) {
            if (filterBaseData((String) result.getLeftExpr(), (String) result.getRightExpr(), result.getToken(), row)) {
                resultList.add(row);
            }
        }
        return resultList;
    }

    // judge if this row is legal or illegal
    private boolean filterBaseData(String name, String value, String token, Map<String, String> row) {
        boolean isExist = true;
        if (row != null && row.containsKey(name) && DAPStringUtils.isNotBlank(token) && DAPStringUtils.isNotBlank(value)) {
            String realValue = row.get(name);
            if (DAPStringUtils.isBlank(realValue)) {
                isExist = false;
            } else {
                switch (ParserToken.getValueOf(token)) {
                    case EQ:
                        if (!realValue.equals(value)) {
                            isExist = false;
                        }
                        break;
                    case LIKE:
                        if (!realValue.contains(value)) {
                            isExist = false;
                        }
                        break;
                    case GT:
                        if (DAPStringUtils.isNumeric(realValue) && DAPStringUtils.isNumeric(value)) {
                            if (Double.valueOf(realValue) <= Double.valueOf(value)) {
                                isExist = false;
                            }
                        } else if (timeKeys != null && !timeKeys.isEmpty() && timeKeys.contains(name)) {
                            Long timeValue = getTime(value);
                            Long realTimeValue = getTime(realValue);
                            if (null == timeValue || null == realTimeValue || realTimeValue <= timeValue) {
                                isExist = false;
                            }
                        }
                        break;
                    case GET:
                        if (DAPStringUtils.isNumeric(realValue) && DAPStringUtils.isNumeric(value)) {
                            if (Double.valueOf(realValue) < Double.valueOf(value)) {
                                isExist = false;
                            }
                        } else if (timeKeys != null && !timeKeys.isEmpty() && timeKeys.contains(name)) {
                            Long timeValue = getTime(value);
                            Long realTimeValue = getTime(realValue);
                            if (null == timeValue || null == realTimeValue || realTimeValue < timeValue) {
                                isExist = false;
                            }
                        }
                        break;
                    case LT:
                        if (DAPStringUtils.isNumeric(realValue) && DAPStringUtils.isNumeric(value)) {
                            if (Double.valueOf(realValue) >= Double.valueOf(value)) {
                                isExist = false;
                            }
                        } else if (timeKeys != null && !timeKeys.isEmpty() && timeKeys.contains(name)) {
                            Long timeValue = getTime(value);
                            Long realTimeValue = getTime(realValue);
                            if (null == timeValue || null == realTimeValue || realTimeValue >= timeValue) {
                                isExist = false;
                            }
                        }
                        break;
                    case LET:
                        if (DAPStringUtils.isNumeric(realValue) && DAPStringUtils.isNumeric(value)) {
                            if (Double.valueOf(realValue) > Double.valueOf(value)) {
                                isExist = false;
                            }
                        } else if (timeKeys != null && !timeKeys.isEmpty() && timeKeys.contains(name)) {
                            Long timeValue = getTime(value);
                            Long realTimeValue = getTime(realValue);
                            if (null == timeValue || null == realTimeValue || realTimeValue > timeValue) {
                                isExist = false;
                            }
                        }
                        break;
                    case NE:
                        if (realValue.equals(value)) {
                            isExist = false;
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return isExist;
    }

    private Long getTime(String value) {
        try {
            return DateUtils.string2Date(value, timePattern).getTime();
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

    public String getTimePattern() {
        return timePattern;
    }

    public void setTimePattern(String timePattern) {
        this.timePattern = timePattern;
    }
}
