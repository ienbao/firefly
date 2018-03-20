package com.dmsoft.firefly.gui.components.utils;

import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

public class TooltipUtil {
    private static String TOOLTIP_PROP_KEY = "javafx.scene.control.Tooltip";

    /**
     * method to install normal tooltip
     *
     * @param node node
     * @param msg  tip msg
     * @return tooltip
     */
    public static Tooltip installNormalTooltip(Node node, String msg) {
        Tooltip normalTooltip = new Tooltip();
        normalTooltip.setText(msg);
        Tooltip.install(node, normalTooltip);
        return normalTooltip;
    }

    /**
     * method to uninstall normal tooltip
     *
     * @param node node
     */
    public static void uninstallNormalTooltip(Node node) {
        if (node != null && node.getProperties() != null && node.getProperties().containsKey(TOOLTIP_PROP_KEY)) {
            Tooltip t = (Tooltip) node.getProperties().get(TOOLTIP_PROP_KEY);
            Tooltip.uninstall(node, t);
        }
    }

    /**
     * method to install warn tooltip
     *
     * @param node node
     * @param msg  tip msg
     * @return tooltip
     */
    public static Tooltip installWarnTooltip(Node node, String msg) {
        Label warnLbl = new Label();
        warnLbl.getStyleClass().add("icon-warn-svg");
        warnLbl.setPrefWidth(26);
        warnLbl.setMinWidth(26);
        warnLbl.setMaxWidth(26);
        Tooltip tooltip = new Tooltip();
        tooltip.setText(msg);
        tooltip.setGraphic(warnLbl);
        tooltip.setContentDisplay(ContentDisplay.LEFT);
        tooltip.getStyleClass().add("tooltip-warn");
        Tooltip.install(node, tooltip);
        return tooltip;
    }

    /**
     * method to uninstall tooltip
     *
     * @param node node
     */
    public static void uninstallWarnTooltip(Node node) {
        if (node != null && node.getProperties() != null && node.getProperties().containsKey(TOOLTIP_PROP_KEY)) {
            Tooltip t = (Tooltip) node.getProperties().get(TOOLTIP_PROP_KEY);
            Tooltip.uninstall(node, t);
        }
    }
}
