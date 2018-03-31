package com.dmsoft.firefly.gui.job;

import com.dmsoft.firefly.sdk.job.core.JobContext;
import com.dmsoft.firefly.sdk.job.core.JobEvent;
import com.dmsoft.firefly.sdk.job.core.JobEventListener;
import com.dmsoft.firefly.sdk.job.core.JobHandler;
import com.google.common.collect.Lists;
import javafx.application.Platform;

import java.util.HashMap;
import java.util.List;

/**
 * basic impl for job context
 */
public class BasicJobContext extends HashMap<String, Object> implements JobContext {
    private boolean isInterrupted = false;
    private List<JobEventListener> listenerList = Lists.newArrayList();
    private Throwable throwable;
    private List<String> doneJobNames = Lists.newArrayList();
    private List<JobHandler> handlers;
    private Double currentProgress = 0d;
    private Double totalWeight = 0d;

    @Override
    public <T> T getParam(String key, Class<T> tClass) {
        if (tClass == null) {
            return null;
        }
        Object o = get(key);
        if (o != null && tClass.isInstance(o)) {
            return tClass.cast(o);
        }
        return null;
    }

    @Override
    public void interruptBeforeNextJobHandler() {
        isInterrupted = true;
    }

    @Override
    public boolean isInterrupted() {
        return isInterrupted;
    }

    @Override
    public void pushEvent(JobEvent event) {
        Platform.runLater(() -> {
            for (JobEventListener listener : listenerList) {
                listener.eventNotify(convert(event));
            }
        });
    }

    @Override
    public void addJobEventListener(JobEventListener listener) {
        this.listenerList.add(listener);
    }

    @Override
    public boolean removeListener(JobEventListener listener) {
        return this.listenerList.remove(listener);
    }

    @Override
    public boolean isError() {
        return this.throwable != null;
    }

    @Override
    public Throwable getError() {
        return this.throwable;
    }

    @Override
    public void setError(Throwable error) {
        this.throwable = error;
    }

    @Override
    public List<String> getDoneHandlerNames() {
        return Lists.newArrayList(this.doneJobNames);
    }

    @Override
    public void addDoneHandlerName(String jobName) {
        Double currentWeight = getCurrentWeight();
        if (currentWeight != null && !Double.isNaN(currentWeight)) {
            this.currentProgress += currentWeight / totalWeight;
        }
        this.doneJobNames.add(jobName);
    }

    @Override
    public Double getCurrentProgress() {
        return this.currentProgress;
    }

    @Override
    public void setCurrentProgress(Double progress) {
        this.currentProgress = progress;
    }

    void setAllHandlers(List<JobHandler> handlers) {
        this.handlers = handlers;
        for (JobHandler handler : handlers) {
            if (handler.getWeight() != null && !Double.isNaN(handler.getWeight())) {
                totalWeight += handler.getWeight();
            }
        }
    }

    JobEvent convert(JobEvent event) {
        JobEvent event1 = new JobEvent();
        event1.setEventName(event.getEventName());
        event1.setEventObject(event.getEventObject());
        Double currentWeight = getCurrentWeight();
        if (currentWeight != null && !Double.isNaN(currentWeight)) {
            event1.setProgress(currentProgress + event.getProgress() * currentWeight / (totalWeight * 100));
        } else if (totalWeight == 0d) {
            event1.setProgress(event.getProgress() * 100);
        } else {
            event1.setProgress(event.getProgress() * 100 / totalWeight);
        }
        return event1;
    }

    private Double getCurrentWeight() {
        if (handlers == null) {
            return null;
        }
        if (this.doneJobNames.isEmpty()) {
            return 0.0;
        }
        for (JobHandler handler : handlers) {
            if (handler.getName().equals(this.doneJobNames.get(this.doneJobNames.size() - 1))) {
                return handler.getWeight();
            }
        }
        return null;
    }
}
