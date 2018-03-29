package com.dmsoft.firefly.gui.job;

import com.dmsoft.firefly.core.utils.CoreExceptionCode;
import com.dmsoft.firefly.core.utils.CoreExceptionParser;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.core.JobHandler;
import com.dmsoft.firefly.sdk.job.core.JobPipeline;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Basic impl for job pipeline
 *
 * @author Can Guan
 */
public class BasicJobPipeline implements JobPipeline {
    private List<JobHandler> handlers = Lists.newArrayList();
    private List<String> handlerNames = Lists.newArrayList();

    private JobHandler completedHandler;
    private JobHandler errorHandler;
    private JobHandler interruptHandler;

    @Override
    public JobPipeline addFirst(JobHandler handler) {
        checkExist(handler);
        handlerNames.add(0, handler.getName());
        handlers.add(0, handler);
        return this;
    }

    @Override
    public JobPipeline addLast(JobHandler handler) {
        checkExist(handler);
        handlers.add(handler);
        handlerNames.add(handler.getName());
        return this;
    }

    @Override
    public JobPipeline addBefore(JobHandler handler, String beforeName) {
        checkExist(handler);
        int index = getIndex(beforeName);
        handlers.add(index, handler);
        handlerNames.add(index, handler.getName());
        return this;
    }

    @Override
    public JobPipeline addAfter(JobHandler handler, String afterName) {
        checkExist(handler);
        int index = getIndex(afterName) + 1;
        handlers.add(index, handler);
        handlerNames.add(index, handler.getName());
        return this;
    }

    @Override
    public JobPipeline setCompleteHandler(JobHandler handler) {
        this.completedHandler = handler;
        return this;
    }

    @Override
    public JobPipeline setErrorHandler(JobHandler handler) {
        this.errorHandler = handler;
        return this;
    }

    @Override
    public boolean remove(String name) {
        int index = getIndex(name);
        handlers.remove(index);
        handlerNames.remove(index);
        return true;
    }

    @Override
    public boolean remove(JobHandler handler) {
        checkExist(handler);
        int index = getIndex(handler.getName());
        handlers.remove(index);
        handlerNames.remove(index);
        return true;
    }

    @Override
    public JobPipeline replace(String oldName, JobHandler handler) {
        checkExist(oldName);
        int index = getIndex(handler.getName());
        handlers.remove(index);
        handlerNames.remove(index);
        checkExist(handler);
        handlers.add(index, handler);
        handlerNames.add(index, handler.getName());
        return null;
    }

    @Override
    public List<String> getAllJobNames() {
        return handlerNames;
    }

    @Override
    public JobHandler getCompletedHandler() {
        return this.completedHandler;
    }

    @Override
    public JobHandler getErrorHandler() {
        return this.errorHandler;
    }

    @Override
    public List<JobHandler> getAllJobHandlers() {
        return this.handlers;
    }

    @Override
    public JobHandler getInterruptHandler() {
        return this.interruptHandler;
    }

    @Override
    public JobPipeline setInterruptHandler(JobHandler handler) {
        this.interruptHandler = handler;
        return this;
    }

    private void checkExist(JobHandler handler) {
        if (handlerNames.contains(handler.getName())) {
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_19001));
        }
    }

    private void checkExist(String jobName) {
        if (handlerNames.contains(jobName)) {
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_19001));
        }
    }

    private int getIndex(String jobName) {
        int index = handlerNames.indexOf(jobName);
        if (index < 0) {
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_19002));
        }
        return index;
    }
}
