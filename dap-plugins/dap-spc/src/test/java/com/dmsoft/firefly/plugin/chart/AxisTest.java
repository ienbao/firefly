package com.dmsoft.firefly.plugin.chart;

import com.dmsoft.firefly.gui.components.chart.ChartOperatorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * Created by cherry on 2018/4/16.
 */
public class AxisTest {

    static Logger logger = LoggerFactory.getLogger(AxisTest.class);

    public static void main(String[] args) {
        int corNumber = 4;
        double corMax = 35;
        double corMin = 0;
        Map<String, Object> axisRangeData = ChartOperatorUtils.getAdjustAxisRangeData(corMax, corMin, corNumber);
        logger.info("corMax: {}, corMin: {}, corNumber: {}",
                axisRangeData.get(ChartOperatorUtils.KEY_MAX),
                axisRangeData.get(ChartOperatorUtils.KEY_MIN),
                axisRangeData.get(ChartOperatorUtils.KEY_CORNUMBER));
    }
}
