package com.dmsoft.firefly.sdk.event;

/**
 * enum event type
 *
 * @author Can Guan
 */
public enum EventType {
    PLATFORM_INFO,
    PLATFORM_WARN,
    PLATFORM_ERROR,
    PLATFORM_TEMPLATE_SHOW, //显示模板弹窗
    SYSTEM_LOGIN_SUCCESS_ACTION,//登陆成功
    SYSTEM_LOGIN_FAIL_ACITON,//登陆失败

    PLATFORM_SHOW_MAIN,//显示平台
    PLATFORM_PROCESS_CLOSE,//关闭加载进度
    PLATFORM_RESET_MAIN,//重置主窗体
    UPDATA_PROGRESS,//更新进度条
    SYSTEM_CHANGE_PASSWORD_FAILE,//修改密码失败
    SYSTEM_OLDPASSWORD_NOT_CORRECT,//old密码不正确
    SYSTEM_CHANGE_PASSWORD_SUCCESS//修改密码成功
}
