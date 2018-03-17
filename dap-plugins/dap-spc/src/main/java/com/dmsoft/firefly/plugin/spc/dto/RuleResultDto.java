package com.dmsoft.firefly.plugin.spc.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

import java.util.Map;

/**
 * Created by GuangLi on 2017/7/12.
 */
public class RuleResultDto extends AbstractValueObject {
    private String ruleName;
    private Double[] x;
    private Double[] y;

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Double[] getX() {
        return x;
    }

    public void setX(Double[] x) {
        this.x = x;
    }

    public Double[] getY() {
        return y;
    }

    public void setY(Double[] y) {
        this.y = y;
    }
}
