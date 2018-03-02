/*
 * Copyright (c) 2017. For intelligent group.
 */

package com.dmsoft.firefly.sdk.dai.dto;

import java.io.Serializable;

/**
 * Created by Eligi.Ran on 2017/6/6.
 */
public class SpecificationDataDto implements Serializable {

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
}

