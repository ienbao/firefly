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
     * @return data column name list
     */
    List<String> getAllTestItemName();

    /**
     * method to get test item dto array.
     *
     * @return test item dto list
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
     * method to get test item dto
     *
     * @param testItemNameList test item name
     * @return test item with type dto list
     */
    List<TestItemWithTypeDto> getTestItemWithTypeDto(List<String> testItemNameList);

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
     * method to get data column by names
     *
     * @param testItemNames list of test item name
     * @return list of data column
     */
    List<DataColumn> getDataColumn(List<String> testItemNames);

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
     * method to append column at index
     *
     * @param index      index
     * @param dataColumn data column
     */
    void appendColumn(int index, DataColumn dataColumn);

    //Row Operation

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
     * method to get data row list
     *
     * @param rowKey row key
     * @return list of values
     */
    List<String> getDataRowList(String rowKey);

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
     * method to get all data row
     *
     * @return row data list
     */
    List<RowDataDto> getAllDataRow();

    /**
     * method to get all row keys
     *
     * @return list of string
     */
    List<String> getAllRowKeys();

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
     * method to get cell result function
     *
     * @return cell result function
     */
    Function<String, Object> getCellResultFunction();

    /**
     * method to set cell result function
     * e.g. set "isNumeric" call back and by calling cell result, you can know is numeric or not
     *
     * @param cellResultFunction cell value judge function, the param in call back is the cell value
     */
    void setCellResultFunction(Function<String, Object> cellResultFunction);

    /**
     * method to get cell result function
     *
     * @param rowKey       row key
     * @param testItemName test item name
     */
    Object getCellResult(String rowKey, String testItemName);

    /**
     * method to get sub data frame by row keys and test item names
     *
     * @param rowKeyList       row key list
     * @param testItemNameList test item name list
     * @return data frame
     */
    DataFrame subDataFrame(List<String> rowKeyList, List<String> testItemNameList);

    /**
     * method to get row size
     *
     * @return row size
     */
    int getRowSize();

    /**
     * method to get column
     *
     * @return column size
     */
    int getColumnSize();
}
