package com.dmsoft.firefly.gui.components.table;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;

public interface TableModel {
    ObservableList<String> getHeaderArray();

    ObjectProperty<String> getCellData(String rowKey, String columnName);

    ObservableList<String> getRowKeyArray();
}
