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
     * @return
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
     * update activated project names
     *
     * @param projectNames project names
     */
    void updateActivatedProject(List<String> projectNames);

    /**
     * update activated template names
     *
     * @param templateName template name
     */
    void updateActivatedTemplate(String templateName);

    /**
     * update activated plugin names
     *
     * @param pluginNames plugin names
     */
    void updateActivatedPlugin(List<String> pluginNames);
}
