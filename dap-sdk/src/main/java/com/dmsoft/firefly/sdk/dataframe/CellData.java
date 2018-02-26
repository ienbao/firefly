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
     * method to judge this data cell is in used or not
     *
     * @return true : in used, false : not in used
     */
    boolean isInUsed();

    /**
     * method to juedge this data cell is pass or not
     *
     * @return true : pass, false : failed
     */
    boolean isPassed();
}
