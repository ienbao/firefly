package com.dmsoft.firefly.plugin.grr.handler;

import com.dmsoft.firefly.plugin.grr.controller.BuildChart;
import com.dmsoft.firefly.plugin.grr.dto.*;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrSummaryResultDto;
import com.dmsoft.firefly.plugin.grr.service.GrrExportService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * handler for detail result export
 *
 * @author Can Guan
 */
public class ExportDetailResultHandler extends AbstractBasicJobHandler {
    /**
     * constructor
     */
    public ExportDetailResultHandler() {
        setName(ParamKeys.GRR_EXPORT_DETAIL_HANDLER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doJob(JobContext context) {
        List<GrrExportDetailDto> grrExportDetailDtoList = (List<GrrExportDetailDto>) context.get(ParamKeys.GRR_DETAIL_DTO_LIST);
        List<GrrSummaryDto> summaryDtos = Lists.newArrayList();
        List<GrrExportResultDto> grrExportResultDtos = Lists.newArrayList();
        SearchConditionDto searchConditionDto = context.getParam(ParamKeys.SEARCH_GRR_CONDITION_DTO, SearchConditionDto.class);
        GrrExportConfigDto configDto = context.getParam(ParamKeys.GRR_EXPORT_CONFIG_DTO, GrrExportConfigDto.class);
        for (GrrExportDetailDto dto : grrExportDetailDtoList) {
            GrrSummaryDto summaryDto = new GrrSummaryDto();
            summaryDto.setItemName(dto.getItemName());
            GrrSummaryResultDto summaryResultDto = new GrrSummaryResultDto();
            summaryResultDto.setUsl(dto.getExportDetailDto().getUsl());
            summaryResultDto.setGrrOnContribution(dto.getExportDetailDto().getGrrOnContribution());
            summaryResultDto.setGrrOnTolerance(dto.getExportDetailDto().getGrrOnTolerance());
            summaryResultDto.setLsl(dto.getExportDetailDto().getLsl());
            summaryResultDto.setRepeatabilityOnContribution(dto.getExportDetailDto().getRepeatabilityOnContribution());
            summaryResultDto.setRepeatabilityOnTolerance(dto.getExportDetailDto().getRepeatabilityOnTolerance());
            summaryResultDto.setReproducibilityOnContribution(dto.getExportDetailDto().getReproducibilityOnContribution());
            summaryResultDto.setReproducibilityOnTolerance(dto.getExportDetailDto().getReproducibilityOnTolerance());
            summaryResultDto.setTolerance(dto.getExportDetailDto().getTolerance());

            summaryDto.setSummaryResultDto(summaryResultDto);
            summaryDtos.add(summaryDto);
            GrrExportResultDto exportResultDto = new GrrExportResultDto();
            exportResultDto.setItemName(dto.getItemName());
            exportResultDto.setGrrAnovaAndSourceResultDto(dto.getExportDetailDto().getAnovaAndSourceResultDto());


            exportResultDto.setGrrImageDto(BuildChart.buildImage(dto.getExportDetailDto(), searchConditionDto.getParts(), searchConditionDto.getAppraisers()));
            grrExportResultDtos.add(exportResultDto);
        }
        RuntimeContext.getBean(GrrExportService.class).exportGrrSummaryDetail(configDto, summaryDtos, grrExportResultDtos);
    }
}
