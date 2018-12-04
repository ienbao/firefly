package com.dmsoft.firefly.plugin.grr.handler;

import com.dmsoft.firefly.plugin.grr.dto.GrrExportConfigDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrSummaryDto;
import com.dmsoft.firefly.plugin.grr.service.GrrExportService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * handler for summary export
 *
 * @author Can Guan, Cherry Peng
 */
public class ExportSummaryHandler extends AbstractBasicJobHandler {

    @Autowired
    private GrrExportService grrExportService;
    /**
     * constructor
     */
    public ExportSummaryHandler() {
        setName(ParamKeys.GRR_EXPORT_SUMMARY_RESULT_HANDLER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doJob(JobContext context) {
        List<GrrSummaryDto> summaryDtoList = (List<GrrSummaryDto>) context.get(ParamKeys.GRR_SUMMARY_DTO_LIST);
        GrrExportConfigDto grrExportConfigDto = context.getParam(ParamKeys.GRR_EXPORT_CONFIG_DTO, GrrExportConfigDto.class);
        String path = this.grrExportService.exportGrrSummary(grrExportConfigDto, summaryDtoList);
        context.put(ParamKeys.EXPORT_PATH, path);
    }
}
