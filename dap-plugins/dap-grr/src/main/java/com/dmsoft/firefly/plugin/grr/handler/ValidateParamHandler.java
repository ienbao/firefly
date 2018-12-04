package com.dmsoft.firefly.plugin.grr.handler;

import com.dmsoft.firefly.plugin.grr.dto.GrrParamDto;
import com.dmsoft.firefly.plugin.grr.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.grr.utils.GrrExceptionCode;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.grr.service.GrrFilterService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;

/**
 * handler for validate param
 *
 * @author Can Guan, Cherry Peng
 */
public class ValidateParamHandler extends AbstractBasicJobHandler {

    @Autowired
    private GrrFilterService grrFilterService;
    /**
     * constructor
     */
    public ValidateParamHandler() {
        setName(ParamKeys.VALIDATE_PARAM_HANDLER);
    }

    @Override
    public void doJob(JobContext context) {
        SearchDataFrame dataFrame = context.getParam(ParamKeys.SEARCH_DATA_FRAME, SearchDataFrame.class);
        SearchConditionDto searchConditionDto = context.getParam(ParamKeys.SEARCH_GRR_CONDITION_DTO, SearchConditionDto.class);

        // progress
//        GrrFilterService grrFilterService = RuntimeContext.getBean(GrrFilterService.class);

        GrrParamDto grrParamDto = grrFilterService.validateGrrParam(dataFrame, searchConditionDto);
        if (grrParamDto.getErrors() == null || grrParamDto.getErrors().isEmpty()) {
            searchConditionDto.setParts(new LinkedList<>(grrParamDto.getParts()));
            if (grrParamDto.getAppraisers() != null && !grrParamDto.getAppraisers().isEmpty()) {
                searchConditionDto.setAppraisers(new LinkedList<>(grrParamDto.getAppraisers()));
            }
            context.put(ParamKeys.SEARCH_GRR_CONDITION_DTO, searchConditionDto);
            context.put(ParamKeys.GRR_PARAM_DTO, grrParamDto);
        } else {
            context.put(ParamKeys.GRR_PARAM_DTO, grrParamDto);
            throw new ApplicationException(GrrFxmlAndLanguageUtils.getString(GrrExceptionCode.ERR_12001));
        }
    }
}
