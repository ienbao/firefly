/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.tm.csvresolver.service;

import com.csvreader.CsvReader;
import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.utils.JsonFileUtil;
import com.dmsoft.firefly.plugin.tm.csvresolver.dto.CsvTemplateDto;
import com.dmsoft.firefly.plugin.tm.csvresolver.utils.DoubleIdUtils;
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
import com.dmsoft.firefly.plugin.tm.csvresolver.utils.DapAssert;
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
@DataParser
public class CsvResolverService implements IDataParser {
    private final Logger logger = LoggerFactory.getLogger(CsvResolverService.class);

    @Autowired
    private SourceDataService sourceDataService;
    @Autowired
    private PluginContext pluginContext;
    @Autowired
    private JobManager jobManager;
    private String fileName = "TMCSVTemplate";
    private String pluginName = "TM CSV Resolver";
    private JsonMapper jsonMapper = JsonMapper.defaultMapper();
    private static int INSERT_PARTION = 100;

    @Override
    public void importFile(String csvPath) {
        logger.info("开发导入CSV文档.");

        File csvFile = new File(csvPath);
        Boolean importSucc = false;
        String projectName = DAPStringUtils.filterSpeChars4Mongo(csvFile.getName());

        try {
            CsvTemplateDto fileFormat = findCsvTemplate();
            DapAssert.isNotNull(fileFormat, "Tm 导入模板不能为空。");

            CsvReader csvReader = new CsvReader(csvPath, ',', Charset.forName("UTF-8"));
            logger.info("创建CSV解释器，CSV文件名[{}]", csvPath);
            pushProgress(20);

            ///读取行信息,csv文件的内容；
            List<String[]> csvList = Lists.newArrayListWithCapacity(INSERT_PARTION);
            while (csvReader.readRecord()) {
                csvList.add(csvReader.getValues());
            }
            csvReader.close();
            logger.info("读取CSV文档内容，CSV文件名[{}]，文件行数[{}].", csvPath, csvList.size());
            pushProgress(30);


            //保存project信息
            int dataIndex = fileFormat.getData() - 1;
            if (dataIndex > csvList.size()) {
                logger.error("解释CSV文档数据行，内容起始行[{}],实际文档长度[{}]", dataIndex, csvList.size());
            }
            sourceDataService.saveProject(projectName);
            logger.info("保存项目到数据库，projectNo[{}]", projectName);
            pushProgress(40);


            //过滤测试项特殊字符
            String[] testItemNames = csvList.get(fileFormat.getItem() - 1);
            for (int i = 0; i < testItemNames.length; i++) {
              testItemNames[i] = DAPStringUtils.mongodbItemFormat(testItemNames[i]);
            }



            //创建project结构性数据
            String[] lslRow = null;
            if (fileFormat.getLsl() != null && fileFormat.getLsl() > 0) {
              lslRow = csvList.get(fileFormat.getLsl() - 1);
            }

            String[] uslRow = null;
            if (fileFormat.getUsl() != null && fileFormat.getUsl() > 0) {
              uslRow = csvList.get(fileFormat.getUsl() - 1);
            }

            String[] unitRow = null;
            if (fileFormat.getUnit() != null && fileFormat.getUnit() > 0) {
              unitRow = csvList.get(fileFormat.getUnit() - 1);
            }


            List<TestItemDto> testItemDtoList = Lists.newArrayListWithCapacity(testItemNames.length);
            for (int i = 0; i < testItemNames.length; i++) {
                TestItemDto testItemDto = new TestItemDto();
                testItemDto.setTestItemName(testItemNames[i]);
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
            List<RowDataDto> rowDataDtoList = Lists.newArrayListWithCapacity(INSERT_PARTION);
            for (int index = fileFormat.getData() - 1; index < len; index++) {
                String[] rows = csvList.get(index);
                RowDataDto rowDataDto = new RowDataDto();
                rowDataDto.setRowKey(DoubleIdUtils.combineIds(projectName, index));
                Map<String, String> itemDatas = Maps.newHashMapWithExpectedSize(testItemNames.length);
                for (int j = 0; j < testItemNames.length; j++) {
                    itemDatas.put(testItemNames[j], DAPStringUtils.formatBigDecimal(rows[j]));
                }
                rowDataDto.setData(itemDatas);
                rowDataDto.setInUsed(true);
                rowDataDtoList.add(rowDataDto);

                pushProgress(60 + 30 * index / len);
                logger.debug("Imported Line No = {}", index);

                if(rowDataDtoList.size() % 100 == 0){
                  sourceDataService.insertAllTestData(projectName, rowDataDtoList);
                  rowDataDtoList = Lists.newArrayListWithCapacity(INSERT_PARTION);
                }
            }

            sourceDataService.insertAllTestData(projectName, rowDataDtoList);
            pushProgress(90);

            importSucc = true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

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
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.TMCsvResolverPlugin").getFolderPath() + File.separator + "config";
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
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.TMCsvResolverPlugin").getFolderPath() + File.separator + "config";
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
