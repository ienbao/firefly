package com.dmsoft.firefly.sdk.utils;

import java.awt.*;
import java.math.BigDecimal;

/**
 * Created by Can.Guan on 2017/3/1.
 */
public class ColorUtils {
    /**
     * method to get color
     *
     * @param bgColor background color
     * @param fgColor foreground color
     * @return new color
     */
    public static Color getColor(Color bgColor, Color fgColor) {
        Double redDouble = bgColor.getRed() * (Double.valueOf(fgColor.getRed()) / 255);
        int red = new BigDecimal(redDouble).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        Double greenDouble = bgColor.getGreen() * (Double.valueOf(fgColor.getGreen()) / 255);
        int green = new BigDecimal(greenDouble).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        Double blueDouble = bgColor.getBlue() * (Double.valueOf(fgColor.getBlue()) / 255);
        int blue = new BigDecimal(blueDouble).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        return new Color(red, green, blue, bgColor.getAlpha());
    }

    /**
     * method to get new color from color and transparent
     *
     * @param color        source color
     * @param transparency alpha
     * @return new color
     */
    public static Color getTransparentColor(Color color, double transparency) {
        Double redDouble = 255 * (1 - transparency) + (color.getRed() * transparency);
        int red = new BigDecimal(redDouble).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        Double greenDouble = 255 * (1 - transparency) + (color.getGreen() * transparency);
        int green = new BigDecimal(greenDouble).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        Double blueDouble = 255 * (1 - transparency) + (color.getBlue() * transparency);
        int blue = new BigDecimal(blueDouble).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        return new Color(red, green, blue);
    }

    /**
     * method to make Color to String
     * @param color color
     * @return
     */
    public static String toHexFromColor(Color color){
        String r,g,b;
        StringBuilder su = new StringBuilder();
        r = Integer.toHexString(color.getRed());
        g = Integer.toHexString(color.getGreen());
        b = Integer.toHexString(color.getBlue());
        r = r.length() == 1 ? "0" + r : r;
        g = g.length() ==1 ? "0" +g : g;
        b = b.length() == 1 ? "0" + b : b;
        r = r.toUpperCase();
        g = g.toUpperCase();
        b = b.toUpperCase();
        su.append("#");
        su.append(r);
        su.append(g);
        su.append(b);
        //0xFF0000FF
        return su.toString();
    }
}
