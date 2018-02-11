package com.dmsoft.firefly.gui.components.searchcombobox;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

/**
 * search combo box
 */
public class SearchComboBox extends GridPane {
    private ISearchComboBoxController searchComboBoxController;
    private ComboBox<String> testItemBox;
    private ComboBox<String> operatorBox;
    private ComboBox<String> valueBox;
    private Button closeBtn;
    private static final String SEARCH_COMPONENT_STYLE_CLASS = "search-component";
    private static final String TEST_ITEM_BOX_STYLE_CLASS = "test-item";
    private static final String OPERATOR_BOX_STYLE_CLASS = "operator";
    private static final String VALUE_BOX_STYLE_CLASS = "value";
    private static final String CLOSE_BTN_STYLE_CLASS = "close";

    /**
     * constructor
     *
     * @param searchComboBoxController search combo box controller
     */
    public SearchComboBox(ISearchComboBoxController searchComboBoxController) {
        this.searchComboBoxController = searchComboBoxController;
        RowConstraints r1 = new RowConstraints(21);
        r1.setVgrow(Priority.NEVER);
        RowConstraints r2 = new RowConstraints(22);
        r2.setVgrow(Priority.NEVER);
        this.getRowConstraints().addAll(r1, r2);
        ColumnConstraints c1 = new ColumnConstraints(40);
        c1.setHgrow(Priority.NEVER);
        ColumnConstraints c2 = new ColumnConstraints(105, 105, Double.MAX_VALUE);
        c2.setHgrow(Priority.ALWAYS);
        ColumnConstraints c3 = new ColumnConstraints(15);
        c3.setHgrow(Priority.NEVER);
        this.getColumnConstraints().addAll(c1, c2, c3);

        FilteredList<String> filterItems = new FilteredList<>(searchComboBoxController.getTestItems(), p -> true);
        FilteredList<String> filterValues = new FilteredList<>(FXCollections.observableArrayList(), p -> true);

        this.testItemBox = new ComboBox<>();
        this.testItemBox.setEditable(true);
        this.testItemBox.setItems(filterItems);
        this.testItemBox.getStyleClass().add(TEST_ITEM_BOX_STYLE_CLASS);
        this.testItemBox.setMinSize(145, 21);
        this.testItemBox.setPrefSize(145, 21);
        this.testItemBox.setMaxSize(Double.MAX_VALUE, 21);
        this.testItemBox.focusedProperty().addListener((ov, b1, b2) -> {
            if (b2) {
                this.testItemBox.setStyle("-fx-border-color: #66c0ff");
                this.operatorBox.setStyle("-fx-border-color: #66c0ff #ccc #ccc #ccc");
                this.valueBox.setStyle("-fx-border-color: #66c0ff #ccc #ccc #ccc");
            } else {
                this.testItemBox.setStyle("-fx-border-color: #ccc");
                this.operatorBox.setStyle("-fx-border-color: #ccc");
                this.valueBox.setStyle("-fx-border-color: #ccc");
            }
        });
        this.testItemBox.getEditor().textProperty().addListener((ov, s1, s2) -> {
            String itemTxt = this.testItemBox.getEditor().getText();
            String selected = this.testItemBox.getSelectionModel().getSelectedItem();
            if (selected == null || !selected.equals(itemTxt)) {
                filterItems.setPredicate(item -> item.toLowerCase().contains(itemTxt.toLowerCase()));
            }
            this.testItemBox.hide();
            this.testItemBox.show();
        });
        this.testItemBox.valueProperty().addListener((ov, s1, s2) -> {
            if (this.searchComboBoxController.isTimeKey(s2)) {
                if (!this.valueBox.lookup(".arrow-button").getStyleClass().contains("arrow-calendar-button")) {
                    this.valueBox.lookup(".arrow-button").getStyleClass().add("arrow-calendar-button");
                }
            } else {
                this.valueBox.lookup(".arrow-button").getStyleClass().remove("arrow-calendar-button");
            }
            ((ObservableListWrapper) filterValues.getSource()).setAll(searchComboBoxController.getValueForTestItem(s2));
        });

        this.operatorBox = new ComboBox<>();
        this.operatorBox.getItems().addAll(" = ", " < ", " > ", " != ", " <= ", " >= ", " %= ");
        this.operatorBox.setValue(" = ");
        this.operatorBox.getStyleClass().add(OPERATOR_BOX_STYLE_CLASS);
        this.operatorBox.setMinSize(40, 22);
        this.operatorBox.setPrefSize(40, 22);
        this.operatorBox.setPrefSize(40, 22);
        this.operatorBox.focusedProperty().addListener((ov, b1, b2) -> {
            if (b2) {
                this.operatorBox.setStyle("-fx-border-color: #66c0ff");
            } else {
                this.operatorBox.setStyle("-fx-border-color: #ccc");
            }
        });

        this.valueBox = new ComboBox<>();
        this.valueBox.setEditable(true);

        this.valueBox.setItems(filterValues);
        this.valueBox.getStyleClass().add(VALUE_BOX_STYLE_CLASS);
        this.valueBox.setMinSize(105, 22);
        this.valueBox.setPrefSize(105, 22);
        this.valueBox.setMaxSize(Double.MAX_VALUE, 22);
        this.valueBox.focusedProperty().addListener((ov, b1, b2) -> {
            if (b2) {
                this.operatorBox.setStyle("-fx-border-color: #ccc #66c0ff #ccc #ccc");
                this.valueBox.setStyle("-fx-border-color: #66c0ff");
            } else {
                this.operatorBox.setStyle("-fx-border-color: #ccc");
                this.valueBox.setStyle("-fx-border-color: #ccc");
            }
        });
        this.valueBox.getEditor().textProperty().addListener((ov, s1, s2) -> {
            String itemTxt = this.valueBox.getEditor().getText();
            filterValues.setPredicate(item -> item.toLowerCase().contains(itemTxt.toLowerCase()));
            this.valueBox.hide();
            this.valueBox.show();
        });

        this.closeBtn = new Button();
        this.closeBtn.getStyleClass().add(CLOSE_BTN_STYLE_CLASS);
        this.closeBtn.setMinSize(15, 43);
        this.closeBtn.setPrefSize(15, 43);
        this.closeBtn.setMaxSize(15, 43);
        this.closeBtn.focusedProperty().addListener((ov, b1, b2) -> {
            if (b2) {
                this.testItemBox.setStyle("-fx-border-color: #ccc #66c0ff #ccc #ccc");
                this.valueBox.setStyle("-fx-border-color: #ccc #66c0ff #ccc #ccc");
            } else {
                this.testItemBox.setStyle("-fx-border-color: #ccc");
                this.valueBox.setStyle("-fx-border-color: #ccc");
            }
        });

        this.getStyleClass().add(SEARCH_COMPONENT_STYLE_CLASS);
        this.add(this.testItemBox, 0, 0, 2, 1);
        this.add(this.operatorBox, 0, 1);
        this.add(this.valueBox, 1, 1);
        this.add(this.closeBtn, 2, 0, 1, 2);
    }

    /**
     * method to get condition
     *
     * @return condition
     */
    public String getCondition() {
        StringBuilder result = new StringBuilder();
        if (!isNotBlank(getTestItem()) && !isNotBlank(getOperator())
                && !isNotBlank(getValue())) {
            result.append("\"").append(getTestItem()).append("\"").append(getOperator())
                    .append("\"").append(getValue()).append("\"");
        }
        return result.toString();
    }

    public String getTestItem() {
        return this.testItemBox.getValue();
    }

    public String getOperator() {
        return this.operatorBox.getValue();
    }

    public String getValue() {
        return this.valueBox.getValue();
    }

    /**
     * method to set test item
     *
     * @param testItem test item
     */
    public void setTestItem(String testItem) {
        this.testItemBox.setValue(testItem);
    }

    /**
     * method to set value
     *
     * @param value value
     */
    public void setValue(String value) {
        this.valueBox.setValue(value);
    }

    /**
     * method to set operator
     *
     * @param operator operator
     */
    public void setOperator(String operator) {
        this.operatorBox.setValue(operator);
    }

    public Button getCloseBtn() {
        return closeBtn;
    }

    private boolean isNotBlank(String s) {
        return !(s == null || s.length() == 0);
    }
}
