package com.dmsoft.firefly.core.daiimpl.entity;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

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
