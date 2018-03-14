
/*
 *
 *  * Copyright (c) 2016. For Intelligent Group.
 *
 */

package com.dmsoft.firefly.plugin.spc.poi;

/**
 * Created by Peter on 2016/4/18.
 */
public enum ExCellType {
    IMAGE("IMAGE"),
    IMAGE_ANCHOR("IMAGE_ANCHOR"),
    TEXT("TEXT"),
    HYPERLINK("HYPERLINK");

    ExCellType(String code) {
        this.code = code;
    }

    private String code;

    public String getCode() {
        return code;
    }

    /**
     * get hyperlink address
     *
     * @param code address
     * @return ExCellType
     */
    public ExCellType withCode(String code) {
        this.code = code;
        return this;
    }

}
