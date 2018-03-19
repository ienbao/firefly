package com.dmsoft.firefly.plugin.grr.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrExportDetailResultDto;

/**
 * grr export detail dto
 *
 * @author Can Guan
 */
public class GrrExportDetailDto extends AbstractValueObject {
    private String key;
    private String itemName;
    private GrrExportDetailResultDto exportDetailDto;

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

    public GrrExportDetailResultDto getExportDetailDto() {
        return exportDetailDto;
    }

    public void setExportDetailDto(GrrExportDetailResultDto exportDetailDto) {
        this.exportDetailDto = exportDetailDto;
    }
}
