package com.dmsoft.firefly.sdk.utils;

import java.awt.*;
import java.math.BigDecimal;

/**
 * Created by Can.Guan on 2017/3/1.
 */
public class ColorUtils {
    private static final double DOUBLE_255 = 255.0;

    /**
     * method to get color
     *
     * @param bgColor background color
     * @param fgColor foreground color
     * @return new color
     */
    public static Color getColor(Color bgColor, Color fgColor) {
        Double redDouble = bgColor.getRed() * (fgColor.getRed() / DOUBLE_255);
        int red = new BigDecimal(redDouble).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        Double greenDouble = bgColor.getGreen() * (fgColor.getGreen() / DOUBLE_255);
        int green = new BigDecimal(greenDouble).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        Double blueDouble = bgColor.getBlue() * (fgColor.getBlue() / DOUBLE_255);
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
     *
     * @param color color
     * @return hex color
     */
    public static String toHexFromColor(Color color) {
        String r, g, b;
        StringBuilder su = new StringBuilder();
        r = Integer.toHexString(color.getRed());
        g = Integer.toHexString(color.getGreen());
        b = Integer.toHexString(color.getBlue());
        r = r.length() == 1 ? "0" + r : r;
        g = g.length() == 1 ? "0" + g : g;
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

    public static String toHexFromFXColor(javafx.scene.paint.Color fxColor) {//设置十六进制的颜色编码
        String r, g, b;
        Color awtColor = toAwtColorFromFxColor(fxColor);//设置颜色
        StringBuilder su = new StringBuilder();
        if (awtColor == null) {
            return su.toString();
        }
        r = Integer.toHexString(awtColor.getRed());
        g = Integer.toHexString(awtColor.getGreen());
        b = Integer.toHexString(awtColor.getBlue());
        r = r.length() == 1 ? "0" + r : r;
        g = g.length() == 1 ? "0" + g : g;
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

    /**
     * toFxColorFromAwtColor
     *
     * @param colors awt colors
     * @return
     */
    public static javafx.scene.paint.Color[] toFxColorFromAwtColor(Color[] colors) {
        if (colors == null) {
            return null;
        }
        javafx.scene.paint.Color[] paintColors = new javafx.scene.paint.Color[colors.length];

        for (int i = 0; i < colors.length; i++) {
            Color awtColor = colors[i];
            javafx.scene.paint.Color fxColor = toFxColorFromAwtColor(awtColor);
            paintColors[i] = fxColor;
        }
        return paintColors;
    }

    /**
     * toFxColorFromAwtColor
     *
     * @param awtColor awt colors
     * @return
     */
    public static javafx.scene.paint.Color toFxColorFromAwtColor(Color awtColor) {
        if (awtColor == null) {
            return null;
        }
        int r = awtColor.getRed();
        int g = awtColor.getGreen();
        int b = awtColor.getBlue();
        int a = awtColor.getAlpha();
        double opacity = a / 255.0;
        javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(r, g, b, opacity);
        return fxColor;
    }

    /**
     * toAwtColorFromFxColor
     *
     * @param fxColor fx color
     * @return
     */
    public static Color toAwtColorFromFxColor(javafx.scene.paint.Color fxColor) {
        if (fxColor == null) {
            return null;
        }
        int r = (int) Math.round(fxColor.getRed() * 255.0);
        int g = (int) Math.round(fxColor.getGreen() * 255.0);
        int b = (int) Math.round(fxColor.getBlue() * 255.0);
        int a = (int) Math.round(fxColor.getOpacity() * 255.0);
        return new Color(r, g, b, a);
    }
}
