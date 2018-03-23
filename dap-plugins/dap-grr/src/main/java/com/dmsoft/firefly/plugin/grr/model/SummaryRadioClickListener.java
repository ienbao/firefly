package com.dmsoft.firefly.plugin.grr.model;

import com.dmsoft.firefly.plugin.grr.dto.GrrSummaryDto;

/**
 * Created by cherry on 2018/3/22.
 */
public interface SummaryRadioClickListener {

    void executeAnalyzeDetail(GrrSummaryDto grrSummaryDto, String tolerence);
}
