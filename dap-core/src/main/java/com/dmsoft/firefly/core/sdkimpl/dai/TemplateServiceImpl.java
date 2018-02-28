package com.dmsoft.firefly.core.sdkimpl.dai;

import com.dmsoft.firefly.core.utils.JsonFileUtil;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.service.TemplateService;
import com.google.common.collect.Lists;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * Created by Lucien.Chen on 2018/2/9.
 */
public class TemplateServiceImpl implements TemplateService {
    private Logger logger = LoggerFactory.getLogger(TemplateServiceImpl.class);

    private static final String parentPath = "../config";
    private static final String fileName = "template";

    @Override

    public List<String> findAllTemplateName() {
        JSONArray json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
        }
        List<TemplateSettingDto> list = (List<TemplateSettingDto>) JSONArray.toCollection(json, TemplateSettingDto.class);
        List<String> result = Lists.newArrayList();
        for (TemplateSettingDto templateSettingDto : list) {
            result.add(templateSettingDto.getName());
        }
        return result;
    }

    @Override
    public TemplateSettingDto findAnalysisTemplateBackups() {
        JSONArray json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
            return null;
        }
        List<TemplateSettingDto> list = (List<TemplateSettingDto>) JSONArray.toCollection(json, TemplateSettingDto.class);
        for (TemplateSettingDto templateSettingDto : list) {
            if (templateSettingDto.getName().equals("default")) {
                return templateSettingDto;
            }
        }
        return null;
    }

    @Override
    public TemplateSettingDto findAnalysisTemplate(String name) {
        JSONArray json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
            return null;
        }
        List<TemplateSettingDto> list = (List<TemplateSettingDto>) JSONArray.toCollection(json, TemplateSettingDto.class);
        for (TemplateSettingDto templateSettingDto : list) {
            if (templateSettingDto.getName().equals(name)) {
                return templateSettingDto;
            }
        }
        return null;
    }

    @Override
    public void saveAnalysisTemplate(TemplateSettingDto templateSettingDto) {
        JSONArray json = JsonFileUtil.readJsonFile(parentPath, fileName);
        List<TemplateSettingDto> list = null;
        if (json == null) {
            logger.debug("Don`t find " + fileName);
            list = Lists.newArrayList();
        } else {
            list = (List<TemplateSettingDto>) JSONArray.toCollection(json, TemplateSettingDto.class);
        }
        Boolean isExst = Boolean.FALSE;
        for (TemplateSettingDto dto : list) {
            if (dto.getName().equals(templateSettingDto.getName())) {
                BeanUtils.copyProperties(templateSettingDto, dto);
                isExst = true;
                break;
            }
        }
        if (!isExst) {
            list.add(templateSettingDto);
        }
        JsonFileUtil.writeJsonFile(JSONArray.fromObject(list), parentPath, fileName);
    }

    @Override
    public int renameTemplate(String oldName, String newName) {
        JSONArray json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
            return 1;
        }

        List<TemplateSettingDto> list = (List<TemplateSettingDto>) JSONArray.toCollection(json, TemplateSettingDto.class);

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
            JsonFileUtil.writeJsonFile(JSONArray.fromObject(list), parentPath, fileName);
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public void deleteTemplate(String templateName) {

        JSONArray json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
            return;
        }
        List<TemplateSettingDto> list = (List<TemplateSettingDto>) JSONArray.toCollection(json, TemplateSettingDto.class);

        Boolean isExst = Boolean.FALSE;
        for (TemplateSettingDto dto : list) {
            if (dto.getName().equals(templateName)) {
                list.remove(dto);
                isExst = true;
                break;
            }
        }
        if (isExst) {
            JsonFileUtil.writeJsonFile(JSONArray.fromObject(list), parentPath, fileName);
        }
    }
}
