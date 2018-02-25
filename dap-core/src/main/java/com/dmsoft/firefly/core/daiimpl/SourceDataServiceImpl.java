package com.dmsoft.firefly.core.daiimpl;

import com.dmsoft.firefly.core.utils.FilterConditionUtil;
import com.dmsoft.firefly.core.utils.MongoUtil;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.*;
import com.dmsoft.firefly.sdk.dai.entity.CellData;
import com.dmsoft.firefly.sdk.dai.entity.Project;
import com.dmsoft.firefly.sdk.dai.entity.TestData;
import com.dmsoft.firefly.sdk.dai.entity.TestItem;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dai.service.TemplateService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Lucien.Chen on 2018/2/6.
 */
public class SourceDataServiceImpl implements SourceDataService {
    private Logger logger = LoggerFactory.getLogger(SourceDataServiceImpl.class);

    private TemplateService templateService = RuntimeContext.getBean(TemplateService.class);

    private List<TestData> testDataCache = Lists.newArrayList();

    @Override
    public void saveProject(ProjectDto projectDto) {
        MongoCollection<Project> mongoCollection = MongoUtil.getCollection("project", Project.class);
        Project project = new Project();
        project.setProjectName(projectDto.getProjectName());
        project.setPath(projectDto.getPath());
        mongoCollection.insertOne(project);
    }

    @Override
    public void saveProject(List<ProjectDto> projectDtos) {

        MongoCollection<Project> mongoCollection = MongoUtil.getCollection("project", Project.class);
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
    public void saveOneProjectData(String projectName, TestDataDto testDataDto) {
        MongoCollection<TestData> mongoCollection = MongoUtil.getCollection(projectName, TestData.class);

        TestData testData = new TestData();
        BeanUtils.copyProperties(testDataDto, testData);
        testData.setProjectName(projectName);
        mongoCollection.insertOne(testData);
    }

    @Override
    public void saveProjectData(String projectName, List<TestDataDto> testDataDtos) {
        MongoCollection<TestData> mongoCollection = MongoUtil.getCollection(projectName, TestData.class);
        testDataDtos.forEach(testDataDto -> {
            TestData testData = new TestData();
            BeanUtils.copyProperties(testDataDto, testData);
            testData.setProjectName(projectName);
            mongoCollection.insertOne(testData);
        });
    }

    @Override
    public List<ProjectDto> findAllProjects() {
        MongoCollection<Project> mongoCollection = MongoUtil.getCollection("project", Project.class);
        List<ProjectDto> result = Lists.newArrayList();
        MongoCursor<Project> mongoCursor = mongoCollection.find().iterator();
        mongoCursor.forEachRemaining(m -> {
            ProjectDto projectDto = new ProjectDto();
            BeanUtils.copyProperties(m, projectDto);
            result.add(projectDto);
        });
        return result;
    }

    @Override
    public List<String> findAllProjectNames() {
        MongoCollection<Project> mongoCollection = MongoUtil.getCollection("project", Project.class);
        List<String> result = Lists.newArrayList();
        MongoCursor<Project> mongoCursor = mongoCollection.find().iterator();
        mongoCursor.forEachRemaining(m -> result.add(m.getProjectName()));
        return result;
    }

    @Override
    public List<String> findItemNames(List<String> projectNames) {
        MongoCollection<TestItem> mongoCollection = MongoUtil.getCollection("testItem", TestItem.class);
        Set<String> result = Sets.newLinkedHashSet();

        BasicDBObject basicDBObject = new BasicDBObject();
//        BasicDBList values = new BasicDBList();
//        values.addAll(projectNames);
        basicDBObject.append("projectName", new BasicDBObject("$in", projectNames));
        MongoCursor<TestItem> mongoCursor = mongoCollection.find(basicDBObject).iterator();
        mongoCursor.forEachRemaining(m -> result.addAll(m.getItemNames()));
        while (mongoCursor.hasNext()) {
            List<String> testItemNames = mongoCursor.next().getItemNames();
            testItemNames.removeAll(result);
            result.addAll(testItemNames);
        }
        return Lists.newArrayList(result);
    }

    @Override
    public List<TestItemDto> findItems(List<String> projectNames, String template) {
        List<TestItemDto> result = Lists.newArrayList();
        TemplateSettingDto templateSettingDto = templateService.findAnalysisTemplate(template);

        projectNames.forEach(projectName -> {
            MongoCollection<TestData> mongoCollection = MongoUtil.getCollection(projectName, TestData.class);
            BasicDBObject basicDBObject = new BasicDBObject();
            basicDBObject.append("data", 0);
            MongoCursor<TestData> mongoCursor = mongoCollection.find().projection(basicDBObject).iterator();
            mongoCursor.forEachRemaining(m -> {
                Boolean isExit = Boolean.FALSE;
                for (TestItemDto testItemDto : result) {
                    if (testItemDto.getItemName().equals(m.getItemName())) {
                        isExit = true;
                        break;
                    }
                }
                if (!isExit) {
                    TestItemDto testItemDto = new TestItemDto();
                    BeanUtils.copyProperties(m, testItemDto);
                    testItemDto.setNumeric(Boolean.TRUE);
                    result.add(testItemDto);
                }
            });
        });
        LinkedHashMap<String, SpecificationDataDto> map = templateSettingDto.getSpecificationDatas();
        for (Map.Entry<String, SpecificationDataDto> entry : map.entrySet()) {
            String itemName = entry.getValue().getTestItemName();
            for (TestItemDto testItemDto : result) {
                if (testItemDto.getItemName().equals(itemName)) {
                    testItemDto.setLsl(entry.getValue().getLslFail());
                    testItemDto.setUsl(entry.getValue().getUslPass());
                    //如果数据类型为A,就将isNumeric设置为False
                    testItemDto.setNumeric(!"Attribute".equals(entry.getValue().getDataType()));
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public List<TestDataDto> findDataByCondition(List<String> projectNames, List<String> itemNames, List<String> conditions, String templateName, Boolean lineUsedValid) {
        List<TestDataDto> result = Lists.newArrayList();
        FilterConditionUtil filterConditionUtil = new FilterConditionUtil();
        TemplateSettingDto templateSettingDto = templateService.findAnalysisTemplate(templateName);
        filterConditionUtil.setTimeKeys(templateSettingDto.getTimePatternDto().getTimeKeys());
        filterConditionUtil.setTimePattern(templateSettingDto.getTimePatternDto().getPattern());

        List<String> searchItems = Lists.newArrayList();
        searchItems.addAll(itemNames);
        if (conditions != null && !conditions.isEmpty()) {
            conditions.forEach(condition -> {
                java.util.Set<String> items = filterConditionUtil.parseItemNameFromConditions(condition);
                items.removeAll(searchItems);
                searchItems.addAll(items);
            });
        }
        searchItems.forEach(itemName -> {
            if (conditions != null && !conditions.isEmpty()) {
                conditions.forEach(condition -> {
                    TestDataDto testDataDto = new TestDataDto();
                    testDataDto.setItemName(itemName);
                    testDataDto.setCondition(condition);
                    result.add(testDataDto);
                });
            } else {
                TestDataDto testDataDto = new TestDataDto();
                testDataDto.setItemName(itemName);
                testDataDto.setCondition("");
                result.add(testDataDto);
            }
        });

        projectNames.forEach(projectName -> {
            if (conditions == null || conditions.isEmpty()) {
                if (lineUsedValid) {
                    List<String> lineUsedList = findLineDataUsed(projectName);
                    List<TestDataDto> partData = findDataByItemNamesAndRowKey(projectName, searchItems, lineUsedList);
                    partData.forEach(testDataDto -> {
                        testDataDto.setCondition("");
                    });
                    addPartToResult(partData, result, lineUsedList.size());
                } else {
                    List<TestDataDto> partData = findDataByItemNames(projectName, searchItems);
                    partData.forEach(testDataDto -> {
                        testDataDto.setCondition("");
                    });
                    addPartToResult(partData, result, partData.get(0).getData().size());
                }
            } else {
                conditions.forEach(condition -> {
                    List<String> conditionItems = Lists.newArrayList();
                    conditionItems.addAll(filterConditionUtil.parseItemNameFromConditions(condition));
                    List<TestDataDto> conditionData = findDataByItemNames(projectName, conditionItems);

                    List<String> lineRowKeys = filterConditionUtil.filterCondition(condition, conditionData);

                    if (lineUsedValid) {
                        List<String> lineUsedList = findLineDataUsed(projectName);
                        lineRowKeys.retainAll(lineUsedList);
                    }

                    List<TestDataDto> partData = findDataByItemNamesAndRowKey(projectName, searchItems, lineRowKeys);
                    partData.forEach(testDataDto -> {
                        testDataDto.setCondition(condition);
                    });
                    addPartToResult(partData, result, lineRowKeys.size());
                });
            }
            limitCache();
        });

//        LinkedHashMap<String, SpecificationDataDto> map = templateSettingDto.getSpecificationDatas();
//        result.forEach(testDataDto -> {
//            for (Map.Entry<String, SpecificationDataDto> entry : map.entrySet()) {
//                String itemName = entry.getValue().getTestItemName();
//                if (testDataDto.getItemName().equals(itemName)) {
//                    testDataDto.setLsl(entry.getValue().getLslFail());
//                    testDataDto.setUsl(entry.getValue().getUslPass());
//                    break;
//                }
//            }
//        });

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

        List<List<String>> searchItemNamesList = Lists.newArrayList();
        if (searchItemNames.size() > 1000) {
            List<String> itemNames = null;
            for (int index = 0; index < searchItemNames.size(); index++) {
                if (index % 1000 == 0) {
                    itemNames = Lists.newArrayList();
                    itemNames.add(searchItemNames.get(index));
                    searchItemNamesList.add(itemNames);
                } else {
                    itemNames.add(searchItemNames.get(index));
                }
            }
        } else {
            searchItemNamesList.add(searchItemNames);
        }

        //创建线程池
        ExecutorService pool = Executors.newFixedThreadPool(5);
        // 创建多个有返回值的任务
        List<Future> futureList = new ArrayList<Future>();
        searchItemNamesList.forEach(itemNameList -> {
            Callable callable = new Callable() {
                @Override
                public Object call() throws Exception {
                    List<TestDataDto> dataList = Lists.newArrayList();
                    BasicDBObject basicDBObject = new BasicDBObject();
                    basicDBObject.append("itemName", new BasicDBObject("$in", itemNameList));
                    MongoCursor<TestData> mongoCursor = mongoCollection.find(basicDBObject).iterator();
                    while (mongoCursor.hasNext()) {

                        TestData testData = mongoCursor.next();
                        addCache(testData);
                        TestDataDto testDataDto = new TestDataDto();
                        BeanUtils.copyProperties(testData, testDataDto);
                        List<CellData> cellDatas = Lists.newArrayList();
                        cellDatas.addAll(testData.getData());
                        testDataDto.setData(cellDatas);
                        dataList.add(testDataDto);
                    }
                    return result;
                }
            };

            Future future = pool.submit(callable);
            futureList.add(future);
        });
        //等线程结束后关闭线程池
        pool.shutdown();
        for (int i = 0; i < futureList.size(); i++) {
            try {
                result.addAll((List<TestDataDto>) (futureList.get(i).get()));
            } catch (Exception e) {
                logger.debug("Search error");
            }
        }

        return result;

    }

    @Override
    public List<TestDataDto> findDataByItemNamesAndRowKey(String projectName, List<String> testItemNames, List<String> rowKeys) {
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
                    if (rowKeys.contains(cellData.getRowKey())) {
                        cellDatas.add(cellData);
                    }
                });
                testDataDto.setData(cellDatas);
                result.add(testDataDto);
            } else {
                searchItemNames.add(itemName);
            }
        });

        List<List<String>> searchItemNamesList = Lists.newArrayList();
        if (searchItemNames.size() > 1000) {
            List<String> itemNames = null;
            for (int index = 0; index < searchItemNames.size(); index++) {
                if (index % 1000 == 0) {
                    itemNames = Lists.newArrayList();
                    itemNames.add(searchItemNames.get(index));
                    searchItemNamesList.add(itemNames);
                } else {
                    itemNames.add(searchItemNames.get(index));
                }
            }
        } else {
            searchItemNamesList.add(searchItemNames);
        }

        //创建线程池
        ExecutorService pool = Executors.newFixedThreadPool(5);
        // 创建多个有返回值的任务
        List<Future> futureList = new ArrayList<Future>();
        searchItemNamesList.forEach(itemNameList -> {
            Callable callable = new Callable() {
                @Override
                public Object call() throws Exception {
                    List<TestDataDto> dataList = Lists.newArrayList();
                    BasicDBObject basicDBObject = new BasicDBObject();
                    basicDBObject.append("itemName", new BasicDBObject("$in", itemNameList));
                    MongoCursor<TestData> mongoCursor = mongoCollection.find(basicDBObject).iterator();
                    while (mongoCursor.hasNext()) {

                        TestData testData = mongoCursor.next();
                        addCache(testData);
                        TestDataDto testDataDto = new TestDataDto();
                        BeanUtils.copyProperties(testData, testDataDto);
                        List<CellData> cellDatas = Lists.newArrayList();
                        testData.getData().forEach(cellData -> {
                            if (rowKeys.contains(cellData.getRowKey())) {
                                cellDatas.add(cellData);
                            }
                        });
                        testDataDto.setData(cellDatas);
                        dataList.add(testDataDto);
                    }
                    return result;
                }
            };

            Future future = pool.submit(callable);
            futureList.add(future);
        });
        //等线程结束后关闭线程池
        pool.shutdown();
        for (int i = 0; i < futureList.size(); i++) {
            try {
                result.addAll((List<TestDataDto>) (futureList.get(i).get()));
            } catch (Exception e) {
                logger.debug("Search error");
            }
        }

        return result;
    }


    @Override
    public void updateLineDataUsed(String projectName, List<CellData> lineUsedData) {
        MongoCollection<TestData> mongoCollection = MongoUtil.getCollection(projectName, TestData.class);
        BasicDBObject whereObject = new BasicDBObject("itemName", "rowKey");
        BasicDBObject setObject = new BasicDBObject("$set", new BasicDBObject("data", lineUsedData));
        mongoCollection.updateOne(whereObject, setObject);
    }

    @Override
    public List<String> findLineDataUsed(String projectName) {
        MongoCollection<TestData> mongoCollection = MongoUtil.getCollection(projectName, TestData.class);
        List<String> lineUsedList = Lists.newArrayList();
        BasicDBObject whereObject = new BasicDBObject("itemName", "rowKey");
        MongoCursor<TestData> mongoCursor = mongoCollection.find(whereObject).iterator();
        if (mongoCursor.hasNext()) {
            TestData testData = mongoCursor.next();
            testData.getData().forEach(cellData -> {
                if ((Boolean) cellData.getValue()) {
                    lineUsedList.add(cellData.getRowKey());
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

        MongoCollection<TestData> testDataCollection = MongoUtil.getCollection("TestData", TestData.class);
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
//        if (testDataCache.size() <= 100) {
        testDataCache.add(testData);
//        } else {
//            testDataCache.remove(0);
//            testDataCache.add(testData);
//        }
    }

    private void limitCache() {
        while (testDataCache.size() > 100) {
            testDataCache.remove(0);
        }
    }

    private void addPartToResult(List<TestDataDto> part, List<TestDataDto> result, int size) {
        result.forEach(resultDto -> {
            Boolean isExist = Boolean.FALSE;
            for (TestDataDto dto : part) {
                if (resultDto.getItemName().equals(dto.getItemName()) && resultDto.getCondition().equals(dto.getCondition())) {
                    resultDto.getData().addAll(dto.getData());
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                for (int i = 0; i < size; i++) {
                    resultDto.getData().add(null);
                }
            }
        });
    }
}
