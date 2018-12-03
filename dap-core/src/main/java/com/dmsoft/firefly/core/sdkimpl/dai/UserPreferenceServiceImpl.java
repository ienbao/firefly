package com.dmsoft.firefly.core.sdkimpl.dai;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.core.utils.ApplicationPathUtil;
import com.dmsoft.firefly.core.utils.CoreExceptionCode;
import com.dmsoft.firefly.core.utils.CoreExceptionParser;
import com.dmsoft.firefly.core.utils.JsonFileUtil;
import com.dmsoft.firefly.sdk.dai.dto.UserPreferenceDto;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.dmsoft.firefly.sdk.utils.FileUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Created by Lucien.Chen on 2018/2/9.
 */
@Service
public class UserPreferenceServiceImpl implements UserPreferenceService {

    private final String parentPath = ApplicationPathUtil.getPath("config");
    private final String defaultParentPath = ApplicationPathUtil.getPath("default");

    private final String fileName = "userPreference";
    private Logger logger = LoggerFactory.getLogger(UserPreferenceServiceImpl.class);
    private JsonMapper mapper = JsonMapper.defaultMapper();

    @Override
    public void addValueItem(UserPreferenceDto userPreferenceDto) {

        String json = JsonFileUtil.readJsonFile(parentPath, fileName);
        List<UserPreferenceDto> list;
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
        if (DAPStringUtils.isBlank(code)) {
            logger.error("Invalid parameter, code is empty!");
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_20001));
        }
        String json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (DAPStringUtils.isEmpty(json)) {
            logger.debug("Don`t find " + fileName);
            return null;
        }
        List<UserPreferenceDto> list = mapper.fromJson(json, mapper.buildCollectionType(List.class, UserPreferenceDto.class));
        String result = null;
        for (UserPreferenceDto userPreferenceDto : list) {
            if (DAPStringUtils.isBlank(userPreferenceDto.getUserName()) || DAPStringUtils.isBlank(userName)) {
                if (userPreferenceDto.getCode().equals(code)) {
                    if (userPreferenceDto.getValue() instanceof String) {
                        result = (String) userPreferenceDto.getValue();
                    } else {
                        result = mapper.toJson(userPreferenceDto.getValue());
                    }

                    break;
                }
            } else {
                if (userPreferenceDto.getCode().equals(code) && userPreferenceDto.getUserName().equals(userName)) {
                    if (userPreferenceDto.getValue() instanceof String) {
                        result = (String) userPreferenceDto.getValue();
                    } else {
                        result = mapper.toJson(userPreferenceDto.getValue());
                    }
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public void updatePreference(UserPreferenceDto userPreferenceDto) {
        if (userPreferenceDto ==  null || DAPStringUtils.isBlank(userPreferenceDto.getCode())) {
            logger.error("Invalid parameter, dto or code is empty!");
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_20001));
        }
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
            if (DAPStringUtils.isBlank(userPreferenceDto.getUserName()) || DAPStringUtils.isBlank(dto.getUserName())) {
                if (userPreferenceDto.getCode().equals(dto.getCode())) {
                    dto.setValue(userPreferenceDto.getValue());
                    isExist = true;
                    break;
                }
            } else {
                if (userPreferenceDto.getCode().equals(dto.getCode()) && userPreferenceDto.getUserName().equals(dto.getUserName())) {
                    dto.setValue(userPreferenceDto.getValue());
                    isExist = true;
                    break;
                }
            }

        }
        if (!isExist) {
            list.add(userPreferenceDto);
        }
        JsonFileUtil.writeJsonFile(list, parentPath, fileName);

    }

    @Override
    public void deletePreference(String userName, String code) {
        if (DAPStringUtils.isBlank(code)) {
            logger.error("Invalid parameter, code is empty!");
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_20001));
        }

        String json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (DAPStringUtils.isEmpty(json)) {
            logger.debug("Don`t find " + fileName);
            return;
        }
        List<UserPreferenceDto> list = mapper.fromJson(json, mapper.buildCollectionType(List.class, UserPreferenceDto.class));
        Boolean isExist = Boolean.FALSE;
        for (UserPreferenceDto dto : list) {
            if (DAPStringUtils.isBlank(dto.getUserName()) || DAPStringUtils.isBlank(userName)) {
                if (dto.getCode().equals(code)) {
                    list.remove(dto);
                    isExist = true;
                    break;
                }
            } else {
                if (dto.getCode().equals(code) && dto.getUserName().equals(userName)) {
                    list.remove(dto);
                    isExist = true;
                    break;
                }
            }
        }
        if (isExist) {
            JsonFileUtil.writeJsonFile(list, parentPath, fileName);
        }
    }

    @Override
    public void resetPreference() {
        FileUtils.delFolder(parentPath);
        FileUtils.copyFolder(defaultParentPath, parentPath);
    }
}
