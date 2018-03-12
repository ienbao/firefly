package com.dmsoft.firefly.plugin.spc.utils;

/**
 * chart data utils
 *
 * @author Can Guan
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
        Double rate = total / (normalCurveY[normalCurveY.length / 2] - min);
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
        for (int i = 0; i < cl.length * 3; i++) {
            x[i] = i / 3.0;
            y[i] = cl[i / 3];
        }
        XYData xyData = new XYData();
        xyData.setX(x);
        xyData.setY(y);
        return xyData;
    }
}
