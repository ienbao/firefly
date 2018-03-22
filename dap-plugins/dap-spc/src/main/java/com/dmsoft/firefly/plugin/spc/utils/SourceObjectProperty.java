/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.utils;

import javafx.beans.property.SimpleObjectProperty;

/**
 * Created by Ethan.Yang on 2018/3/5.
 */
public class SourceObjectProperty<T> extends SimpleObjectProperty<T> {
    private T sourceValue;
    private boolean error = false;

    /**
     * constructor
     *
     * @param initialValue initial value
     */
    public SourceObjectProperty(T initialValue) {
        super(initialValue);
        this.sourceValue = initialValue;
    }

    public T getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(T sourceValue) {
        this.sourceValue = sourceValue;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
