package com.dmsoft.firefly.gui.components.utils;

/**
 * base class for validate rule
 *
 * @author Can Guan
 */
public class ValidateRule {
    private int maxLength;
    private String pattern;
    private Double maxValue;
    private Double minValue;
    private String errorStyle;
    private Boolean allowEmpty;
    private String emptyErrorMsg;
    private String rangErrorMsg;

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public String getErrorStyle() {
        return errorStyle;
    }

    public void setErrorStyle(String errorStyle) {
        this.errorStyle = errorStyle;
    }

    public Boolean getAllowEmpty() {
        return allowEmpty;
    }

    public void setAllowEmpty(Boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
    }

    public String getEmptyErrorMsg() {
        return emptyErrorMsg;
    }

    public void setEmptyErrorMsg(String emptyErrorMsg) {
        this.emptyErrorMsg = emptyErrorMsg;
    }

    public String getRangErrorMsg() {
        return rangErrorMsg;
    }

    public void setRangErrorMsg(String rangErrorMsg) {
        this.rangErrorMsg = rangErrorMsg;
    }
}
