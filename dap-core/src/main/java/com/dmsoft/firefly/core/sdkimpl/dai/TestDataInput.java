package com.dmsoft.firefly.core.sdkimpl.dai;

import java.util.List;

/**
 * input class for test data
 *
 * @author Can Guan
 */
public class TestDataInput {
    private List<String> projectNameList;
    private String testItemName;

    public List<String> getProjectNameList() {
        return projectNameList;
    }

    public void setProjectNameList(List<String> projectNameList) {
        this.projectNameList = projectNameList;
    }

    public String getTestItemName() {
        return testItemName;
    }

    public void setTestItemName(String testItemName) {
        this.testItemName = testItemName;
    }
}
