package com.dmsoft.firefly.core.sdkimpl.dataframe;

import com.dmsoft.firefly.core.utils.FilterUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dataframe.DataColumn;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
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
public class BasicSearchDataFrame extends BasicDataFrame implements SearchDataFrame {
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
    BasicSearchDataFrame(List<TestItemWithTypeDto> testItemDtoList, List<RowDataDto> rowDataDtoList) {
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

    @Override
    public DataColumn getDataColumn(String testItemName, String searchCondition) {
        if (isTestItemExist(testItemName)) {
            TestItemWithTypeDto testItemDto = getTestItemWithTypeDto(testItemName);
            List<String> rowKeyList = getSearchRowKey(searchCondition);
            List<String> valueList = getDataValue(testItemName, rowKeyList);
            List<Boolean> inUsed = Lists.newArrayList();
            for (String rowKey : rowKeyList) {
                inUsed.add(isInUsed(rowKey));
            }
            return new BasicDataColumn(testItemDto, rowKeyList, valueList, inUsed);
        }
        return null;
    }

    @Override
    public List<DataColumn> getDataColumn(List<String> testItemNames, String searchCondition) {
        List<DataColumn> dataColumns = Lists.newArrayList();
        for (String testItemName : testItemNames) {
            dataColumns.add(getDataColumn(testItemName, searchCondition));
        }
        return dataColumns;
    }

    @Override
    public List<RowDataDto> getDataRowArray(String searchCondition) {
        if (!this.searchConditions.contains(searchCondition) && filterUtils.isLegal(searchCondition)) {
            search(searchCondition);
        }
        List<RowDataDto> result = Lists.newArrayList();
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
            if (filterUtils.isLegal(s) && !this.searchConditions.contains(s)) {
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
        List<String> result = Lists.newArrayList();
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
        List<String> result = Lists.newArrayList();
        for (int i = 0; i < this.rowSearchConditionResultList.size(); i++) {
            if (this.rowSearchConditionResultList.get(i).contains(searchCondition)) {
                result.add(this.getRowKeys().get(i));
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
        List<String> rowKeyToBeRemoved = Lists.newArrayList();
        for (int i = 0; i < this.rowSearchConditionResultList.size(); i++) {
            if (this.rowSearchConditionResultList.get(i).isEmpty()) {
                rowKeyToBeRemoved.add(this.getRowKeys().get(i));
            }
        }
        if (!rowKeyToBeRemoved.isEmpty()) {
            removeRows(rowKeyToBeRemoved);
        }
    }


    private void search(String searchCondition) {
        for (int i = 0; i < this.getRowSize(); i++) {
            if (filterUtils.filterData(searchCondition, getDataMap(this.getRowKeys().get(i)))) {
                this.rowSearchConditionResultList.get(i).add(searchCondition);
            }
        }
    }

    private Set<String> getSearchConditions(Map<String, String> data) {
        Set<String> result = Sets.newLinkedHashSet();
        for (String searchCondition : this.searchConditions) {
            if (filterUtils.filterData(searchCondition, data)) {
                result.add(searchCondition);
            }
        }
        return result;
    }
}
