/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr.dto;

import java.util.List;

/**
 * Created by GuangLi on 2017/7/21.
 */
public class GrrAnovaAndSourceResultDto {
    private List<GrrAnovaDto> grrAnovaDtos;
    private List<GrrSourceDto> grrSourceDtos;
    private String numberOfDc;

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

    public String getNumberOfDc() {
        return numberOfDc;
    }

    public void setNumberOfDc(String numberOfDc) {
        this.numberOfDc = numberOfDc;
    }
}
