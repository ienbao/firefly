package com.dmsoft.firefly.sdk.dataframe;

import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;

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
    List<TestItemDto> getAllTestItemDto();

    /**
     * method to get test item dto
     *
     * @param testItemName test item name
     * @return test item dto
     */
    TestItemDto getTestItemDto(String testItemName);

    /**
     * method to update test item, e.g. usl or lsl
     *
     * @param testItemDto test item dto
     */
    void updateTestItem(TestItemDto testItemDto);

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
     * @param searchCondition search condition
     * @param testItemName    column name
     * @return data column
     */
    DataColumn getDataColumn(String searchCondition, String testItemName);

    /**
     * method to get data column by column name and search condition.
     *
     * @param searchCondition search condition
     * @param testItemNames   column name
     * @return data column
     */
    List<DataColumn> getDataColumn(String searchCondition, List<String> testItemNames);

    /**
     * method to get all data column array
     *
     * @return data column array
     */
    List<DataColumn> getAllDataColumn();

    /**
     * method to append columns at last
     *
     * @param dataColumnList list of data column
     */
    void appendColumns(List<DataColumn> dataColumnList);

    /**
     * method to get data row by row id.
     *
     * @param rowKey row key
     * @return row data
     */
    RowDataDto getDataRow(String rowKey);

    /**
     * method to get data row by row ids
     *
     * @param rowKeys row ids
     * @return row data list
     */
    List<RowDataDto> getDataRowArray(List<String> rowKeys);

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
     * @param rowKeys row keys
     */
    void removeRows(List<String> rowKeys);

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
     * @return is pass or not, null for skip depend on policy or unable to check
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
     * method to search condition
     *
     * @param searchConditionList search condition
     */
    void search(List<String> searchConditionList);

    //Shrink Operation

    /**
     * method to shrink, remove redundant rows (which do not belong to any search condition)
     */
    void shrink();

    /**
     * get sub data frame by row keys
     *
     * @param rowKeys row keys
     * @return sub data frame
     */
    DataFrame subDataFrame(List<String> rowKeys);
}
