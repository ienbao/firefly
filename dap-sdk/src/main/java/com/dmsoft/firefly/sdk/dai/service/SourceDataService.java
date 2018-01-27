package com.dmsoft.firefly.sdk.dai.service;


import com.dmsoft.firefly.sdk.dai.dto.LineDataDto;
import com.dmsoft.firefly.sdk.dai.dto.ProjectDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;

import java.util.List;

/**
 * Created by GuangLi on 2018/1/24.
 */
public interface SourceDataService {

    void saveProject(ProjectDto projectDto);

    /**
     * save project message
     *
     * @param projectDtos project
     */
    void saveProject(List<ProjectDto> projectDtos);

    void saveTestItem(TestItemDto testItemDto);

    /**
     * save test item
     *
     * @param testItemDtos test item
     */
    void saveTestItem(List<TestItemDto> testItemDtos);

    /**
     * save project line data
     *
     * @param lineDataDtos line data
     */
    void saveProjectData(List<LineDataDto> lineDataDtos);

    /**
     * find all project
     *
     * @return project dto
     */
    List<ProjectDto> findAllProjects();

    /**
     * find all project names
     *
     * @return list of project name
     */
    List<String> findAllProjectNames();

    /**
     * find all test items by project names
     *
     * @param projectNames project names
     * @param templateName template names
     * @return list of item dto
     */
    List<TestItemDto> findTestItems(List<String> projectNames, String templateName);

    /**
     * find test item by item name
     *
     * @param itemName item name
     * @param templateName template names
     * @return item dto
     */
    TestItemDto findTestItemByItemName(String itemName, String templateName);

    /**
     * find all item names by project names
     *
     * @param projectNames project names
     * @return list of item name
     */
    List<String> findItemNames(List<String> projectNames);

    /**
     * find data by search condition
     *
     * @param projectNames project names
     * @param itemNames    item names
     * @param conditions   conditions
     * @param templateName template names
     * @return list of lineDataDto
     */
    List<LineDataDto> findDataByCondition(List<String> projectNames, List<String> itemNames, List<String> conditions, String templateName);

    /**
     * find data by line number
     *
     * @param projectName project name
     * @param lineNum     line number
     * @return line data
     */
    LineDataDto findDataByLineNum(String projectName, String lineNum);

    /**
     * update line data isUsed
     *
     * @param projectName peoject name
     * @param lineNum     line number
     * @param used        isUsed
     */
    void updateLineDataUsed(String projectName, String lineNum, Boolean used);

    /**
     * delete data by project name
     *
     * @param projectName project name
     */
    void deleteProject(String projectName);
}
