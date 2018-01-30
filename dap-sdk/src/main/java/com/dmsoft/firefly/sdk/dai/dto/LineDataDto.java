package com.dmsoft.firefly.sdk.dai.dto;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by cherry on 2018/1/23.
 */
public class LineDataDto implements Serializable {

    private String projectName;
    private String lineNo;
    private Boolean lineUsed = true;
    private Map<String, Object> testData;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getLineNo() {
        return lineNo;
    }

    public void setLineNo(String lineNo) {
        this.lineNo = lineNo;
    }

    public Boolean getLineUsed() {
        return lineUsed;
    }

    public void setLineUsed(Boolean lineUsed) {
        this.lineUsed = lineUsed;
    }

    public Map<String, Object> getTestData() {
        return testData;
    }

    public void setTestData(Map<String, Object> testData) {
        this.testData = testData;
    }
}
