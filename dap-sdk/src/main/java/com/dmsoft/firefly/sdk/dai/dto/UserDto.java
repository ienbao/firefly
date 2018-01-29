/*
 * Copyright (c) 2016. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.dai.dto;

/**
 * Created by GuangLi on 2016/7/25.
 */
public class UserDto {

    private Long id;
    private String name;
    private String loginName;
    private String salt;
    private String password;
    private String email;

//    private List<Role> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
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
}
