package com.dmsoft.firefly.gui.components.searchcombobox;

import javafx.collections.ObservableList;

/**
 * interface class for search combo box controller
 *
 * @author Can Guan
 */
public interface ISearchComboBoxController {
    /**
     * method to get value for test item
     *
     * @param testItem test item name
     * @return list of value
     */
    ObservableList<String> getValueForTestItem(String testItem);

    /**
     * method to get test items
     *
     * @return list of test item
     */
    ObservableList<String> getTestItems();

    /**
     * method to judge is time key or not
     *
     * @param testItem test item name
     * @return true : is time key; false : is not time key
     */
    boolean isTimeKey(String testItem);

    /**
     * method to get time pattern
     *
     * @return time pattern
     */
    String getTimePattern();
}
