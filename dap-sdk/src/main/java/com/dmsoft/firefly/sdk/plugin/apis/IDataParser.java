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
     * @param csvPath        the path of csv file
     * @param importTemplate csv import template name
     * @return the id of the csv imported file
     */
    Long importCsv(String csvPath, String importTemplate);
}
