package com.dmsoft.firefly.gui.components.searchcombobox;

public interface ISearchComboBox {
    String getCondition();

    String getTestItem();

    String getOperator();

    String getValue();

    void setCondition(String condition);

    void setTestItem(String testItem);

    void setValue(String value);
}
