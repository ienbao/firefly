package com.dmsoft.firefly.plugin.yield.utils;

import com.dmsoft.firefly.gui.components.utils.ValidateUtil;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.concurrent.atomic.AtomicBoolean;


public class YieldValidateUtil {

    public static boolean validateYield(Node... nodes) {
        if (nodes != null && nodes.length > 0) {
            AtomicBoolean result = new AtomicBoolean(true);
            for (Node node : nodes) {
                if (node instanceof TextField) {
                    TextField textField = (TextField) node;
                    ValidateUtil.validateIsNotEmpty(textField.getText(), textField);
                    result.set(validateResult(node));
                }
                else if (node instanceof ComboBox) {
                    ComboBox comboBox = (ComboBox) node;
                    ValidateUtil.validateIsNotEmpty(comboBox.getValue(), comboBox);
                    result.set(validateResult(node));
                    comboBox.valueProperty().addListener((obVal, oldVal, newVal) -> {
                        ValidateUtil.validateIsNotEmpty(newVal, comboBox);
                        result.set(validateResult(node));
                    });
                }
            }
            return result.get();
        }
        return true;
    }
    public static boolean validateResult(Node... nodes) {
        if (nodes != null && nodes.length > 0) {
            for (Node node: nodes) {
                if (node.getStyleClass().contains(ValidateUtil.TEXT_FIELD_ERROR_STYLE)
                        || node.getStyleClass().contains(ValidateUtil.COMBO_BOX_ERROR_STYLE)) {
                    return false;
                }
            }
        }
        return true;
    }

}
