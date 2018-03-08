package com.dmsoft.firefly.sdk.dai.service;

import com.dmsoft.firefly.sdk.dai.dto.UserDto;

/**
 * Created by GuangLi on 2018/1/25.
 */
public interface UserService {
    /**
     * validate if login information is legal.
     *
     * @param userName name of user
     * @param password password of user
     * @return if legal return corresponding userDto, else return null
     */
    UserDto validateUser(String userName, String password);

    /**
     * update user password.
     *
     * @param userName name of user
     * @param oldPwd   password before updating
     * @param newPwd   password after updating
     */
    void updatePassword(String userName, String oldPwd, String newPwd);

    /**
     * update user legal.
     *
     * @param acceptLegal   accept Legal
     */
    void updateLegal(boolean acceptLegal);

    /**
     * find user legal.
     *
     */
    boolean findLegal();
}
