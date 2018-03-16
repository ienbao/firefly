package com.dmsoft.firefly.plugin.grr.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

import java.util.List;
import java.util.Map;

/**
 * Created by cherry on 2018/3/15.
 */
public class GrrItemResultDto extends AbstractValueObject {

    private List<String> totalMeans;
    private List<String> totalRanges;
    private Map<String, GrrMeanAndRangeDto> meanAndRangeDtos;

    public List<String> getTotalMeans() {
        return totalMeans;
    }

    public void setTotalMeans(List<String> totalMeans) {
        this.totalMeans = totalMeans;
    }

    public List<String> getTotalRanges() {
        return totalRanges;
    }

    public void setTotalRanges(List<String> totalRanges) {
        this.totalRanges = totalRanges;
    }

    public Map<String, GrrMeanAndRangeDto> getMeanAndRangeDtos() {
        return meanAndRangeDtos;
    }

    public void setMeanAndRangeDtos(Map<String, GrrMeanAndRangeDto> meanAndRangeDtos) {
        this.meanAndRangeDtos = meanAndRangeDtos;
    }
}
