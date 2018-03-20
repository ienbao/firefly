/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.grr.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * Created by Julia.Zhou on 2018/3/20.
 */
public class GrrPreferenceDto extends AbstractValueObject {
    private String part;
    private String appraiser;
    private Integer partInt;
    private Integer appraiserInt;
    private Integer trialInt;

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
}
