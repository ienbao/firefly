package com.dmsoft.firefly.sdk.job;

/**
 * Created by Lucien.Chen on 2018/1/31.
 */
public interface Handler {

    /**
     * active
     *
     * @param o object
     * @return result
     */
    Object active(Object o);

    /**
     * throw exception
     */
    void throwException();
}
