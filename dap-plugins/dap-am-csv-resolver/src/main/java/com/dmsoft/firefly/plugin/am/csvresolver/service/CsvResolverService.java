/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.am.csvresolver.service;

import com.csvreader.CsvReader;
import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.utils.JsonFileUtil;
import com.dmsoft.firefly.plugin.am.csvresolver.dto.CsvTemplateDto;
import com.dmsoft.firefly.plugin.am.csvresolver.utils.DoubleIdUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import com.dmsoft.firefly.sdk.job.core.JobEvent;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.plugin.apis.IDataParser;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.DataParser;
import com.dmsoft.firefly.sdk.plugin.apis.annotation.ExcludeMethod;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
@Service
public class CsvResolverService implements IDataParser {
    private final Logger logger = LoggerFactory.getLogger(CsvResolverService.class);

    @Autowired
    private SourceDataService sourceDataService;
    @Autowired
    private PluginContext pluginContext;
    @Autowired
    private JobManager jobManager;

    private String fileName = "AMCSVTemplate";
    private String pluginName = "AM CSV Resolver";
    private final int MAX_LENGTH = 255;

    private JsonMapper jsonMapper = JsonMapper.defaultMapper();

    @Override
    public void importFile(String csvPath) {
        logger.info("Start csv importing.");
        CsvReader csvReader;
        File csvFile = new File(csvPath);
        Boolean importSucc = false;
        String logStr = null;
        String projectName = DAPStringUtils.filterSpeChars4Mongo(csvFile.getName());

        try {
            logStr = "Start to import <" + csvPath + ">.";
            logger.debug(logStr);
            pushProgress(10);

            List<String[]> csvList = Lists.newArrayList();
            csvReader = new CsvReader(csvPath, ',', Charset.forName("UTF-8"));
            pushProgress(20);
            logger.debug("Parsing <" + csvPath + ">.");
            CsvTemplateDto fileFormat = findCsvTemplate();
            if (fileFormat == null) {
                return;
            }

            while (csvReader.readRecord()) {
                csvList.add(csvReader.getValues());
            }
            logger.debug("Parsing <" + csvPath + "> done.");
            pushProgress(30);
            final int rowSize = csvList.size();
            int dataIndex = fileFormat.getData() - 1;
            if (dataIndex > rowSize) {
                logStr = "Import <" + csvPath + "> failed. Csv data missing. ";
                logger.debug(logStr);
            }
            sourceDataService.saveProject(projectName);
            pushProgress(40);
            String[] items = csvList.get(fileFormat.getItem() - 1);
            for (int i = 0; i < items.length; i++) {
                items[i] = DAPStringUtils.mongodbItemFormat(items[i]);
            }
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
            String autoItemName = "AUTO GENERATED NAME_",
                    currentItemName = null;
            int autoIndex = 0;
            for (int i = 0; i < items.length; i++) {
                TestItemDto testItemDto = new TestItemDto();
                currentItemName = items[i];
                if (currentItemName.isEmpty() || currentItemName.length() > MAX_LENGTH) {
                    currentItemName = autoItemName + (++autoIndex);
                }
                testItemDto.setTestItemName(currentItemName);
                if (lslRow != null) {
                    testItemDto.setLsl(DAPStringUtils.formatBigDecimal(lslRow[i]));
                }
                if (uslRow != null) {
                    testItemDto.setUsl(DAPStringUtils.formatBigDecimal(uslRow[i]));
                }
                if (unitRow != null) {
                    testItemDto.setUnit(DAPStringUtils.formatBigDecimal(unitRow[i]));
                }
                testItemDtoList.add(testItemDto);
            }
            sourceDataService.saveTestItem(projectName, testItemDtoList);
            pushProgress(60);
            int len = csvList.size();
            int row = 0;
            autoIndex = 0;
            for (int i = dataIndex; i < csvList.size(); i++) {
                List<String> data = Arrays.asList(csvList.get(i));
                RowDataDto rowDataDto = new RowDataDto();
                rowDataDto.setRowKey(DoubleIdUtils.combineIds(projectName, i));
                Map<String, String> itemDatas = Maps.newLinkedHashMap();
                for (int j = 0; j < items.length; j++) {
                    String value = "";
                    value = DAPStringUtils.formatBigDecimal(data.get(j));
                    currentItemName = items[j];
                    if (currentItemName.isEmpty() || currentItemName.length() > MAX_LENGTH) {
                        currentItemName = autoItemName + (++autoIndex);
                    }
                    itemDatas.put(currentItemName, value);
                }
                rowDataDto.setData(itemDatas);
                sourceDataService.saveTestData(projectName, DoubleIdUtils.combineIds(projectName, i), itemDatas, false);
                row++;
                pushProgress(60 + 30 * row / len);
                logger.debug("Imported Line No = {}", row);
            }
            pushProgress(90);

            importSucc = true;
            csvReader.close();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (!importSucc) {
                sourceDataService.deleteProject(Lists.newArrayList(projectName));
                throw new ApplicationException();
            }
        }
        logger.info("End csv importing.");
        pushProgress(100);
    }

    private void pushProgress(int progress) {
        JobContext context = this.jobManager.findJobContext(Thread.currentThread());
        if (context != null) {
            context.pushEvent(new JobEvent("CsvResolverService", progress + 0.0, null));
        }
    }

    /**
     * get plugin name
     *
     * @return string plugin name
     */
    @Override
    public String getName() {
        return pluginName;
    }

    /**
     * save csv template setting
     *
     * @param csvTemplateDto csv template setting
     */
    @ExcludeMethod
    public void saveCsvTemplate(CsvTemplateDto csvTemplateDto) {
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.AMCsvResolverPlugin").getFolderPath() + File.separator + "config";
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
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.AMCsvResolverPlugin").getFolderPath() + File.separator + "config";
        CsvTemplateDto csvTemplateDto = null;

        String json = JsonFileUtil.readJsonFile(path, fileName);
        if (json == null) {
            logger.debug("Don`t find " + fileName);
        }

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
        CsvReader csvReader;
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
