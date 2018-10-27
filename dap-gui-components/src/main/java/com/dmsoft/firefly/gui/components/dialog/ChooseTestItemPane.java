package com.dmsoft.firefly.gui.components.dialog;

import com.dmsoft.firefly.gui.components.skin.ExpandableTableViewSkin;
import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.gui.components.utils.CommonResourceMassages;
import com.dmsoft.firefly.gui.components.utils.FxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.google.common.collect.Lists;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.*;

import java.util.Comparator;
import java.util.List;

/**
 * ui class for choose items dialog
 */
public class ChooseTestItemPane extends GridPane {
    private ObservableList<ChooseTestItemModel> items;
    private int maxLength = 50;
    private Button okBtn;
    private FilteredList<ChooseTestItemModel> filteredList;
    private SortedList<ChooseTestItemModel> sortedList;
    private Label errorLabel;
    private TableView<ChooseTestItemModel> chooseTB;
    private GridPane titlePane;
    private GridPane bottomPane;
    private CheckBox allCheck;

    /**
     * constructor
     *
     * @param items         items
     * @param selectedItems selected item list
     * @param maxLength     max length
     */
    ChooseTestItemPane(List<String> items, List<String> selectedItems, int maxLength) {
        this.items = FXCollections.observableArrayList();
        if (maxLength > 0) {
            this.maxLength = maxLength;
        }
        List<String> selecteds = Lists.newArrayList();
        if (selectedItems != null) {
            selecteds.addAll(selectedItems);
            if (selecteds.size() > getMaxLength()) {
                selecteds.removeAll(selecteds.subList(getMaxLength(), selecteds.size()));
            }
        }
        if (items != null) {
            for (String s : items) {
                ChooseTestItemModel item = new ChooseTestItemModel(s, selecteds.contains(s));
                item.selectedProperty().addListener((ov, b1, b2) -> this.handleNumberChangeEvent());
                this.items.add(item);
            }
        }
        this.filteredList = this.items.filtered(p -> {

            return true;
        });
        this.sortedList = new SortedList<>(filteredList);

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
        this.add(getBottomPane(), 1, 4);
    }

    private Pane getTilePane() {
        if (titlePane == null) {
            titlePane = new GridPane();
            TextFieldFilter textFieldFilter = new TextFieldFilter();
            textFieldFilter.getTextField().setPromptText(FxmlAndLanguageUtils.getString(CommonResourceMassages.TEST_ITEM));
            CheckBox unSelectedCheckBox = new CheckBox(FxmlAndLanguageUtils.getString(CommonResourceMassages.INVERT));
            ComboBox<String> pageComboBox = new ComboBox<>();
            pageComboBox.setItems(generatePageList());
            pageComboBox.getSelectionModel().selectFirst();
            pageComboBox.setPrefWidth(70);
            pageComboBox.setMinWidth(70);
            pageComboBox.setMaxWidth(70);

            //handler
            textFieldFilter.getTextField().textProperty().addListener((ov, s1, s2) -> handleFilterEvent(s2));
            unSelectedCheckBox.selectedProperty().addListener((ov, b1, b2) -> handleInvertEvent());
            pageComboBox.getSelectionModel().selectedIndexProperty().addListener((ov, i1, i2) -> handlePageIndexEvent(i2.intValue()));


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
        }
        return titlePane;
    }

    private TableView<ChooseTestItemModel> getTableView() {
        if (chooseTB == null) {
            chooseTB = new TableView<>(sortedList);
            chooseTB.setSkin(new ExpandableTableViewSkin(chooseTB));
            chooseTB.setItems(sortedList);
            sortedList.comparatorProperty().bind(chooseTB.comparatorProperty());

            chooseTB.setEditable(true);
            allCheck = new CheckBox();
            allCheck.selectedProperty().addListener((ov, b1, b2) -> this.handleAllCheckBoxEvent(b2));
            TableColumn<ChooseTestItemModel, ?> checkCol = new TableColumn<>();
            checkCol.setGraphic(allCheck);
            checkCol.setSortable(false);
            checkCol.setResizable(false);
            checkCol.setPrefWidth(32);
            checkCol.setCellFactory(CheckBoxTableCell.forTableColumn((i) -> {
                if (i >= 0 && i < getTableView().getItems().size()) {
                    return getTableView().getItems().get(i).selectedProperty();
                } else {
                    return new SimpleBooleanProperty(false);
                }
            }));
            TableColumn<ChooseTestItemModel, String> itemCol = new TableColumn<>();
            itemCol.setCellValueFactory(cell -> cell.getValue().itemNameProperty());
            itemCol.setResizable(false);
            itemCol.setComparator(Comparator.naturalOrder());

            HBox itemHeader = new HBox();
            itemHeader.setStyle("-fx-padding: 0 0 0 -10");
            itemHeader.setAlignment(Pos.CENTER_LEFT);
            Label testItemLabel = new Label(FxmlAndLanguageUtils.getString(CommonResourceMassages.TEST_ITEM));
            errorLabel = new Label();
            errorLabel.setStyle("-fx-text-fill: red; -fx-padding: 0 0 0 -10");
            errorLabel.setVisible(false);
            itemHeader.getChildren().addAll(testItemLabel, errorLabel);
            itemCol.setGraphic(itemHeader);

            chooseTB.getColumns().add(checkCol);
            chooseTB.getColumns().add(itemCol);
            if (chooseTB.getSkin() != null) {
                TableViewWrapper.decorateSkinForSortHeader((TableViewSkin) chooseTB.getSkin(), chooseTB);
            } else {
                chooseTB.skinProperty().addListener((ov, s1, s2) -> TableViewWrapper.decorateSkinForSortHeader((TableViewSkin) s2, chooseTB));
            }
        }
        return chooseTB;
    }

    private Pane getBottomPane() {
        if (bottomPane == null) {
            bottomPane = new GridPane();
            Label infoIcon = new Label();
            infoIcon.getStyleClass().add("message-tip-warn-mark");
            Label infoLB = new Label(FxmlAndLanguageUtils.getString(CommonResourceMassages.MAX_CHOICES, new Object[]{maxLength}), infoIcon);
            okBtn = new Button(FxmlAndLanguageUtils.getString(CommonResourceMassages.GLOBAL_BTN_OK));
            okBtn.setMaxWidth(80);
            okBtn.setMinWidth(80);
            okBtn.setPrefWidth(80);
            ColumnConstraints c0 = new ColumnConstraints(310, 310, 310);
            ColumnConstraints c1 = new ColumnConstraints(80, 80, 80);
            RowConstraints r0 = new RowConstraints(22, 22, 22);
            bottomPane.getRowConstraints().add(r0);
            bottomPane.getColumnConstraints().addAll(c0, c1);
            bottomPane.add(infoLB, 0, 0);
            bottomPane.add(okBtn, 1, 0);
        }
        return bottomPane;
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
        filteredList.setPredicate(s -> s.itemNameProperty().getValue().toLowerCase().contains(filterText.toLowerCase()));
    }

    private void handleInvertEvent() {
        for (int i = 0, max = filteredList.size(); i < max; i++) {
            filteredList.get(i).selectedProperty().set(!filteredList.get(i).selectedProperty().getValue());
        }
    }

    private void handlePageIndexEvent(int index) {
        int begin = (index - 1) * maxLength + 1;
        int end = index * maxLength > items.size() ? items.size() : index * maxLength;
        filteredList.setPredicate(chooseTestItemModel -> {
            if (index == 0) {
                return true;
            }
            int site = items.indexOf(chooseTestItemModel) + 1;
            return (site >= begin && site <= end);
        });
    }

    private void handleAllCheckBoxEvent(boolean isSelected) {
        for (int i = 0, max = filteredList.size(); i < max; i++) {
            filteredList.get(i).selectedProperty().set(isSelected);
        }
    }

    void handleNumberChangeEvent() {
        okBtn.setDisable(false);
        errorLabel.setVisible(false);
        allCheck.getStyleClass().removeAll("error");
        int total = 0;
        for (int i = 0, max = items.size(); i < max; i++) {
            if (items.get(i).selectedProperty().get()) {
                total++;
            }
        }
        if (total > maxLength) {
            errorLabel.setText(" " + total + "/" + maxLength);
            errorLabel.setVisible(true);
            okBtn.setDisable(true);
            if (!allCheck.getStyleClass().contains("error")) {
                allCheck.getStyleClass().add("error");
            }
        }
    }

    /**
     * method to get selected items
     *
     * @return list of selected items
     */
    public List<String> getSelectedItems() {
        List<String> result = Lists.newArrayList();
        for (int i = 0, max = items.size(); i < max; i++) {
            if (items.get(i).selectedProperty().getValue()) {
                result.add(items.get(i).itemNameProperty().getValue());
            }
        }
        return result;
    }

    public Button getOkBtn() {
        return okBtn;
    }

    public ObservableList<ChooseTestItemModel> getItems() {
        return items;
    }

    int getMaxLength() {
        return maxLength;
    }
}
