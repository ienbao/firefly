package com.dmsoft.firefly.gui.component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.util.Stack;

/**
 * extended stack pane for adding pane or node api
 *
 * @author Julia
 */
public class ContentStackPane extends StackPane {
    private static final int UNDEFINED = -1;
    private int curPageIdx = UNDEFINED;
    private ObservableList<Node> pages = FXCollections.observableArrayList();
    private Stack<Integer> history = new Stack<>();

    public ContentStackPane() {

    }

    /**
     * constructor
     *
     * @param nodes panes node
     */
    public ContentStackPane(Node... nodes) {
        pages.addAll(nodes);
        navTo(0);
    }

    /**
     * method to add pane
     *
     * @param node node
     */
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

    /**
     * mehtod to remove pane by index
     *
     * @param index index
     */
    public void remove(int index) {
        pages.remove(index);
    }

    /**
     * method to remove all panes
     */
    public void removeAll() {
        pages.clear();
    }

    /**
     * method to get has next page or not
     *
     * @return true : has next page, false : has no next page
     */
    public boolean hasNextPage() {
        return (curPageIdx < pages.size() - 1);
    }

    /**
     * method to get has previous page or not
     *
     * @return true : has previous page, false : has no previous page
     */
    public boolean hasPreviousPage() {
        return !history.isEmpty();
    }

    /**
     * method to rearrange pane
     *
     * @param nextPageIdx nav to which pane
     */
    public void navTo(int nextPageIdx) {
        navTo(nextPageIdx, true);
    }

    /**
     * method to rearrange pane
     *
     * @param nextPageIndex nav to which pane
     * @param pushHistory   push into history
     */
    public void navTo(int nextPageIndex, boolean pushHistory) {
        if (nextPageIndex < 0 || nextPageIndex >= pages.size()) {
            return;
        }
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

    /**
     * method to rearrange pane
     *
     * @param id pane id to nav to
     */
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
