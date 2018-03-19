package com.dmsoft.firefly.gui.components.utils;

import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

public class TooltipUtil {
    private static Tooltip normalTooltip;
    private static Tooltip tooltip;

    /**
     * method to install normal tooltip
     *
     * @param msg tip msg
     * @return tooltip
     */
    public static Tooltip installNormalTooltip(Node node, String msg) {
        if (normalTooltip == null) {
            normalTooltip = new Tooltip();
            normalTooltip.setText(msg);
        } else {
            normalTooltip.setText(msg);
        }
        Tooltip.install(node, normalTooltip);
        return normalTooltip;
    }

    /**
     * method to uninstall normal tooltip
     *
     * @param node node
     */
    public static void uninstallNormalTooltip(Node node) {
        if (normalTooltip != null && node != null) {
            Tooltip.uninstall(node, normalTooltip);
        }
    }

    /**
     * method to install warn tooltip
     *
     * @param msg tip msg
     * @return tooltip
     */
    public static Tooltip installWarnTooltip(Node node, String msg) {
        if (tooltip == null) {
            Label warnLbl = new Label();
            warnLbl.getStyleClass().add("icon-warn-svg");
            warnLbl.setPrefWidth(26);
            warnLbl.setMinWidth(26);
            warnLbl.setMaxWidth(26);
            tooltip = new Tooltip();
            tooltip.setText(msg);
            tooltip.setGraphic(warnLbl);
            tooltip.setContentDisplay(ContentDisplay.LEFT);
            tooltip.getStyleClass().add("tooltip-warn");
        } else {
            tooltip.getStyleClass().add("tooltip-warn");
            tooltip.setText(msg);
        }
        Tooltip.install(node, tooltip);
        return tooltip;
    }

    /**
     * method to uninstall tooltip
     *
     * @param node node
     */
    public static void uninstallWarnTooltip(Node node) {
        if (tooltip != null && node != null) {
            Tooltip.uninstall(node, tooltip);
        }
    }
}
