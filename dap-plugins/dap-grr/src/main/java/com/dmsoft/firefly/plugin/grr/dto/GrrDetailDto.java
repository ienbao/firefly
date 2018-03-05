package com.dmsoft.firefly.plugin.grr.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrDetailResultDto;

/**
 * detail dto for grr
 *
 * @author Can Guan
 */
public class GrrDetailDto extends AbstractValueObject {
    private String key;
    private String itemName;
    private GrrDetailResultDto grrDetailResultDto;

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

    public GrrDetailResultDto getGrrDetailResultDto() {
        return grrDetailResultDto;
    }

    public void setGrrDetailResultDto(GrrDetailResultDto grrDetailResultDto) {
        this.grrDetailResultDto = grrDetailResultDto;
    }
}
