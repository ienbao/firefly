/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

import java.util.Map;

/**
 * Created by Ethan.Yang on 2018/2/6.
 */
public class SpcViewDataDto extends AbstractValueObject {

    private String lineKey;
    private Map<String,Object> testData;

    public String getLineKey() {
        return lineKey;
    }

    public void setLineKey(String lineKey) {
        this.lineKey = lineKey;
    }

    public Map<String, Object> getTestData() {
        return testData;
    }

    public void setTestData(Map<String, Object> testData) {
        this.testData = testData;
    }
}
