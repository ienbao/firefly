package com.dmsoft.firefly.sdk.job.core;

import java.util.List;
import java.util.Map;

/**
 * interface class for job context
 *
 * @author Can Guan
 */
public interface JobContext extends Map<String, Object>, JobListable {
    /**
     * method to get param
     *
     * @param key    key
     * @param tClass class type
     * @param <T>    any object
     * @return param
     */
    <T> T getParam(String key, Class<T> tClass);

    /**
     * method to interrupt pipeline before next job handler
     */
    void interruptBeforeNextJobHandler();

    /**
     * method to judge is interrupted or not
     *
     * @return is interrupted or not
     */
    boolean isInterrupted();

    /**
     * method to push event
     *
     * @param event job event
     */
    void pushEvent(JobEvent event);

    /**
     * has error or not
     *
     * @return true : has error, false : no error
     */
    boolean isError();

    /**
     * method to get error
     *
     * @return null : no error, object : error object
     */
    Throwable getError();

    /**
     * method to set error
     *
     * @param error exception
     */
    void setError(Throwable error);

    /**
     * get done handler names
     *
     * @return list of done names
     */
    List<String> getDoneHandlerNames();

    /**
     * method to add done handler name
     *
     * @param jobName job name
     */
    void addDoneHandlerName(String jobName);

    /**
     * method to get current process
     *
     * @return current progcess
     */
    Double getCurrentProgress();

    /**
     * method to set current progress
     *
     * @param progress progress
     */
    void setCurrentProgress(Double progress);
}
