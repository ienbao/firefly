package com.dmsoft.firefly.plugin.spc.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * spc analysis config dto
 *
 * @author Can Guan
 */
public class SpcAnalysisConfigDto extends AbstractValueObject {
    private int subgroupSize;
    private int intervalNumber;

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
