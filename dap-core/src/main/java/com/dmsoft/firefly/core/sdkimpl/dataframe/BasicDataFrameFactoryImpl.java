package com.dmsoft.firefly.core.sdkimpl.dataframe;

import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dataframe.DataColumn;
import com.dmsoft.firefly.sdk.dataframe.DataFrame;
import com.dmsoft.firefly.sdk.dataframe.DataFrameFactory;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * basic data frame factory impl for data frame factory
 *
 * @author Can Guan
 */
public class BasicDataFrameFactoryImpl implements DataFrameFactory {
    @Override
    public DataFrame createDataFrame(List<TestItemWithTypeDto> testItemWithTypeDtoList, List<RowDataDto> rowDataDtoList) {
        return new BasicDataFrame(testItemWithTypeDtoList, rowDataDtoList);
    }

    @Override
    public SearchDataFrame createSearchDataFrame(List<TestItemWithTypeDto> testItemWithTypeDtoList, List<RowDataDto> rowDataDtoList) {
        return new BasicSearchDataFrame(testItemWithTypeDtoList, rowDataDtoList);
    }

    @Override
    public SearchDataFrame createSearchDataFrame(DataFrame dataFrame) {
        return new BasicSearchDataFrame(dataFrame.getAllTestItemWithTypeDto(), dataFrame.getAllDataRow());
    }

    @Override
    public List<DataColumn> createDataColumn(List<TestItemWithTypeDto> testItemWithTypeDtoList, List<RowDataDto> rowDataDtoList) {
        if (testItemWithTypeDtoList == null || rowDataDtoList == null) {
            return null;
        }
        List<DataColumn> result = Lists.newArrayList();
        for (int i = 0; i < testItemWithTypeDtoList.size(); i++) {
            TestItemWithTypeDto testItemWithTypeDto = testItemWithTypeDtoList.get(i);
            if (testItemWithTypeDto.getTestItemName() != null) {
                List<String> rowKeyList = Lists.newArrayList();
                List<String> valueList = Lists.newArrayList();
                List<Boolean> inUsedList = Lists.newArrayList();
                for (int j = 0; j < rowDataDtoList.size(); j++) {
                    if (rowDataDtoList.get(j).getRowKey() != null) {
                        rowKeyList.add(rowDataDtoList.get(j).getRowKey());
                        valueList.add(rowDataDtoList.get(j).getData().get(testItemWithTypeDto.getTestItemName()));
                        inUsedList.add(rowDataDtoList.get(j).getInUsed());
                    }
                }
                DataColumn dataColumn = new BasicDataColumn(testItemWithTypeDto, rowKeyList, valueList, inUsedList);
                result.add(dataColumn);
            }
        }
        return result;
    }
}
