package com.dmsoft.firefly.plugin.spc.handler;

import com.dmsoft.firefly.plugin.spc.dto.SpcSettingDto;
import com.dmsoft.firefly.plugin.spc.service.SpcSettingService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;

/**
 * find spc setting data handler
 *
 * @author Can Guan, Ethan Yang
 */
public class FindSpcSettingDataHandler extends AbstractBasicJobHandler {
    /**
     * constructor
     */
    public FindSpcSettingDataHandler() {
        setName(ParamKeys.FIND_SPC_SETTING_HANDLER);
    }

    @Override
    public void doJob(JobContext context) {
        SpcSettingService spcSettingService = RuntimeContext.getBean(SpcSettingService.class);
        SpcSettingDto spcSettingDto = spcSettingService.findSpcSetting();
        context.put(ParamKeys.SPC_SETTING_DTO, spcSettingDto);
    }
}
