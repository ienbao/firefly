package com.dmsoft.firefly.core.sdkimpl.dai;

import com.dmsoft.firefly.core.sdkimpl.dai.entity.Project;
import com.dmsoft.firefly.core.sdkimpl.dai.entity.RowData;
import com.dmsoft.firefly.core.sdkimpl.dai.entity.TestItem;
import com.dmsoft.firefly.core.utils.CoreExceptionCode;
import com.dmsoft.firefly.core.utils.CoreExceptionParser;
import com.dmsoft.firefly.core.utils.DoubleIdUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * impl class for new source data service
 *
 * @author Can Guan
 */
public class SourceDataServiceImpl implements SourceDataService {
    private static final String PROJECT_COLLECTION_NAME = "project";
    private static final String PROJECT_NAME_FIELD = "projectName";
    private static final String TEST_ITEM_FIELD = "testItems";
    private static final String TEST_ITEM_NAME_FIELD = "testItemName";
    private static final String ROW_KEY_FIELD = "rowKey";
    private static final String IN_USED_FIELD = "inUsed";
    private static final String DATA_FIELD = "data";
    private final Logger logger = LoggerFactory.getLogger(SourceDataServiceImpl.class);

    @Override
    public void saveProject(String projectName) {
        if (!isProjectExist(projectName)) {
            Project project = new Project();
            project.setProjectName(projectName);
            try {
                logger.debug("Saving project = {} ...", projectName);
                getMongoTemplate().save(project, PROJECT_COLLECTION_NAME);
                logger.info("Save project = {} done.", projectName);
            } catch (Exception e) {
                logger.error("Save project = {} error! Exception = {}", projectName, e.getMessage());
                throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_20001), e);
            }
        } else {
            logger.error("Save project = {} error! Exception = {}", "Project already exist!");
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_11001));
        }
    }

    @Override
    public void saveTestItem(String projectName, List<TestItemDto> testItemDtoList) {
        if (testItemDtoList == null) {
            return;
        }
        try {
            Project project = getMongoTemplate().findOne(new Query(where(PROJECT_NAME_FIELD).is(projectName)), Project.class);
            //project may be null, do not believe idea
            if (project == null) {
                logger.error("Save test item in project = {} error! Exception = {}", projectName, "Project doesn't exist!");
                throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_11002));
            }
            if (project.getTestItems() == null) {
                project.setTestItems(Maps.newLinkedHashMap());
            }
            for (TestItemDto testItemDto : testItemDtoList) {
                if (StringUtils.isNotBlank(testItemDto.getTestItemName())) {
                    TestItem testItem = new TestItem();
                    BeanUtils.copyProperties(testItemDto, testItem);
                    project.getTestItems().put(testItem.getTestItemName(), testItem);
                }
            }
            logger.debug("Saving test item in project = {} ...", projectName);
            getMongoTemplate().save(project, PROJECT_COLLECTION_NAME);
            logger.info("Save test item project = {} done.", projectName);
        } catch (ApplicationException ex) {
            throw ex;
        } catch (Exception e) {
            logger.error("Save test item in project = {} error! Exception = {}", projectName, e.getMessage());
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_20001), e);
        }
    }

    @Override
    public void saveTestData(String projectName, List<RowDataDto> rowDataDtoList) {
        if (isProjectExist(projectName)) {
            try {
                logger.debug("Saving test data in project = {} ...", projectName);
                for (RowDataDto rowDataDto : rowDataDtoList) {
                    privateSaveTestData(projectName, rowDataDto.getRowKey(), rowDataDto.getData(), true);
                }
                logger.info("Save test data project = {} done.", projectName);
            } catch (Exception e) {
                logger.error("Save test data in project = {} error! Exception = {}", projectName, e.getMessage());
                throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_20001), e);
            }
        } else {
            logger.error("Save test data in project = {} error! Exception = {}", projectName, "Project doesn't exist!");
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_11002));
        }
    }

    @Override
    public void saveTestData(String projectName, String rowKey, Map<String, String> rowData) {
        if (isProjectExist(projectName)) {
            try {
                logger.debug("Saving test data (rowKey = {}) in project = {} ...", rowKey, projectName);
                privateSaveTestData(projectName, rowKey, rowData, true);
                logger.info("Save test data (rowKey = {}) in project = {} done.", rowKey, projectName);
            } catch (Exception e) {
                logger.error("Save test data (rowKey = {}) in project = {} error! Exception = {}", rowKey, projectName, e.getMessage());
                throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_20001), e);
            }
        } else {
            logger.error("Save test data (rowKey = {}) in project = {} error! Exception = {}", rowKey, projectName, "Project doesn't exist!");
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_11002));
        }
    }

    @Override
    public void replaceTestData(String projectName, String rowKey, Map<String, String> rowData) {
        if (isProjectExist(projectName)) {
            try {
                logger.debug("Appending test data (rowKey = {}) in project = {} ...", rowKey, projectName);
                privateSaveTestData(projectName, rowKey, rowData, false);
                logger.info("Append test data (rowKey = {}) in project = {} done.", rowKey, projectName);
            } catch (Exception e) {
                logger.error("Append test data (rowKey = {}) in project = {} error! Exception = {}", rowKey, projectName, e.getMessage());
                throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_20001), e);
            }
        } else {
            logger.error("Append test data (rowKey = {}) in project = {} error! Exception = {}", rowKey, projectName, "Project doesn't exist!");
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_11002));
        }
    }

    @Override
    public List<String> findAllProjectName() {
        List<String> result = Lists.newArrayList();
        Query query = new Query();
        query.fields().exclude("testItems");
        try {
            logger.debug("Finding all projectNames ...");
            List<Project> projectList = getMongoTemplate().find(query, Project.class, PROJECT_COLLECTION_NAME);
            logger.info("Find all projectNames done.");
            for (Project project : projectList) {
                result.add(project.getProjectName());
            }
        } catch (Exception e) {
            logger.error("Find all projectNames error! Exception = {}", e.getMessage());
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_11002));
        }
        return result;
    }

    @Override
    public boolean isProjectExist(String projectName) {
        try {
            logger.debug("Finding project exist or not by project name = {}...", projectName);
            boolean flag = getMongoTemplate().exists(new Query(where(PROJECT_NAME_FIELD).is(projectName)), PROJECT_COLLECTION_NAME);
            logger.info("Find project exist or not by project name = {} done.", projectName);
            return flag;
        } catch (Exception e) {
            logger.error("Find project exist or not by project name = {} error! Exception = {}", projectName, e.getMessage());
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_20001), e);
        }
    }

    @Override
    public boolean isTestItemExist(String projectName, String testItemName) {
        try {
            logger.debug("Finding test item exist or not by project name = {} and test item name = {}...", projectName, testItemName);
            boolean flag = getMongoTemplate().exists(new Query(where(PROJECT_NAME_FIELD).is(projectName).andOperator(where(TEST_ITEM_FIELD + "." + testItemName).exists(true))), PROJECT_COLLECTION_NAME);
            logger.info("Find test item exist or not by project name = {} and test item name = {} done.", projectName, testItemName);
            return flag;
        } catch (Exception e) {
            logger.error("Find test item exist or not by project name = {} and test item name = {} error! Exception = {}", projectName, testItemName, e.getMessage());
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_20001), e);
        }
    }

    @Override
    public boolean isRowKeyExist(String rowKey) {
        String projectName = DoubleIdUtils.getId0(rowKey);
        try {
            logger.debug("Finding row key exist or not by row key = {}...", rowKey);
            boolean flag = getMongoTemplate().exists(new Query(where(ROW_KEY_FIELD).is(rowKey)), projectName);
            logger.info("Find row key exist or not by row key = {} done.", rowKey);
            return flag;
        } catch (Exception e) {
            logger.error("Find row key exist or not by row key = {} error! Exception = {}", rowKey, e.getMessage());
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_20001), e);
        }
    }

    @Override
    public List<String> findAllTestItemName(List<String> projectNameList) {
        List<String> result = Lists.newArrayList();
        try {
            logger.debug("Finding all test item name by project names = {}...", StringUtils.join(projectNameList, ','));
            for (String projectName : projectNameList) {
                Project project = getMongoTemplate().findOne(new Query(where(PROJECT_NAME_FIELD).is(projectName)), Project.class, PROJECT_COLLECTION_NAME);
                //project may be null, do not believe idea
                if (project != null && project.getTestItems() != null) {
                    for (TestItem testItem : project.getTestItems().values()) {
                        if (!result.contains(testItem.getTestItemName())) {
                            result.add(testItem.getTestItemName());
                        }
                    }
                }
            }
            logger.info("Find all test item name by project names = {} done.", StringUtils.join(projectNameList, ','));
        } catch (Exception e) {
            logger.error("Find all test item name by project names = {} error! Exception = {}", StringUtils.join(projectNameList, ','), e.getMessage());
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_20001), e);
        }
        return result;
    }

    @Override
    public Map<String, TestItemDto> findAllTestItem(List<String> projectNameList) {
        Map<String, TestItemDto> result = Maps.newLinkedHashMap();
        try {
            logger.debug("Finding all test item by project names = {}...", StringUtils.join(projectNameList, ','));
            for (String projectName : projectNameList) {
                Project project = getMongoTemplate().findOne(new Query(where(PROJECT_NAME_FIELD).is(projectName)), Project.class, PROJECT_COLLECTION_NAME);
                //project may be null, do not believe idea
                if (project != null && project.getTestItems() != null) {
                    for (TestItem testItem : project.getTestItems().values()) {
                        if (!result.containsKey(testItem.getTestItemName())) {
                            TestItemDto testItemDto = new TestItemDto();
                            BeanUtils.copyProperties(testItem, testItemDto);
                            result.put(testItem.getTestItemName(), testItemDto);
                        }
                    }
                }
            }
            logger.info("Find all test item by project names = {} done.", StringUtils.join(projectNameList, ','));
        } catch (Exception e) {
            logger.error("Find all test item by project names = {} error! Exception = {}", StringUtils.join(projectNameList, ','), e.getMessage());
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_20001), e);
        }
        return result;
    }

    @Override
    public Map<String, TestItemDto> findTestItem(List<String> projectNameList, List<String> testItemNameList) {
        Map<String, TestItemDto> result = Maps.newLinkedHashMap();
        List<String> testItemNames = Lists.newArrayList(testItemNameList);
        try {
            logger.debug("Finding test item by project names = {} and test item name = {}...", StringUtils.join(projectNameList, ','),
                    StringUtils.join(testItemNameList, ','));
            for (String projectName : projectNameList) {
                Query query = new Query(where(PROJECT_NAME_FIELD).is(projectName));
                for (String testItemName : testItemNames) {
                    query.fields().include(TEST_ITEM_FIELD + "." + testItemName);
                }
                Project project = getMongoTemplate().findOne(query, Project.class, PROJECT_COLLECTION_NAME);
                if (project != null && project.getTestItems() != null) {
                    for (TestItem testItem : project.getTestItems().values()) {
                        if (!result.containsKey(testItem.getTestItemName())) {
                            TestItemDto testItemDto = new TestItemDto();
                            BeanUtils.copyProperties(testItem, testItemDto);
                            result.put(testItem.getTestItemName(), testItemDto);
                            testItemNames.remove(testItem.getTestItemName());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Find test item by project names = {} and test item name = {} error! Exception = {}", StringUtils.join(projectNameList, ','),
                    StringUtils.join(testItemNameList, ','), e.getMessage());
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_20001), e);
        }
        return result;
    }

    @Override
    public TestItemDto findTestItem(List<String> projectNameList, String testItemName) {
        TestItemDto testItemDto = new TestItemDto();
        try {
            logger.debug("Finding test item by project names = {} and test item name = {}...", StringUtils.join(projectNameList, ','), testItemName);
            for (String projectName : projectNameList) {
                Query query = new Query(where(PROJECT_NAME_FIELD).is(projectName).andOperator(where(TEST_ITEM_FIELD + "." + testItemName + "." + TEST_ITEM_NAME_FIELD).is(testItemName)));
                query.fields().include(TEST_ITEM_FIELD + "." + testItemName);
                Project project = getMongoTemplate().findOne(query, Project.class, PROJECT_COLLECTION_NAME);
                //project may be null, do not believe idea
                if (project != null && project.getTestItems().containsKey(testItemName)) {
                    TestItem testItem = project.getTestItems().get(testItemName);
                    BeanUtils.copyProperties(testItem, testItemDto);
                }
            }
            logger.info("Find test item by project names = {} and test item name = {} done.", StringUtils.join(projectNameList, ','), testItemName);
        } catch (Exception e) {
            logger.error("Find test item by project names = {} and test item name = {} error! Exception = {}", StringUtils.join(projectNameList, ','), testItemName, e.getMessage());
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_20001), e);
        }
        return testItemDto.getTestItemName() == null ? null : testItemDto;
    }

    @Override
    public List<RowDataDto> findTestData(List<String> projectNameList, List<String> testItemNameList) {
        return findTestData(projectNameList, testItemNameList, true);
    }

    @Override
    public Set<String> findUniqueTestData(List<String> projectNameList, String testItemName) {
        return RuntimeContext.getBean(TestDataCacheFactory.class).findTestData(projectNameList, testItemName);
    }

    @Override
    public List<RowDataDto> findTestData(List<String> projectNameList, List<String> testItemNameList, Boolean inUsedFlag) {
        List<RowDataDto> rowDataDtoList = Lists.newArrayList();
        try {
            Criteria criteria = new Criteria();
            if (inUsedFlag != null) {
                criteria = where(IN_USED_FIELD).is(inUsedFlag);
            }
            Query query = new Query(criteria);
            if (testItemNameList != null) {
                for (String testItemName : testItemNameList) {
                    query.fields().include(DATA_FIELD + "." + testItemName);
                }
            }
            for (String projectName : projectNameList) {
                logger.debug("Finding Test Data for project name = {}...", projectName);
                List<RowData> rowDataList = getMongoTemplate().find(query, RowData.class, projectName);
                for (RowData rowData : rowDataList) {
                    RowDataDto rowDataDto = new RowDataDto();
                    BeanUtils.copyProperties(rowData, rowDataDto);
                    rowDataDtoList.add(rowDataDto);
                }
                logger.info("Find Test Data for project name = {} done.", projectName);
            }
            logger.info("Find Test Data done.");
        } catch (Exception e) {
            logger.error("Find Test Data error! Exception = {}", e.getMessage());
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_20001), e);
        }
        return rowDataDtoList;
    }

    @Override
    public RowDataDto findTestData(String rowKey) {
        RowDataDto rowDataDto = new RowDataDto();
        try {
            String projectName = DoubleIdUtils.getId0(rowKey);
            RowData rowData = getMongoTemplate().findOne(new Query(where(ROW_KEY_FIELD).is(rowKey)), RowData.class, projectName);
            //row data may be null, do not believe idea
            if (rowData != null) {
                BeanUtils.copyProperties(rowData, rowDataDto);
            }
        } catch (Exception e) {
            logger.error("Find Test Data by row key = {} error! Exception = {}", rowKey, e.getMessage());
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_20001), e);
        }
        return rowDataDto.getRowKey() == null ? null : rowDataDto;
    }

    @Override
    public void changeRowDataInUsed(List<String> rowKeyList, boolean inUsed) {
        if (rowKeyList == null || rowKeyList.isEmpty()) {
            return;
        }
        try {
            logger.debug("Changing row data in used value...");
            Update update = new Update().set(IN_USED_FIELD, inUsed);
            for (String rowKey : rowKeyList) {
                String projectName = DoubleIdUtils.getId0(rowKey);
                getMongoTemplate().updateFirst(new Query(where(ROW_KEY_FIELD).is(rowKey)), update, projectName);
            }
            logger.info("Change row data in used done.");
        } catch (Exception e) {
            logger.error("Change row data in used value error! Exception = {}", e.getMessage());
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_20001), e);
        }
    }

    @Override
    public void deleteProject(List<String> projectNameList) {
        try {
            for (String projectName : projectNameList) {
                logger.debug("Deleting project for project name = {}...", projectName);
                getMongoTemplate().remove(new Query(where(PROJECT_NAME_FIELD).is(projectName)), PROJECT_COLLECTION_NAME);
                getMongoTemplate().dropCollection(projectName);
                logger.info("Delete project for project name = {} done.", projectName);
            }
        } catch (Exception e) {
            logger.error("Delete project error! Exception = {}", e.getMessage());
            throw new ApplicationException(CoreExceptionParser.parser(CoreExceptionCode.ERR_20001), e);
        }
    }

    private MongoTemplate getMongoTemplate() {
        return RuntimeContext.getBean(MongoTemplate.class);
    }

    // private method without check project exist or not
    private void privateSaveTestData(String projectName, String rowKey, Map<String, String> rowDataMap, boolean appendFlag) {
        if (isRowKeyExist(rowKey)) {
            RowData rowData = getMongoTemplate().find(new Query(where(ROW_KEY_FIELD).is(rowKey)), RowData.class, projectName).get(0);
            if (appendFlag && rowData.getData() != null) {
                rowData.getData().putAll(rowDataMap);
            } else {
                rowData.setData(rowDataMap);
            }
            getMongoTemplate().save(rowData, projectName);
        } else {
            RowData rowData = new RowData();
            rowData.setRowKey(rowKey);
            rowData.setInUsed(true);
            rowData.setData(rowDataMap);
            getMongoTemplate().save(rowData, projectName);
        }
    }
}
