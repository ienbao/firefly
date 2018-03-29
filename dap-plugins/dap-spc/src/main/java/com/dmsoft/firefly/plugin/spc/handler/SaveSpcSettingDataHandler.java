package com.dmsoft.firefly.plugin.spc.handler;

import com.dmsoft.firefly.plugin.spc.dto.SpcSettingDto;
import com.dmsoft.firefly.plugin.spc.service.SpcSettingService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;

/**
 * handler for saving spc setting data
 *
 * @author Can Guan, Ethan Yang
 */
public class SaveSpcSettingDataHandler extends AbstractBasicJobHandler {
    /**
     * constructor
     */
    public SaveSpcSettingDataHandler() {
        setName(ParamKeys.SAVE_SPC_SETTING_HANDLER);
    }

    @Override
    public void doJob(JobContext context) {
        SpcSettingDto spcSettingDto = context.getParam(ParamKeys.SPC_SETTING_DTO, SpcSettingDto.class);
        SpcSettingService spcSettingService = RuntimeContext.getBean(SpcSettingService.class);
        spcSettingService.saveSpcSetting(spcSettingDto);
    }
}
