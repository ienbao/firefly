package com.dmsoft.firefly.gui.model;


import com.dmsoft.firefly.sdk.dai.dto.UserDto;

/**
 * Created by Julia on 2018/03/08.
 */
public final class UserModel {
    private static UserModel ourInstance = new UserModel();
    private UserDto user;

    private UserModel() {
    }

    public static UserModel getInstance() {
        return ourInstance;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}
