package com.dmsoft.firefly.sdk.dataframe;

import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * interface class for data frame
 *
 * @author Can Guan
 */
public interface DataFrame {
    //Test item Operation

    /**
     * method to get data column name array.
     *
     * @return data column name array
     */
    List<String> getAllTestItemName();

    /**
     * method to get test item dto array.
     *
     * @return test item dto array
     */
    List<TestItemWithTypeDto> getAllTestItemWithTypeDto();

    /**
     * method to get test item dto
     *
     * @param testItemName test item name
     * @return test item dto
     */
    TestItemWithTypeDto getTestItemWithTypeDto(String testItemName);

    /**
     * method to update test item, e.g. usl or lsl
     *
     * @param testItemWithTypeDto test item dto
     */
    void updateTestItem(TestItemWithTypeDto testItemWithTypeDto);

    /**
     * method to judge test item exist in this data frame or not
     *
     * @param testItemName test item name
     * @return true : exist, false : not exsit
     */
    boolean isTestItemExist(String testItemName);

    //Column Operation

    /**
     * method to get data column by test item name.
     *
     * @param testItemName column name
     * @return data column
     */
    DataColumn getDataColumn(String testItemName);

    /**
     * method to get data column by column name and search condition.
     *
     * @param testItemName    column name
     * @param searchCondition search condition
     * @return data column
     */
    DataColumn getDataColumn(String testItemName, String searchCondition);

    /**
     * method to get data column by names
     *
     * @param testItemNames list of test item name
     * @return list of data column
     */
    List<DataColumn> getDataColumn(List<String> testItemNames);

    /**
     * method to get data column by column name and search condition.
     *
     * @param testItemNames   column name
     * @param searchCondition search condition
     * @return data column
     */
    List<DataColumn> getDataColumn(List<String> testItemNames, String searchCondition);

    /**
     * method to get column data
     *
     * @param testItemName test item name
     * @return list of string value
     */
    List<String> getDataValue(String testItemName);

    /**
     * method to get data value by test item name and list of row key
     *
     * @param testItemName test item name
     * @param rowKeyList   list of row keys
     * @return list of test data value
     */
    List<String> getDataValue(String testItemName, List<String> rowKeyList);

    /**
     * method to get all data column array
     *
     * @return data column array
     */
    List<DataColumn> getAllDataColumn();

    /**
     * method to remove columns by test item names
     *
     * @param testItemNameList list of test item name
     */
    void removeColumns(List<String> testItemNameList);

    /**
     * method to append columns at last
     *
     * @param dataColumnList list of data column
     */
    void appendColumns(List<DataColumn> dataColumnList);

    /**
     * method to judge row key exist or not
     *
     * @param rowKey row key
     * @return true : exist; false : not exsit
     */
    boolean isRowKeyExist(String rowKey);

    /**
     * method to get data row by row id.
     *
     * @param rowKey row key
     * @return row data
     */
    RowDataDto getDataRow(String rowKey);

    /**
     * method to get data map
     *
     * @param rowKey row key
     * @return data map
     */
    Map<String, String> getDataMap(String rowKey);

    /**
     * method to get data row by row ids
     *
     * @param rowKeyList row ids
     * @return row data list
     */
    List<RowDataDto> getDataRowArray(List<String> rowKeyList);

    /**
     * method to get data row by search conditions
     *
     * @param searchCondition search condition
     * @return row data list
     */
    List<RowDataDto> getDataRowArray(String searchCondition);

    /**
     * method to get all data row
     *
     * @return row data list
     */
    List<RowDataDto> getAllDataRow();

    /**
     * method to remove rows by row keys
     *
     * @param rowKeyList row keys
     */
    void removeRows(List<String> rowKeyList);

    /**
     * method to replace row
     *
     * @param targetRowKey which row to be replace
     * @param rowDataDto   row data dto
     */
    void replaceRow(String targetRowKey, RowDataDto rowDataDto);

    /**
     * method to filter row key by custom filter
     *
     * @param filterFunction filter function
     * @return only filter result is true will be return
     */
    List<String> filterRowKey(Function<Map<String, String>, Boolean> filterFunction);

    /**
     * method to get in used status
     *
     * @param rowKey row key
     * @return true : in used, false : no, null : unknown
     */
    Boolean isInUsed(String rowKey);

    //Cell Operation

    /**
     * method to get cell value
     *
     * @param rowKey       row key
     * @param testItemName test item name
     * @return cell value
     */
    String getCellValue(String rowKey, String testItemName);

    /**
     * method to get pass policy
     *
     * @return pass policy
     */
    PassPolicy getPassPolicy();

    /**
     * method to set pass policy
     *
     * @param passPolicy pass policy
     */
    void setPassPolicy(PassPolicy passPolicy);

    /**
     * method to judge is pass or not
     *
     * @param rowKey       row key
     * @param testItemName test item name
     * @return is pass or not, null for skip depend on policy or unable to check or no exist
     */
    Boolean isPass(String rowKey, String testItemName);


    /**
     * method to get cell result function
     *
     * @return cell result function
     */
    Function<String, Boolean> getCellResultFunction();

    /**
     * method to set cell result function
     * e.g. set "isNumeric" call back and by calling cell result, you can know is numeric or not
     *
     * @param cellResultFunction cell value judge function, the param in call back is the cell value
     */
    void setCellResultFunction(Function<String, Boolean> cellResultFunction);

    /**
     * method to get cell result function
     *
     * @param rowKey       row key
     * @param testItemName test item name
     */
    Boolean getCellResult(String rowKey, String testItemName);

    //Search Operation

    /**
     * method to get searched row key
     *
     * @param searchConditionList list of search condition
     * @return list of row key
     */
    List<String> getSearchedRowKey(List<String> searchConditionList);

    //Shrink Operation

    /**
     * method to shrink, remove redundant rows (which do not belong to any search condition)
     *
     * @param searchConditionList list of search condition
     */
    void shrink(List<String> searchConditionList);

    /**
     * get sub data frame by row keys
     *
     * @param rowKeys row keys
     * @return sub data frame
     */
    DataFrame subDataFrame(List<String> rowKeys);
}
