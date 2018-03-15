package com.dmsoft.firefly.plugin.spc.dto;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

import java.util.Map;

/**
 * Created by GuangLi on 2017/7/12.
 */
public class RuleResultDto extends AbstractValueObject {
//    private String key;
    private Map<String, String> cpwMap;
    private Map<String, Boolean> cusCpwMap;
//
//    public String getKey() {
//        return key;
//    }
//
//    public void setKey(String key) {
//        this.key = key;
//    }

    public Map<String, String> getCpwMap() {
        return cpwMap;
    }

    public void setCpwMap(Map<String, String> cpwMap) {
        this.cpwMap = cpwMap;
    }

    public Map<String, Boolean> getCusCpwMap() {
        return cusCpwMap;
    }

    public void setCusCpwMap(Map<String, Boolean> cusCpwMap) {
        this.cusCpwMap = cusCpwMap;
    }
}
