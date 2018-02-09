package com.dmsoft.firefly.core.dai;

import com.dmsoft.firefly.sdk.dai.dto.UserPreferenceDto;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;

/**
 * Created by Lucien.Chen on 2018/2/9.
 */
public class UserPreferenceServiceImpl implements UserPreferenceService {
    @Override
    public void addValueItem(UserPreferenceDto userPreferenceDto) {

    }

    @Override
    public String findPreferenceByUserId(String code, Long userId) {
        return null;
    }

    @Override
    public void updatePreference(UserPreferenceDto userPreferenceDto) {

    }

    @Override
    public void deletePreference(Long userId, String code) {

    }
}
