package com.dmsoft.firefly.plugin.yield.utils;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

/**
 * Created by cherry on 2018/3/15.
 */
public class ScrollPaneValueUtils {

    /**
     * Set scroll pane vertical scroll bar location
     *
     * @param scrollPane scrollPane
     * @param node       it will move to node
     */
    public static void setScrollVerticalValue(ScrollPane scrollPane, Node node) {
        final Bounds visibleBounds = scrollPane.getViewportBounds();
        double totalHeight = scrollPane.getContent().getBoundsInLocal().getHeight();
        double visibleHeight = visibleBounds.getHeight();
        double scrollHeight = totalHeight - visibleHeight;
        double vValue = scrollPane.getVvalue();
        double maxY = visibleHeight + vValue * scrollHeight;
        if (node.getBoundsInParent().getMinY() < maxY - visibleHeight) {
            double y = node.getBoundsInParent().getMinY();
            scrollPane.setVvalue(y / scrollHeight);
        } else if (node.getBoundsInParent().getMaxY() > maxY) {
            double y = node.getBoundsInParent().getMinY();
            scrollPane.setVvalue(y / scrollHeight);
        }
    }
}
