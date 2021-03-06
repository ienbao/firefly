package com.dmsoft.firefly.gui.job;

import com.dmsoft.firefly.sdk.job.core.JobContext;
import com.dmsoft.firefly.sdk.job.core.JobFactory;
import com.dmsoft.firefly.sdk.job.core.JobPipeline;
import org.springframework.stereotype.Service;

/**
 * basic impl for job factory
 *
 * @author Can Guan
 */
@Service
public class BasicJobFactory implements JobFactory {
    @Override
    public JobContext createJobContext() {
        return new BasicJobContext();
    }

    @Override
    public JobPipeline createJobPipeLine() {
        return new BasicJobPipeline();
    }
}
