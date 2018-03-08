package com.dmsoft.firefly.core.sdkimpl.dai;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.bamboo.common.utils.text.EncodeUtil;
import com.dmsoft.bamboo.common.utils.text.HashUtil;
import com.dmsoft.firefly.core.utils.JsonFileUtil;
import com.dmsoft.firefly.sdk.dai.dto.UserDto;
import com.dmsoft.firefly.sdk.dai.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Lucien.Chen on 2018/2/9.
 */
public class UserServiceImpl implements UserService {
    private final String parentPath = this.getClass().getResource("/").getPath() + "config";
    private final String fileName = "user";
    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private JsonMapper mapper = JsonMapper.defaultMapper();
    private final String salt = "uB7qbuCB";

    @Override
    public UserDto validateUser(String userName, String password) {
        String json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
            return null;
        }
        List<UserDto> list = mapper.fromJson(json, mapper.buildCollectionType(List.class, UserDto.class));
        for (UserDto userDto : list) {
            String passwordEn = EncodeUtil.encodeBase64(HashUtil.sha1(password, salt.getBytes()));
            if (userDto.getUserName().equals(userName) && userDto.getPassword().equals(passwordEn)) {
                return userDto;
            }
        }
        return null;
    }

    @Override
    public void updatePassword(String userName, String oldPwd, String newPwd) {
        String json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
            return;
        }
        List<UserDto> list = mapper.fromJson(json, mapper.buildCollectionType(List.class, UserDto.class));
        Boolean isExist = Boolean.FALSE;
        for (UserDto userDto : list) {
            String name = userDto.getUserName();
            String resultOld = EncodeUtil.encodeBase64(HashUtil.sha1(oldPwd, salt.getBytes()));
            String resultNew = EncodeUtil.encodeBase64(HashUtil.sha1(newPwd, salt.getBytes()));
            if (name.equals(userName) && userDto.getPassword().equals(resultOld)) {
                userDto.setPassword(resultNew);
                isExist = true;
                break;
            }
        }
        if (isExist) {
            JsonFileUtil.writeJsonFile(list, parentPath, fileName);
        }
    }

    @Override
    public void updateLegal(boolean acceptLegal) {
        String json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
            return;
        }
        List<UserDto> list = mapper.fromJson(json, mapper.buildCollectionType(List.class, UserDto.class));
        Boolean isExist = Boolean.FALSE;
        for (UserDto userDto : list) {
            String name = userDto.getUserName();
            if (name.equals("operationSystem")) {
                userDto.setAcceptLegal(acceptLegal);
                isExist = true;
                break;
            }
        }
        if (isExist) {
            JsonFileUtil.writeJsonFile(list, parentPath, fileName);
        }
    }

    @Override
    public boolean findLegal() {
        String json = JsonFileUtil.readJsonFile(parentPath, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
            return false;
        }
        List<UserDto> list = mapper.fromJson(json, mapper.buildCollectionType(List.class, UserDto.class));
        for (UserDto userDto : list) {
            String name = userDto.getUserName();
            if (name.equals("operationSystem")) {
                return userDto.isAcceptLegal();
            }
        }
        return false;
    }
}
