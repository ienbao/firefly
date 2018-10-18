package com.dmsoft.firefly.plugin.yield.utils;

import javafx.beans.property.SimpleObjectProperty;

public class SourceObjectProperty<T> extends SimpleObjectProperty<T> {
    private T sourceValue;
    private boolean error = false;
    /**
     * constructor
     */
    public SourceObjectProperty(){

    }
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

    @Override
    public void setValue(T v) {
        set(v);
    }
}
