package com.dmsoft.firefly.plugin.grr.service;

import com.dmsoft.firefly.plugin.grr.dto.*;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;

/**
 * interface for grr service
 *
 * @author Julia
 */
public interface GrrFilterService {

    /**
     * method to validate param and get auto param
     *
     * @param dataFrame       data frame
     * @param searchConditionDto  search condition dto
     * @return list of grr summary dto
     */
     GrrParamDto validateGrrParam(SearchDataFrame dataFrame, SearchConditionDto searchConditionDto);

    /**
     * method to get grr view data
     *
     * @param dataFrame       data frame
     * @param configDto  config dto
     * @param templateSettingDto       template setting dto
     * @param searchConditionDto  search condition dto
     * @return list of grr summary dto
     */
    GrrDataFrameDto getGrrViewData(SearchDataFrame dataFrame, GrrConfigDto configDto, TemplateSettingDto templateSettingDto, SearchConditionDto searchConditionDto);
}
