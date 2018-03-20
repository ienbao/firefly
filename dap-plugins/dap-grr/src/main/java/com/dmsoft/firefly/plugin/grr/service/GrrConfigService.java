package com.dmsoft.firefly.plugin.grr.service;

import com.dmsoft.firefly.plugin.grr.dto.GrrConfigDto;

import java.util.Map;

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
     * save export setting
     *
     * @param export export setting
     */
    void saveGrrExportConfig(Map<String, Boolean> export);

    /**
     * find grr config setting
     *
     * @return grr config setting
     */
    GrrConfigDto findGrrConfig();

    /**
     * find grr export config setting
     *
     * @return grr export config setting
     */
    Map<String, Boolean> findGrrExportConfig();
}
