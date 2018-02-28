package com.dmsoft.firefly.core.sdkimpl.dataframe;

import com.dmsoft.firefly.sdk.dataframe.CellData;

/**
 * basic class for cell data
 *
 * @author Can Guan
 */
public class BasicCellData implements CellData {
    private String rowKey;
    private String testItemName;
    private String value;
    private Boolean inUsed;

    /**
     * constructor
     *
     * @param rowKey       row key
     * @param testItemName test item name
     * @param value        value
     * @param inUsed       in used status
     */
    BasicCellData(String rowKey, String testItemName, String value, Boolean inUsed) {
        this.rowKey = rowKey;
        this.testItemName = testItemName;
        this.value = value;
        this.inUsed = inUsed;
    }

    @Override
    public String getRowKey() {
        return this.rowKey;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public Boolean getInUsed() {
        return this.inUsed;
    }

    @Override
    public String getTestItemName() {
        return this.testItemName;
    }
}
