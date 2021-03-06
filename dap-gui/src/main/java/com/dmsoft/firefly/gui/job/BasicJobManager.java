package com.dmsoft.firefly.gui.job;

import com.dmsoft.firefly.sdk.job.core.JobContext;
import com.dmsoft.firefly.sdk.job.core.JobHandler;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.dmsoft.firefly.sdk.job.core.JobPipeline;
import com.google.common.collect.Maps;
import javafx.application.Platform;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.springframework.stereotype.Service;

/**
 * basic impl for job manager
 *
 * @author Can Guan
 */
@Service
public class BasicJobManager implements JobManager {
    private Map<String, JobPipeline> pipelineMap = Maps.newHashMap();
    private Map<Thread, JobContext> contextMap = Maps.newHashMap();

    @Override
    public void initializeJob(String pipelineName, JobPipeline jobPipeline) {
        pipelineMap.put(pipelineName, jobPipeline);
    }

    @Override
    public JobPipeline getPipeLine(String pipelineName) {
        return pipelineMap.get(pipelineName);
    }

    @Override
    public JobContext fireJobSyn(String pipelineName, JobContext context, boolean skipError) {
        return fireJobSyn(pipelineMap.get(pipelineName), context, skipError);
    }

    @Override
    public JobContext fireJobSyn(String pipelineName, JobContext context) {
        return fireJobSyn(pipelineName, context, false);
    }

    @Override
    public JobContext fireJobSyn(JobPipeline jobPipeline, JobContext context, boolean skipError) {
        if (jobPipeline == null) {
            return null;
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Thread thread = new Thread(() -> {
            fireJob(jobPipeline, context, skipError);
            countDownLatch.countDown();
        });
        contextMap.put(thread, context);
        thread.start();
        try {
            countDownLatch.await();
        } catch (InterruptedException ignored) {
        }
        contextMap.remove(thread);
        return context;
    }

    @Override
    public JobContext fireJobSyn(JobPipeline jobPipeline, JobContext context) {
        return fireJobSyn(jobPipeline, context, false);
    }

    @Override
    public void fireJobASyn(String pipelineName, JobContext context, boolean skipError) {
        fireJobASyn(pipelineMap.get(pipelineName), context, skipError);
    }

    @Override
    public void fireJobASyn(String pipelineName, JobContext context) {
        fireJobSyn(pipelineName, context, false);
    }

    @Override
    public void fireJobASyn(JobPipeline jobPipeline, JobContext context, boolean skipError) {
        if (jobPipeline == null) {
            return;
        }
        Thread thread = new Thread(() -> {
            contextMap.put(Thread.currentThread(), context);
            fireJob(jobPipeline, context, skipError);
            contextMap.remove(Thread.currentThread());
        });
        thread.start();
    }

    @Override
    public void fireJobASyn(JobPipeline jobPipeline, JobContext context) {
        fireJobASyn(jobPipeline, context, false);
    }

    @Override
    public JobContext findJobContext(Thread thread) {
        return contextMap.get(thread);
    }

    private void fireJob(JobPipeline jobPipeline, JobContext context, boolean skipError) {
        if (context instanceof BasicJobContext) {
            ((BasicJobContext) context).setAllHandlers(jobPipeline.getAllJobHandlers());
        }
        for (JobHandler handler : jobPipeline.getAllJobHandlers()) {
            if ((!context.isError() && !context.isInterrupted()) || (context.isError() && skipError)) {
                try {
                    context.addDoneHandlerName(handler.getName());
                    handler.doJob(context);
                } catch (Exception e) {
                    context.setError(e);
                    if (jobPipeline.getErrorHandler() != null) {
                        Platform.runLater(() -> {
                            context.addDoneHandlerName(jobPipeline.getErrorHandler().getName());
                            jobPipeline.getErrorHandler().doJob(context);
                        });
                    } else {
                        throw e;
                    }
                    if (!skipError) {
                        return;
                    }

                }
            } else if (context.isInterrupted()) {
                if (jobPipeline.getInterruptHandler() != null) {
                    Platform.runLater(() -> {
                        context.addDoneHandlerName(jobPipeline.getInterruptHandler().getName());
                        jobPipeline.getInterruptHandler().doJob(context);
                    });
                }
                return;
            }
        }
        if (jobPipeline.getCompletedHandler() != null) {
            Platform.runLater(() -> jobPipeline.getCompletedHandler().doJob(context));
        }
    }
}
