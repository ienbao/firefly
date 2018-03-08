package com.dmsoft.firefly.sdk.dai.dto;

/**
 * Created by GuangLi on 2017/7/25.
 *
 * @param <T> string or other json
 */
public class UserPreferenceDto<T> {
    private String userName;
    private String code;
    private T value;

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

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
