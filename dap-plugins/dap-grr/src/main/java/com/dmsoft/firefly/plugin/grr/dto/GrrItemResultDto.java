package com.dmsoft.firefly.plugin.grr.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

import java.util.List;
import java.util.Map;

/**
 * Created by cherry on 2018/3/15.
 */
public class GrrItemResultDto extends AbstractValueObject {

    private Map<String, String> totalMeans;
    private Map<String, String> totalRanges;
    private Map<String, GrrMeanAndRangeDto> meanAndRangeDtos;

    public Map<String, String> getTotalMeans() {
        return totalMeans;
    }

    public void setTotalMeans(Map<String, String> totalMeans) {
        this.totalMeans = totalMeans;
    }

    public Map<String, String> getTotalRanges() {
        return totalRanges;
    }

    public void setTotalRanges(Map<String, String> totalRanges) {
        this.totalRanges = totalRanges;
    }

    public Map<String, GrrMeanAndRangeDto> getMeanAndRangeDtos() {
        return meanAndRangeDtos;
    }

    public void setMeanAndRangeDtos(Map<String, GrrMeanAndRangeDto> meanAndRangeDtos) {
        this.meanAndRangeDtos = meanAndRangeDtos;
    }
}
