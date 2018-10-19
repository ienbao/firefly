package com.dmsoft.firefly.plugin.yield.utils;

import java.awt.*;

public class Colur {
    //base color
    public static final Color BLACK = new Color(0x000000);
    public static final Color BLACK_L2 = new Color(0x222222);
    public static final Color BLACK_L3 = new Color(0x666666);
    public static final Color BLACK_L4 = new Color(0xAAAAAA);
    public static final Color BLACK_L5 = new Color(0xCCCCCC);

    public static final Color GREY_L1 = new Color(0xDCDCDC);
    public static final Color GREY_L2 = new Color(0xEDEDED);
    public static final Color GREY_L3 = new Color(0xF0F0F0);
    public static final Color GREY_L4 = new Color(0xF5F5F5);

    public static final Color WHITE = new Color(0xFFFFFF);

    public static final Color RED = new Color(0xEA2028);
    public static final Color RED_P10 = new Color(0xFDE8E9);
    public static final Color GREEN = new Color(0x7DAE14);
    public static final Color BLUE = new Color(0x487CF4);
    public static final Color LIGHT_BLUE = new Color(228, 235, 253);

    public static final Color LIGHT_GREEN = new Color(0x5eb511);
    public static final Color YELLOW = new Color(0xf8d251);

    //result
    public static final Color SUCCESS = GREEN;
    public static final Color ERROR = RED;

    //field
    public static final Color FIELD_BACKGROUND = WHITE;
    //panel
    public static final Color PANEL_BORDER = GREY_L2;
    public static final Color PANEL_BACKGROUND = GREY_L4;
    public static final Color PANEL_BORDER_WARNING = new Color(0xEBE5BC);
    public static final Color PANEL_BACKGROUND_WARNING = new Color(0xF9F5D7);

    //state bar
    public static final Color STATE_BAR_BORDER = GREY_L1;
    public static final Color STATE_BAR_BACKGROUND = GREY_L3;

    //tab
    public static final Color TAB_BORDER = GREY_L1;
    public static final Color TAB_BACKGROUND = GREY_L3;

    //process bar
    public static final Color PROCESSBAR_BORDER = GREY_L1;
    public static final Color PROCESSBAR_FOREGROUND = GREEN;
    public static final Color PROCESSBAR_BACKGROUND_LIGHT = WHITE;
    public static final Color PROCESSBAR_BACKGROUND_DARK = GREY_L2;

    //table
    public static final Color TABLE_BORDER = GREY_L2;
    public static final Color TABLE_CELL_BACKGROUND = GREY_L4;
    public static final Color TABLE_CELL_BACKGROUND_HOVER = GREY_L3;
    public static final Color TABLE_BACKGROUND_SELECTION = new Color(0xD9D9D9);
    public static final Color TABLE_BACKGROUND_OVER = new Color(242, 242, 242);

    //font
    public static final Color FONT_NORAML = BLACK_L2;
    public static final Color FONT_DISABLE = BLACK_L4;
    public static final Color FONT_TIPS = BLACK_L4;
    public static final Color FONT_NOTES = BLACK_L3;
    public static final Color FONT_FOREGROUND = WHITE;
    public static final Color FONT_FIX = new Color(0xe77d00);

    //level
    public static final Color LEVEL_AP = GREEN;
    public static final Color LEVEL_A = new Color(0x27B5B3);
    public static final Color LEVEL_B = new Color(0x487CF4);
    public static final Color LEVEL_C = new Color(0xF38400);
    public static final Color LEVEL_D = RED;

    //button
    public static final Color BUTTON_RED_NORMAL = RED;
    public static final Color BUTTON_RED_OVER = new Color(0xD21D24);
    public static final Color BUTTON_RED_CLICK = new Color(0xBB1A20);

    public static final Color BUTTON_GRAY_NORMAL = GREY_L4;
    public static final Color BUTTON_GRAY_NORMAL_BORDER = GREY_L1;
    public static final Color BUTTON_GRAY_OVER = GREY_L1;
    public static final Color BUTTON_GRAY_OVER_BORDER = new Color(0xC6C6C6);
    public static final Color BUTTON_GRAY_CLICK = new Color(0xC4C4C4);
    public static final Color BUTTON_GRAY_CLICK_BORDER = new Color(0xB2B2B2);

    public static final Color BUTTON_DISABLE = GREY_L4;
    public static final Color BUTTON_DISABLE_BORDER = GREY_L1;

    public static final Color TABLE_ROW_HING_LIGHT = new Color(0xF8D251);

    //tree
    public static final Color TREE_FOREGROUND_MARK = LEVEL_B;

    //Grr TestItem Table
    public static final Color TABLE_BACKGROUND_AVG = new Color(0xF2F2F2);
    public static final Color TABLE_BACKGROUND_TOTAL = new Color(0xE5E5E5);

    public static final Color[] RAW_VALUES = {
            new Color(0x0f9893),
            new Color(0x364b97),


            new Color(0xf16000),
            new Color(0xe9027f),
            new Color(0x8dad00),
            new Color(0x6b3aaa),
            new Color(0x27a521),
            new Color(0xb7e000),

            new Color(0xae0b9c),
            new Color(0x0f70a6),
    };

    //grr chart
    public static final Color GRAYBLUE = new Color(0x333366);
    public static final Color DEGREEN = new Color(0x568A89);
    public static final Color ORANGE = new Color(0xFF7F00);
    public static final Color DEYELLOW = new Color(0xFFCC33);
    public static final Color MAGENTA = new Color(0xFF00FF);
    public static final Color NAVY = new Color(0x000080);

    /**
     * Check the YUV color from RGB whether it needs to use light font.
     *
     * @param color color
     * @return true/false
     */
    public static boolean isUsingLightFont(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        double grayLevel = r * 0.299 + g * 0.587 + b * 0.114;
        if (grayLevel >= 192) {
            return false;
        }
        return true;
    }
}
