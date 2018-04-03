package com.dmsoft.firefly.plugin.spc.utils;

/**
 * chart data utils
 *
 * @author Can Guan
 * @update Cherry 2018/04/03
 */
public class ChartDataUtils {
    /**
     * method to rebase data
     *
     * @param histY        histogram y data
     * @param normalCurveY normal curve y data
     * @return rebased normal curve y array
     */
    public static Double[] rebaseNormalCurveData(Double[] histY, Double[] normalCurveY) {
        Double[] result = new Double[normalCurveY.length];
        Double max = Double.NEGATIVE_INFINITY;
        Double total = 0.0;
        for (Double d : histY) {
            if (d > max) {
                max = d;
            }
            total = total + d;
        }
        Double min = normalCurveY[0];
        Double rate = total * 0.33 / (normalCurveY[normalCurveY.length / 2] - min);
        for (int i = 0; i < normalCurveY.length; i++) {
            result[i] = (normalCurveY[i] - min) > 0 ? (normalCurveY[i] - min) * rate : 0;
        }
        return result;
    }

    /**
     * method to fold cl data
     *
     * @param cl cl data
     * @return xy data
     */
    public static XYData foldCLData(Double[] cl) {
        Double[] x = new Double[cl.length * 3];
        Double[] y = new Double[cl.length * 3];
        Double beforeThreshold = 1.0 / 3.0;
        Double afterThreshold = 1.0 / 3.0;
        for (int i = 0; i < cl.length * 3; i++) {
            if (i % 3 == 1) {
                x[i] = (i + 2) / 3.0;
            }
            if (i % 3 == 0) {
                x[i] = (i + 2) / 3.0 - beforeThreshold;
            }
            if (i % 3 == 2) {
                x[i] = x[i - 1] + afterThreshold;
            }
            y[i] = cl[i / 3];
        }

//        for (int i = 0; i < cl.length * 3; i++) {
//            x[i] = (i + 1) - 0.5;
//            x[i + 1] = Double.valueOf(i + 1);
//            x[i + 2] = (i + 1) + 0.5;
//            y[i] = cl[i / 3];
//            y[i + 1] = cl[i / 3];
//            y[i + 2] = cl[i / 3];
//        }
        XYData xyData = new XYData();
        xyData.setX(x);
        xyData.setY(y);
        return xyData;
    }
}
