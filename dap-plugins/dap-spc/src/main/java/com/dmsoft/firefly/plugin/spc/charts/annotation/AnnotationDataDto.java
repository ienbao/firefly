package com.dmsoft.firefly.plugin.spc.charts.annotation;

/**
 * Created by cherry on 2018/4/19.
 */
public class AnnotationDataDto {

    private Object selectName;
    private String value;

    /**
     * Constructor for AnnotationDataDto
     */
    public AnnotationDataDto() {
    }

    /**
     *
     * @param selectName
     * @param value
     */
    public AnnotationDataDto(Object selectName, String value) {
        this.selectName = selectName;
        this.value = value;
    }

    public Object getSelectName() {
        return selectName;
    }

    public void setSelectName(Object selectName) {
        this.selectName = selectName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
