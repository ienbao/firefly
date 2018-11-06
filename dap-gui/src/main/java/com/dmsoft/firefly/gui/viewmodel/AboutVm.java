package com.dmsoft.firefly.gui.viewmodel;

import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;

import java.util.Properties;

/**
 * @Author: draven.xu
 * Date: 2018/11/5 16:10
 * Description:
 */
public class AboutVm {
    private static final String COLON = " : ";

    private String osName;

    private String version;

    public AboutVm(){
        this.setVersion(GuiFxmlAndLanguageUtils.getString("VERSION") + COLON + GuiFxmlAndLanguageUtils.getString("STATE_BAR_VERSION"));
        Properties props = System.getProperties();
        this.setOsName(GuiFxmlAndLanguageUtils.getString("OPERATION_SYSTEM") + COLON + props.getProperty("os.name"));
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
