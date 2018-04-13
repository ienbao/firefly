package com.dmsoft.firefly.core.sdkimpl.dai;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.core.utils.ApplicationPathUtil;
import com.dmsoft.firefly.core.utils.JsonFileUtil;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.TemplateService;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.dmsoft.firefly.sdk.utils.FileUtils;
import com.dmsoft.firefly.sdk.utils.enums.TestItemType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lucien.Chen on 2018/2/9.
 */
public class TemplateServiceImpl implements TemplateService {
    private final String parentPath = ApplicationPathUtil.getPath("config");
    private final String defaultParentPath = ApplicationPathUtil.getPath("default");    private final String fileName = "template";
    private Logger logger = LoggerFactory.getLogger(TemplateServiceImpl.class);
    private JsonMapper mapper = JsonMapper.defaultMapper();

    @Override
    public List<String> findAllTemplateName() {

        List<TemplateSettingDto> list = findAllTemplate();

        List<String> result = Lists.newArrayList();
        if (list != null) {
            for (TemplateSettingDto templateSettingDto : list) {
                result.add(templateSettingDto.getName());
            }
        }
        return result;
    }

    @Override
    public List<TemplateSettingDto> findAllTemplate() {
        String json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
        }
        List<TemplateSettingDto> list = mapper.fromJson(json, mapper.buildCollectionType(List.class, TemplateSettingDto.class));

        return list;
    }

    @Override
    public TemplateSettingDto findAnalysisTemplateBackups() {

        String json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
        }
        List<TemplateSettingDto> list = mapper.fromJson(json, mapper.buildCollectionType(List.class, TemplateSettingDto.class));
        for (TemplateSettingDto templateSettingDto : list) {
            if (templateSettingDto.getName().equals("default")) {
                return templateSettingDto;
            }
        }
        return null;
    }

    @Override
    public TemplateSettingDto findAnalysisTemplate(String name) {

        String json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
        }
        List<TemplateSettingDto> list = mapper.fromJson(json, mapper.buildCollectionType(List.class, TemplateSettingDto.class));

        for (TemplateSettingDto templateSettingDto : list) {
            if (templateSettingDto.getName().equals(name)) {
                return templateSettingDto;
            }
        }
        return null;
    }

    @Override
    public void saveAnalysisTemplate(TemplateSettingDto templateSettingDto) {

        String json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
        }
        List<TemplateSettingDto> list = mapper.fromJson(json, mapper.buildCollectionType(List.class, TemplateSettingDto.class));

        Boolean isExist = Boolean.FALSE;
        for (TemplateSettingDto dto : list) {
            if (dto.getName().equals(templateSettingDto.getName())) {
                BeanUtils.copyProperties(templateSettingDto, dto);
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            list.add(templateSettingDto);
        }
        JsonFileUtil.writeJsonFile(list, parentPath, fileName);
    }

    @Override
    public void saveAllAnalysisTemplate(List<TemplateSettingDto> templateSettingDto) {

        String json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
        }

        JsonFileUtil.writeJsonFile(templateSettingDto, parentPath, fileName);
    }

    @Override
    public int renameTemplate(String oldName, String newName) {

        String json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
            return 1;
        }
        List<TemplateSettingDto> list = mapper.fromJson(json, mapper.buildCollectionType(List.class, TemplateSettingDto.class));

        for (TemplateSettingDto dto : list) {
            if (dto.getName().equals(newName)) {
                return 2;
            }
        }
        Boolean isExist = Boolean.FALSE;
        if (!isExist) {
            for (TemplateSettingDto dto : list) {
                if (dto.getName().equals(newName)) {
                    dto.setName(newName);
                    isExist = true;
                }
            }
        }
        if (isExist) {
            JsonFileUtil.writeJsonFile(list, parentPath, fileName);
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public void deleteTemplate(String templateName) {

        String json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
        }
        List<TemplateSettingDto> list = mapper.fromJson(json, mapper.buildCollectionType(List.class, TemplateSettingDto.class));

        Boolean isExst = Boolean.FALSE;
        for (TemplateSettingDto dto : list) {
            if (dto.getName().equals(templateName)) {
                list.remove(dto);
                isExst = true;
                break;
            }
        }
        if (isExst) {
            JsonFileUtil.writeJsonFile(list, parentPath, fileName);
        }
    }

    @Override
    public LinkedHashMap<String, TestItemWithTypeDto> assembleTemplate(Map<String, TestItemDto> testItemDtoMap, String templateName) {
        String json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
        }
        List<TemplateSettingDto> list = mapper.fromJson(json, mapper.buildCollectionType(List.class, TemplateSettingDto.class));
        TemplateSettingDto curr = null;
        for (TemplateSettingDto dto : list) {
            if (dto.getName().equals(templateName)) {
                curr = dto;
            }
        }
        LinkedHashMap<String, TestItemWithTypeDto> result = Maps.newLinkedHashMap();

        if (curr != null) {
            for (TestItemDto itemDto : testItemDtoMap.values()) {
                TestItemWithTypeDto testItemWithTypeDto = new TestItemWithTypeDto();
                BeanUtils.copyProperties(itemDto, testItemWithTypeDto);
                if (curr.getSpecificationDatas() != null && curr.getSpecificationDatas().containsKey(itemDto.getTestItemName())) {
                    if (curr.getSpecificationDatas().get(itemDto.getTestItemName()).getDataType().equals("VARIABLE")) {
                        testItemWithTypeDto.setTestItemType(TestItemType.VARIABLE);
                    }
                    testItemWithTypeDto.setLsl(curr.getSpecificationDatas().get(itemDto.getTestItemName()).getLslFail());
                    testItemWithTypeDto.setUsl(curr.getSpecificationDatas().get(itemDto.getTestItemName()).getUslPass());
                } else {
                    if (DAPStringUtils.isSpecialBlank(testItemWithTypeDto.getLsl())) {
                        testItemWithTypeDto.setLsl("");
                    }
                    if (DAPStringUtils.isSpecialBlank(testItemWithTypeDto.getUsl())) {
                        testItemWithTypeDto.setUsl("");
                    }
                }
                result.put(itemDto.getTestItemName(), testItemWithTypeDto);
            }
        }
        return result;
    }

    @Override
    public String getConfigName() {
        return "ANALYSIS_TEMPLATE";
    }

    @Override
    public byte[] exportConfig() {
        List<TemplateSettingDto> templateSettingDtos = findAllTemplate();
        if (mapper != null) {
            return mapper.toJson(templateSettingDtos).getBytes();
        }
        return new byte[0];
    }

    @Override
    public void importConfig(byte[] config) {
        if (config == null) {
            return;
        }
        saveAllAnalysisTemplate(mapper.fromJson(new String(config), mapper.buildCollectionType(List.class, TemplateSettingDto.class)));
    }

    @Override
    public void restoreConfig() {
        FileUtils.delFolder(parentPath);
        FileUtils.copyFolder(defaultParentPath, parentPath);
    }
}
