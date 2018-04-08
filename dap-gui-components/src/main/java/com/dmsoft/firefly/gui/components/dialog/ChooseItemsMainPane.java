package com.dmsoft.firefly.gui.components.dialog;

import com.dmsoft.firefly.gui.components.utils.CommonResourceMassages;
import com.dmsoft.firefly.gui.components.utils.FxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;

import java.util.List;

/**
 * ui class for choose items dialog
 */
public class ChooseItemsMainPane extends GridPane {
    private List<String> items;
    private int maxLength = 50;
    private TextFieldFilter textFieldFilter;
    private CheckBox unSelectedCheckBox;
    private ComboBox<String> pageComboBox;
    private Label toopTipLB;
    private Button okBtn;
    private TableView<ChooseItem> chooseTB;
    private CheckBox allCheck;

    /**
     * constructor
     *
     * @param items items
     */
    public ChooseItemsMainPane(List<String> items) {
        this(items, 50);
    }

    /**
     * constructor
     *
     * @param items     items
     * @param maxLength max length
     */
    public ChooseItemsMainPane(List<String> items, int maxLength) {
        if (items != null) {
            this.items = items;
        } else {
            this.items = Lists.newArrayList();
        }
        if (maxLength > 0) {
            this.maxLength = maxLength;
        }
        init();
    }

    //method to init ui and event
    private void init() {
        ColumnConstraints c0 = new ColumnConstraints(10, 10, 10);
        ColumnConstraints c1 = new ColumnConstraints(390, 390, 390);
        ColumnConstraints c2 = new ColumnConstraints(10, 10, 10);
        this.getColumnConstraints().addAll(c0, c1, c2);
        RowConstraints r0 = new RowConstraints(10, 10, 10);
        RowConstraints r1 = new RowConstraints(29, 29, 29);
        RowConstraints r2 = new RowConstraints(249, 249, 249);
        RowConstraints r3 = new RowConstraints(10, 10, 10);
        RowConstraints r4 = new RowConstraints(22, 22, 22);
        RowConstraints r5 = new RowConstraints(10, 10, 10);
        this.getRowConstraints().addAll(r0, r1, r2, r3, r4, r5);

        this.add(getTilePane(), 1, 1);
        this.add(getTableView(), 1, 2);
    }

    private Pane getTilePane() {
        textFieldFilter = new TextFieldFilter();
        textFieldFilter.getTextField().setPromptText(FxmlAndLanguageUtils.getString(CommonResourceMassages.TEST_ITEM));
        unSelectedCheckBox = new CheckBox(FxmlAndLanguageUtils.getString(CommonResourceMassages.INVERT));
        pageComboBox = new ComboBox<>();
        pageComboBox.setItems(generatePageList());
        pageComboBox.getSelectionModel().selectFirst();
        pageComboBox.setPrefWidth(70);
        pageComboBox.setMinWidth(70);
        pageComboBox.setMaxWidth(70);

        //handler
        textFieldFilter.getTextField().textProperty().addListener((ov, s1, s2) -> handleFilterEvent(s2));
        unSelectedCheckBox.selectedProperty().addListener((ov, b1, b2) -> handleInvertEvent(b2));
        pageComboBox.getSelectionModel().selectedIndexProperty().addListener((ov, i1, i2) -> handlePageIndexEvent(i2.intValue()));

        GridPane titlePane = new GridPane();
        titlePane.setPadding(new Insets(3));
        RowConstraints r0 = new RowConstraints(22, 22, 22);
        ColumnConstraints c0 = new ColumnConstraints(200, 200, 200);
        ColumnConstraints c1 = new ColumnConstraints(10, 10, 10);
        ColumnConstraints c2 = new ColumnConstraints(92, 92, 92);
        ColumnConstraints c3 = new ColumnConstraints(10, 10, 10);
        ColumnConstraints c4 = new ColumnConstraints(70, 70, 70);
        titlePane.setAlignment(Pos.CENTER_LEFT);
        titlePane.getRowConstraints().addAll(r0);
        titlePane.getColumnConstraints().addAll(c0, c1, c2, c3, c4);
        titlePane.add(textFieldFilter, 0, 0);
        titlePane.add(unSelectedCheckBox, 2, 0);
        titlePane.add(pageComboBox, 4, 0);
        titlePane.setStyle("-fx-border-width: 1 1 0 1; -fx-border-color: #DCDCDC");
        return titlePane;
    }

    private TableView<ChooseItem> getTableView() {
        //TODO
        return chooseTB;
    }

    private ObservableList<String> generatePageList() {
        ObservableList<String> result = FXCollections.observableArrayList();
        result.add("All");
        if (!items.isEmpty()) {
            int length = items.size() / maxLength;
            if (items.size() % maxLength != 0) {
                length++;
            }
            for (int i = 0; i < length; i++) {
                int begin = i * maxLength + 1;
                int end = (i + 1) * maxLength > items.size() ? items.size() : (i + 1) * maxLength;
                result.add(begin + " - " + end);
            }
        }
        return result;
    }

    private void handleFilterEvent(String filterText) {
        //TODO
    }

    private void handleInvertEvent(boolean isSelected) {
        //TODO
    }

    private void handlePageIndexEvent(int index) {
        //TODO
    }

    /**
     * method to get selected items
     *
     * @return list of selected items
     */
    public List<String> getSelectedItems() {
        //TODO
        return null;
    }

    public Button getOkBtn() {
        return okBtn;
    }
}
