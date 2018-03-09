package com.dmsoft.firefly.core.sdkimpl.dai;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.dto.UserPreferenceDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.TemplateService;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
import com.dmsoft.firefly.sdk.utils.enums.LanguageType;
import com.google.common.collect.Lists;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Lucien.Chen on 2018/2/10.
 */
public class EnvServiceImpl implements EnvService {
    private String userName = "admin";
    private String templateName;
    private LinkedHashMap<String, TestItemWithTypeDto> testItemDtos;
    private List<String> projectNames;
    private List<String> plugNames;
    private JsonMapper mapper = JsonMapper.defaultMapper();

    @Override
    public String getUserName() {
        return this.userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public void setActivatedTemplate(String templateName) {
        this.templateName = templateName;
    }

    @Override
    public TemplateSettingDto findActivatedTemplate() {
        return templateName != null ? getTemplateService().findAnalysisTemplate(templateName) : null;

    }

    @Override
    public List<String> findActivatedProjectName() {
        return projectNames;
    }

    @Override
    public void setActivatedProjectName(List<String> activatedProjectName) {
        this.projectNames = activatedProjectName;

        UserPreferenceDto userPreferenceDto = new UserPreferenceDto();
        userPreferenceDto.setCode("selectProject");
        userPreferenceDto.setUserName(userName);
        userPreferenceDto.setValue(activatedProjectName);
        getUserPreferenceService().updatePreference(userPreferenceDto);
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
        String preference = getUserPreferenceService().findPreferenceByUserId(code, userName);
        return preference;
    }

    @Override
    public List<TestItemWithTypeDto> findTestItems() {
        return testItemDtos != null ? Lists.newArrayList(testItemDtos.values()) : Lists.newArrayList();
    }

    @Override
    public List<String> findTestItemNames() {
        if (testItemDtos != null) {
            return Lists.newArrayList(testItemDtos.keySet());
        }
        return Lists.newArrayList();
    }

    @Override
    public TestItemWithTypeDto findTestItemNameByItemName(String itemName) {
        if (testItemDtos != null) {
            return testItemDtos.get(itemName);
        }
        return null;
    }

    @Override
    public void setTestItems(LinkedHashMap<String, TestItemWithTypeDto> testItems) {
        testItemDtos = testItems;
    }

    @Override
    public LanguageType getLanguageType() {
        String languageType = RuntimeContext.getBean(UserPreferenceService.class).findPreferenceByUserId("languageType", userName);
        return LanguageType.valueOf(mapper.fromJson(languageType, String.class));

    }

    @Override
    public void setLanguageType(LanguageType languageType) {
        UserPreferenceDto userPreferenceDto = new UserPreferenceDto();
        userPreferenceDto.setCode("languageType");
        userPreferenceDto.setUserName(userName);
        userPreferenceDto.setValue(languageType);
        getUserPreferenceService().updatePreference(userPreferenceDto);
    }

    private UserPreferenceService getUserPreferenceService() {
        return RuntimeContext.getBean(UserPreferenceService.class);
    }

    private TemplateService getTemplateService() {
        return RuntimeContext.getBean(TemplateService.class);
    }
}
