/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.gui.model;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * Created by Garen.Pang on 2018/2/27.
 */
public class DataAndProgress extends AbstractValueObject {

    private String value;
    private double progress;
    private boolean isSelect = false;
    private boolean isOver = false;

    public DataAndProgress(String value, double progress, boolean isSelect, boolean isOver) {
        this.value = value;
        this.progress = progress;
        this.isSelect = isSelect;
        this.isOver = isOver;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isOver() {
        return isOver;
    }

    public void setOver(boolean over) {
        isOver = over;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }
}
