/*
 *  Copyright (c) 2018. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.csvresolver;

import com.csvreader.CsvReader;
import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.sdk.plugin.annotation.DataParser;
import com.dmsoft.firefly.sdk.plugin.apis.IDataParser;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.List;

/**
 * spc service
 */
@DataParser
public class CsvResolverService implements IDataParser {
    private final Logger logger = LoggerFactory.getLogger(CsvResolverService.class);
    private final String dataTablePre = "t_testdata_";
    private final int MAXLENGTH = 255;

    private JsonMapper jsonMapper;

    private CsvReader csvReader;

    public Long importCsv(String csvPath, String importTemplate) {
        logger.info("Start csv importing.");
        Integer exceptionNumber = null;
        Long insertedId = 0L;
        Connection conn = null;
        String filePath = csvPath;
        File csvFile = new File(filePath);
        int lastProcess = 0, currentProcess = 0;
        boolean importResult = false, rollbackDone = false;
        String logStr = null;

        try {
            logStr = "Start to import <" + filePath + ">.";
            logger.debug(logStr);

//            push(new ProcessResult(0, logStr, csvPath));
            logger.debug("Parsing <" + filePath + ">.");

            List<String[]> csvList = Lists.newArrayList();
            csvReader = new CsvReader(csvPath, ',', Charset.forName("UTF-8"));
//            push(new ProcessResult(0, "paring file<" + filePath + ">.", csvPath));
            while (csvReader.readRecord()) { //逐行读入除表头的数据
                csvList.add(csvReader.getValues());
            }
            logger.debug("Parsing <" + filePath + "> done.");

            StringBuilder fieldSql = new StringBuilder("INSERT INTO t_testitem (name,file_id,lsl,usl,unit) VALUES ");

//            GlobalSettingDto fileFormatDto = globalSettingService.findFileSetting(SystemKey.FILE_FORMAT.getCode());
            CsvTemplateDto fileFormat = findCsvTemplate();
            Integer headerIndex = null, lslIndex = null, uslIndex = null, unitIndex = null;
            String[] lslRow = null, uslRow = null, unitRow = null;
            final int rowSize = csvList.size();
            int dataIndex = fileFormat.getData() - 1;
            if (dataIndex > rowSize) {
                exceptionNumber = 17003;
                logStr = "Import <" + filePath + "> failed. Csv data missing. ";
                logger.debug(logStr);
//                pushErrorMsg(logStr, csvPath);
//                throw new ApplicationException(exceptionNumber, logStr);
            }
            if (fileFormat.getHeader() != null && fileFormat.getHeader() > 0) {
                headerIndex = fileFormat.getHeader() - 1;
                csvList.set(headerIndex, null);
            }
            if (fileFormat.getLsl() != null && fileFormat.getLsl() > 0) {
                lslIndex = fileFormat.getLsl() - 1;
                lslRow = csvList.get(lslIndex);
                csvList.set(lslIndex, null);
            }
            if (fileFormat.getUsl() != null && fileFormat.getUsl() > 0) {
                uslIndex = fileFormat.getUsl() - 1;
                uslRow = csvList.get(uslIndex);
                csvList.set(uslIndex, null);
            }
            if (fileFormat.getUnit() != null && fileFormat.getUnit() > 0) {
                unitIndex = fileFormat.getUnit() - 1;
                unitRow = csvList.get(unitIndex);
                csvList.set(unitIndex, null);
            }

            int fieldIndex = fileFormat.getItem() - 1;
            String[] fieldRow = csvList.get(fieldIndex);


        } catch (Exception e) {
            exceptionNumber = 17001;
            e.printStackTrace();
            logStr = "Exception happened. Details recorded in the log";
//            pushErrorMsg(logStr, csvPath);
//            throw new ApplicationException(exceptionNumber, e.getMessage());
        } finally {

        }
        logger.info("End csv importing.");
        return insertedId;
    }

    private CsvTemplateDto findCsvTemplate(){
        CsvTemplateDto csvTemplateDto = new CsvTemplateDto();

        return csvTemplateDto;
    }

}
