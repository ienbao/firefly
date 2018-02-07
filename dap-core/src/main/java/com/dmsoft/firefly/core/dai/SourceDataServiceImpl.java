package com.dmsoft.firefly.core.dai;

import com.dmsoft.bamboo.common.utils.mapper.BeanMapper;
import com.dmsoft.firefly.core.utils.MongoUtil;
import com.dmsoft.firefly.sdk.dai.dto.ProjectDto;
import com.dmsoft.firefly.sdk.dai.dto.TestDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.dmsoft.firefly.sdk.dai.entity.CellData;
import com.dmsoft.firefly.sdk.dai.entity.Project;
import com.dmsoft.firefly.sdk.dai.entity.TestData;
import com.dmsoft.firefly.sdk.dai.entity.TestItem;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * Created by Lucien.Chen on 2018/2/6.
 */
public class SourceDataServiceImpl implements SourceDataService {

    @Override
    public void saveProject(ProjectDto projectDto) {
        MongoCollection mongoCollection = MongoUtil.getCollection("project", Project.class);
        Project project = new Project();
        project.setProjectName(projectDto.getProjectName());
        project.setPath(projectDto.getPath());
        mongoCollection.insertOne(project);
    }

    @Override
    public void saveProject(List<ProjectDto> projectDtos) {

        MongoCollection mongoCollection = MongoUtil.getCollection("project", Project.class);
        projectDtos.forEach(projectDto -> {
            Project project = new Project();
            project.setProjectName(projectDto.getProjectName());
            project.setPath(projectDto.getPath());
            mongoCollection.insertOne(project);
        });
    }

    @Override
    public void saveTestItem(String projectName, List<String> testItemNames) {

        MongoCollection<TestItem> mongoCollection = MongoUtil.getCollection("testItem", TestItem.class);
        TestItem testItem = new TestItem();
        testItem.setProjectName(projectName);
        testItem.setItemNames(testItemNames);
        mongoCollection.insertOne(testItem);
    }

    @Override
    public void saveOneProjectData(TestDataDto testDataDto) {
        MongoCollection<TestData> mongoCollection = MongoUtil.getCollection("testData", TestData.class);

        TestData testData = new TestData();
        BeanUtils.copyProperties(testDataDto, testData);
        mongoCollection.insertOne(testData);
    }

    @Override
    public void saveProjectData(List<TestDataDto> testDataDtos) {
        MongoCollection<TestData> mongoCollection = MongoUtil.getCollection("testData", TestData.class);
        testDataDtos.forEach(testDataDto -> {
            TestData testData = new TestData();
            BeanUtils.copyProperties(testDataDto, testData);
            mongoCollection.insertOne(testData);
        });
    }

    @Override
    public List<ProjectDto> findAllProjects() {
        MongoCollection mongoCollection = MongoUtil.getCollection("project", Project.class);
        List<ProjectDto> result = Lists.newArrayList();
        MongoCursor<Project> mongoCursor = mongoCollection.find().iterator();
        while (mongoCursor.hasNext()) {
            ProjectDto projectDto = new ProjectDto();
            Project project = mongoCursor.next();
            BeanUtils.copyProperties(project, projectDto);
            result.add(projectDto);
        }

        return result;
    }

    @Override
    public List<String> findAllProjectNames() {
        MongoCollection mongoCollection = MongoUtil.getCollection("project", Project.class);
        List<String> result = Lists.newArrayList();
        MongoCursor<Project> mongoCursor = mongoCollection.find().iterator();
        while (mongoCursor.hasNext()) {
            result.add(mongoCursor.next().getProjectName());
        }
        ;
        return null;
    }

    @Override
    public List<String> findItemNames(List<String> projectNames) {
        MongoCollection<TestItem> mongoCollection = MongoUtil.getCollection("testItem", TestItem.class);
        List<String> result = Lists.newArrayList();

        BasicDBObject basicDBObject = new BasicDBObject();
//        BasicDBList values = new BasicDBList();
//        values.addAll(projectNames);
        basicDBObject.append("itemName", new BasicDBObject("$in", projectNames));
        MongoCursor<TestItem> mongoCursor = mongoCollection.find(basicDBObject).iterator();
        while (mongoCursor.hasNext()) {
            List<String>  testItemNames =  mongoCursor.next().getItemNames();
            testItemNames.removeAll(result);
            result.addAll(testItemNames);
        }
        return result;
    }

    @Override
    public List<TestDataDto> findDataByCondition(List<String> projectNames, List<String> itemNames, List<String> conditions, String templateName) {
        return null;
    }

    @Override
    public List<TestDataDto> findDataByItemName(String projectName, List<String> testItemNames) {
        return null;
    }

    @Override
    public List<TestDataDto> findDataByItemNamesAndLineNo(String projectName, List<String> testItemName, List<String> LineNo) {
        return null;
    }


    @Override
    public void updateLineDataUsed(String projectName, List<CellData> lineUsedData) {

    }

    @Override
    public void deleteProject(String projectName) {

    }
}
