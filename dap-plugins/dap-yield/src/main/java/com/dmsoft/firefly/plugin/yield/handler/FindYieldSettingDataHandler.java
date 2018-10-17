package com.dmsoft.firefly.plugin.yield.handler;

import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;

public class FindYieldSettingDataHandler extends AbstractBasicJobHandler {
    /**
     * constructor
     */
    public FindYieldSettingDataHandler() {
        setName(ParamKeys.FIND_YIELD_SETTING_HANDLER);
    }

    @Override
    public void doJob(JobContext context) {
//        YieldSettingService spcSettingService = RuntimeContext.getBean(YieldSettingService.class);
//        YieldSettingDto yieldSettingDto = spcSettingService.findYieldSetting();
//        context.put(ParamKeys.YIELD_SETTING_DTO, yieldSettingDto);
    }
}
