package com.dmsoft.firefly.plugin.grr.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrSummaryResultDto;

/**
 * summary result dto for grr
 *
 * @author Can Guan
 */
public class GrrSummaryDto extends AbstractValueObject {
    private String key;
    private String itemName;
    private GrrSummaryResultDto summaryResultDto;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public GrrSummaryResultDto getSummaryResultDto() {
        return summaryResultDto;
    }

    public void setSummaryResultDto(GrrSummaryResultDto summaryResultDto) {
        this.summaryResultDto = summaryResultDto;
    }
}
