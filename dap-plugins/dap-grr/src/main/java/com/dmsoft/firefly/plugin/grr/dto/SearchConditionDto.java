/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.grr.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;

import java.util.List;

/**
 * Created by Ethan.Yang on 2018/2/6.
 */
public class SearchConditionDto extends AbstractValueObject {
    private List<TestItemWithTypeDto> selectedTestItemDtos;
    private List<String> searchCondition;
    private String part;
    private String appraiser;
    private Integer partInt;
    private Integer appraiserInt;
    private Integer trialInt;
    private List<String> parts;
    private List<String> appraisers;

    public List<TestItemWithTypeDto> getSelectedTestItemDtos() {
        return selectedTestItemDtos;
    }

    public void setSelectedTestItemDtos(List<TestItemWithTypeDto> selectedTestItemDtos) {
        this.selectedTestItemDtos = selectedTestItemDtos;
    }

    public List<String> getSearchCondition() {
        return searchCondition;
    }

    public void setSearchCondition(List<String> searchCondition) {
        this.searchCondition = searchCondition;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getAppraiser() {
        return appraiser;
    }

    public void setAppraiser(String appraiser) {
        this.appraiser = appraiser;
    }

    public Integer getPartInt() {
        return partInt;
    }

    public void setPartInt(Integer partInt) {
        this.partInt = partInt;
    }

    public Integer getAppraiserInt() {
        return appraiserInt;
    }

    public void setAppraiserInt(Integer appraiserInt) {
        this.appraiserInt = appraiserInt;
    }

    public Integer getTrialInt() {
        return trialInt;
    }

    public void setTrialInt(Integer trialInt) {
        this.trialInt = trialInt;
    }

    public List<String> getParts() {
        return parts;
    }

    public void setParts(List<String> parts) {
        this.parts = parts;
    }

    public List<String> getAppraisers() {
        return appraisers;
    }

    public void setAppraisers(List<String> appraisers) {
        this.appraisers = appraisers;
    }
}
