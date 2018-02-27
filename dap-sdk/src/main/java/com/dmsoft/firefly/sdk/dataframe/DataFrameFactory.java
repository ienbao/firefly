package com.dmsoft.firefly.sdk.dataframe;

import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;

import java.util.List;

/**
 * interface for data frame factory
 *
 * @author Can Guan
 */
public interface DataFrameFactory {

    /**
     * method to create data frame from test item dto list and row data dto list
     *
     * @param testItemWithTypeDtoList list of test item dto
     * @param rowDataDtoList          list of row data dto
     * @return data frame
     */
    DataFrame createDataFrame(List<TestItemWithTypeDto> testItemWithTypeDtoList, List<RowDataDto> rowDataDtoList);

    /**
     * method to create data frame from test item dto list and row data dto list
     *
     * @param testItemWithTypeDtoList list of test item dto
     * @param rowDataDtoList          list of row data dto
     * @return search data frame
     */
    SearchDataFrame createSearchDataFrame(List<TestItemWithTypeDto> testItemWithTypeDtoList, List<RowDataDto> rowDataDtoList);

    /**
     * method to create search data frame from data frame
     *
     * @param dataFrame data frame
     * @return search data frame
     */
    SearchDataFrame createDataFrame(DataFrame dataFrame);

    /**
     * method to create data column from test item dto list and row data dto list
     *
     * @param testItemWithTypeDtoList list of test item dto
     * @param rowDataDtoList          list of row data dto
     * @return list of data column
     */
    List<DataColumn> createDataColumn(List<TestItemWithTypeDto> testItemWithTypeDtoList, List<RowDataDto> rowDataDtoList);
}
