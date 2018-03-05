package com.dmsoft.firefly.sdk.plugin.apis;

/**
 * data parser api
 * {@link com.dmsoft.firefly.sdk.plugin.annotation.DataParser}
 *
 * @author Can Guan
 */
public interface IDataParser {

    /**
     * This method is used to import csv file.
     *
     * @param csvPath the path of csv file
     * @return the id of the csv imported file
     */
    void importFile(String csvPath);

    /**
     * @return
     */
    String getName();
}
