package com.dmsoft.firefly.plugin.grr.utils;

import javafx.beans.value.ObservableValue;

/**
 * Created by cherry on 2018/3/17.
 */
public interface ChangeCallBack<T> {

    void execute(ObservableValue<? extends T> observable, T oldValue, T newValue);
}
