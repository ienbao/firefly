package com.dmsoft.firefly.core.dai;

import com.dmsoft.firefly.core.utils.FilterConditionUtil;
import com.dmsoft.firefly.core.utils.MongoUtil;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.ProjectDto;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.dto.TestDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.dmsoft.firefly.sdk.dai.entity.CellData;
import com.dmsoft.firefly.sdk.dai.entity.Project;
import com.dmsoft.firefly.sdk.dai.entity.TestData;
import com.dmsoft.firefly.sdk.dai.entity.TestItem;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;

import com.dmsoft.firefly.sdk.dai.service.TemplateService;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by Lucien.Chen on 2018/2/6.
 */
public class SourceDataServiceImpl implements SourceDataService {
    private TemplateService templateService = RuntimeContext.getBean(TemplateService.class);

    private List<TestData> testDataCache = Lists.newArrayList();

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
    public void saveOneProjectData(String projectName,TestDataDto testDataDto) {
        MongoCollection<TestData> mongoCollection = MongoUtil.getCollection(projectName, TestData.class);

        TestData testData = new TestData();
        BeanUtils.copyProperties(testDataDto, testData);
        mongoCollection.insertOne(testData);
    }

    @Override
    public void saveProjectData(String projectName,List<TestDataDto> testDataDtos) {
        MongoCollection<TestData> mongoCollection = MongoUtil.getCollection(projectName, TestData.class);
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
        basicDBObject.append("projectName", new BasicDBObject("$in", projectNames));
        MongoCursor<TestItem> mongoCursor = mongoCollection.find(basicDBObject).iterator();
        while (mongoCursor.hasNext()) {
            List<String> testItemNames = mongoCursor.next().getItemNames();
            testItemNames.removeAll(result);
            result.addAll(testItemNames);
        }
        return result;
    }

    @Override
    public List<TestItemDto> findItemNames(List<String> projectNames, String template) {
        return null;
    }

    @Override
    public List<TestDataDto> findDataByCondition(List<String> projectNames, List<String> itemNames, List<String> conditions, String templateName, Boolean lineUsedValid) {
        List<TestDataDto> result = Lists.newArrayList();
        FilterConditionUtil filterConditionUtil = new FilterConditionUtil();
        TemplateSettingDto templateSettingDto = templateService.findAnalysisTemplate(templateName);
        filterConditionUtil.setTimeKeys(templateSettingDto.getTimePatternDto().getTimeKeys());
        filterConditionUtil.setTimePattern(templateSettingDto.getTimePatternDto().getPattern());

        projectNames.forEach(projectName -> {
            List<TestDataDto> projectData = Lists.newArrayList();
            if (conditions == null || conditions.isEmpty()) {
                if (lineUsedValid) {
                    List<String> lineUsedList = findLineDataUsed(projectName);
                    List<TestDataDto> partData = findDataByItemNamesAndLineNo(projectName, itemNames, lineUsedList);
                    partData.forEach(testDataDto -> {
                        testDataDto.setCodition("");
                    });
                    projectData.addAll(partData);
                } else {
                    List<TestDataDto> partData = findDataByItemNames(projectName, itemNames);
                    partData.forEach(testDataDto -> {
                        testDataDto.setCodition("");
                    });
                    projectData.addAll(partData);
                }
            } else {
                conditions.forEach(condition -> {
                    List<String> conditionItems = Lists.newArrayList();
                    conditionItems.addAll(filterConditionUtil.parseItemNameFromConditions(condition));
                    List<TestDataDto> conditionData = findDataByItemNames(projectName, conditionItems);

                    List<String> lineNoList = filterConditionUtil.filterCondition(condition, conditionData);

                    if (lineUsedValid) {
                        List<String> lineUsedList = findLineDataUsed(projectName);
                        lineNoList.retainAll(lineUsedList);
                    }

                    List<String> searchItems = Lists.newArrayList();
                    searchItems.addAll(itemNames);
                    conditionItems.removeAll(searchItems);
                    searchItems.addAll(conditionItems);
                    List<TestDataDto> partData = findDataByItemNamesAndLineNo(projectName, searchItems, lineNoList);
                    partData.forEach(testDataDto -> {
                        testDataDto.setCodition(condition);
                    });
                    projectData.addAll(partData);
                });
                if (result != null && !result.isEmpty()) {
                    projectData.forEach(testDataDto -> {
                        Boolean isExist = false;
                        for (TestDataDto resultData : result) {
                            if (testDataDto.getCodition().equals(resultData.getCodition()) && testDataDto.getItemName().equals(resultData.getItemName())) {
                                resultData.getData().addAll(testDataDto.getData());
                                isExist = true;
                                break;
                            }
                        }
                        if(!isExist){
                            result.add(testDataDto);
                        }
                    });
                } else {
                    result.addAll(projectData);
                }

            }
        });

        return result;
    }

    @Override
    public List<TestDataDto> findDataByItemNames(String projectName, List<String> testItemNames) {
        MongoCollection<TestData> mongoCollection = MongoUtil.getCollection(projectName, TestData.class);
        List<TestDataDto> result = Lists.newArrayList();

        List<String> searchItemNames = Lists.newArrayList();
        testItemNames.forEach(itemName -> {
            TestData testData = findCache(projectName, itemName);
            if (testData != null) {
                TestDataDto testDataDto = new TestDataDto();
                BeanUtils.copyProperties(testData, testDataDto);
                List<CellData> cellDatas = Lists.newArrayList();
                cellDatas.addAll(testData.getData());
                testDataDto.setData(cellDatas);
                result.add(testDataDto);
            } else {
                searchItemNames.add(itemName);
            }
        });

        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.append("itemName", new BasicDBObject("$in", testItemNames));
        MongoCursor<TestData> mongoCursor = mongoCollection.find(basicDBObject).iterator();
        while (mongoCursor.hasNext()) {
            TestData testData = mongoCursor.next();
            addCache(testData);
            TestDataDto testDataDto = new TestDataDto();
            BeanUtils.copyProperties(testData, testDataDto);
            List<CellData> cellDatas = Lists.newArrayList();
            cellDatas.addAll(testData.getData());
            testDataDto.setData(cellDatas);
            result.add(testDataDto);
        }

        return result;
    }

    @Override
    public List<TestDataDto> findDataByItemNamesAndLineNo(String projectName, List<String> testItemNames, List<String> lineNo) {
        MongoCollection<TestData> mongoCollection = MongoUtil.getCollection(projectName, TestData.class);
        List<TestDataDto> result = Lists.newArrayList();

        List<String> searchItemNames = Lists.newArrayList();
        testItemNames.forEach(itemName -> {
            TestData testData = findCache(projectName, itemName);
            if (testData != null) {
                TestDataDto testDataDto = new TestDataDto();
                BeanUtils.copyProperties(testData, testDataDto);
                List<CellData> cellDatas = Lists.newArrayList();
                testData.getData().forEach(cellData -> {
                    if (lineNo.contains(cellData.getLineNo())) {
                        cellDatas.add(cellData);
                    }
                });
                testDataDto.setData(cellDatas);
                result.add(testDataDto);
            } else {
                searchItemNames.add(itemName);
            }
        });

        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.append("itemName", new BasicDBObject("$in", testItemNames));
        MongoCursor<TestData> mongoCursor = mongoCollection.find(basicDBObject).iterator();
        while (mongoCursor.hasNext()) {
            TestData testData = mongoCursor.next();
            addCache(testData);
            TestDataDto testDataDto = new TestDataDto();
            BeanUtils.copyProperties(testData, testDataDto);
            List<CellData> cellDatas = Lists.newArrayList();
            testData.getData().forEach(cellData -> {
                if (lineNo.contains(cellData.getLineNo())) {
                    cellDatas.add(cellData);
                }
            });
            testDataDto.setData(cellDatas);
            result.add(testDataDto);
        }
        return result;
    }


    @Override
    public void updateLineDataUsed(String projectName, List<CellData> lineUsedData) {
        MongoCollection<TestData> mongoCollection = MongoUtil.getCollection(projectName, TestData.class);
        BasicDBObject whereObject = new BasicDBObject("itemName", "lineNo");
        BasicDBObject setObject = new BasicDBObject("$set", new BasicDBObject("data", lineUsedData));
        mongoCollection.updateOne(whereObject, setObject);
    }

    @Override
    public List<String> findLineDataUsed(String projectName) {
        MongoCollection<TestData> mongoCollection = MongoUtil.getCollection(projectName, TestData.class);
        List<String> lineUsedList = Lists.newArrayList();
        BasicDBObject whereObject = new BasicDBObject("itemName", "lineNo");
        MongoCursor<TestData> mongoCursor = mongoCollection.find(whereObject).iterator();
        if (mongoCursor.hasNext()) {
            TestData testData = mongoCursor.next();
            testData.getData().forEach(cellData -> {
                if ((Boolean) cellData.getValue()) {
                    lineUsedList.add(cellData.getLineNo());
                }
            });
        }
        return null;
    }

    @Override
    public void deleteProject(String projectName) {
        MongoCollection projectCollection = MongoUtil.getCollection("project", Project.class);
        projectCollection.deleteOne(new BasicDBObject("projectName", projectName));

        MongoCollection<TestItem> testItemCollection = MongoUtil.getCollection("testItem", TestItem.class);
        testItemCollection.deleteOne(new BasicDBObject("projectName", projectName));

        MongoCollection<TestData> testDataCollection = MongoUtil.getCollection("TestData", TestItem.class);
        testDataCollection.drop();
    }

    private TestData findCache(String projectName, String itemName) {

        for (TestData testData : testDataCache) {
            if (testData.getProjectName().equals(projectName) && testData.getItemName().equals(itemName)) {
                return testData;
            }
        }
        return null;
    }

    private void addCache(TestData testData) {
        if (findCache(testData.getProjectName(), testData.getItemName()) == null) {
            if (testDataCache.size() <= 100) {
                testDataCache.add(testData);
            } else {
                testDataCache.remove(0);
                testDataCache.add(testData);
            }
        }
    }
}
