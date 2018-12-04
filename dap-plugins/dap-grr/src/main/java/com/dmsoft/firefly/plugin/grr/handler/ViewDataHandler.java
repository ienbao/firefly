package com.dmsoft.firefly.plugin.grr.handler;

import com.dmsoft.firefly.plugin.grr.dto.GrrConfigDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrDataFrameDto;
import com.dmsoft.firefly.plugin.grr.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.grr.service.GrrFilterService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * view data handler
 *
 * @author Can Guan, Cherry Peng
 */
public class ViewDataHandler extends AbstractBasicJobHandler {

    @Autowired
    private GrrFilterService grrFilterService;
    /**
     * constructor
     */
    public ViewDataHandler() {
        setName(ParamKeys.GRR_VIEW_DATA_HANDLER);
    }

    @Override
    public void doJob(JobContext context) {
        SearchDataFrame dataFrame = context.getParam(ParamKeys.SEARCH_DATA_FRAME, SearchDataFrame.class);
        GrrConfigDto grrConfigDto = context.getParam(ParamKeys.SEARCH_GRR_CONFIG_DTO, GrrConfigDto.class);
        TemplateSettingDto templateSettingDto = context.getParam(ParamKeys.SEARCH_TEMPLATE_SETTING_DTO, TemplateSettingDto.class);
        SearchConditionDto searchConditionDto = context.getParam(ParamKeys.SEARCH_GRR_CONDITION_DTO, SearchConditionDto.class);

        // progress
//        GrrFilterService grrFilterService = RuntimeContext.getBean(GrrFilterService.class);
        GrrDataFrameDto grrDataFrameDto = grrFilterService.getGrrViewData(dataFrame, grrConfigDto, templateSettingDto, searchConditionDto);
        context.put(ParamKeys.SEARCH_VIEW_DATA_FRAME, grrDataFrameDto);
    }
}
