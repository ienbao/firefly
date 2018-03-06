/*
 * Copyright (c) 2016. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.dai.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * Created by GuangLi on 2016/7/25.
 */
public class UserDto extends AbstractValueObject {

    private String userName;
    private String password;
    private String email;
    private boolean acceptLegal;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAcceptLegal() {
        return acceptLegal;
    }

    public void setAcceptLegal(boolean acceptLegal) {
        this.acceptLegal = acceptLegal;
    }
}
