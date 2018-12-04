package com.dmsoft.firefly.plugin.yield.handler;

import com.dmsoft.firefly.plugin.yield.dto.YieldSettingDto;
import com.dmsoft.firefly.plugin.yield.service.YieldSettingService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import org.springframework.beans.factory.annotation.Autowired;

public class FindYieldSettingDataHandler extends AbstractBasicJobHandler {

    @Autowired
    private YieldSettingService yieldSettingService;
    /**
     * constructor
     */
    public FindYieldSettingDataHandler() {
        setName(ParamKeys.FIND_YIELD_SETTING_HANDLER);
    }

    @Override
    public void doJob(JobContext context) {
//        YieldSettingService yieldSettingService = RuntimeContext.getBean(YieldSettingService.class);
        YieldSettingDto yieldSettingDto = yieldSettingService.findYieldSetting();
        context.put(ParamKeys.YIELD_SETTING_DTO, yieldSettingDto);
    }
}
