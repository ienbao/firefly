
package com.dmsoft.firefly.gui.chooseDialog;

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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

//

public class ChooseDialogController implements Initializable {
    @FXML
    private TextFieldFilter textFieldFilter;
    @FXML
    private CheckBox unSelectedCheckBox;
    @FXML
    private ComboBox pageComboBox;
    @FXML
    private TableView<ChooseTestItemModel> chooseColumnTable;
    @FXML
    private TableColumn<ChooseTestItemModel, CheckBox> checkCol;
    @FXML
    private TableColumn<ChooseTestItemModel, String> itemCol;
    @FXML
    private Label infoLB;
    @FXML
    private Button okBtn;
    private CheckBox allCheck;

    private int maxLength = 50;
    private Label errorLabel;//testItem显示最大限度和当前最大行
    private ObservableList<ChooseTestItemModel> items;
    private FilteredList<ChooseTestItemModel> filteredList;
    private SortedList<ChooseTestItemModel> sortedList;

    @Override
    public void initialize(URL location, ResourceBundle resources) { }

    public void ChooseTestItemPane(List<String> items, List<String> selectedItems, int maxLength) {
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

        this.initComponentEvent();
        this.getTableView();
        this.initBottomPane();


    }

    int getMaxLength() {
        return maxLength;
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

    private void initComponentEvent() {
        textFieldFilter.getTextField().setPromptText(FxmlAndLanguageUtils.getString(CommonResourceMassages.TEST_ITEM));
        unSelectedCheckBox.setText(FxmlAndLanguageUtils.getString(CommonResourceMassages.INVERT));
        pageComboBox.setItems(generatePageList());
        pageComboBox.getSelectionModel().selectFirst();
        textFieldFilter.getTextField().textProperty().addListener((ov, s1, s2) -> handleFilterEvent(s2));
        unSelectedCheckBox.selectedProperty().addListener((ov, b1, b2) -> handleInvertEvent());
        pageComboBox.getSelectionModel().selectedIndexProperty().addListener((ov, i1, i2) -> handlePageIndexEvent(i2.intValue()));
    }

    public void initBottomPane(){
        Label infoIcon = new Label();
        infoIcon.getStyleClass().add("message-tip-warn-mark");
        infoLB.setText(FxmlAndLanguageUtils.getString(CommonResourceMassages.MAX_CHOICES, new Object[]{maxLength}));
        infoLB.setGraphic(infoIcon);
        okBtn.setText(FxmlAndLanguageUtils.getString(CommonResourceMassages.GLOBAL_BTN_OK));
        okBtn.setMaxWidth(80);
        okBtn.setMinWidth(80);
        okBtn.setPrefWidth(80);
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

    private TableView<ChooseTestItemModel> getTableView() {
            chooseColumnTable.setItems(sortedList);
            chooseColumnTable.setSkin(new ExpandableTableViewSkin(chooseColumnTable));
            sortedList.comparatorProperty().bind(chooseColumnTable.comparatorProperty());
            chooseColumnTable.setEditable(true);
            allCheck = new CheckBox();
            allCheck.selectedProperty().addListener((ov, b1, b2) -> this.handleAllCheckBoxEvent(b2));
            checkCol.setGraphic(allCheck);
            checkCol.setSortable(false);
            checkCol.setResizable(false);
            checkCol.setPrefWidth(32);
            checkCol.setCellFactory(CheckBoxTableCell.forTableColumn((i) -> {
                if (i >= 0 && i < chooseColumnTable.getItems().size()) {
                    return chooseColumnTable.getItems().get(i).selectedProperty();
                } else {
                    return new SimpleBooleanProperty(false);
                }
            }));
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
            if (chooseColumnTable.getSkin() != null) {
                TableViewWrapper.decorateSkinForSortHeader((TableViewSkin) chooseColumnTable.getSkin(), chooseColumnTable);
            } else {
                chooseColumnTable.skinProperty().addListener((ov, s1, s2) -> TableViewWrapper.decorateSkinForSortHeader((TableViewSkin) s2, chooseColumnTable));
            }
//        }
        return chooseColumnTable;
    }

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
}
