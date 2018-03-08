/*
 *
 *  * Copyright (c) 2016. For Intelligent Group.
 *
 */

package com.dmsoft.firefly.sdk.utils.parser;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by Julia on 2017/4/14.
 */
public class ParserUtils {

    /**
     * Parser source string to SEPResult tree struct.
     *
     * @param value string.
     * @return SEPResult
     */
    public static SEPResult parserString(String value) {
        SEPResult result = new SEPResult();
        FilterExpressionParser fep = new FilterExpressionParser();
        if (StringUtils.isNotBlank(value)) {
            value = value.replaceAll("\"\"\"\"", "\"/NULL>\"");
            result = fep.doParser(value);
        }
        return result;
    }

    /**
     * Parser source string to List SEPResult tree struct.
     *
     * @param values list string.
     * @return List<SEPResult>
     */
    public static List<SEPResult> parserString(List<String> values) {
        List<SEPResult> results = Lists.newArrayList();
        if (values != null && !values.isEmpty()) {
            for (String value : values) {
                results.add(parserString(value));
            }
        }
        return results;
    }

    /**
     * Merge string.
     *
     * @param left  string.
     * @param token string.
     * @param right string.
     * @return string
     */
    public static String mergeToString(String left, String token, String right) {
        StringBuilder builder = new StringBuilder();
        builder.append("\"").append(left).append("\"").append(token).append("\"").append(right).append("\"");
        return builder.toString();
    }

    /**
     * Merge string.
     *
     * @param values list string[].
     * @return list string
     */
    public static List<String> mergeToListString(List<String[]> values) {
        List<String> result = Lists.newArrayList();
        if (values != null && !values.isEmpty()) {
            for (String[] value : values) {
                if (value != null && value.length == 3) {
                    result.add(mergeToString(value[0], value[1], value[2]));
                }
            }
        }
        return result;
    }

    /**
     * Merge string.
     *
     * @param values list string[].
     * @return list string
     */
    public static String mergeToString(List<String[]> values) {
        StringBuilder builder = new StringBuilder();
        if (values != null && !values.isEmpty()) {
            for (String[] value : values) {
                if (value != null && value.length == 3) {
                    builder.append(mergeToString(value[0], value[1], value[2])).append(" & ");
                }
            }
            if (builder.length() > 3) {
                builder.delete(builder.length() - 3, builder.length());
            }
        }
        return builder.toString();
    }

    /**
     * Merge string.
     *
     * @param values list string.
     * @return list string
     */
    public static String mergeListToString(List<String> values) {
        StringBuilder builder = new StringBuilder();
        if (values != null && !values.isEmpty()) {
            for (String value : values) {
                if (value != null) {
                    builder.append(value).append(" & ");
                }
            }
            if (builder.length() > 3) {
                builder.delete(builder.length() - 3, builder.length());
            }
        }
        return builder.toString();
    }
}
