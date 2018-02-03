package com.dmsoft.firefly.sdk.dai.dto;

import java.util.List;

/**
 * Created by GuangLi on 2018/1/24.
 */
public class ProjectDto {
    private String projectName;
    private String path;
    private List<String> itemNames;

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

    public List<String> getItemNames() {
        return itemNames;
    }

    public void setItemNames(List<String> itemNames) {
        this.itemNames = itemNames;
    }
}
