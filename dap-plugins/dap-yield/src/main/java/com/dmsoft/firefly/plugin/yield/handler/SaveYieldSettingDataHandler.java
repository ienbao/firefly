package com.dmsoft.firefly.plugin.yield.handler;

import com.dmsoft.firefly.plugin.yield.dto.YieldSettingDto;
import com.dmsoft.firefly.plugin.yield.service.YieldSettingService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;

public class SaveYieldSettingDataHandler extends AbstractBasicJobHandler {
    /**
     * constructor
     */
    public SaveYieldSettingDataHandler() {
        setName(ParamKeys.SAVE_YIELD_SETTING_HANDLER);
    }

    @Override
    public void doJob(JobContext context) {
        YieldSettingDto yieldSettingDto = context.getParam(ParamKeys.YIELD_SETTING_DTO, YieldSettingDto.class);
        YieldSettingService yieldSettingService = RuntimeContext.getBean(YieldSettingService.class);
        yieldSettingService.saveYieldSetting(yieldSettingDto);
    }
}
