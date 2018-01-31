package com.dmsoft.firefly.sdk.job;

/**
 * Created by Lucien.Chen on 2018/1/31.
 */
public interface Handler {

    Object active(Object o);

    void throwException();
}
