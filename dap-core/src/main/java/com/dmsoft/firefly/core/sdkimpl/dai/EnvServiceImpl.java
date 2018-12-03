package com.dmsoft.firefly.core.sdkimpl.dai;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.dto.UserPreferenceDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dai.service.TemplateService;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.dmsoft.firefly.sdk.utils.enums.LanguageType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Lucien.Chen on 2018/2/10.
 */
@Service
public class EnvServiceImpl implements EnvService {
    public static final String DEFAULT_TEMPLATE_NAME = "Default";

    private String userName;
    private String templateName;
    private LinkedHashMap<String, TestItemWithTypeDto> testItemDtos;
    private List<String> projectNames;
    private List<String> plugNames;
    private JsonMapper mapper = JsonMapper.defaultMapper();
    @Autowired
    private UserPreferenceService userPreferenceService;
    @Autowired
    private TemplateService templateService;
    @Autowired
    private SourceDataService sourceDataService;

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
        UserPreferenceDto<String> userPreferenceDto = new UserPreferenceDto<>();
        userPreferenceDto.setCode("activeTemplate");
        userPreferenceDto.setUserName(userName);
        userPreferenceDto.setValue(templateName);
        this.userPreferenceService.updatePreference(userPreferenceDto);
    }

    @Override
    public TemplateSettingDto findActivatedTemplate() {
        if(templateName != null){

            return this.templateService.findAnalysisTemplate(templateName);
        }else {

            return this.templateService.findAnalysisTemplate(findPreference("activeTemplate"));
        }


    }

    /**
     * 获取当前活跃模板名称
     */
    @Override
    public String findActivatedTemplateName() {
        TemplateSettingDto templateSettingDto = this.findActivatedTemplate();
        String activeTemplateName;
        if (templateSettingDto == null || DAPStringUtils.isBlank(templateSettingDto.getName())) {
            this.setActivatedTemplate(DEFAULT_TEMPLATE_NAME);
            activeTemplateName = DEFAULT_TEMPLATE_NAME;
        } else {
            activeTemplateName = templateSettingDto.getName();
        }

        return activeTemplateName;
    }

    @Override
    public List<String> findActivatedProjectName() {
        List<String> projectName = mapper.fromJson(findPreference("selectProject"), mapper.buildCollectionType(List.class, String.class));
        return projectNames != null ? projectNames : projectName;
    }

    @Override
    public void setActivatedProjectName(List<String> activatedProjectName) {
        this.projectNames = activatedProjectName;
        UserPreferenceDto<List> userPreferenceDto = new UserPreferenceDto<>();
        userPreferenceDto.setCode("selectProject");
        userPreferenceDto.setUserName(userName);
        userPreferenceDto.setValue(activatedProjectName);
        this.userPreferenceService.updatePreference(userPreferenceDto);
    }

    @Override
    public String findPreference(String code) {
        return this.userPreferenceService.findPreferenceByUserId(code, userName);
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
    public Map<String, TestItemWithTypeDto> findTestItemsMap() {
        return Maps.newHashMap(testItemDtos);
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
        String languageType = this.userPreferenceService.findPreferenceByUserId("languageType", userName);
        if (DAPStringUtils.isBlank(languageType)) {
            this.setLanguageType(LanguageType.EN);
            languageType = LanguageType.EN.toString();
        }
        return LanguageType.valueOf(languageType);

    }

    @Override
    public void setLanguageType(LanguageType languageType) {
        UserPreferenceDto userPreferenceDto = new UserPreferenceDto();
        userPreferenceDto.setCode("languageType");
        userPreferenceDto.setUserName(userName);
        userPreferenceDto.setValue(languageType);
        this.userPreferenceService.updatePreference(userPreferenceDto);
    }

    @Override
    public void initTestItem(String activeTemplateName) {

        List<String> projectName = this.findActivatedProjectName();
        if (projectName != null && !projectName.isEmpty()) {
            Map<String, TestItemDto> testItemDtoMap = sourceDataService.findAllTestItem(projectName);
            LinkedHashMap<String, TestItemWithTypeDto> itemWithTypeDtoMap = this.templateService.assembleTemplate(testItemDtoMap, activeTemplateName);
            this.setTestItems(itemWithTypeDtoMap);
            this.setActivatedProjectName(projectName);
        }
    }

}
