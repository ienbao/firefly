/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr.dto;


import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * Created by Eligi.Ran on 2017/6/6.
 */
public class SpecificationDataDto extends AbstractValueObject {

    private String testItemName;
    private String dataType;
    private String lslFail;
    private String uslPass;

    public String getTestItemName() {
        return testItemName;
    }

    public void setTestItemName(String testItemName) {
        this.testItemName = testItemName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getLslFail() {
        return lslFail;
    }

    public void setLslFail(String lslFail) {
        this.lslFail = lslFail;
    }

    public String getUslPass() {
        return uslPass;
    }

    public void setUslPass(String uslPass) {
        this.uslPass = uslPass;
    }

    public boolean compareWith(SpecificationDataDto other) {
        if (other == null) {
            return false;
        }
        return testItemName.equals(other.testItemName) && dataType.equals(other.dataType)
                && lslFail.equals(other.lslFail) && uslPass.equals(other.uslPass);
    }
}

