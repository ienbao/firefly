/*
 * Copyright (c) 2016. For Intelligent Group.
 */
package com.dmsoft.firefly.sdk.dai.service;

import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.utils.enums.LanguageType;

import java.util.List;

/**
 * Created by Guang.Li on 2018/1/29.
 */
public interface EnvService {
    /**
     * get user name
     *
     * @return user name
     */
    String getUserName();

    /**
     * set user name
     *
     * @param userName user name
     */
    void setUserName(String userName);

    /**
     * method to set activated template name
     *
     * @param templateName activated template name
     */
    void setActivatedTemplate(String templateName);

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
     * method to set activated project name
     *
     * @param activatedProjectName activated project name
     */
    void setActivatedProjectName(List<String> activatedProjectName);

    /**
     * find list of activated plugin names
     *
     * @return list of project names
     */
    List<String> findActivatedPluginName();

    /**
     * method to set activated plugin name
     *
     * @param activatedPluginName activated plugin name
     */
    void setActivatedPluginName(List<String> activatedPluginName);

    /**
     * method to find preference
     *
     * @param code code
     * @return preference
     */
    String findPreference(String code);

    /**
     * find test item
     *
     * @return list of test item
     */
    List<TestItemWithTypeDto> findTestItems();

    /**
     * find test item
     *
     * @return list of test item
     */
    List<String> findTestItemNames();

    /**
     * method to set TestItemWithTypeDto list
     *
     * @param testItems TestItemWithTypeDto list
     */
    void setTestItems(List<TestItemWithTypeDto> testItems);

    /**
     * method to get language type
     *
     * @return language type
     */
    LanguageType getLanguageType();

    /**
     * method to set language type
     *
     * @param languageType language type
     */
    void setLanguageType(LanguageType languageType);
}
