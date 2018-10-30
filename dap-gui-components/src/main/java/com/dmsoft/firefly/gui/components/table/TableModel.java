package com.dmsoft.firefly.gui.components.table;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

/**
 * basic model interface for table view
 *
 * @author Can Guan
 */
public interface TableModel {

    /**
     * 获取表头列表
     *
     * @return observable string list
     */
    ObservableList<String> getHeaderArray();

    /**
     * 根据rowKey获取单元格数据
     *
     * @param rowKey     row key
     * @param columnName column name
     * @return table view data
     */
    ObservableValue<String> getCellData(String rowKey, String columnName);

    /**
     * 获取所有行key列数据
     *
     * @return observable string list
     */
    ObservableList<String> getRowKeyArray();

    /**
     * 检查某列是否可以编辑
     *
     * @param columnName column name
     * @return true : editable, false : uneditable
     */
    boolean isEditableTextField(String columnName);

    /**
     * 获取此列是否复选框
     *
     * @param columnName column name
     * @return true : is check box; false : is not check box
     */
    boolean isCheckBox(String columnName);

    /**
     * 获取某列的checkbox的选择值
     *
     * @param rowKey     row key
     * @param columnName column name
     * @return check property
     */
    ObjectProperty<Boolean> getCheckValue(String rowKey, String columnName);

    /**
     * 获取全选的选择值
     *
     * @param columnName column name
     * @return selected value
     */
    ObjectProperty<Boolean> getAllCheckValue(String columnName);

    /**
     * 获取表格行菜单事件列表
     *
     * @return list of menu event
     */
    List<TableMenuRowEvent> getMenuEventList();

    /**
     * 封装表格单元格
     *
     * @param <T>       any type
     * @param rowKey    row key
     * @param column    column
     * @param tableCell table cell
     * @return table cell
     */
    <T> TableCell<String, T> decorate(String rowKey, String column, TableCell<String, T> tableCell);

    /**
     * 设置全选按钮状态
     *
     * @param checkBox check box
     */
    void setAllCheckBox(CheckBox checkBox);

    /**
     * 设置tableview
     *
     * @param tableView table view
     */
    void setTableViewWidth(TableView<String> tableView);

    /**
     * 检查是否开启行级菜单
     *
     * @param rowKey row key
     * @return is enable or not
     */
    default boolean isMenuEventEnable(String rowKey) {
        return true;
    }

    /**
     * method to  judge text input error or not
     *
     * @param textField  text field
     * @param oldText    old text
     * @param newText    new text
     * @param rowKey     row key
     * @param columnName columnName
     * @return true : is error, false : no error
     */
    default boolean isTextInputError(TextField textField, String oldText, String newText, String rowKey, String columnName) {
        return false;
    }
}
