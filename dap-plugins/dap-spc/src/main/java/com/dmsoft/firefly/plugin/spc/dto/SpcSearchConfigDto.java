/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.dto;

import java.util.List;

/**
 * Created by Ethan.Yang on 2018/2/6.
 */
public class SpcSearchConfigDto extends SpcAnalysisConfigDto {

    private List<String> projectNames;
    private String templateName;

    private int subgroupSize;
    private int intervalNumber;

    public List<String> getProjectNames() {
        return projectNames;
    }

    public void setProjectNames(List<String> projectNames) {
        this.projectNames = projectNames;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public int getSubgroupSize() {
        return subgroupSize;
    }

    public void setSubgroupSize(int subgroupSize) {
        this.subgroupSize = subgroupSize;
    }

    public int getIntervalNumber() {
        return intervalNumber;
    }

    public void setIntervalNumber(int intervalNumber) {
        this.intervalNumber = intervalNumber;
    }
}
