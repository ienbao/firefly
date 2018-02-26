package com.dmsoft.firefly.sdk.dataframe;

import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;

import java.util.List;

/**
 * interface class for data column
 *
 * @author Can Guan
 */
public interface DataColumn {
    /**
     * method to get test item dto.
     *
     * @return data column info
     */
    TestItemWithTypeDto getTestItemWithTypeDto();

    /**
     * method to get data string array.
     *
     * @return data string array
     */
    List<CellData> getData();

    /**
     * method to get data by row key
     *
     * @param rowKey row key
     * @return value
     */
    String getDataValue(String rowKey);

    /**
     * method to get data in used value by row key
     *
     * @param rowKey row key
     * @return true : in used, false : not
     */
    Boolean getInUsed(String rowKey);

    /**
     * method to get cell data
     *
     * @param rowKey row key
     * @return cell data
     */
    CellData getCellData(String rowKey);
}
