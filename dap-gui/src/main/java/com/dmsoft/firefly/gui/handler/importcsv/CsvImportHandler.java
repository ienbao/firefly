package com.dmsoft.firefly.gui.handler.importcsv;

import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import com.dmsoft.firefly.sdk.plugin.apis.IDataParser;

/**
 * handler for import csv
 *
 * @author Can Guan, Garen Pang
 */
public class CsvImportHandler extends AbstractBasicJobHandler {
    /**
     * constructor
     */
    public CsvImportHandler() {
        setName(ParamKeys.IMPORT_HANDLER);
    }

    @Override
    public void doJob(JobContext context) {
        IDataParser parser = context.getParam(ParamKeys.IDATA_PARSER, IDataParser.class);
        parser.importFile(context.get(ParamKeys.FILE_PATH).toString());
    }
}
