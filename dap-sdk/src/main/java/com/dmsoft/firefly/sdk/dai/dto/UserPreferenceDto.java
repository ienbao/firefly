package com.dmsoft.firefly.sdk.dai.dto;

/**
 * Created by GuangLi on 2017/7/25.
 */
public class UserPreferenceDto {
    private Long userId;
    private String code;
    private Object value;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
