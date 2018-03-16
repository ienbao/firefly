package com.dmsoft.firefly.plugin.grr.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * grr export detail dto
 *
 * @author Can Guan
 */
public class GrrExportDetailDto extends AbstractValueObject {
    private String key;
    private String itemName;
    private GrrExportDetailDto exportDetailDto;

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

    public GrrExportDetailDto getExportDetailDto() {
        return exportDetailDto;
    }

    public void setExportDetailDto(GrrExportDetailDto exportDetailDto) {
        this.exportDetailDto = exportDetailDto;
    }
}
