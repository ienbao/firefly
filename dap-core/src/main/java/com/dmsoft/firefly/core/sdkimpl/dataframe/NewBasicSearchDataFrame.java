package com.dmsoft.firefly.core.sdkimpl.dataframe;

import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDataset;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dataframe.DataColumn;
import com.dmsoft.firefly.sdk.dataframe.DataFrameFactory;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.utils.FilterUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * basic data frame class
 *
 * @author Can Guan
 */
public class NewBasicSearchDataFrame extends NewBasicDataFrame implements SearchDataFrame {
    //outside list index is same with the rowKeys index
    private List<Set<String>> rowSearchConditionResultList;
    //Additional
    private Set<String> searchConditions;
    private FilterUtils filterUtils;

    /**
     * constructor
     *
     * @param testItemDtoList test item dto list
     * @param rowDataDtoList  row data dto list
     */
    NewBasicSearchDataFrame(List<TestItemWithTypeDto> testItemDtoList, List<RowDataDto> rowDataDtoList) {
        super(testItemDtoList, rowDataDtoList);

        this.rowSearchConditionResultList = Lists.newArrayList();
        this.searchConditions = Sets.newLinkedHashSet();
        List<String> timeKeys = RuntimeContext.getBean(EnvService.class).findActivatedTemplate().getTimePatternDto().getTimeKeys();
        String timePattern = RuntimeContext.getBean(EnvService.class).findActivatedTemplate().getTimePatternDto().getPattern();
        this.filterUtils = new FilterUtils(timeKeys, timePattern);
        for (int i = 0; i < this.getRowSize(); i++) {
            this.rowSearchConditionResultList.add(Sets.newHashSet());
        }
    }


    NewBasicSearchDataFrame(List<TestItemWithTypeDto> testItemWithTypeDtoList, TestItemDataset testItemDataset){
        super(testItemWithTypeDtoList, testItemDataset);

        this.rowSearchConditionResultList = Lists.newArrayList();
        this.searchConditions = Sets.newLinkedHashSet();
        List<String> timeKeys = RuntimeContext.getBean(EnvService.class).findActivatedTemplate().getTimePatternDto().getTimeKeys();
        String timePattern = RuntimeContext.getBean(EnvService.class).findActivatedTemplate().getTimePatternDto().getPattern();
        this.filterUtils = new FilterUtils(timeKeys, timePattern);
        for (int i = 0; i < this.getRowSize(); i++) {
            this.rowSearchConditionResultList.add(Sets.newHashSet());
        }
    }

    @Override
    public DataColumn getDataColumn(String testItemName, String searchCondition) {
        if (isTestItemExist(testItemName)) {
            TestItemWithTypeDto testItemDto = getTestItemWithTypeDto(testItemName);
            List<String> rowKeyList = getSearchRowKey(searchCondition);
            List<String> valueList = getDataValue(testItemName, rowKeyList);
            List<Boolean> inUsed = Lists.newArrayListWithCapacity(rowKeyList.size());
            for (String rowKey : rowKeyList) {
                inUsed.add(isInUsed(rowKey));
            }
            return new BasicDataColumn(testItemDto, rowKeyList, valueList, inUsed);
        }
        return null;
    }

    @Override
    public void removeRows(List<String> rowKeyList) {
        for (String rowKey : rowKeyList) {
            if (isRowKeyExist(rowKey)) {
                int targetRowIndex = getRowKeys().indexOf(rowKey);
                getRowKeys().remove(targetRowIndex);
                getInUsedList().remove(targetRowIndex);
                getCellValues().remove(targetRowIndex);
                rowSearchConditionResultList.remove(targetRowIndex);
            }
        }
    }

    @Override
    public List<DataColumn> getDataColumn(List<String> testItemNames, String searchCondition) {
        List<DataColumn> dataColumns = Lists.newArrayListWithCapacity(testItemNames.size());
        for (String testItemName : testItemNames) {
            dataColumns.add(getDataColumn(testItemName, searchCondition));
        }
        return dataColumns;
    }

    @Override
    public List<RowDataDto> getDataRowArray(String searchCondition) {
        if (!this.searchConditions.contains(searchCondition) && FilterUtils.isLegal(searchCondition)) {
            search(searchCondition);
        }
        List<RowDataDto> result = Lists.newArrayListWithCapacity(this.getRowSize());
        for (int i = 0; i < this.getRowSize(); i++) {
            if (this.rowSearchConditionResultList.get(i).contains(searchCondition)) {
                result.add(getDataRow(this.getRowKeys().get(i)));
            }
        }
        return result;
    }

    @Override
    public void addSearchCondition(List<String> searchConditionList) {
        for (String s : searchConditionList) {
            if (FilterUtils.isLegal(s) && !this.searchConditions.contains(s)) {
                search(s);
                this.searchConditions.add(s);
            }
        }
    }

    @Override
    public List<String> getSearchConditionList() {
        return Lists.newArrayList(this.searchConditions);
    }

    @Override
    public void removeSearchCondition(String searchCondition) {
        if (this.searchConditions.contains(searchCondition)) {
            this.searchConditions.remove(searchCondition);
            for (Set<String> searchResult : this.rowSearchConditionResultList) {
                searchResult.remove(searchCondition);
            }
        }
    }

    @Override
    public void clearSearchConditions() {
        this.searchConditions.clear();
        for (Set<String> rowSearch : this.rowSearchConditionResultList) {
            rowSearch.clear();
        }
    }

    @Override
    public List<String> getSearchedRowKey() {
        List<String> result = Lists.newArrayListWithCapacity(rowSearchConditionResultList.size());
        for (int i = 0; i < this.rowSearchConditionResultList.size(); i++) {
            if (!this.rowSearchConditionResultList.get(i).isEmpty()) {
                result.add(this.getRowKeys().get(i));
            }
        }
        return result;
    }

    @Override
    public List<String> getSearchRowKey(String searchCondition) {
        if (!this.searchConditions.contains(searchCondition)) {
            search(searchCondition);
        }
        List<String> result = Lists.newArrayListWithCapacity(rowSearchConditionResultList.size());
        for (int i = 0; i < this.rowSearchConditionResultList.size(); i++) {
            if (this.rowSearchConditionResultList.get(i).contains(searchCondition)) {
                result.add(this.getRowKeys().get(i));
            }
        }
        return result;
    }

    @Override
    public List<String> getSearchRowKey(List<String> searchConditionList) {
        for (String condition : searchConditionList) {
            if (!this.searchConditions.contains(condition)) {
                search(condition);
            }
        }
        List<String> result = Lists.newArrayListWithCapacity(rowSearchConditionResultList.size());
        for (int i = 0; i < this.rowSearchConditionResultList.size(); i++) {
            Set<String> eachRowSearchConditionSet = this.rowSearchConditionResultList.get(i);
            for (String condition : searchConditionList) {
                if (eachRowSearchConditionSet.contains(condition)) {
                    result.add(this.getRowKeys().get(i));
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public void replaceRow(String targetRowKey, RowDataDto rowDataDto) {
        super.replaceRow(targetRowKey, rowDataDto);
        if (rowDataDto != null && rowDataDto.getData() != null && rowDataDto.getRowKey() != null && isRowKeyExist(targetRowKey)) {
            int targetRowIndex = this.getRowKeys().indexOf(targetRowKey);
            this.rowSearchConditionResultList.remove(targetRowIndex);
            this.rowSearchConditionResultList.add(targetRowIndex, getSearchConditions(rowDataDto.getData()));
        }
    }

    @Override
    public void shrink() {
        List<String> rowKeyToBeRemoved = Lists.newArrayListWithCapacity(rowSearchConditionResultList.size());
        for (int i = 0; i < this.rowSearchConditionResultList.size(); i++) {
            if (this.rowSearchConditionResultList.get(i).isEmpty()) {
                rowKeyToBeRemoved.add(this.getRowKeys().get(i));
            }
        }
        if (!rowKeyToBeRemoved.isEmpty()) {
            removeRows(rowKeyToBeRemoved);
        }
    }

    @Override
    public SearchDataFrame subDataFrame(List<String> rowKeyList, List<String> testItemNameList) {
        SearchDataFrame dataFrame = RuntimeContext.getBean(DataFrameFactory.class).createSearchDataFrame(super.subDataFrame(rowKeyList, testItemNameList));
        if (dataFrame instanceof NewBasicSearchDataFrame) {
            ((NewBasicSearchDataFrame) dataFrame).setFilterUtils(filterUtils);
            ((NewBasicSearchDataFrame) dataFrame).setSearchConditions(searchConditions);
            List<Set<String>> searchConditionRowList = Lists.newArrayListWithCapacity(rowSearchConditionResultList.size());
            for (String rowKey : rowKeyList) {
                Set<String> searchCondition = this.rowSearchConditionResultList.get(getRowKeys().indexOf(rowKey));
                searchConditionRowList.add(searchCondition);
            }
            ((NewBasicSearchDataFrame) dataFrame).setRowSearchConditionResultList(searchConditionRowList);
        }
        return dataFrame;
    }

    private void search(String searchCondition) {
        List<Boolean> searchedRowKeys = filterUtils.filterDF(searchCondition, this);
        for (int i = 0; i < searchedRowKeys.size(); i++) {
            if (searchedRowKeys.get(i)) {
                this.rowSearchConditionResultList.get(i).add(searchCondition);
            }
        }
    }

    private Set<String> getSearchConditions(Map<String, String> data) {
        Set<String> result = Sets.newLinkedHashSetWithExpectedSize(this.searchConditions.size());
        for (String searchCondition : this.searchConditions) {
            if (filterUtils.filterData(searchCondition, data)) {
                result.add(searchCondition);
            }
        }
        return result;
    }

    private void setRowSearchConditionResultList(List<Set<String>> rowSearchConditionResultList) {
        this.rowSearchConditionResultList = rowSearchConditionResultList;
    }

    private void setSearchConditions(Set<String> searchConditions) {
        this.searchConditions = searchConditions;
    }

    private void setFilterUtils(FilterUtils filterUtils) {
        this.filterUtils = filterUtils;
    }
}
