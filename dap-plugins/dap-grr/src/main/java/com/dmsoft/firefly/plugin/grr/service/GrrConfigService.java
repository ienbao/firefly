package com.dmsoft.firefly.plugin.grr.service;

import com.dmsoft.firefly.plugin.grr.dto.GrrConfigDto;

/**
 * Created by GuangLi on 2018/3/8.
 */
public interface GrrConfigService {
    /**
     * save grr config setting
     *
     * @param grrConfigDto grr config setting
     */
    void saveGrrConfig(GrrConfigDto grrConfigDto);

    /**
     * find grr config setting
     *
     * @return grr config setting
     */
    GrrConfigDto findGrrConfig();
}
