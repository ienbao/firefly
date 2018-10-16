package com.dmsoft.firefly.plugin.yield.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * Created by Tod Dylan on 2018/10/16.
 */
public class YieldAnalysisConfigDto extends AbstractValueObject {
    private String primaryKey;

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }
}
