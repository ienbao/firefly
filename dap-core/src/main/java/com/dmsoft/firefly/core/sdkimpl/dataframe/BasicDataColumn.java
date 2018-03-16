package com.dmsoft.firefly.core.sdkimpl.dataframe;

import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dataframe.CellData;
import com.dmsoft.firefly.sdk.dataframe.DataColumn;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Basic data column for data column
 *
 * @author Can Guan
 */
public class BasicDataColumn implements DataColumn {
    private TestItemWithTypeDto testItemDto;
    private List<CellData> cellDataList;

    /**
     * constructor
     *
     * @param testItemDto test item dto
     * @param rowKeyList  row key list
     * @param valueList   value list
     * @param inUsed      in used status
     */
    BasicDataColumn(TestItemWithTypeDto testItemDto, List<String> rowKeyList, List<String> valueList, List<Boolean> inUsed) {
        this.testItemDto = testItemDto;
        this.cellDataList = Lists.newArrayList();
        for (int i = 0; i < rowKeyList.size(); i++) {
            CellData cellData = new BasicCellData(rowKeyList.get(i), testItemDto.getTestItemName(), valueList.get(i), inUsed.get(i));
            this.cellDataList.add(cellData);
        }
    }

    @Override
    public TestItemWithTypeDto getTestItemWithTypeDto() {
        return this.testItemDto;
    }

    @Override
    public List<CellData> getCellData() {
        return this.cellDataList;
    }

    @Override
    public List<String> getData() {
        List<String> result = Lists.newArrayList();
        for (CellData cellData : cellDataList) {
            result.add(cellData.getValue());
        }
        return result;
    }

    @Override
    public String getData(String rowKey) {
        if (rowKey == null) {
            return null;
        }
        for (CellData cellData : this.cellDataList) {
            if (rowKey.equals(cellData.getRowKey())) {
                return cellData.getValue();
            }
        }
        return null;
    }

    @Override
    public List<String> getData(List<String> rowKeys) {
        if (rowKeys == null) {
            return null;
        }
        String[] result = new String[rowKeys.size()];
        for (CellData cellData : this.cellDataList) {
            if (rowKeys.contains(cellData.getRowKey())) {
                result[rowKeys.indexOf(cellData.getRowKey())] =cellData.getValue();
            }
        }
        return Arrays.asList(result);
    }

    @Override
    public Boolean getInUsed(String rowKey) {
        if (rowKey == null) {
            return null;
        }
        for (CellData cellData : this.cellDataList) {
            if (rowKey.equals(cellData.getRowKey())) {
                return cellData.getInUsed();
            }
        }
        return null;
    }

    @Override
    public CellData getCellData(String rowKey) {
        if (rowKey == null) {
            return null;
        }
        for (CellData cellData : this.cellDataList) {
            if (rowKey.equals(cellData.getRowKey())) {
                return cellData;
            }
        }
        return null;
    }
}
