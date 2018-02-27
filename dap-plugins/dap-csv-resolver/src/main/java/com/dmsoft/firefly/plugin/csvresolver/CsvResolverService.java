/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.csvresolver;

import com.csvreader.CsvReader;
import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.RowDataDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.plugin.annotation.DataParser;
import com.dmsoft.firefly.sdk.plugin.annotation.ExcludeMethod;
import com.dmsoft.firefly.sdk.plugin.apis.IDataParser;
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

    private String fileName = "csvTemplate.txt";

    private JsonMapper jsonMapper = new JsonMapper();

    private CsvReader csvReader;

    /**
     * method to import csv
     *
     * @param csvPath        the path of csv file
     */
    public void importFile(String csvPath) {
        logger.info("Start csv importing.");
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
            CsvTemplateDto fileFormat = findCsvTemplate();
            if (fileFormat == null) {
                return;
            }

            while (csvReader.readRecord()) {
                csvList.add(csvReader.getValues());
            }
            logger.debug("Parsing <" + csvPath + "> done.");

            final int rowSize = csvList.size();
            int dataIndex = fileFormat.getData() - 1;
            if (dataIndex > rowSize) {
                logStr = "Import <" + csvPath + "> failed. Csv data missing. ";
                logger.debug(logStr);
//                pushErrorMsg(logStr, csvPath);
//                throw new ApplicationException(exceptionNumber, logStr);
            }
            sourceDataService.saveProject(csvFile.getName());

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
                unitRow = csvList.get(fileFormat.getUnit());
                csvList.set(fileFormat.getUnit(), null);
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
            //save line data
            List<RowDataDto> rowDataDtos = Lists.newArrayList();
            for (int i = fileFormat.getData() - 1; i < csvList.size(); i++) {
                List<String> data = Arrays.asList(csvList.get(i));
                RowDataDto rowDataDto = new RowDataDto();
                rowDataDto.setRowKey(DoubleIdUtils.combineIds(csvFile.getName(), i));
                Map<String, String> itemDatas = Maps.newLinkedHashMap();
                for (int j = 0; j < data.size(); j++) {
                    itemDatas.put(items[j], data.get(i));
                }
                rowDataDto.setData(itemDatas);
                rowDataDtos.add(rowDataDto);
            }
            sourceDataService.saveTestData(csvFile.getName(), rowDataDtos);
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
    }

    @Override
    public String getName() {
        return "Csv resolver";
    }

    private void saveProject(File file, CsvTemplateDto csvTemplateDto, List<String[]> value) {
        String[] lslRow = null, uslRow = null, unitRow = null;

        if (csvTemplateDto.getHeader() != null && csvTemplateDto.getHeader() > 0) {
            value.set(csvTemplateDto.getHeader() - 1, null);
        }
        if (csvTemplateDto.getLsl() != null && csvTemplateDto.getLsl() > 0) {
            lslRow = value.get(csvTemplateDto.getLsl() - 1);
            value.set(csvTemplateDto.getLsl() - 1, null);
        }
        if (csvTemplateDto.getUsl() != null && csvTemplateDto.getUsl() > 0) {
            uslRow = value.get(csvTemplateDto.getUsl() - 1);
            value.set(csvTemplateDto.getUsl() - 1, null);
        }
        if (csvTemplateDto.getUnit() != null && csvTemplateDto.getUnit() > 0) {
            unitRow = value.get(csvTemplateDto.getUnit());
            value.set(csvTemplateDto.getUnit(), null);
        }
        //save project
        String[] items = value.get(csvTemplateDto.getItem() - 1);
        //projectDto.setItemNames(Arrays.asList(items));
        sourceDataService.saveProject(file.getName());

        //save teat item
        List<TestItemDto> itemDtos = Lists.newArrayList();
        for (int i = 0; i < items.length; i++) {
            TestItemDto testItemDto = new TestItemDto();
            testItemDto.setTestItemName(items[i]);
            testItemDto.setUsl(uslRow[i]);
            testItemDto.setLsl(lslRow[i]);
            testItemDto.setUnit(unitRow[i]);
            itemDtos.add(testItemDto);
        }
//        sourceDataService.saveTestItem(itemDtos);

    }

    /**
     * save csv template setting
     *
     * @param csvTemplateDto csv template setting
     */
    @ExcludeMethod
    public void saveCsvTemplate(CsvTemplateDto csvTemplateDto) {
        FileOutputStream fos = null;
        String text = jsonMapper.toJson(csvTemplateDto);
        String path = pluginContext.getEnabledPluginInfo("com.dmsoft.dap.CsvResolverPlugin").getFolderPath();
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
     * @return csv template dto
     */
    @ExcludeMethod
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
//            while (csvReader.readRecord()) {
//                csvList.add(csvReader.getValues());
//            }
            csvReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvList;
    }

//    public static void main(String[] args) {
//        System.out.println("ASFDA");
//        CsvResolverService service = new CsvResolverService();
//        CsvTemplateDto csvTemplateDto = new CsvTemplateDto();
//        csvTemplateDto.setData(1);
//        csvTemplateDto.setItem(1);
//        csvTemplateDto.setHeader(1);
//        csvTemplateDto.setLsl(1);
//        csvTemplateDto.setUsl(1);
//        csvTemplateDto.setUnit(1);
//
//        service.saveCsvTemplate(csvTemplateDto);
//        System.out.println(service.findCsvTemplate());
//    }
}
