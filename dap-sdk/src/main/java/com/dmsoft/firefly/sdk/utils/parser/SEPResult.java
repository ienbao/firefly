/*
 *
 *  * Copyright (c) 2016. For Intelligent Group.
 *
 */

package com.dmsoft.firefly.sdk.utils.parser;

import com.dmsoft.bamboo.common.dto.AbstractValueObject;

/**
 * Created by Peter on 2016/8/26.
 */
public class SEPResult extends AbstractValueObject {
    private Object leftExpr;
    private Object rightExpr;
    private String token;
    private String logicalToken;

    public Object getLeftExpr() {
        return leftExpr;
    }

    public void setLeftExpr(Object leftExpr) {
        this.leftExpr = leftExpr;
    }

    public Object getRightExpr() {
        return rightExpr;
    }

    public void setRightExpr(Object rightExpr) {
        this.rightExpr = rightExpr;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLogicalToken() {
        return logicalToken;
    }

    public void setLogicalToken(String logicalToken) {
        this.logicalToken = logicalToken;
    }
}
