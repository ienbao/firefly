package com.dmsoft.firefly.core.sdkimpl.dataframe;

import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dataframe.DataColumn;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * basic data frame class
 *
 * @author Can Guan
 */
public class BasicDataFrame extends AbstractBasicDataFrame {
    //Column
    protected List<String> testItemNames;
    protected List<TestItemWithTypeDto> testItemDtoList;
    //Row
    protected List<String> rowKeys;
    protected List<Boolean> inUsedList;
    //Column & Row
    //outside list index is same with the rowKeys index, inside list index is same with the testItemNames index
    protected List<List<String>> cellValues;

    /**
     * constructor
     *
     * @param testItemDtoList test item dto list
     * @param rowDataDtoList  row data dto list
     */
    public BasicDataFrame(List<TestItemWithTypeDto> testItemDtoList, List<RowDataDto> rowDataDtoList) {
        this.testItemNames = Lists.newArrayList();
        this.rowKeys = Lists.newArrayList();
        this.testItemDtoList = Lists.newArrayList(testItemDtoList);
        this.inUsedList = Lists.newArrayList();
        this.cellValues = Lists.newArrayList();
        for (TestItemWithTypeDto testItemDto : this.testItemDtoList) {
            if (!this.testItemNames.contains(testItemDto.getTestItemName()) && testItemDto.getTestItemName() != null) {
                this.testItemNames.add(testItemDto.getTestItemName());
            }
        }
        for (RowDataDto rowDataDto : rowDataDtoList) {
            if (!this.rowKeys.contains(rowDataDto.getRowKey()) && rowDataDto.getRowKey() != null && rowDataDto.getData() != null) {
                this.rowKeys.add(rowDataDto.getRowKey());
                this.inUsedList.add(rowDataDto.getInUsed());
                List<String> values = Lists.newArrayList();
                for (String testItemName : testItemNames) {
                    values.add(rowDataDto.getData().get(testItemName));
                }
                this.cellValues.add(values);
            }
        }
    }

    @Override
    public List<String> getAllTestItemName() {
        return Lists.newArrayList(this.testItemNames);
    }

    @Override
    public List<TestItemWithTypeDto> getAllTestItemWithTypeDto() {
        return Lists.newArrayList(this.testItemDtoList);
    }

    @Override
    public TestItemWithTypeDto getTestItemWithTypeDto(String testItemName) {
        int targetIndex = this.testItemNames.indexOf(testItemName);
        if (targetIndex > 0) {
            return this.testItemDtoList.get(targetIndex);
        }
        return null;
    }

    @Override
    public void updateTestItem(TestItemWithTypeDto testItemWithTypeDto) {
        String targetTestItemName = testItemWithTypeDto.getTestItemName();
        int targetIndex = this.testItemNames.indexOf(targetTestItemName);
        if (targetIndex > 0) {
            this.testItemNames.remove(targetIndex);
            this.testItemNames.add(targetIndex, targetTestItemName);
            this.testItemDtoList.remove(targetIndex);
            this.testItemDtoList.add(targetIndex, testItemWithTypeDto);
        }
    }

    @Override
    public boolean isTestItemExist(String testItemName) {
        return testItemName != null && this.testItemNames.contains(testItemName);
    }

    @Override
    public DataColumn getDataColumn(String testItemName) {
        if (isTestItemExist(testItemName)) {
            TestItemWithTypeDto testItemDto = getTestItemWithTypeDto(testItemName);
            List<String> valueList = getDataValue(testItemName);
            return new BasicDataColumn(testItemDto, this.rowKeys, valueList, this.inUsedList);
        }
        return null;
    }

    @Override
    public List<DataColumn> getDataColumn(List<String> testItemNames) {
        List<DataColumn> dataColumns = Lists.newArrayList();
        for (String testItemName : testItemNames) {
            dataColumns.add(getDataColumn(testItemName));
        }
        return dataColumns;
    }

    @Override
    public List<String> getDataValue(String testItemName) {
        if (isTestItemExist(testItemName)) {
            List<String> result = Lists.newArrayList();
            int targetIndex = this.testItemNames.indexOf(testItemName);
            for (int i = 0; i < this.rowKeys.size(); i++) {
                result.add(this.cellValues.get(i).get(targetIndex));
            }
            return result;
        }
        return null;
    }

    @Override
    public List<String> getDataValue(String testItemName, List<String> rowKeyList) {
        if (isTestItemExist(testItemName)) {
            List<String> result = Lists.newArrayList();
            for (String rowKey : rowKeyList) {
                result.add(getCellValue(rowKey, testItemName));
            }
            return result;
        }
        return null;
    }

    @Override
    public List<DataColumn> getAllDataColumn() {
        List<DataColumn> result = Lists.newArrayList();
        for (String testItemName : this.testItemNames) {
            result.add(getDataColumn(testItemName));
        }
        return result;
    }

    @Override
    public void removeColumns(List<String> testItemNameList) {
        for (String testItemName : testItemNameList) {
            if (isTestItemExist(testItemName)) {
                int targetColumnIndex = this.testItemNames.indexOf(testItemName);
                this.testItemNames.remove(targetColumnIndex);
                this.testItemDtoList.remove(targetColumnIndex);
                for (int i = 0; i < this.rowKeys.size(); i++) {
                    this.cellValues.get(i).remove(targetColumnIndex);
                }
            }
        }
    }

    @Override
    public void appendColumns(List<DataColumn> dataColumnList) {
        for (DataColumn dataColumn : dataColumnList) {
            if (dataColumn != null && dataColumn.getTestItemWithTypeDto() != null
                    && dataColumn.getTestItemWithTypeDto().getTestItemName() != null
                    && !this.testItemNames.contains(dataColumn.getTestItemWithTypeDto().getTestItemName())) {
                this.testItemNames.add(dataColumn.getTestItemWithTypeDto().getTestItemName());
                this.testItemDtoList.add(dataColumn.getTestItemWithTypeDto());
                for (int i = 0; i < this.rowKeys.size(); i++) {
                    String rowKey = this.rowKeys.get(i);
                    this.cellValues.get(i).add(dataColumn.getDataValue(rowKey));
                }
            }
        }
    }

    @Override
    public boolean isRowKeyExist(String rowKey) {
        return rowKey != null && this.rowKeys.contains(rowKey);
    }

    @Override
    public RowDataDto getDataRow(String rowKey) {
        if (isRowKeyExist(rowKey)) {
            Boolean inUsed = isInUsed(rowKey);
            Map<String, String> data = Maps.newHashMap();
            for (String testItemName : this.testItemNames) {
                data.put(testItemName, getCellValue(rowKey, testItemName));
            }
            RowDataDto rowDataDto = new RowDataDto();
            rowDataDto.setInUsed(inUsed);
            rowDataDto.setRowKey(rowKey);
            rowDataDto.setData(data);
            return rowDataDto;
        }
        return null;
    }

    @Override
    public Map<String, String> getDataMap(String rowKey) {
        if (isRowKeyExist(rowKey)) {
            Map<String, String> result = Maps.newHashMap();
            for (String testItemName : this.testItemNames) {
                result.put(testItemName, getCellValue(rowKey, testItemName));
            }
            return result;
        }
        return null;
    }

    @Override
    public List<RowDataDto> getDataRowArray(List<String> rowKeyList) {
        if (rowKeyList != null) {
            List<RowDataDto> result = Lists.newArrayList();
            for (String rowKey : rowKeyList) {
                result.add(getDataRow(rowKey));
            }
            return result;
        }
        return null;
    }

    @Override
    public List<RowDataDto> getAllDataRow() {
        List<RowDataDto> result = Lists.newArrayList();
        for (String rowKey : this.rowKeys) {
            result.add(getDataRow(rowKey));
        }
        return result;
    }

    @Override
    public void removeRows(List<String> rowKeyList) {
        for (String rowKey : rowKeyList) {
            if (isRowKeyExist(rowKey)) {
                int targetRowIndex = this.rowKeys.indexOf(rowKey);
                this.rowKeys.remove(targetRowIndex);
                this.inUsedList.remove(targetRowIndex);
                this.cellValues.remove(targetRowIndex);
            }
        }
    }

    @Override
    public void replaceRow(String targetRowKey, RowDataDto rowDataDto) {
        if (rowDataDto != null && rowDataDto.getData() != null && rowDataDto.getRowKey() != null && isRowKeyExist(targetRowKey)) {
            int targetRowIndex = this.rowKeys.indexOf(targetRowKey);
            this.rowKeys.remove(targetRowIndex);
            this.rowKeys.add(targetRowIndex, rowDataDto.getRowKey());
            this.inUsedList.remove(targetRowIndex);
            this.inUsedList.add(targetRowIndex, rowDataDto.getInUsed());
            this.cellValues.remove(targetRowIndex);
            List<String> data = Lists.newArrayList();
            for (String testItemName : this.testItemNames) {
                data.add(rowDataDto.getData().get(testItemName));
            }
            this.cellValues.add(targetRowIndex, data);
        }
    }

    @Override
    public List<String> filterRowKey(Function<Map<String, String>, Boolean> filterFunction) {
        List<String> result = Lists.newArrayList();
        for (String rowKey : this.rowKeys) {
            if (filterFunction.apply(getDataMap(rowKey))) {
                result.add(rowKey);
            }
        }
        return result;
    }

    @Override
    public Boolean isInUsed(String rowKey) {
        if (isRowKeyExist(rowKey)) {
            return this.inUsedList.get(rowKeys.indexOf(rowKey));
        }
        return null;
    }

    @Override
    public String getCellValue(String rowKey, String testItemName) {
        if (isRowKeyExist(rowKey) && isTestItemExist(testItemName)) {
            int targetRowIndex = this.rowKeys.indexOf(rowKey);
            int targetColumnIndex = this.testItemNames.indexOf(testItemName);
            return this.cellValues.get(targetRowIndex).get(targetColumnIndex);
        }
        return null;
    }
}