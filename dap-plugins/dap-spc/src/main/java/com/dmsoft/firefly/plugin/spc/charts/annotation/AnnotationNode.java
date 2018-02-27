package com.dmsoft.firefly.plugin.spc.charts.annotation;

import javafx.scene.Node;

/**
 * Created by cherry on 2018/2/25.
 */
public class AnnotationNode<X, Y> {

    private final Node node;
    private String color;
    private X x;
    private Y y;

    public AnnotationNode(Node node, String color, X x, Y y) {
        this.node = node;
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public AnnotationNode(Node node, X x, Y y) {
        this.node = node;
        this.x = x;
        this.y = y;
    }

    public Node getNode() {
        return node;
    }

    public X getX() {
        return x;
    }

    public void setX(X x) {
        this.x = x;
    }

    public Y getY() {
        return y;
    }

    public void setY(Y y) {
        this.y = y;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
