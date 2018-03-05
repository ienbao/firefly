package com.dmsoft.firefly.plugin.grr.dto.analysis;

import java.util.List;

/**
 * Created by GuangLi on 2017/7/21.
 */
public class GrrAnovaAndSourceResultDto {
    private List<GrrAnovaDto> grrAnovaDtos;
    private List<GrrSourceDto> grrSourceDtos;
    private Double numberOfDc;

    public List<GrrAnovaDto> getGrrAnovaDtos() {
        return grrAnovaDtos;
    }

    public void setGrrAnovaDtos(List<GrrAnovaDto> grrAnovaDtos) {
        this.grrAnovaDtos = grrAnovaDtos;
    }

    public List<GrrSourceDto> getGrrSourceDtos() {
        return grrSourceDtos;
    }

    public void setGrrSourceDtos(List<GrrSourceDto> grrSourceDtos) {
        this.grrSourceDtos = grrSourceDtos;
    }

    public Double getNumberOfDc() {
        return numberOfDc;
    }

    public void setNumberOfDc(Double numberOfDc) {
        this.numberOfDc = numberOfDc;
    }
}
