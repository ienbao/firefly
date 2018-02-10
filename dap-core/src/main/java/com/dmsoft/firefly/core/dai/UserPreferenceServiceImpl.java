package com.dmsoft.firefly.core.dai;

import com.dmsoft.firefly.core.utils.JsonFileUtil;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.dto.UserPreferenceDto;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
import com.google.common.collect.Lists;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Lucien.Chen on 2018/2/9.
 */
public class UserPreferenceServiceImpl implements UserPreferenceService {

    private Logger logger = LoggerFactory.getLogger(UserPreferenceServiceImpl.class);

    private static final String parentPath = "../config";
    private static final String fileName = "userPreference";

    @Override
    public void addValueItem(UserPreferenceDto userPreferenceDto) {


        JSONArray json = JsonFileUtil.readJsonFile(parentPath, fileName);
        List<UserPreferenceDto> list = null;
        if (json == null) {
            logger.debug("Don`t find " + fileName);
            list = Lists.newArrayList();
        }else {
            list = (List<UserPreferenceDto>) JSONArray.toCollection(json, UserPreferenceDto.class);
        }
        list.add(userPreferenceDto);
        JsonFileUtil.writeJsonFile(JSONArray.fromObject(list), parentPath, fileName);

    }

    @Override
    public String findPreferenceByUserId(String code,String userName) {
        JSONArray json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
            return null;
        }
        List<UserPreferenceDto> list = (List<UserPreferenceDto>) JSONArray.toCollection(json,UserPreferenceDto.class);
        String result = null;
        for (UserPreferenceDto userPreferenceDto : list) {
            if (userPreferenceDto.getCode().equals(code) && userPreferenceDto.getUserName().equals(userName)){
                result = userPreferenceDto.getValue().toString();
                break;
            }
        }
        return result;
    }

    @Override
    public void updatePreference(UserPreferenceDto userPreferenceDto) {
        JSONArray json = JsonFileUtil.readJsonFile(parentPath, fileName);
        List<UserPreferenceDto> list = null;
        if (json == null) {
            logger.debug("Don`t find " + fileName);
            list = Lists.newArrayList();
        }else {
            list = (List<UserPreferenceDto>) JSONArray.toCollection(json, UserPreferenceDto.class);
        }
        Boolean isExist = Boolean.FALSE;
        for (UserPreferenceDto dto : list) {
            if (dto.getCode().equals(userPreferenceDto.getCode()) && dto.getUserName().equals(userPreferenceDto.getUserName())){
                dto.setValue(userPreferenceDto.getValue());
                isExist = true;
                break;
            }
        }
        if(!isExist){
            list.add(userPreferenceDto);
        }
        JsonFileUtil.writeJsonFile(JSONArray.fromObject(list), parentPath, fileName);

    }

    @Override
    public void deletePreference(String userName, String code) {
        JSONArray json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
            return ;
        }
        List<UserPreferenceDto> list = (List<UserPreferenceDto>) JSONArray.toCollection(json,UserPreferenceDto.class);
        Boolean isExist = Boolean.FALSE;
        for (UserPreferenceDto dto : list) {
            if (dto.getCode().equals(code) && dto.getUserName().equals(userName)){
                list.remove(dto);
                isExist = true;
                break;
            }
        }
        if(isExist){
            JsonFileUtil.writeJsonFile(JSONArray.fromObject(list), parentPath, fileName);
        }

    }
}
