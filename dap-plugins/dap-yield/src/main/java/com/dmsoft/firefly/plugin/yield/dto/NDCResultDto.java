package com.dmsoft.firefly.plugin.yield.dto;

public class NDCResultDto {
    private Double[] histX;//x轴的值
    private Double[] histY;//y轴的值

    public NDCResultDto(Double[] histX, Double[] histY) {
        this.histX = histX;
        this.histY = histY;
    }

    public Double[] getHistX() {
        return histX;
    }

    public void setHistX(Double[] histX) {
        this.histX = histX;
    }

    public Double[] getHistY() {
        return histY;
    }

    public void setHistY(Double[] histY) {
        this.histY = histY;
    }
}
