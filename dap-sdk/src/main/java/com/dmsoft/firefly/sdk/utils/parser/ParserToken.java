/*
 *
 *  * Copyright (c) 2016. For Intelligent Group.
 *
 */

package com.dmsoft.firefly.sdk.utils.parser;

/**
 * Created by Peter on 2016/8/25.
 */
public enum ParserToken {
    EQ("="),
    GT(">"),
    LT("<"),
    GET(">="),
    LET("<="),
    NE("!="),
    LIKE("%="),
    LB("("),
    RB(")"),
    AND("&"),
    OR("|");

    private String code;

    ParserToken(String t) {
        this.code = t;
    }

    /**
     * Get value of code.
     *
     * @param code string code.
     * @return ParserToken
     */
    public static ParserToken getValueOf(String code) {
        if (EQ.getCode().equals(code)) {
            return EQ;
        } else if (GT.getCode().equals(code)) {
            return GT;
        } else if (LT.getCode().equals(code)) {
            return LT;
        } else if (GET.getCode().equals(code)) {
            return GET;
        } else if (LET.getCode().equals(code)) {
            return LET;
        } else if (NE.getCode().equals(code)) {
            return NE;
        } else if (LIKE.getCode().equals(code)) {
            return LIKE;
        } else if (LB.getCode().equals(code)) {
            return LB;
        } else if (RB.getCode().equals(code)) {
            return RB;
        } else if (AND.getCode().equals(code)) {
            return AND;
        } else if (OR.getCode().equals(code)) {
            return OR;
        } else {
            return null;
        }
    }

    public String getCode() {
        return code;
    }
}
