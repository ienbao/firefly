package com.dmsoft.firefly.sdk.dai.service;

import com.dmsoft.firefly.sdk.dai.dto.UserPreferenceDto;

/**
 * Created by GuangLi on 2018/1/25.
 */
public interface UserPreferenceService {

    /**
     * @param userPreferenceDto user preference dto
     */
    void addValueItem(UserPreferenceDto userPreferenceDto);

    /**
     * find SPC chart line
     *
     * @param code     code
     * @param userName user name
     * @return preference json string, need decode by jsonMapper
     */
    String findPreferenceByUserId(String code, String userName);

    /**
     * Update user preference
     *
     * @param userPreferenceDto user preference dto
     */
    void updatePreference(UserPreferenceDto userPreferenceDto);

    /**
     * @param userName user name
     * @param code     code
     */
    void deletePreference(String userName, String code);

    /**
     * Reset user preference
     */
    void resetPreference();
}
