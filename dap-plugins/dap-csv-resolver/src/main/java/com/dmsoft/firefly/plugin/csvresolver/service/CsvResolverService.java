/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.csvresolver.service;

import com.csvreader.CsvReader;
import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.utils.JsonFileUtil;
import com.dmsoft.firefly.plugin.csvresolver.dto.CsvTemplateDto;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.job.AbstractProcessMonitorAutoAdd;
import com.dmsoft.firefly.sdk.job.JobThread;
import com.dmsoft.firefly.sdk.job.ProcessMonitorAuto;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.DataParser;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.ExcludeMethod;
import com.dmsoft.firefly.sdk.plugin.apis.IDataParser;
import com.dmsoft.firefly.plugin.csvresolver.utils.DoubleIdUtils;
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
 * csv resolver service
 * update by Can Guan on 2018/2/25 for updating dai api
 *
 * @author Li Guang
 */
@DataParser
public class CsvResolverService implements IDataParser {
    private final Logger logger = LoggerFactory.getLogger(CsvResolverService.class);
    private SourceDataService sourceDataService = RuntimeContext.getBean(SourceDataService.class);

    private PluginContext pluginContext = RuntimeContext.getBean(PluginContext.class);

    private String fileName = "csvTemplate";

    private JsonMapper jsonMapper = JsonMapper.defaultMapper();

    private CsvReader csvReader;

    /**
     * method to import csv
     *
     * @param csvPath the path of csv file
     */
    public void importFile(String csvPath) {
        logger.info("Start csv importing.");
        File csvFile = new File(csvPath);
        Boolean importSucc = false;
        String logStr = null;
        ProcessMonitorAuto processMonitor = null;
        if (Thread.currentThread() instanceof JobThread) {
            processMonitor = (ProcessMonitorAuto) Thread.currentThread();
        } else {
            processMonitor = new AbstractProcessMonitorAutoAdd() {
            };
        }


        try {
            logStr = "Start to import <" + csvPath + ">.";
            logger.debug(logStr);
            processMonitor.push(10);

//            push(new ProcessResult(0, logStr, csvPath));

            List<String[]> csvList = Lists.newArrayList();
            csvReader = new CsvReader(csvPath, ',', Charset.forName("UTF-8"));
//            push(new ProcessResult(0, "paring file<" + filePath + ">.", csvPath));
            processMonitor.push(20);
            logger.debug("Parsing <" + csvPath + ">.");
            CsvTemplateDto fileFormat = findCsvTemplate();
            if (fileFormat == null) {
                return;
            }

            while (csvReader.readRecord()) {
                csvList.add(csvReader.getValues());
            }
            logger.debug("Parsing <" + csvPath + "> done.");
            processMonitor.push(30);
            final int rowSize = csvList.size();
            int dataIndex = fileFormat.getData() - 1;
            if (dataIndex > rowSize) {
                logStr = "Import <" + csvPath + "> failed. Csv data missing. ";
                logger.debug(logStr);
//                pushErrorMsg(logStr, csvPath);
//                throw new ApplicationException(exceptionNumber, logStr);
            }
            sourceDataService.saveProject(csvFile.getName());
            processMonitor.push(40);
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
            processMonitor.push(50, 80, "", 5000);
//            processMonitor.push(50);
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
                    } catch (IndexOutOfBoundsException e) {

                    }
                }
                rowDataDto.setData(itemDatas);
                rowDataDtos.add(rowDataDto);
            }
            sourceDataService.saveTestData(csvFile.getName(), rowDataDtos);
            processMonitor.push(90);
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
        processMonitor.push(100);
    }

    @Override
    public String getName() {
        return "Csv resolver";
    }

    /**
     * save csv template setting
     *
     * @param csvTemplateDto csv template setting
     */
    @ExcludeMethod
    public void saveCsvTemplate(CsvTemplateDto csvTemplateDto) {
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.CsvResolverPlugin").getFolderPath();
        String json = JsonFileUtil.readJsonFile(path, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
        }

        JsonFileUtil.writeJsonFile(csvTemplateDto, path, fileName);
    }

    /**
     * find csv template setting
     *
     * @return csv template dto
     */
    @ExcludeMethod
    public CsvTemplateDto findCsvTemplate() {
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.CsvResolverPlugin").getFolderPath();
        String json = JsonFileUtil.readJsonFile(path, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
        }

        CsvTemplateDto csvTemplateDto = null;
        if (!StringUtils.isEmpty(json)) {
            csvTemplateDto = jsonMapper.fromJson(json, CsvTemplateDto.class);
        }
        return csvTemplateDto;
    }

    /**
     * read pre ten row data
     *
     * @param path file path
     * @return ten row data
     */
    @ExcludeMethod
    public List<String[]> rowParser(String path) {
        List<String[]> csvList = Lists.newArrayList();
        try {
            csvReader = new CsvReader(path, ',', Charset.forName("UTF-8"));
            logger.debug("Parsing <" + path + ">.");
            int i = 0;
            while (csvReader.readRecord()) {
                if (i >= 10) {
                    break;
                }
                csvList.add(Arrays.copyOfRange(csvReader.getValues(), 0, 3));
                i++;
            }

            csvReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvList;
    }

}
