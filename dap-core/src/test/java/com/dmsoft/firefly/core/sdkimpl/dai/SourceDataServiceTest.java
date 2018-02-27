package com.dmsoft.firefly.core.sdkimpl.dai;

import com.dmsoft.firefly.core.utils.DoubleIdUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.MongoClient;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class SourceDataServiceTest {

    Logger logger = LoggerFactory.getLogger(SourceDataServiceTest.class);

    @Test
    public void saveProjectTest() {
        MongoClient mongoClient = new MongoClient("localhost");
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "test");
        RuntimeContext.registerBean(MongoTemplate.class, mongoTemplate);
        SourceDataServiceImpl sourceDataService = new SourceDataServiceImpl();
        sourceDataService.saveProject("TestProject");
    }

    @Test
    public void saveTestItemTest() {
        MongoClient mongoClient = new MongoClient("localhost");
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "test");
        RuntimeContext.registerBean(MongoTemplate.class, mongoTemplate);
        SourceDataServiceImpl sourceDataService = new SourceDataServiceImpl();
        TestItemDto testItemDto = new TestItemDto();
        testItemDto.setLsl("1238");
        testItemDto.setUsl("4567");
        testItemDto.setTestItemName("asdfa12");
        testItemDto.setUnit("AV");
        sourceDataService.saveTestItem("TestProject", Lists.newArrayList(testItemDto));
    }

    @Test
    public void saveTestDataTest() {
        MongoClient mongoClient = new MongoClient("localhost");
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "test");
        RuntimeContext.registerBean(MongoTemplate.class, mongoTemplate);
        SourceDataServiceImpl sourceDataService = new SourceDataServiceImpl();
        int totalRow = 2000;
        int totalColumn = 2000;
        String projectName = totalRow + "*" + totalColumn;
        sourceDataService.saveProject(projectName);
        List<String> itemNames = Lists.newArrayList();

        long startTime = new Date().getTime();
        List<RowDataDto> rowDataDtos = Lists.newArrayList();
        for (int i = 0; i < totalRow; i++) {
            RowDataDto testData = new RowDataDto();
            testData.setData(Maps.newHashMap());
            for (int j = 0; j < totalColumn; j++) {
                String itemName = "_Forward_Bias_Diodes_INRUSH_OUT_TO_PP_20V_BOOST_SP4" + j;
                testData.getData().put(itemName, (i + 1) * (j + 1) + "");
            }
            testData.setRowKey(DoubleIdUtils.combineIds(projectName, i + ""));
            rowDataDtos.add(testData);
        }
        sourceDataService.saveTestData(projectName, rowDataDtos);

        long endTime = new Date().getTime();
        logger.info("Total time {} min", (endTime - startTime) / 1000.0 / 60);
    }

    @Test
    public void saveTestDataTest1() {
        MongoClient mongoClient = new MongoClient("localhost");
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "test");
        RuntimeContext.registerBean(MongoTemplate.class, mongoTemplate);
        SourceDataServiceImpl sourceDataService = new SourceDataServiceImpl();
        Map<String, String> data = Maps.newHashMap();
        data.put("AA", "BB");
        sourceDataService.saveTestData("2000*2000", "2000*2000_!@#_1", data);
    }

    @Test
    public void appendTestDataTest() {
        MongoClient mongoClient = new MongoClient("localhost");
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "test");
        RuntimeContext.registerBean(MongoTemplate.class, mongoTemplate);
        SourceDataServiceImpl sourceDataService = new SourceDataServiceImpl();
        Map<String, String> data = Maps.newHashMap();
        data.put("CC", "DD");
        sourceDataService.replaceTestData("2000*2000", "2000*2000_!@#_1", data);
    }

    @Test
    public void findAllProjectNameTest() {
        MongoClient mongoClient = new MongoClient("localhost");
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "test");
        RuntimeContext.registerBean(MongoTemplate.class, mongoTemplate);
        SourceDataServiceImpl sourceDataService = new SourceDataServiceImpl();
        System.out.println(sourceDataService.findAllProjectName().size());
    }

    @Test
    public void isProjectExistTest() {
        MongoClient mongoClient = new MongoClient("localhost");
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "test");
        RuntimeContext.registerBean(MongoTemplate.class, mongoTemplate);
        SourceDataServiceImpl sourceDataService = new SourceDataServiceImpl();
        System.out.println(sourceDataService.isRowKeyExist("TestProject1"));
    }

    @Test
    public void isTestItemExistTest() {
        MongoClient mongoClient = new MongoClient("localhost");
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "test");
        RuntimeContext.registerBean(MongoTemplate.class, mongoTemplate);
        SourceDataServiceImpl sourceDataService = new SourceDataServiceImpl();
        System.out.println(sourceDataService.isTestItemExist("TestProject", "asdfa"));
    }

    @Test
    public void isRowKeyExistTest() {
        MongoClient mongoClient = new MongoClient("localhost");
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "test");
        RuntimeContext.registerBean(MongoTemplate.class, mongoTemplate);
        SourceDataServiceImpl sourceDataService = new SourceDataServiceImpl();
        System.out.println(sourceDataService.isRowKeyExist("2000*2000_!@#_1"));
    }

    @Test
    public void findAllTestItemNameTest() {
        MongoClient mongoClient = new MongoClient("localhost");
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "test");
        RuntimeContext.registerBean(MongoTemplate.class, mongoTemplate);
        SourceDataServiceImpl sourceDataService = new SourceDataServiceImpl();
        List<String> testItemList = sourceDataService.findAllTestItemName(Lists.newArrayList("TestProject"));
        for (String s : testItemList) {
            System.out.println(s);
        }
    }

    @Test
    public void findAllTestItemTest() {
        MongoClient mongoClient = new MongoClient("localhost");
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "test");
        RuntimeContext.registerBean(MongoTemplate.class, mongoTemplate);
        SourceDataServiceImpl sourceDataService = new SourceDataServiceImpl();
        Map<String, TestItemDto> testItemMap = sourceDataService.findAllTestItem(Lists.newArrayList("TestProject"));
        for (TestItemDto testItemDto : testItemMap.values()) {
            System.out.println(testItemDto);
        }
    }

    @Test
    public void findTestItemTest() {
        MongoClient mongoClient = new MongoClient("localhost");
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "test");
        RuntimeContext.registerBean(MongoTemplate.class, mongoTemplate);
        SourceDataServiceImpl sourceDataService = new SourceDataServiceImpl();
        TestItemDto testItem = sourceDataService.findTestItem(Lists.newArrayList("TestProject"), "asdfa");
        System.out.println(testItem.getUnit());
    }

    @Test
    public void findTestDataTest() {
        MongoClient mongoClient = new MongoClient("localhost");
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "test");
        RuntimeContext.registerBean(MongoTemplate.class, mongoTemplate);
        SourceDataServiceImpl sourceDataService = new SourceDataServiceImpl();
        long startTime = new Date().getTime();
        List<RowDataDto> dataDtoList = sourceDataService.findTestData(Lists.newArrayList("2000*2000"), Lists.newArrayList("_Forward_Bias_Diodes_INRUSH_OUT_TO_PP_20V_BOOST_SP4907"));
        long endTime = new Date().getTime();
        logger.info("Total time {} s", (endTime - startTime) / 1000.0);
        System.out.println(dataDtoList.size());
    }

    @Test
    public void findTestDataTest1() {
        MongoClient mongoClient = new MongoClient("localhost");
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "test");
        RuntimeContext.registerBean(MongoTemplate.class, mongoTemplate);
        SourceDataServiceImpl sourceDataService = new SourceDataServiceImpl();
        System.out.println(sourceDataService.findTestData("2000*2000_!@#_1").getRowKey());
    }

    @Test
    public void changeRowDataInUsedTest() {
        MongoClient mongoClient = new MongoClient("localhost");
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "test");
        RuntimeContext.registerBean(MongoTemplate.class, mongoTemplate);
        SourceDataServiceImpl sourceDataService = new SourceDataServiceImpl();
        sourceDataService.changeRowDataInUsed(Lists.newArrayList("2000*2000_!@#_1"), false);
    }

    @Test
    public void deleteProjectTest() {
        MongoClient mongoClient = new MongoClient("localhost");
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "test");
        RuntimeContext.registerBean(MongoTemplate.class, mongoTemplate);
        SourceDataServiceImpl sourceDataService = new SourceDataServiceImpl();
        sourceDataService.deleteProject(Lists.newArrayList("2000*2000"));
    }
}
