package com.dmsoft.firefly.plugin.yield.dto;

public class IgnoreTestItemValue {
    private String rowKey;
    private SearchConditionDto searchConditionDto;

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public SearchConditionDto getSearchConditionDto() {
        return searchConditionDto;
    }

    public void setSearchConditionDto(SearchConditionDto searchConditionDto) {
        this.searchConditionDto = searchConditionDto;
    }
}
