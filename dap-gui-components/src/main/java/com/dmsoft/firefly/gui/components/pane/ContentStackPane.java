package com.dmsoft.firefly.gui.components.pane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.util.Stack;

public class ContentStackPane extends StackPane {
    private static final int UNDEFINED = -1;
    private ObservableList<Node> pages = FXCollections.observableArrayList();
    private Stack<Integer> history = new Stack<>();
    protected int curPageIdx = UNDEFINED;

    public ContentStackPane(Node... nodes) {
        pages.addAll(nodes);
        navTo(0);
    }

    public void add(Node node) {
        if (node == null) {
            return;
        }
        String id = node.getId();
        if (id != null) {
            pages.stream()
                    .filter(page -> id.equals(page.getId()))
                    .findFirst()
                    .ifPresent(page -> {
                                remove(pages.indexOf(page));
                            }
                    );
        }
        pages.add(node);
    }

    public void remove(int index) {
        pages.remove(index);
    }

    public void removeAll() {
         pages.clear();
    }

    public boolean hasNextPage() {
        return (curPageIdx < pages.size() - 1);
    }

    public boolean hasPreviousPage() {
        return !history.isEmpty();
    }

    public void navTo(int nextPageIdx) {
        navTo(nextPageIdx, true);
    }

    public void navTo(int nextPageIndex, boolean pushHistory) {
        if (nextPageIndex < 0 || nextPageIndex >= pages.size()) return;
        if (curPageIdx != UNDEFINED) {
            if (pushHistory) {
                history.push(curPageIdx);
            }
        }

        Node nextPage = pages.get(nextPageIndex);
        curPageIdx = nextPageIndex;
        getChildren().clear();
        getChildren().add(nextPage);
    }

    public void navTo(String id) {
        if (id == null) {
            return;
        }

        pages.stream()
                .filter(page -> id.equals(page.getId()))
                .findFirst()
                .ifPresent(page -> navTo(pages.indexOf(page))
                );
    }
}
