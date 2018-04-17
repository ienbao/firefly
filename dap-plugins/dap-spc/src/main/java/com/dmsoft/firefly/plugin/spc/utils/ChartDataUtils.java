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
        if (cl == null) {
            return null;
        }
        Double[] x = new Double[cl.length * 3 - 1];
        Double[] y = new Double[cl.length * 3 - 1];
        final Double threshold = 0.5;
        int j = -1;
        for (int i = 0; i < cl.length * 3 - 1; i++) {
            if (i % 3 == 0) {
                j++;
            }
            if (i % 3 == 1) {
                x[i] = Double.valueOf(j + 1);
            }
            if (i % 3 == 0) {
                x[i] = (j + 1) - threshold;
            }
            if (i % 3 == 2) {
                x[i] = (j + 1) + threshold;
            }
            y[i] = cl[j];
        }

        XYData xyData = new XYData();
        xyData.setX(x);
        xyData.setY(y);
        return xyData;
    }
}
