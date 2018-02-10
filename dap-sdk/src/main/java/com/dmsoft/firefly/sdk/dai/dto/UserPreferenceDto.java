package com.dmsoft.firefly.sdk.dai.dto;

/**
 * Created by GuangLi on 2017/7/25.
 */
public class UserPreferenceDto {
    private String userName;
    private String code;
    private Object value;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
