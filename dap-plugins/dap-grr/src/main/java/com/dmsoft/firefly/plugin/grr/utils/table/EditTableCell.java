package com.dmsoft.firefly.plugin.grr.utils.table;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
/**
 * Created by cherry on 2018/3/13.
 */
public class EditTableCell extends TableCell {

    private TextField textField;
    private int columnIndex;
    private TableCellCallBack callBack;

    public void setCallBack(TableCellCallBack callBack) {
        this.callBack = callBack;
    }

    public EditTableCell(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (textField == null) {
            createTextField();
        }
        setText(null);
        setGraphic(textField);
        textField.selectAll();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText((String) getItem());
        setGraphic(null);
    }

    @Override
    public void commitEdit(Object newValue) {
        super.commitEdit(newValue);
        if (callBack != null) {
            this.getIndex();
            callBack.execute(this, columnIndex);
        }
    }

    @Override
    public void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(getString());
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(getString());
                setGraphic(null);
            }
        }
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        textField.focusedProperty().addListener((arg0, arg1, arg2) -> {
            if (!arg2) {
                commitEdit(textField.getText());
            }
        });

        textField.setOnKeyReleased(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                String value = textField.getText();
                if (value != null) {
                    commitEdit(value);
                } else {
                    commitEdit(null);
                }
            } else if (t.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });
    }

    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }
}
