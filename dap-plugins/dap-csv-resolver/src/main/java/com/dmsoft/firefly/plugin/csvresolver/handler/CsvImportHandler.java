/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.csvresolver.handler;

import com.csvreader.CsvReader;
import com.dmsoft.bamboo.common.monitor.ProcessResult;
import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.plugin.csvresolver.CsvResolverService;
import com.dmsoft.firefly.plugin.csvresolver.CsvTemplateDto;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.job.core.JobHandlerContext;
import com.dmsoft.firefly.sdk.job.core.JobInboundHandler;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.utils.DoubleIdUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Garen.Pang on 2018/3/2.
 */
public class CsvImportHandler implements JobInboundHandler {

    private final Logger logger = LoggerFactory.getLogger(CsvResolverService.class);
    private SourceDataService sourceDataService = RuntimeContext.getBean(SourceDataService.class);

    private PluginContext pluginContext = RuntimeContext.getBean(PluginContext.class);

    private String fileName = "csvTemplate.txt";

    private JsonMapper jsonMapper = new JsonMapper();

    private CsvReader csvReader;

    @Override
    public void doJob(JobHandlerContext context, Object... in) throws Exception {

        String csvPath = (String) in[0];
        logger.info("Start csv importing.");
        File csvFile = new File(csvPath);
        Boolean importSucc = false;
        String logStr = null;

        try {
            context.fireJobEvent(new ProcessResult(10));
            logStr = "Start to import <" + csvPath + ">.";
            logger.debug(logStr);

//            push(new ProcessResult(0, logStr, csvPath));

            List<String[]> csvList = Lists.newArrayList();
            csvReader = new CsvReader(csvPath, ',', Charset.forName("UTF-8"));
//            push(new ProcessResult(0, "paring file<" + filePath + ">.", csvPath));
            context.fireJobEvent(new ProcessResult(20));
            logger.debug("Parsing <" + csvPath + ">.");
            CsvTemplateDto fileFormat = findCsvTemplate();
            if (fileFormat == null) {
                return;
            }

            while (csvReader.readRecord()) {
                csvList.add(csvReader.getValues());
            }
            logger.debug("Parsing <" + csvPath + "> done.");
            context.fireJobEvent(new ProcessResult(30));
            final int rowSize = csvList.size();
            int dataIndex = fileFormat.getData() - 1;
            if (dataIndex > rowSize) {
                logStr = "Import <" + csvPath + "> failed. Csv data missing. ";
                logger.debug(logStr);
//                pushErrorMsg(logStr, csvPath);
//                throw new ApplicationException(exceptionNumber, logStr);
            }
            sourceDataService.saveProject(csvFile.getName());
            context.fireJobEvent(new ProcessResult(50));
            String[] items = csvList.get(fileFormat.getItem() - 1);
            String[] lslRow = null, uslRow = null, unitRow = null;

            if (fileFormat.getHeader() != null && fileFormat.getHeader() > 0) {
                csvList.set(fileFormat.getHeader() - 1, null);
            }
            if (fileFormat.getLsl() != null && fileFormat.getLsl() > 0) {
                lslRow = csvList.get(fileFormat.getLsl() - 1);
                csvList.set(fileFormat.getLsl() - 1, null);
            }
            if (fileFormat.getUsl() != null && fileFormat.getUsl() > 0) {
                uslRow = csvList.get(fileFormat.getUsl() - 1);
                csvList.set(fileFormat.getUsl() - 1, null);
            }
            if (fileFormat.getUnit() != null && fileFormat.getUnit() > 0) {
                unitRow = csvList.get(fileFormat.getUnit() - 1);
                csvList.set(fileFormat.getUnit() - 1, null);
            }
            List<TestItemDto> testItemDtoList = Lists.newArrayList();
            for (int i = 0; i < items.length; i++) {
                TestItemDto testItemDto = new TestItemDto();
                testItemDto.setTestItemName(items[i]);
                if (lslRow != null) {
                    testItemDto.setLsl(lslRow[i]);
                }
                if (uslRow != null) {
                    testItemDto.setUsl(uslRow[i]);
                }
                if (unitRow != null) {
                    testItemDto.setUnit(unitRow[i]);
                }
                testItemDtoList.add(testItemDto);
            }
            sourceDataService.saveTestItem(csvFile.getName(), testItemDtoList);
            context.fireJobEvent(new ProcessResult(60));
            //save line data
            List<RowDataDto> rowDataDtos = Lists.newArrayList();
            for (int i = dataIndex; i < csvList.size(); i++) {
                List<String> data = Arrays.asList(csvList.get(i));
                RowDataDto rowDataDto = new RowDataDto();
                rowDataDto.setRowKey(DoubleIdUtils.combineIds(csvFile.getName(), i));
                Map<String, String> itemDatas = Maps.newLinkedHashMap();
                for (int j = 0; j < items.length; j++) {
                    String value = "";
                    try {
                        value = data.get(j);
                        itemDatas.put(items[j], value);
                    } catch (IndexOutOfBoundsException e){

                    }
                }
                rowDataDto.setData(itemDatas);
                rowDataDtos.add(rowDataDto);
            }
            sourceDataService.saveTestData(csvFile.getName(), rowDataDtos);
            context.fireJobEvent(new ProcessResult(90));
            //save column data
//            List<TestDataDto> testDataDtos = Lists.newArrayList();
//            for (int i = 0; i < items.length; i++) {
//                TestDataDto testDataDto = new TestDataDto();
////                testDataDto.setProjectName(csvFile.getName());
//                testDataDto.setItemName(items[i]);
//                testDataDto.setUsl(uslRow[i]);
//                testDataDto.setLsl(lslRow[i]);
//                testDataDto.setUnit(unitRow[i]);
//
//                List<CellData> cellDatas = Lists.newArrayList();
//                for (int j = fileFormat.getData() - 1; j < csvList.size(); j++) {
//                    CellData cellData = new CellData();
//                    cellData.setRowKey(String.valueOf(j));
//                    cellData.setValue(csvList.get(j)[i]);
//                    cellDatas.add(cellData);
//                }
//                testDataDto.setData(cellDatas);
//                testDataDtos.add(testDataDto);
//            }
//            sourceDataService.saveProjectData(csvFile.getName(), testDataDtos);

            importSucc = true;
            csvReader.close();

        } catch (Exception e) {
            e.printStackTrace();
            logStr = "Exception happened. Details recorded in the log";
//            pushErrorMsg(logStr, csvPath);
//            throw new ApplicationException(exceptionNumber, e.getMessage());
        } finally {
            if (!importSucc) {
                sourceDataService.deleteProject(Lists.newArrayList(csvFile.getName()));
            }
        }
        logger.info("End csv importing.");
        context.fireJobEvent(new ProcessResult(100));
    }

    public CsvTemplateDto findCsvTemplate() {
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.CsvResolverPlugin").getFolderPath() + File.separator + fileName;
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        CsvTemplateDto csvTemplateDto = null;
        BufferedReader reader = null;
        String text = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                text += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!StringUtils.isEmpty(text)) {
            csvTemplateDto = jsonMapper.fromJson(text, CsvTemplateDto.class);
        }
        return csvTemplateDto;
    }

    @Override
    public void exceptionCaught(JobHandlerContext context, Throwable cause) throws Exception {

    }
}
