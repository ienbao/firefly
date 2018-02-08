package com.dmsoft.firefly.sdk.dai.service;


import com.dmsoft.firefly.sdk.dai.dto.LineDataDto;
import com.dmsoft.firefly.sdk.dai.dto.ProjectDto;
import com.dmsoft.firefly.sdk.dai.dto.TestDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.dmsoft.firefly.sdk.dai.entity.CellData;

import java.util.List;

/**
 * Created by GuangLi on 2018/1/24.
 */
public interface SourceDataService {

    /**
     * method to save project
     *
     * @param projectDto project dto
     */
    void saveProject(ProjectDto projectDto);

    /**
     * save project message
     *
     * @param projectDtos project
     */
    void saveProject(List<ProjectDto> projectDtos);
//
//    /**
//     * method to save test item
//     *
//     * @param testItemDto test item dto
//     */
//    void saveTestItem(TestItemDto testItemDto);

    /**
     * save test item
     *
     * @param projectName   project name
     * @param testItemNames testItem names
     */
    void saveTestItem(String projectName, List<String> testItemNames);

    /**
     * save project line data
     *
     * @param testDataDto TestDataDto
     */
    void saveOneProjectData(TestDataDto testDataDto);

    /**
     * save project line data
     *
     * @param testDataDtos TestDataDto
     */
    void saveProjectData(List<TestDataDto> testDataDtos);

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
     * find all item names by project names
     *
     * @param projectNames project names
     * @return list of item name
     */
    List<String> findItemNames(List<String> projectNames);

    /**
     * find all item names by project names
     *
     * @param projectNames project names
     * @param template     template names
     * @return list of TestItemDto
     */
    List<TestItemDto> findItemNames(List<String> projectNames, String template);

    /**
     * find data by search condition
     *
     * @param projectNames project names
     * @param itemNames    item names
     * @param conditions   conditions
     * @param templateName template names
     * @return list of lineDataDto
     */
    List<TestDataDto> findDataByCondition(List<String> projectNames, List<String> itemNames, List<String> conditions, String templateName);

    /**
     * find data by line number
     *
     * @param projectName  project name
     * @param testItemName test item name
     * @return item datas
     */
    List<TestDataDto> findDataByItemName(String projectName, List<String> testItemName);


    /**
     * find data by line number
     *
     * @param projectName   project name
     * @param testItemNames test item name
     * @return line data
     */
    List<TestDataDto> findDataByItemNamesAndLineNo(String projectName, List<String> testItemNames, List<String> lineNo);

    /**
     * update line data isUsed
     *
     * @param projectName  peoject name
     * @param lineUsedData lineUsed data
     */
    void updateLineDataUsed(String projectName, List<CellData> lineUsedData);

    /**
     * delete data by project name
     *
     * @param projectName project name
     */
    void deleteProject(String projectName);
}
