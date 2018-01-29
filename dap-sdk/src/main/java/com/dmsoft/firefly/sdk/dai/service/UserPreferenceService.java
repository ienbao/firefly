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
     * @param code   code
     * @param userId user id
     * @return
     */
    String findPreferenceByUserId(String code, Long userId);

    /**
     * Update user preference
     *
     * @param userPreferenceDto user preference dto
     */
    void updatePreference(UserPreferenceDto userPreferenceDto);

    /**
     * @param userId user id
     * @param code   code
     */
    void deletePreference(Long userId, String code);
}
