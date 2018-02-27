package com.dmsoft.firefly.sdk.dataframe;

/**
 * interface class for data cell
 *
 * @author Can Guan
 */
public interface CellData {
    /**
     * method get row key
     *
     * @return row key
     */
    String getRowKey();

    /**
     * method to get value
     *
     * @return value
     */
    String getValue();

    /**
     * method to get inUsed
     *
     * @return in used value
     */
    Boolean getInUsed();

    /**
     * method to get test item name
     *
     * @return test item name
     */
    String getTestItemName();
}
