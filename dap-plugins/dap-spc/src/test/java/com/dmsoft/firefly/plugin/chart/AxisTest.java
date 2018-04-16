package com.dmsoft.firefly.plugin.chart;

import com.dmsoft.firefly.gui.components.chart.ChartOperatorUtils;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * Created by cherry on 2018/4/16.
 */
public class AxisTest {

    static Logger logger = LoggerFactory.getLogger(AxisTest.class);

    public static void main(String[] args) {
        int corNumber = 3;
        double corMax = 0.87;
        double corMin = 0;
        corMax += (corMax - corMin) * UIConstant.Y_FACTOR;
        corMin -= (corMax - corMin) * UIConstant.Y_FACTOR;
        Map<String, Object> axisRangeData = ChartOperatorUtils.getAdjustAxisRangeData(corMax, corMin, corNumber);
        logger.info("corMax: {}, corMin: {}, corNumber: {}",
                axisRangeData.get(ChartOperatorUtils.KEY_MAX),
                axisRangeData.get(ChartOperatorUtils.KEY_MIN),
                axisRangeData.get(ChartOperatorUtils.KEY_CORNUMBER));
    }
}
