package com.dmsoft.firefly.gui.utils;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.scene.Node;

public class NodeHelper {

    public static <T extends Node> T withState(T node, String state) {
        if (node != null && state != null) {
            // stop user from being able to change state
            node.setMouseTransparent(true);
            node.setFocusTraversable(false);
            // set state to chosen state
            final String[] pseudoClasses = (state).split("[\\s,]+");
            for (String pseudoClass : pseudoClasses) {
                node.pseudoClassStateChanged(PseudoClass.getPseudoClass(pseudoClass), true);
            }
        }
        return node;
    }

    public static <T extends Node> T withState(final T node, final String state, final String subNodeStyleClass, final String subNodeState) {
        withState(node, state);
        Platform.runLater(() -> withState(node.lookup(subNodeStyleClass), subNodeState));
        return node;
    }
}
