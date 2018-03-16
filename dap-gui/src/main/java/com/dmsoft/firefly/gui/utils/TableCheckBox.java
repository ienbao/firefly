/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.gui.utils;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;

/**
 * Created by GuangLi on 2018/2/8.
 */
public class TableCheckBox {
    private CheckBox checkbox = new CheckBox();

    public ObservableValue<CheckBox> getCheckBox() {
        return new ObservableValue<CheckBox>() {
            @Override
            public void addListener(ChangeListener<? super CheckBox> listener) {

            }

            @Override
            public void removeListener(ChangeListener<? super CheckBox> listener) {

            }

            @Override
            public CheckBox getValue() {
                return checkbox;
            }

            @Override
            public void addListener(InvalidationListener listener) {

            }

            @Override
            public void removeListener(InvalidationListener listener) {

            }
        };
    }

    public CheckBox getCheckbox() {
        return checkbox;
    }

    public Boolean isSelected() {
        return checkbox.isSelected();
    }

    public void setValue(Boolean select){
        checkbox.setSelected(select);
    }
}
