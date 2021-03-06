package com.dmsoft.firefly.plugin.spc.charts.view;

import com.dmsoft.firefly.plugin.spc.charts.select.SelectCallBack;
import com.dmsoft.firefly.plugin.spc.charts.utils.enums.Orientation;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;

import java.util.List;
import java.util.Set;

/**
 * Created by cherry on 2018/2/8.
 */
public class ChartOperateButton extends Button {

    private Popup popup;
    private ListView<String> listView;
    private SelectCallBack selectCallBack;

    private final double threshold = 6;
    private boolean selected = DEFAULT_SELECTED;
    private static final boolean DEFAULT_SELECTED = false;
    private Orientation orientation = DEFAULT_ORIENTATION;
    private static final Orientation DEFAULT_ORIENTATION = Orientation.UPLEFT;

    private Set<String> selectedSets = Sets.newLinkedHashSet();
    private Set<String> disableRules = Sets.newLinkedHashSet();

    /**
     * Constructor for ChartOperateButton
     */
    public ChartOperateButton() {
        this(DEFAULT_SELECTED, DEFAULT_ORIENTATION);
    }

    /**
     * Constructor for ChartOperateButton
     *
     * @param orientation popup of orientation
     */
    public ChartOperateButton(Orientation orientation) {
        this("", DEFAULT_SELECTED, orientation);
    }

    /**
     * Constructor for ChartOperateButton
     *
     * @param selected list all data is checked
     */
    public ChartOperateButton(boolean selected) {
        this("", selected, DEFAULT_ORIENTATION);
    }

    /**
     * Constructor for ChartOperateButton
     *
     * @param selected    list all data is checked
     * @param orientation popup of orientation
     */
    public ChartOperateButton(boolean selected, Orientation orientation) {
        this("", selected, orientation);
    }

    /**
     * Constructor for ChartOperateButton
     *
     * @param name        text of button
     * @param selected    list all data is checked
     * @param orientation popup of orientation
     */
    public ChartOperateButton(String name, boolean selected, Orientation orientation) {
        super(name);
        this.selected = selected;
        this.orientation = orientation;
        listView = new ListView<>();
        listView.setCellFactory(e -> buildCallback());
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

    /**
     * Set button tooltip content
     *
     * @param content tooltip content
     */
    public void setButtonTooltipContent(String content) {
        Tooltip.install(this, new Tooltip(content));
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

    private ListCell buildCallback() {

        return new ListCell() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    CheckBox checkBox = new CheckBox();
                    Label label = new Label(item == null ? "" : String.valueOf(item));
                    boolean selectedBool = selectedSets.contains(item);
                    boolean disabled = disableRules.contains(item);
                    BooleanProperty observable = new SimpleBooleanProperty(selectedBool);
                    checkBox.selectedProperty().bindBidirectional(observable);
                    observable.setValue(selectedBool);
                    checkBox.setOnMouseClicked(event -> {
                        updateSelectedSets(checkBox.isSelected(), (String) item);
                        if (selectCallBack != null) {
                            selectCallBack.execute((String) item, checkBox.isSelected(), selectedSets);
                        }
                    });
//                    observable.addListener((obs, wasSelected, isNowSelected) -> {
//                        updateSelectedSets(isNowSelected, (String) item);
//                        if (selectCallBack != null) {
//                            selectCallBack.execute((String) item, isNowSelected, selectedSets);
//                        }
//                    });
                    if (disabled) {
                        checkBox.setDisable(true);
                    }
                    HBox cell = new HBox(checkBox, label);
                    setGraphic(cell);
                }
            }
        };
    }

    private void updateSelectedSets(boolean isNowSelected, String item) {
        if (isNowSelected) {
            selectedSets.add(item);
        } else {
            selectedSets.remove(item);
        }
    }

    /**
     * Set list view data
     *
     * @param data list view data
     */
    public void setListViewData(List<String> data) {
        data = (data == null) ? Lists.newArrayList() : data;
        listView.getItems().addAll(data);
    }

    /**
     * Remove data
     *
     * @param data list item data
     */
    public void removeData(List<String> data) {
        if (!listView.getItems().isEmpty()) {
            listView.getItems().removeAll(data);
            selectedSets.removeAll(data);
            disableRules.removeAll(data);
            listView.refresh();
        }
    }

    public void addRuleData(List<String> data) {
        listView.getItems().clear();
        selectedSets.clear();
        if (data != null) {
            listView.getItems().addAll(data);
        }
        listView.refresh();
    }

    /**
     * Set list view size
     *
     * @param width  list width
     * @param height list height
     */
    public void setListViewSize(double width, double height) {
        listView.setPrefWidth(width);
        listView.setPrefHeight(height);
    }

    public void setSelectCallBack(SelectCallBack selectCallBack) {
        this.selectCallBack = selectCallBack;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    /**
     * Set selected items
     *
     * @param selectedSets selected items
     */
    public void setSelectedSets(Set<String> selectedSets) {
        if (this.selectedSets == null) {
            return;
        }
        this.selectedSets.clear();
        this.selectedSets = selectedSets;
        this.listView.refresh();
    }

    /**
     * Get selected items
     *
     * @return selected items
     */
    public Set<String> getSelectedSets() {
        return selectedSets;
    }

    /**
     * Set disable rule items
     *
     * @param disableRules disable rule items
     */
    public void setDisableRules(Set<String> disableRules) {
        this.disableRules.clear();
        this.disableRules = disableRules;
        this.listView.refresh();
    }
}
