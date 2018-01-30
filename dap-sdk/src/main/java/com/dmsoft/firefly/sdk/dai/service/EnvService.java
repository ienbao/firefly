/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.sdk.dai.service;

import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;

import java.util.List;

/**
 * Created by Guang.Li on 2018/1/29.
 */
public interface EnvService {
    /**
     * set user id
     */
    void setUserId();

    /**
     * get user id
     *
     * @return user id
     */
    Long getUserId();

    /**
     * find activated template setting
     *
     * @return TemplateSettingDto
     */
    TemplateSettingDto findActivatedTemplate();

    /**
     * find list of activated project names
     *
     * @return list of project names
     */
    List<String> findActivatedProjectName();

    /**
     * find list of activated plugin names
     *
     * @return list of project names
     */
    List<String> findActivatedPluginName();

    /**
     * method to find preference
     *
     * @param code code
     * @return preference
     */
    String findPreference(String code);
}
