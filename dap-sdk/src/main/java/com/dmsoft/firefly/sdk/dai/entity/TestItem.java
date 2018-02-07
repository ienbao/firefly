package com.dmsoft.firefly.sdk.dai.entity;

import org.bson.types.ObjectId;

import java.util.List;

/**
 * Created by cherry on 2018/1/16.
 */
public class TestItem {
    private ObjectId id;

    String projectName;

    private List<String> itemNames;

    public TestItem() {
    }

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

    public List<String> getItemNames() {
        return itemNames;
    }

    public void setItemNames(List<String> itemNames) {
        this.itemNames = itemNames;
    }
}
