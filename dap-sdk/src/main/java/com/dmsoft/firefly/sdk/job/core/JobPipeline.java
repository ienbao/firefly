package com.dmsoft.firefly.sdk.job.core;

import java.util.List;

/**
 * interface class for pipeline
 *
 * @author Can Guan
 */
public interface JobPipeline {
    /**
     * method to add handler at first
     *
     * @param handler handler
     * @return added pipeline
     */
    JobPipeline addFirst(JobHandler handler);

    /**
     * method to add handler at last
     *
     * @param handler handler
     * @return added pipeline
     */
    JobPipeline addLast(JobHandler handler);

    /**
     * method to add handler before other handler
     *
     * @param handler    new handler
     * @param beforeName which handler to add before
     * @return added pipeline
     */
    JobPipeline addBefore(JobHandler handler, String beforeName);

    /**
     * method to add handler after other handler
     *
     * @param handler   new handler
     * @param afterName which handler to add after
     * @return added pipeline
     */
    JobPipeline addAfter(JobHandler handler, String afterName);

    /**
     * method to set complete handler
     *
     * @param handler complete handler
     * @return added pipeline
     */
    JobPipeline setCompleteHandler(JobHandler handler);

    /**
     * method to get completed handler
     *
     * @return job handler
     */
    JobHandler getCompletedHandler();

    /**
     * method to get error handler
     *
     * @return job handler
     */
    JobHandler getErrorHandler();

    /**
     * method to set error handler
     *
     * @param handler error handler
     * @return added pipeline
     */
    JobPipeline setErrorHandler(JobHandler handler);

    /**
     * method to get interrupt handler
     *
     * @return job handler
     */
    JobHandler getInterruptHandler();

    /**
     * method to set interrupt handler
     *
     * @param handler interrupted handler
     * @return added pipeline
     */
    JobPipeline setInterruptHandler(JobHandler handler);

    /**
     * method to remove handler from pipe line
     *
     * @param name name
     * @return job handler
     */
    boolean remove(String name);

    /**
     * method to remove handler from pipe line
     *
     * @param handler handler
     * @return job handler
     */
    boolean remove(JobHandler handler);

    /**
     * method to replace handler by oldName
     *
     * @param oldName old oldName
     * @param handler new handler
     * @return pipe line
     */
    JobPipeline replace(String oldName, JobHandler handler);

    /**
     * method get all job name
     *
     * @return list of job name
     */
    List<String> getAllJobNames();

    /**
     * method to get all handlers
     *
     * @return list of handlers
     */
    List<JobHandler> getAllJobHandlers();
}
