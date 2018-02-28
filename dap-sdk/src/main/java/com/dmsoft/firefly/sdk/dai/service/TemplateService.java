package com.dmsoft.firefly.sdk.dai.service;

import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;

import java.util.List;
import java.util.Map;

/**
 * Created by GuangLi on 2018/1/25.
 */
public interface TemplateService {

    /**
     * find all template name
     *
     * @return list of names
     */
    List<String> findAllTemplateName();

    /**
     * find setting backups
     *
     * @return template setting dto
     */
    TemplateSettingDto findAnalysisTemplateBackups();

    /**
     * find setting by name
     *
     * @param name name
     * @return found name ,null indications not found
     */
    TemplateSettingDto findAnalysisTemplate(String name);

    /**
     * save setting
     *
     * @param templateSettingDto template setting dto
     */
    void saveAnalysisTemplate(TemplateSettingDto templateSettingDto);

    /**
     * rename template name
     *
     * @param oldName original name
     * @param newName new name
     * @return if value 0, rename succeed,
     * if value 1, cannot find template to rename
     * if value 2, new name duplicated with other templates
     */
    int renameTemplate(String oldName, String newName);

    /**
     * delete template
     *
     * @param templateName template name
     */
    void deleteTemplate(String templateName);

    /**
     * method to assemble test item dto into test item with type dto
     *
     * @param testItemDtoMap test item dto map
     * @param templateName   template name
     * @return test item with type dto map
     */
    Map<String, TestItemWithTypeDto> assembleTemplate(Map<String, TestItemDto> testItemDtoMap, String templateName);
}
