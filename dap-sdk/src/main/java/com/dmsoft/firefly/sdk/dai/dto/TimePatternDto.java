/*
 *
 *  *  Copyright (c) 2017. For Intelligent Group.
 *  *
 *
 */

package com.dmsoft.firefly.sdk.dai.dto;

import java.util.List;

/**
 * Created by cherry on 2017/9/23.
 */
public class TimePatternDto {

    private List<String> timeKeys;
    private String pattern;

    public List<String> getTimeKeys() {
        return timeKeys;
    }

    public void setTimeKeys(List<String> timeKeys) {
        this.timeKeys = timeKeys;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
