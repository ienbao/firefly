package com.dmsoft.firefly.plugin.grr.charts;

import com.dmsoft.firefly.plugin.grr.utils.enums.Orientation;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Popup;
import javafx.util.Callback;

import java.util.List;
import java.util.Set;

/**
 * Created by cherry on 2018/2/8.
 */
public class ChartOperateButton extends Button {

    private Popup popup;
    private ListView<String> listView;
    private SelectCallBack selectCallBack;

    private boolean selected = DEFAULTSELECTED;
    private Orientation orientation = DEFAULTORIENTATION;

    private static final Orientation DEFAULTORIENTATION = Orientation.UPLEFT;
    private static final boolean DEFAULTSELECTED = false;
    private final double threshold = 6;
    private Set<String> selectedSets = Sets.newHashSet();

    /**
     * Construct a new ChartOperateButton
     */
    public ChartOperateButton() {
        this(DEFAULTSELECTED, DEFAULTORIENTATION);
    }

    /**
     * Construct a new ChartOperateButton with given orientation
     *
     * @param orientation orientation
     */
    public ChartOperateButton(Orientation orientation) {
        this("", DEFAULTSELECTED, orientation);
    }

    /**
     * Construct a new ChartOperateButton with given selected
     *
     * @param selected whether is true or false
     */
    public ChartOperateButton(boolean selected) {
        this("", selected, DEFAULTORIENTATION);
    }

    /**
     * Construct a new ChartOperateButton with given selected and orientation
     *
     * @param selected    whether is true or false
     * @param orientation orientation
     */
    public ChartOperateButton(boolean selected, Orientation orientation) {
        this("", selected, orientation);
    }

    /**
     * Construct a new ChartOperateButton with given name, selected and orientation
     *
     * @param name        button text
     * @param selected    whether is true or false
     * @param orientation orientation
     */
    public ChartOperateButton(String name, boolean selected, Orientation orientation) {
        super(name);
        this.selected = selected;
        this.orientation = orientation;
        listView = new ListView<>();
        listView.setCellFactory(buildCallback());
        popup = new Popup();
        popup.getContent().addAll(listView);
        popup.setAutoHide(true);
        this.setMaxWidth(20);
        this.setMinWidth(20);
        this.setPrefWidth(20);
        this.setMaxHeight(20);
        this.setMinHeight(20);
        this.setPrefHeight(20);
        this.getStyleClass().add("btn-icon-b");
        Button button = this;
        this.setOnMousePressed(event -> showPopupForButton(button));
    }

    private void showPopupForButton(Button button) {
        double x = button.getScene().getWindow().getX() + button.getScene().getX() + button.localToScene(0, 0).getX();
        double y = button.getScene().getWindow().getY() + button.getScene().getY() + button.localToScene(0, 0).getY();
        if (orientation.equals(Orientation.UPLEFT)) {
            x -= listView.getPrefWidth();
            x += button.getPrefWidth();
            y -= listView.getPrefHeight();
            y -= threshold;
        } else if (orientation.equals(Orientation.BOTTOMLEFT)) {
            x -= listView.getPrefWidth();
            x += button.getPrefWidth();
            y += button.getPrefHeight();
            y += threshold;
        }
        popup.show(button, x, y);
    }

    private Callback buildCallback() {
        return CheckBoxListCell.forListView(item -> {
            boolean hasSelected = selectedSets.contains(item);
            BooleanProperty observable = new SimpleBooleanProperty(hasSelected);
            observable.setValue(hasSelected);
            observable.addListener((obs, wasSelected, isNowSelected) -> {
                updateSelectedSets(isNowSelected, (String) item);
                if (selectCallBack != null) {
                    selectCallBack.execute((String) item, isNowSelected, selectedSets);
                }
            });
            return observable;
        });
    }

    private void updateSelectedSets(boolean isNowSelected, String item) {
        if (isNowSelected) {
            selectedSets.add(item);
        } else {
            selectedSets.remove(item);
        }
    }

    /**
     * Set list view data source
     *
     * @param data data source
     */
    public void setListViewData(List<String> data) {
        data = (data == null) ? Lists.newArrayList() : data;
        listView.getItems().addAll(data);
    }

    /**
     * Set list view size
     *
     * @param width  width
     * @param height height
     */
    public void setListViewSize(double width, double height) {
        listView.setPrefWidth(width);
        listView.setPrefHeight(height);
    }

    /**
     * Set selected call back function
     *
     * @param selectCallBack selected call back function
     */
    public void setSelectCallBack(SelectCallBack selectCallBack) {
        this.selectCallBack = selectCallBack;
    }

    /**
     * Set popup orientation
     *
     * @param orientation orientation
     */
    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    /**
     * Set selectedSets
     *
     * @param selectedSets selectedSets
     */
    public void setSelectedSets(Set<String> selectedSets) {
        this.selectedSets = selectedSets;
        this.listView.refresh();
    }

    /**
     * Get selectedSets
     *
     * @return selectedSets
     */
    public Set<String> getSelectedSets() {
        return selectedSets;
    }
}
