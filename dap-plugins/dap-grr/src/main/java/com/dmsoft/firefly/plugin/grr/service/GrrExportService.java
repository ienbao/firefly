/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.plugin.grr.service;


import com.dmsoft.firefly.plugin.grr.dto.GrrExportConfigDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrExportResultDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrSummaryDto;
import com.dmsoft.firefly.plugin.grr.dto.GrrSummaryExportDto;

import java.util.List;

/**
 * Created by Garen.Pang on 2018/3/15.
 */
public interface GrrExportService {

    String exportGrrSummary(GrrExportConfigDto grrExportConfigDto, List<GrrSummaryDto> grrSummaryExportDtos);

    String exportGrrSummaryDetail(GrrExportConfigDto grrExportConfigDto, List<GrrSummaryDto> grrSummaryExportDtos, List<GrrExportResultDto> grrExportResultDtos);
}
