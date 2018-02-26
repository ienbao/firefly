package com.dmsoft.firefly.sdk.dataframe;

import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;

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
    TestItemDto getTestItemDto();

    /**
     * method to get data string array.
     *
     * @return data string array
     */
    List<CellData> getData();
}
