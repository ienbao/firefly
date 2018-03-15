package com.dmsoft.firefly.plugin.grr.utils;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

/**
 * Created by cherry on 2018/3/15.
 */
public class ScrollPaneValueUtils {

    public static void setScrollVerticalValue(ScrollPane scrollPane, Node node) {
        final Bounds visibleBounds = scrollPane.getViewportBounds();
        double totalHeight = scrollPane.getContent().getBoundsInLocal().getHeight();//总高度
        double visibleHeight = visibleBounds.getHeight();//可见高度
        double scrollHeight = totalHeight - visibleHeight;//待滚动高度
        double vValue = scrollPane.getVvalue();
        double maxY = visibleHeight + vValue * scrollHeight;//当前显示的maxY
        if (node.getBoundsInParent().getMinY() < maxY - visibleHeight) {//超出上边
            double y = node.getBoundsInParent().getMinY();
            scrollPane.setVvalue(y / scrollHeight);
        } else if (node.getBoundsInParent().getMaxY() > maxY) {//超出下边
            double y = node.getBoundsInParent().getMinY();
            scrollPane.setVvalue(y / scrollHeight);
        }
    }
}
