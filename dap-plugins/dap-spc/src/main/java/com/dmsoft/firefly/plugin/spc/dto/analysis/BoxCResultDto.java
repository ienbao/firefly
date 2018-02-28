package com.dmsoft.firefly.plugin.spc.dto.analysis;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

import java.util.List;

/**
 * box chart result dto
 *
 * @author Can Guan
 */
public class BoxCResultDto extends AbstractValueObject {
    private List<SingleBoxDataDto> boxData;

    public List<SingleBoxDataDto> getBoxData() {
        return boxData;
    }

    public void setBoxData(List<SingleBoxDataDto> boxData) {
        this.boxData = boxData;
    }
}
