package com.dmsoft.firefly.plugin.grr.handler;

import com.dmsoft.firefly.plugin.grr.dto.*;
import com.dmsoft.firefly.plugin.grr.dto.analysis.GrrSummaryResultDto;
import com.dmsoft.firefly.plugin.grr.utils.enums.GrrExportItemKey;
import com.dmsoft.firefly.plugin.grr.controller.BuildChart;
import com.dmsoft.firefly.plugin.grr.service.GrrExportService;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.job.core.AbstractBasicJobHandler;
import com.dmsoft.firefly.sdk.job.core.JobContext;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * handler for detail result export
 *
 * @author Can Guan
 */
public class ExportDetailResultHandler extends AbstractBasicJobHandler {

    private GrrExportService grrExportService;
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
            if (configDto.getGrrConfigDto() != null && configDto.getGrrConfigDto().getExport().containsKey(GrrExportItemKey.EXPORT_DETAIL_SHEET.getCode())) {
                Map<String, Boolean> data = configDto.getGrrConfigDto().getExport();
                if (!data.get(GrrExportItemKey.EXPORT_DETAIL_SHEET.getCode())) {
                    exportResultDto.setGrrAnovaAndSourceResultDto(null);
                    exportResultDto.setGrrImageDto(null);
                } else {

                    if (data.containsKey(GrrExportItemKey.EXPORT_SOURCE_RESULT.getCode())) {
                        if (data.get(GrrExportItemKey.EXPORT_SOURCE_RESULT.getCode())) {
                            exportResultDto.setGrrAnovaAndSourceResultDto(dto.getExportDetailDto().getAnovaAndSourceResultDto());
                        } else {
                            exportResultDto.setGrrAnovaAndSourceResultDto(null);
                        }
                    }

                    if (data.containsKey(GrrExportItemKey.EXPORT_CHART.getCode())) {
                        if (data.get(GrrExportItemKey.EXPORT_CHART.getCode())) {
                            exportResultDto.setGrrImageDto(BuildChart.buildImage(dto.getExportDetailDto(), searchConditionDto, configDto.getGrrConfigDto().getExport()));
                        } else {
                            exportResultDto.setGrrImageDto(null);
                        }
                    }
                }
            }
//            exportResultDto.setGrrAnovaAndSourceResultDto(dto.getExportDetailDto().getAnovaAndSourceResultDto());
//            exportResultDto.setGrrImageDto(BuildChart.buildImage(dto.getExportDetailDto(), searchConditionDto.getParts(), searchConditionDto.getAppraisers(), configDto.getGrrConfigDto().getExport()));
            grrExportResultDtos.add(exportResultDto);
        }
        String path =this.grrExportService.exportGrrSummaryDetail(configDto, summaryDtos, grrExportResultDtos);
        context.put(ParamKeys.EXPORT_PATH, path);
    }
}
