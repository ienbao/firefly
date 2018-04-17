/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.gui.utils;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * Created by Garen.Pang on 2018/3/7.
 *
 * @param <T> any class
 */
public class KeyValueDto<T> extends AbstractValueObject {

    private String key;
    private T value;

    /**
     * constructor
     */
    public KeyValueDto() {
    }

    /**
     * constructor
     *
     * @param key   key
     * @param value value
     */
    public KeyValueDto(String key, T value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
