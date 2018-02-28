package com.dmsoft.firefly.sdk.dai.service;

import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * interface for source data service
 *
 * @author Can Guan
 */
public interface SourceDataService {

    /**
     * method to save project.
     *
     * @param projectName project name
     */
    void saveProject(String projectName);

    /**
     * method to save & update test item.
     *
     * @param projectName     project name
     * @param testItemDtoList list of test item dto
     */
    void saveTestItem(String projectName, List<TestItemDto> testItemDtoList);

    /**
     * method to save test data, will be appended with same row key.
     *
     * @param projectName    project name
     * @param rowDataDtoList list of row data dto
     */
    void saveTestData(String projectName, List<RowDataDto> rowDataDtoList);

    /**
     * method to save test data, will be append with same row key.
     *
     * @param projectName project name
     * @param rowKey      row key
     * @param rowData     row data
     */
    void saveTestData(String projectName, String rowKey, Map<String, String> rowData);

    /**
     * method to save test data, will be replaced with same row key.
     *
     * @param projectName project name
     * @param rowKey      row key
     * @param rowData     row data
     */
    void replaceTestData(String projectName, String rowKey, Map<String, String> rowData);

    /**
     * method to find all project name.
     *
     * @return list of project name
     */
    List<String> findAllProjectName();

    /**
     * method to judge project exist or not by project name.
     *
     * @param projectName project name
     * @return true : exist, false : not exist
     */
    boolean isProjectExist(String projectName);

    /**
     * method to judge test item exist or not by project name and test item name.
     *
     * @param projectName  project name
     * @param testItemName test item name
     * @return true : exist, false : not exist
     */
    boolean isTestItemExist(String projectName, String testItemName);

    /**
     * method to judge row key exist or not by project name and test item name.
     *
     * @param rowKey row key
     * @return is row key exist or not
     */
    boolean isRowKeyExist(String rowKey);

    /**
     * method to find all test item names by project name.
     *
     * @param projectNameList list of project name
     * @return list of test item names
     */
    List<String> findAllTestItemName(List<String> projectNameList);

    /**
     * method to find all test item.
     *
     * @param projectNameList list of project names
     * @return list of test item dtos
     */
    Map<String, TestItemDto> findAllTestItem(List<String> projectNameList);

    /**
     * method to find test item by project name list and test item name.
     *
     * @param projectNameList list of project name
     * @param testItemName    test item name
     * @return test item dto
     */
    TestItemDto findTestItem(List<String> projectNameList, String testItemName);

    /**
     * method to find test data by project name and test item name.
     *
     * @param projectNameList  project name list
     * @param testItemNameList test item name list
     * @return project data
     */
    List<RowDataDto> findTestData(List<String> projectNameList, List<String> testItemNameList);

    /**
     * method to find unique test data by project name and test item name
     *
     * @param projectNameList project name list
     * @param testItemName    test item name
     * @return string list
     */
    Set<String> findUniqueTestData(List<String> projectNameList, String testItemName);

    /**
     * method to find test data by project name and test item name.
     *
     * @param projectNameList  project name list
     * @param testItemNameList test item name list
     * @param inUsedFlag       in used or not or all(null)
     * @return project data
     */
    List<RowDataDto> findTestData(List<String> projectNameList, List<String> testItemNameList, Boolean inUsedFlag);

    /**
     * method to find project data by row key.
     *
     * @param rowKey row key
     * @return row data
     */
    RowDataDto findTestData(String rowKey);

    /**
     * method to chang row data in used status by row key.
     *
     * @param rowKeyList list of row key
     * @param inUsed     in use status
     */
    void changeRowDataInUsed(List<String> rowKeyList, boolean inUsed);

    /**
     * method to delete project by project name list.
     *
     * @param projectNameList project name list
     */
    void deleteProject(List<String> projectNameList);
}
