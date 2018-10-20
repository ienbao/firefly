package com.dmsoft.firefly.plugin.yield.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

public class YieldChartResultDto extends AbstractValueObject {
    private  NDCResultDto ndcResult;

    public NDCResultDto getNdcResult() {
        return ndcResult;
    }

    public void setNdcResult(NDCResultDto ndcResult) {
        this.ndcResult = ndcResult;
    }
}
