package com.dmsoft.firefly.plugin.spc.charts.utils;

import javafx.scene.paint.Color;

/**
 * Created by cherry on 2018/3/5.
 */
public class ColorUtils {

    public static String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}
