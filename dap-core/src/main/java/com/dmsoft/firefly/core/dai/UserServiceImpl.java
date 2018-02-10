package com.dmsoft.firefly.core.dai;

import com.dmsoft.firefly.core.utils.JsonFileUtil;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.dto.UserDto;
import com.dmsoft.firefly.sdk.dai.service.UserService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Lucien.Chen on 2018/2/9.
 */
public class UserServiceImpl implements UserService {
    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String parentPath = "../config";
    private static final String fileName = "user";

    @Override
    public UserDto validateUser(String userName, String password) {
        JSONArray json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
            return null;
        }
        List<UserDto> list = (List<UserDto>) JSONArray.toCollection(json, UserDto.class);
        for (UserDto userDto : list) {
            if (userDto.getName().equals(userName) && userDto.getPassword().equals(password)) {
                return userDto;
            }
        }
        return null;
    }

    @Override
    public void updatePassword(String userName, String oldPwd, String newPwd) {
        JSONArray json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
            return;
        }
        List<UserDto> list = (List<UserDto>) JSONArray.toCollection(json, UserDto.class);
        Boolean isExist = Boolean.FALSE;
        for (UserDto userDto : list) {
            if (userDto.getName().equals(userName) && userDto.getPassword().equals(oldPwd)) {
                userDto.setPassword(newPwd);
                isExist = true;
                break;
            }
        }
        if (isExist) {
            JsonFileUtil.writeJsonFile(JSONArray.fromObject(list), parentPath, fileName);
        }
    }
}
