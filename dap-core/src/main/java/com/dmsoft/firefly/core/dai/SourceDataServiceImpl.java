package com.dmsoft.firefly.core.dai;

import com.dmsoft.firefly.sdk.dai.dto.ProjectDto;
import com.dmsoft.firefly.sdk.dai.dto.TestDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.dmsoft.firefly.sdk.dai.entity.CellData;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;

import java.util.List;

/**
 * Created by Lucien.Chen on 2018/2/6.
 */
public class SourceDataServiceImpl implements SourceDataService{

    @Override
    public void saveProject(ProjectDto projectDto) {

    }

    @Override
    public void saveProject(List<ProjectDto> projectDtos) {

    }

    @Override
    public void saveTestItem(String projectName, List<String> testItemNames) {

    }

    @Override
    public void saveProjectData(List<TestDataDto> testDataDtos) {

    }

    @Override
    public List<ProjectDto> findAllProjects() {
        return null;
    }

    @Override
    public List<String> findAllProjectNames() {
        return null;
    }

    @Override
    public List<TestItemDto> findTestItems(List<String> projectNames, String templateName) {
        return null;
    }

    @Override
    public TestItemDto findTestItemByItemName(String itemName, String templateName) {
        return null;
    }

    @Override
    public List<String> findItemNames(List<String> projectNames) {
        return null;
    }

    @Override
    public List<TestDataDto> findDataByCondition(List<String> projectNames, List<String> itemNames, List<String> conditions, String templateName) {
        return null;
    }

    @Override
    public TestDataDto findDataByItemName(String projectName, String testItemName) {
        return null;
    }

    @Override
    public TestDataDto findDataByItemNameAndLineNo(String projectName, String testItemName, List<String> LineNo) {
        return null;
    }

    @Override
    public void updateLineDataUsed(String projectName, List<CellData> lineUsedData) {

    }

    @Override
    public void deleteProject(String projectName) {

    }
}
