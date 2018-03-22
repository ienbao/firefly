package com.dmsoft.firefly.gui.components.table;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

/**
 * copied from {@link javafx.scene.control.cell.TextFieldTableCell} and add some method to change behavior
 *
 * @param <S> any object
 * @param <T> any object
 */
public class CustomTextFieldTableCell<S, T> extends TableCell<S, T> {
    private TextField textField;
    // --- converter
    private ObjectProperty<StringConverter<T>> converter =
            new SimpleObjectProperty<StringConverter<T>>(this, "converter");

    /**
     * constructor copied from {@link javafx.scene.control.cell.TextFieldTableCell}
     */
    public CustomTextFieldTableCell() {
        this(null);
    }

    /**
     * constructor copied from {@link javafx.scene.control.cell.TextFieldTableCell}
     *
     * @param converter string converter
     */
    public CustomTextFieldTableCell(StringConverter<T> converter) {
        this.getStyleClass().add("text-field-table-cell");
        setConverter(converter);
    }

    /**
     * copied from {@link javafx.scene.control.cell.TextFieldTableCell}
     *
     * @param <S> any object
     * @return call back
     */
    public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn() {
        return forTableColumn(new DefaultStringConverter());
    }

    /**
     * copied from {@link javafx.scene.control.cell.TextFieldTableCell}
     *
     * @param stringConverter converter
     * @param <S>             any object
     * @param <T>             any object
     * @return call back
     */
    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(final StringConverter<T> stringConverter) {
        return list -> new CustomTextFieldTableCell<S, T>(stringConverter);
    }

    static <T> void updateItem(final Cell<T> cell,
                               final StringConverter<T> converter,
                               final HBox hbox,
                               final Node graphic,
                               final TextField textField) {
        if (cell.isEmpty()) {
            cell.setText(null);
            cell.setGraphic(null);
        } else {
            if (cell.isEditing()) {
                if (textField != null) {
                    textField.setText(getItemText(cell, converter));
                }
                cell.setText(null);

                if (graphic != null) {
                    hbox.getChildren().setAll(graphic, textField);
                    cell.setGraphic(hbox);
                } else {
                    cell.setGraphic(textField);
                }
            } else {
                cell.setText(getItemText(cell, converter));
                cell.setGraphic(graphic);
            }
        }
    }

    static <T> void startEdit(final Cell<T> cell,
                              final StringConverter<T> converter,
                              final HBox hbox,
                              final Node graphic,
                              final TextField textField) {
        if (textField != null) {
            textField.setText(getItemText(cell, converter));
        }
        cell.setText(null);

        if (graphic != null) {
            hbox.getChildren().setAll(graphic, textField);
            cell.setGraphic(hbox);
        } else {
            cell.setGraphic(textField);
        }

        textField.selectAll();

        // requesting focus so that key input can immediately go into the
        // TextField (see RT-28132)
        textField.requestFocus();
    }

    static <T> void cancelEdit(Cell<T> cell, final StringConverter<T> converter, Node graphic) {
        cell.setText(getItemText(cell, converter));
        cell.setGraphic(graphic);
    }

    private static <T> String getItemText(Cell<T> cell, StringConverter<T> converter) {
        return converter == null ? cell.getItem() == null ? "" : cell.getItem().toString() : converter.toString(cell.getItem());
    }

    /**
     * method to create text
     *
     * @param cell      table cell
     * @param converter converter
     * @param <T>       any object
     * @return text field
     */
    public <T> TextField createTextField(final Cell<T> cell, final StringConverter<T> converter) {
        final TextField textField1 = new TextField(getItemText(cell, converter));

        // Use onAction here rather than onKeyReleased (with check for Enter),
        // as otherwise we encounter RT-34685
        textField1.setOnAction(event -> {
            if (converter == null) {
                throw new IllegalStateException(
                        "Attempting to convert text input into Object, but provided "
                                + "StringConverter is null. Be sure to set a StringConverter "
                                + "in your cell factory.");
            }
            cell.commitEdit(converter.fromString(textField1.getText()));
            event.consume();
        });
        textField1.setOnKeyReleased(t -> {
            if (t.getCode() == KeyCode.ESCAPE) {
                cell.cancelEdit();
                t.consume();
            }
        });
        return textField1;
    }

    /**
     * The {@link StringConverter} property.
     *
     * @return object property
     */
    public final ObjectProperty<StringConverter<T>> converterProperty() {
        return converter;
    }

    /**
     * Returns the {@link StringConverter} used in this cell.
     *
     * @return converter
     */
    public final StringConverter<T> getConverter() {
        return converterProperty().get();
    }

    /**
     * Sets the {@link StringConverter} to be used in this cell.
     *
     * @param value string converter
     */
    public final void setConverter(StringConverter<T> value) {
        converterProperty().set(value);
    }

    @Override
    public void startEdit() {
        if (!isEditable()
                || !getTableView().isEditable()
                || !getTableColumn().isEditable()) {
            return;
        }
        super.startEdit();

        if (isEditing()) {
            if (textField == null) {
                textField = createTextField(this, getConverter());
                textField.focusedProperty().addListener((ov, b1, b2) -> {
                    if (!b2) {
                        commitEdit(converter.get().fromString(textField.getText()));
                    }
                });
            }

            startEdit(this, getConverter(), null, null, textField);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelEdit() {
        super.cancelEdit();
        cancelEdit(this, getConverter(), null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        updateItem(this, getConverter(), null, null, textField);
    }

    public TextField getTextField() {
        return textField;
    }
}
