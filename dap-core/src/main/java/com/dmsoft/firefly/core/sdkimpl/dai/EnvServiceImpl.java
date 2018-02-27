package com.dmsoft.firefly.core.sdkimpl.dai;

import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.dto.UserPreferenceDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.TemplateService;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
import com.dmsoft.firefly.sdk.utils.enums.LanguageType;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by Lucien.Chen on 2018/2/10.
 */
public class EnvServiceImpl implements EnvService {
    private TemplateService templateService = RuntimeContext.getBean(TemplateService.class);
    private UserPreferenceService userPreferenceService = RuntimeContext.getBean(UserPreferenceService.class);

    private String userName;
    private String templateName;
    private List<TestItemDto> testItemDtos;
    private List<String> projectNames;
    private List<String> plugNames;

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getUserName() {
        return this.userName;
    }

    @Override
    public void setActivatedTemplate(String templateName) {
        this.templateName = templateName;
    }

    @Override
    public TemplateSettingDto findActivatedTemplate() {
        return templateName != null ? templateService.findAnalysisTemplate(templateName) : null;

    }

    @Override
    public List<String> findActivatedProjectName() {
        return projectNames;
    }

    @Override
    public void setActivatedProjectName(List<String> activatedProjectName) {
        this.projectNames = activatedProjectName;
    }

    @Override
    public List<String> findActivatedPluginName() {
        return plugNames;
    }

    @Override
    public void setActivatedPluginName(List<String> activatedPluginName) {
        plugNames = activatedPluginName;
    }

    @Override
    public String findPreference(String code) {
        String preference = userPreferenceService.findPreferenceByUserId(code, userName);
        return preference;
    }

    @Override
    public List<TestItemDto> findTestItems() {
        return testItemDtos != null ? testItemDtos : Lists.newArrayList();
    }

    @Override
    public void setTestItems(List<TestItemDto> testItems) {
        testItemDtos = testItems;
    }

    @Override
    public LanguageType getLanguageType() {
        String languageType = userPreferenceService.findPreferenceByUserId("languageType", userName);
        return LanguageType.valueOf(languageType);

    }

    @Override
    public void setLanguageType(LanguageType languageType) {
        UserPreferenceDto userPreferenceDto = new UserPreferenceDto();
        userPreferenceDto.setCode("languageType");
        userPreferenceDto.setUserName(userName);
        userPreferenceDto.setValue(languageType);
        userPreferenceService.updatePreference(userPreferenceDto);
    }
}