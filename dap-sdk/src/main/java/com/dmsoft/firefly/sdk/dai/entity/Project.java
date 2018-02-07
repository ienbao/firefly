package com.dmsoft.firefly.sdk.dai.entity;

import org.bson.types.ObjectId;

/**
 * Created by Lucien.Chen on 2018/2/6.
 */
public class Project {

    private ObjectId id;

    private String projectName;
    private String path;

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
