package com.dmsoft.firefly.core.sdkimpl.dataframe;

import com.dmsoft.bamboo.common.utils.collection.CollectionUtil;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDataset;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dataframe.DataColumn;
import com.dmsoft.firefly.sdk.dataframe.DataFrame;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * basic data frame class
 *
 * @author Can Guan
 */
public class BasicDataFrame extends AbstractBasicDataFrame {
    //所有列名称
    private List<String> testItemNameList;
    //数据列相关上下限集合对象
    private List<TestItemWithTypeDto> testItemDtoList;

    private List<Boolean> inUsedList;

    //查询数据集对象
    private TestItemDataset testItemDataset;

    /**
     * constructor
     *
     * @param testItemDtoList test item dto list
     * @param rowDataDtoList  row data dto list
     */
    BasicDataFrame(List<TestItemWithTypeDto> testItemDtoList, List<RowDataDto> rowDataDtoList) {
        this.testItemNameList = Lists.newArrayList();
        this.testItemDtoList = Lists.newArrayList(testItemDtoList);
//        this.rowKeys = Lists.newArrayList();
//        this.inUsedList = Lists.newArrayList();
//        this.cellValues = Lists.newArrayList();
        for (TestItemWithTypeDto testItemDto : this.testItemDtoList) {
            if (!this.testItemNameList.contains(testItemDto.getTestItemName()) && testItemDto.getTestItemName() != null) {
                this.testItemNameList.add(testItemDto.getTestItemName());
            }
        }
//        for (RowDataDto rowDataDto : rowDataDtoList) {
//            if (!this.rowKeys.contains(rowDataDto.getRowKey()) && rowDataDto.getRowKey() != null && rowDataDto.getData() != null) {
//                this.rowKeys.add(rowDataDto.getRowKey());
//                this.inUsedList.add(rowDataDto.getInUsed());
//                List<String> values = Lists.newArrayList();
//                for (String testItemName : testItemNameList) {
//                    values.add(rowDataDto.getData().get(testItemName));
//                }
//                this.cellValues.add(values);
//            }
//        }
    }


    BasicDataFrame(List<TestItemWithTypeDto> testItemDtoList, TestItemDataset testItemDataset){
        this.testItemDtoList = testItemDtoList;
        this.testItemDataset = testItemDataset;

        this.testItemNameList = Lists.newArrayListWithCapacity(this.testItemDataset.getColumnLen());
        this.testItemNameList.addAll(this.testItemDataset.getColumnNameList());
    }

    @Override
    public List<String> getAllTestItemName() {
        return this.testItemNameList;
    }

    @Override
    public List<TestItemWithTypeDto> getAllTestItemWithTypeDto() {
        return this.testItemDtoList;
    }

    @Override
    public TestItemWithTypeDto getTestItemWithTypeDto(String testItemName) {
        int targetIndex = this.testItemNameList.indexOf(testItemName);
        if (targetIndex > -1) {
            return this.testItemDtoList.get(targetIndex);
        }
        return null;
    }

    @Override
    public List<TestItemWithTypeDto> getTestItemWithTypeDto(List<String> testItemNameList) {
        if (testItemNameList != null) {
            List<TestItemWithTypeDto> result = Lists.newArrayList();
            for (String testItemName : testItemNameList) {
                TestItemWithTypeDto testItemWithTypeDto = getTestItemWithTypeDto(testItemName);
                if (testItemWithTypeDto != null) {
                    result.add(testItemWithTypeDto);
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public void updateTestItem(TestItemWithTypeDto testItemWithTypeDto) {
        String targetTestItemName = testItemWithTypeDto.getTestItemName();
        int targetIndex = this.testItemNameList.indexOf(targetTestItemName);
        if (targetIndex > -1) {
            this.testItemNameList.remove(targetIndex);
            this.testItemNameList.add(targetIndex, targetTestItemName);
            this.testItemDtoList.remove(targetIndex);
            this.testItemDtoList.add(targetIndex, testItemWithTypeDto);
        }
    }

    @Override
    public boolean isTestItemExist(String testItemName) {
        return testItemName != null && this.testItemNameList.contains(testItemName);
    }

    @Override
    public DataColumn getDataColumn(String testItemName) {
        TestItemWithTypeDto testItemDto = getTestItemWithTypeDto(testItemName);
        if (testItemDto == null) {
            return null;
        }
        List<String> valueList = getDataValue(testItemName);
        if (valueList == null) {
            return null;
        }
        return new BasicDataColumn(testItemDto, this.testItemDataset.getRowKeyList(), valueList, this.inUsedList);
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
        if (!isTestItemExist(testItemName)) {
            return null;
        }

        String[] rowData = (String[]) this.testItemDataset.getColumn(testItemName);
        if(rowData == null){
            return null;
        }

        return Arrays.asList(rowData);
    }

    @Override
    public List<String> getDataValue(String testItemName, List<String> rowKeyList) {
        if (isTestItemExist(testItemName)) {
            List<String> result = new ArrayList<String>(rowKeyList.size());
            int targetIndex = this.testItemNameList.indexOf(testItemName);
            for (String rowKey : rowKeyList) {
                result.add(privateGetCellValue(rowKey, targetIndex));
            }
            return result;
        }
        return null;
    }

    @Override
    public List<DataColumn> getAllDataColumn() {
        List<DataColumn> result = Lists.newArrayList();
        for (String testItemName : this.testItemNameList) {
            result.add(getDataColumn(testItemName));
        }
        return result;
    }


    @Override
    public void removeColumns(List<String> testItemNameList) {
        if(CollectionUtil.isEmpty(testItemNameList)){
            return;
        }

        for (String testItemName : testItemNameList) {
            if (!isTestItemExist(testItemName)) {
                continue;
            }

            this.testItemNameList.remove(testItemName);
        }
    }

    @Override
    public void appendColumns(List<DataColumn> dataColumnList) {
        //TODO YUANWEN 后面更新
//        for (DataColumn dataColumn : dataColumnList) {
//
//            if (dataColumn != null && dataColumn.getTestItemWithTypeDto() != null
//                    && dataColumn.getTestItemWithTypeDto().getTestItemName() != null
//                    && !this.testItemNameList.contains(dataColumn.getTestItemWithTypeDto().getTestItemName())) {
//                this.testItemNameList.add(dataColumn.getTestItemWithTypeDto().getTestItemName());
//                this.testItemDtoList.add(dataColumn.getTestItemWithTypeDto());
//                for (int i = 0; i < this.rowKeys.size(); i++) {
//                    String rowKey = this.rowKeys.get(i);
//                    this.cellValues.get(i).add(dataColumn.getData(rowKey));
//                }
//            }
//        }
    }

    @Override
    public void appendColumn(int index, DataColumn dataColumn) {
        //TODO YUANWEN 后面更新
//        if (dataColumn != null && dataColumn.getTestItemWithTypeDto() != null
//                && dataColumn.getTestItemWithTypeDto().getTestItemName() != null
//                && !this.testItemNameList.contains(dataColumn.getTestItemWithTypeDto().getTestItemName())) {
//            this.testItemNameList.add(index, dataColumn.getTestItemWithTypeDto().getTestItemName());
//            this.testItemDtoList.add(index, dataColumn.getTestItemWithTypeDto());
//            for (int i = 0; i < this.rowKeys.size(); i++) {
//                String rowKey = this.rowKeys.get(i);
//                this.cellValues.get(i).add(index, dataColumn.getData(rowKey));
//            }
//        }
    }

    @Override
    public boolean isRowKeyExist(String rowKey) {
        return rowKey != null && this.testItemDataset.getRowKeyList().contains(rowKey);
    }

    @Override
    public RowDataDto getDataRow(String rowKey) {
        //TODO YUANWEN 后面更新
//        if (isRowKeyExist(rowKey)) {
//            Boolean inUsed = isInUsed(rowKey);
//            Map<String, String> data = Maps.newHashMap();
//            int targetRowIndex = this.rowKeys.indexOf(rowKey);
//            for (int i = 0; i < this.testItemNameList.size(); i++) {
//                data.put(this.testItemNameList.get(i), this.cellValues.get(targetRowIndex).get(i));
//            }
//            RowDataDto rowDataDto = new RowDataDto();
//            rowDataDto.setInUsed(inUsed);
//            rowDataDto.setRowKey(rowKey);
//            rowDataDto.setData(data);
//            return rowDataDto;
//        }
        return null;
    }

    @Override
    public List<String> getDataRowList(String rowKey) {

        //TODO YUANWEN 后面更新
//        if (isRowKeyExist(rowKey)) {
//            List<String> data = Lists.newArrayList();
//            int targetRowIndex = this.rowKeys.indexOf(rowKey);
//            for (int i = 0; i < this.testItemNameList.size(); i++) {
//                data.add(this.cellValues.get(targetRowIndex).get(i));
//            }
//            return data;
//        }
        return null;
    }

    @Override
    public Map<String, String> getDataMap(String rowKey) {
        Map<String, String> result = Maps.newHashMap();
        for (String testItemName : this.testItemNameList) {
            result.put(testItemName, getCellValue(rowKey, testItemName));
        }
        return result;
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
        for (String rowKey : this.testItemDataset.getRowKeyList()) {
            result.add(getDataRow(rowKey));
        }
        return result;
    }

    @Override
    public List<String> getAllRowKeys() {
        return Lists.newArrayList(this.testItemDataset.getRowKeyList());
    }

    @Override
    public void removeRows(List<String> rowKeyList) {
        //TODO YUANWEN 后面更新
//        for (String rowKey : rowKeyList) {
//            if (isRowKeyExist(rowKey)) {
//                int targetRowIndex = this.rowKeys.indexOf(rowKey);
//                this.rowKeys.remove(targetRowIndex);
//                this.inUsedList.remove(targetRowIndex);
//                this.cellValues.remove(targetRowIndex);
//            }
//        }
    }

    @Override
    public void replaceRow(String targetRowKey, RowDataDto rowDataDto) {

        //TODO YUANWEN 后面更新
//        if (rowDataDto != null && rowDataDto.getData() != null && rowDataDto.getRowKey() != null && isRowKeyExist(targetRowKey)) {
//            int targetRowIndex = this.rowKeys.indexOf(targetRowKey);
//            this.rowKeys.remove(targetRowIndex);
//            this.rowKeys.add(targetRowIndex, rowDataDto.getRowKey());
//            this.inUsedList.remove(targetRowIndex);
//            this.inUsedList.add(targetRowIndex, rowDataDto.getInUsed());
//            this.cellValues.remove(targetRowIndex);
//            List<String> data = Lists.newArrayList();
//            for (String testItemName : this.testItemNameList) {
//                data.add(rowDataDto.getData().get(testItemName));
//            }
//            this.cellValues.add(targetRowIndex, data);
//        }
    }

    @Override
    public List<String> filterRowKey(Function<Map<String, String>, Boolean> filterFunction) {
        List<String> result = Lists.newArrayList();
        for (String rowKey : this.testItemDataset.getRowKeyList()) {
            if (filterFunction.apply(getDataMap(rowKey))) {
                result.add(rowKey);
            }
        }
        return result;
    }

    @Override
    public Set<String> getValueSet(String testItemName) {
        List<String> valueList = getDataValue(testItemName);
        Set<String> result = Sets.newLinkedHashSet();
        valueList.forEach(s -> result.add(s));
        return result;
    }

    @Override
    public Boolean isInUsed(String rowKey) {
        if (isRowKeyExist(rowKey)) {
            return this.inUsedList.get(this.testItemDataset.getRowKeyList().indexOf(rowKey));
        }
        return Boolean.FALSE;
    }

    @Override
    public String getCellValue(String rowKey, String testItemName) {

        if (isRowKeyExist(rowKey) && isTestItemExist(testItemName)) {

            return this.testItemDataset.getCellValue(rowKey, testItemName);
        }
        return null;
    }

    @Override
    public DataFrame subDataFrame(List<String> rowKeyList, List<String> testItemNameList) {
        List<RowDataDto> rowDataDtoList = getDataRowArray(rowKeyList);
        List<TestItemWithTypeDto> typeDtoList = getTestItemWithTypeDto(testItemNameList);
        return new BasicDataFrame(typeDtoList, rowDataDtoList);
    }

    @Override
    public int getRowSize() {
        return this.testItemDataset.getRowKeyList() == null ? 0 : this.testItemDataset.getRowKeyList().size();
    }

    @Override
    public int getColumnSize() {
        return this.testItemNameList == null ? 0 : this.testItemNameList.size();
    }

    private String privateGetCellValue(String rowKey, int targetColumnIndex) {

        //TODO YUANWEN 后面更新
//        if (targetColumnIndex < 0) {
//            return null;
//        }
//        int targetRowIndex = this.rowKeys.indexOf(rowKey);
//        if (targetRowIndex > -1) {
//            return this.cellValues.get(targetRowIndex).get(targetColumnIndex);
//        }
        return null;
    }

    protected List<String> getTestItemNameList() {
        return testItemNameList;
    }

    protected List<TestItemWithTypeDto> getTestItemDtoList() {
        return testItemDtoList;
    }

    protected List<String> getRowKeys() {
        return this.testItemDataset.getRowKeyList();
    }

    protected List<Boolean> getInUsedList() {
        return inUsedList;
    }

    protected List<List<String>> getCellValues() {
        //TODO YUANWEN 后面更新
//        return cellValues;

        return null;
    }
}
