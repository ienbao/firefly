package com.dmsoft.firefly.plugin.grr.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

import java.util.List;
import java.util.Map;

/**
 * Created by cherry on 2018/3/15.
 */
public class GrrMeanAndRangeDto extends AbstractValueObject {

    private Map<String, String> means;
    private Map<String, String> ranges;

    public Map<String, String> getMeans() {
        return means;
    }

    public void setMeans(Map<String, String> means) {
        this.means = means;
    }

    public Map<String, String> getRanges() {
        return ranges;
    }

    public void setRanges(Map<String, String> ranges) {
        this.ranges = ranges;
    }
}
