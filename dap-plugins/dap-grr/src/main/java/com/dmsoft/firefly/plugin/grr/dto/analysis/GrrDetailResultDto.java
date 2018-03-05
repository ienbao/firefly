package com.dmsoft.firefly.plugin.grr.dto.analysis;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * grr detail result dto
 *
 * @author Can Guan
 */
public class GrrDetailResultDto extends AbstractValueObject {
    private GrrComponentCResultDto componentChartDto;
    private GrrPACResultDto partAppraiserChartDto;
    private GrrControlChartDto xbarAppraiserChartDto;
    private GrrControlChartDto rangeAppraiserChartDto;
    private GrrScatterChartDto rrbyAppraiserChartDto;
    private GrrScatterChartDto rrbyPartChartDto;
    private GrrAnovaAndSourceResultDto anovaAndSourceResultDto;

    public GrrComponentCResultDto getComponentChartDto() {
        return componentChartDto;
    }

    public void setComponentChartDto(GrrComponentCResultDto componentChartDto) {
        this.componentChartDto = componentChartDto;
    }

    public GrrPACResultDto getPartAppraiserChartDto() {
        return partAppraiserChartDto;
    }

    public void setPartAppraiserChartDto(GrrPACResultDto partAppraiserChartDto) {
        this.partAppraiserChartDto = partAppraiserChartDto;
    }

    public GrrControlChartDto getXbarAppraiserChartDto() {
        return xbarAppraiserChartDto;
    }

    public void setXbarAppraiserChartDto(GrrControlChartDto xbarAppraiserChartDto) {
        this.xbarAppraiserChartDto = xbarAppraiserChartDto;
    }

    public GrrControlChartDto getRangeAppraiserChartDto() {
        return rangeAppraiserChartDto;
    }

    public void setRangeAppraiserChartDto(GrrControlChartDto rangeAppraiserChartDto) {
        this.rangeAppraiserChartDto = rangeAppraiserChartDto;
    }

    public GrrScatterChartDto getRrbyAppraiserChartDto() {
        return rrbyAppraiserChartDto;
    }

    public void setRrbyAppraiserChartDto(GrrScatterChartDto rrbyAppraiserChartDto) {
        this.rrbyAppraiserChartDto = rrbyAppraiserChartDto;
    }

    public GrrScatterChartDto getRrbyPartChartDto() {
        return rrbyPartChartDto;
    }

    public void setRrbyPartChartDto(GrrScatterChartDto rrbyPartChartDto) {
        this.rrbyPartChartDto = rrbyPartChartDto;
    }

    public GrrAnovaAndSourceResultDto getAnovaAndSourceResultDto() {
        return anovaAndSourceResultDto;
    }

    public void setAnovaAndSourceResultDto(GrrAnovaAndSourceResultDto anovaAndSourceResultDto) {
        this.anovaAndSourceResultDto = anovaAndSourceResultDto;
    }
}
