package com.dmsoft.firefly.core.sdkimpl.dai.entity;

import org.bson.types.ObjectId;

import java.util.Map;

/**
 * entity class for project
 *
 * @author Can Guan
 */
public class Project {
    private ObjectId id;
    private String projectName;
    private Map<String, TestItem> testItems;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Map<String, TestItem> getTestItems() {
        return testItems;
    }

    public void setTestItems(Map<String, TestItem> testItems) {
        this.testItems = testItems;
    }
}
