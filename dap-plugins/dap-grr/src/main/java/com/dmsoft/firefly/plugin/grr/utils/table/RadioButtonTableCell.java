package com.dmsoft.firefly.plugin.grr.utils.table;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;

/**
 * Created by cherry on 2018/3/12.
 */
public class RadioButtonTableCell<S, T> extends TableCell<S, T> {

    private final RadioButton radio;
    private ObservableValue<T> ov;

    public RadioButtonTableCell() {
        this.radio = new RadioButton();
        setAlignment(Pos.CENTER);
        setGraphic(radio);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setGraphic(radio);
            if (item instanceof Boolean) {
                radio.setSelected((Boolean) item);
            }
//            if (ov instanceof BooleanProperty) {
//                radio.selectedProperty().unbindBidirectional(
//                        (BooleanProperty) ov);
//            }
//            ov = getTableColumn().getCellObservableValue(getIndex());
//            if (ov instanceof BooleanProperty) {
//                radio.selectedProperty()
//                        .bindBidirectional((BooleanProperty) ov);
//            }
        }
    }
}
