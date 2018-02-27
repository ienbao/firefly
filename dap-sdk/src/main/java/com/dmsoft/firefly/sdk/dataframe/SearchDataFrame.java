package com.dmsoft.firefly.sdk.dataframe;

import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;

import java.util.List;

/**
 * interface class for data frame
 *
 * @author Can Guan
 */
public interface SearchDataFrame extends DataFrame {
    //Test item Operation

    /**
     * method to get all test item dto
     *
     * @param searchCondition search condition
     * @return test item dto list
     */
    List<TestItemWithTypeDto> getAllTestItemWithTypeDto(String searchCondition);

    /**
     * method to get test item dto
     *
     * @param testItemName    test item name
     * @param searchCondition search condition
     * @return test item dto
     */
    TestItemWithTypeDto getTestItemWithTypeDto(String testItemName, String searchCondition);

    /**
     * method to update test item by search condition
     *
     * @param testItemWithTypeDto test item dto
     * @param searchCondition     search condition
     */
    void updateTestItem(TestItemWithTypeDto testItemWithTypeDto, String searchCondition);

    //Column Operation

    /**
     * method to get data column by column name and search condition.
     *
     * @param testItemName    column name
     * @param searchCondition search condition
     * @return data column
     */
    DataColumn getDataColumn(String testItemName, String searchCondition);

    /**
     * method to get data column by column name and search condition.
     *
     * @param testItemNames   column name
     * @param searchCondition search condition
     * @return data column
     */
    List<DataColumn> getDataColumn(List<String> testItemNames, String searchCondition);


    /**
     * method to get data row by search conditions
     *
     * @param searchCondition search condition
     * @return row data list
     */
    List<RowDataDto> getDataRowArray(String searchCondition);

    //Search Operation

    /**
     * method add search condition in data frame
     *
     * @param searchConditionList search condition list
     */
    void addSearchCondition(List<String> searchConditionList);

    /**
     * method to get all search condition list
     *
     * @return list of search condition
     */
    List<String> getSearchConditionList();

    /**
     * method to remove search condition
     *
     * @param searchCondition search condition
     */
    void removeSearchCondition(String searchCondition);

    /**
     * method to clear all search conditions
     */
    void clearSearchConditions();

    /**
     * method to get searched row key (search conditon is the all added search condition)
     *
     * @return list of row key
     */
    List<String> getSearchedRowKey();

    /**
     * method to get row key by condition
     *
     * @param searchCondition search condition
     * @return list of row key
     */
    List<String> getSearchRowKey(String searchCondition);

    //Shrink Operation

    /**
     * method to shrink, remove redundant rows (which do not belong to any search condition)
     */
    void shrink();
}
