/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.csvresolver;

import com.csvreader.CsvReader;
import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.LineDataDto;
import com.dmsoft.firefly.sdk.dai.dto.ProjectDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.plugin.annotation.DataParser;
import com.dmsoft.firefly.sdk.plugin.apis.IDataParser;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * spc service
 */
@DataParser
public class CsvResolverService implements IDataParser {
    private final Logger logger = LoggerFactory.getLogger(CsvResolverService.class);
    private SourceDataService sourceDataService = RuntimeContext.getBean(SourceDataService.class);

    private String path = System.getProperty("user.dir");
    private String fileName = "csvTemplate.txt";

    private JsonMapper jsonMapper;

    private CsvReader csvReader;

    public Long importCsv(String csvPath, String importTemplate) {
        logger.info("Start csv importing.");
        Long insertedId = 0L;
        File csvFile = new File(csvPath);
        Boolean importSucc = false;
        String logStr = null;

        try {
            logStr = "Start to import <" + csvPath + ">.";
            logger.debug(logStr);

//            push(new ProcessResult(0, logStr, csvPath));

            List<String[]> csvList = Lists.newArrayList();
            csvReader = new CsvReader(csvPath, ',', Charset.forName("UTF-8"));
//            push(new ProcessResult(0, "paring file<" + filePath + ">.", csvPath));

            logger.debug("Parsing <" + csvPath + ">.");

            while (csvReader.readRecord()) {
                csvList.add(csvReader.getValues());
            }
            logger.debug("Parsing <" + csvPath + "> done.");

            CsvTemplateDto fileFormat = findCsvTemplate();
            if (fileFormat == null) {
                return null;
            }
            String[] lslRow = null, uslRow = null, unitRow = null;
            final int rowSize = csvList.size();
            int dataIndex = fileFormat.getData() - 1;
            if (dataIndex > rowSize) {
                logStr = "Import <" + csvPath + "> failed. Csv data missing. ";
                logger.debug(logStr);
//                pushErrorMsg(logStr, csvPath);
//                throw new ApplicationException(exceptionNumber, logStr);
            }
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
                unitRow = csvList.get(fileFormat.getUnit());
                csvList.set(fileFormat.getUnit(), null);
            }

            //save project
            String[] items = csvList.get(fileFormat.getItem() - 1);
            ProjectDto projectDto = new ProjectDto();
            projectDto.setProjectName(csvFile.getName());
            projectDto.setPath(csvFile.getPath());
            projectDto.setItemNames(Arrays.asList(items));
            sourceDataService.saveProject(projectDto);

            //save teat item
            List<TestItemDto> itemDtos = Lists.newArrayList();
            for (int i = 0; i < items.length; i++) {
                TestItemDto testItemDto = new TestItemDto();
                testItemDto.setProjectName(csvFile.getName());
                testItemDto.setItemName(items[i]);
                testItemDto.setUsl(uslRow[i]);
                testItemDto.setLsl(lslRow[i]);
                testItemDto.setUnit(unitRow[i]);
                itemDtos.add(testItemDto);
            }
            sourceDataService.saveTestItem(itemDtos);
            //save data
            List<LineDataDto> lineDataDtos = Lists.newArrayList();
            for (int i = fileFormat.getData() - 1; i < csvList.size(); i++) {
                List<String> data = Arrays.asList(csvList.get(i));
                LineDataDto lineDataDto = new LineDataDto();
                lineDataDto.setLineNo(String.valueOf(i));
                lineDataDto.setProjectName(csvFile.getName());
                Map<String, Object> itemDatas = Maps.newHashMap();
                for (int j = 0; j < data.size(); j++) {
                    itemDatas.put(items[j], data.get(i));
                }
                lineDataDto.setTestData(itemDatas);
                lineDataDtos.add(lineDataDto);
            }
            sourceDataService.saveProjectData(lineDataDtos);
            importSucc = true;
        } catch (Exception e) {
            e.printStackTrace();
            logStr = "Exception happened. Details recorded in the log";
//            pushErrorMsg(logStr, csvPath);
//            throw new ApplicationException(exceptionNumber, e.getMessage());
        } finally {
            if (!importSucc) {
                sourceDataService.deleteProject(csvFile.getName());
            }
        }
        logger.info("End csv importing.");
        return insertedId;
    }

    /**
     * save csv template setting
     *
     * @param csvTemplateDto csv template setting
     */
    public void saveCsvTemplate(CsvTemplateDto csvTemplateDto) {
        FileOutputStream fos = null;
        String text = jsonMapper.toJson(csvTemplateDto);
        File tempFile = new File(path);
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }
        try {
            fos = new FileOutputStream(tempFile.getPath() + File.separator + fileName);
            fos.write(text.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 完毕，关闭所有链接
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * find csv template setting
     *
     * @return
     */
    public CsvTemplateDto findCsvTemplate() {
        CsvTemplateDto csvTemplateDto = new CsvTemplateDto();
        BufferedReader reader = null;
        String text = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(path + File.separator + fileName);
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
        csvTemplateDto = jsonMapper.fromJson(text, CsvTemplateDto.class);
        return csvTemplateDto;
    }

    public static void main(String[] args) {
        System.out.println("ASFDA");
    }
}
