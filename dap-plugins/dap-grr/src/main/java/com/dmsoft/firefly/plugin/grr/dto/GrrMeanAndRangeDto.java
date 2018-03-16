package com.dmsoft.firefly.plugin.grr.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

import java.util.List;

/**
 * Created by cherry on 2018/3/15.
 */
public class GrrMeanAndRangeDto extends AbstractValueObject {

    private List<String> means;
    private List<String> ranges;

    public List<String> getMeans() {
        return means;
    }

    public void setMeans(List<String> means) {
        this.means = means;
    }

    public List<String> getRanges() {
        return ranges;
    }

    public void setRanges(List<String> ranges) {
        this.ranges = ranges;
    }
}
