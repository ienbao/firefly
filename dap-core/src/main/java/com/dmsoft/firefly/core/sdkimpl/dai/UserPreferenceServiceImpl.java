package com.dmsoft.firefly.core.sdkimpl.dai;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.core.utils.ApplicationPathUtil;
import com.dmsoft.firefly.core.utils.JsonFileUtil;
import com.dmsoft.firefly.sdk.dai.dto.UserPreferenceDto;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Lucien.Chen on 2018/2/9.
 */
public class UserPreferenceServiceImpl implements UserPreferenceService {

    private final String parentPath = ApplicationPathUtil.getPath("config");
    private final String fileName = "userPreference";
    private Logger logger = LoggerFactory.getLogger(UserPreferenceServiceImpl.class);
    private JsonMapper mapper = JsonMapper.defaultMapper();

    @Override
    public void addValueItem(UserPreferenceDto userPreferenceDto) {

        String json = JsonFileUtil.readJsonFile(parentPath, fileName);
        List<UserPreferenceDto> list = null;
        if (DAPStringUtils.isEmpty(json)) {
            logger.debug("Don`t find " + fileName);
            list = Lists.newArrayList();
        } else {
            list = mapper.fromJson(json, mapper.buildCollectionType(List.class, UserPreferenceDto.class));
        }
        list.add(userPreferenceDto);
        JsonFileUtil.writeJsonFile(list, parentPath, fileName);

    }

    /*
     * preference json string, need decode by jsonMapper
     */
    @Override
    public String findPreferenceByUserId(String code, String userName) {
        String json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (DAPStringUtils.isEmpty(json)) {
            logger.debug("Don`t find " + fileName);
            return null;
        }
        List<UserPreferenceDto> list = mapper.fromJson(json, mapper.buildCollectionType(List.class, UserPreferenceDto.class));
        String result = null;
        for (UserPreferenceDto userPreferenceDto : list) {
            if (userPreferenceDto.getCode().equals(code) && userPreferenceDto.getUserName().equals(userName)) {
                result = mapper.toJson(userPreferenceDto.getValue());
                break;
            }
        }
        return result;
    }

    @Override
    public void updatePreference(UserPreferenceDto userPreferenceDto) {
        String json = JsonFileUtil.readJsonFile(parentPath, fileName);
        List<UserPreferenceDto> list = null;
        if (DAPStringUtils.isEmpty(json)) {
            logger.debug("Don`t find " + fileName);
            list = Lists.newArrayList();
        } else {
            list = mapper.fromJson(json, mapper.buildCollectionType(List.class, UserPreferenceDto.class));
        }
        Boolean isExist = Boolean.FALSE;
        for (UserPreferenceDto dto : list) {
            if (dto.getCode().equals(userPreferenceDto.getCode()) && dto.getUserName().equals(userPreferenceDto.getUserName())) {
                dto.setValue(userPreferenceDto.getValue());
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            list.add(userPreferenceDto);
        }
        JsonFileUtil.writeJsonFile(list, parentPath, fileName);

    }

    @Override
    public void deletePreference(String userName, String code) {
        String json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (DAPStringUtils.isEmpty(json)) {
            logger.debug("Don`t find " + fileName);
            return;
        }
        List<UserPreferenceDto> list = mapper.fromJson(json, mapper.buildCollectionType(List.class, UserPreferenceDto.class));
        Boolean isExist = Boolean.FALSE;
        for (UserPreferenceDto dto : list) {
            if (dto.getCode().equals(code) && dto.getUserName().equals(userName)) {
                list.remove(dto);
                isExist = true;
                break;
            }
        }
        if (isExist) {
            JsonFileUtil.writeJsonFile(list, parentPath, fileName);
        }

    }
}
