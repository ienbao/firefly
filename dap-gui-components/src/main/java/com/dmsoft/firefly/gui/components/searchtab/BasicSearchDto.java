package com.dmsoft.firefly.gui.components.searchtab;

import java.io.Serializable;

/**
 * Created by GuangLi on 2018/3/5.
 */
public class BasicSearchDto implements Serializable {
    private String testItem;
    private String operator;
    private String value;

    public String getTestItem() {
        return testItem;
    }

    public void setTestItem(String testItem) {
        this.testItem = testItem;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
