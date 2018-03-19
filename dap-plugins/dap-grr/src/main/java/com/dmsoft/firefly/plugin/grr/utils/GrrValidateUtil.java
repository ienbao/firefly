package com.dmsoft.firefly.plugin.grr.utils;

import com.dmsoft.firefly.gui.components.utils.FxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.components.utils.ValidateUtil;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * grr validate tool
 *
 * @author Julia
 *
 */
public class GrrValidateUtil {
    private static Integer TEXT_GRR_MIN_INT = 1;
    private static Integer TEXT_GRR_MAX_INT = 20;
    private static String grrRegType = "^([1-9]|[1-1][0-9]|20)$";

    public static boolean validateGrr(Node... nodes) {
        if (nodes != null && nodes.length > 0) {
            AtomicBoolean result = new AtomicBoolean(true);
            for (Node node : nodes) {
                if (node instanceof TextField) {
                    TextField textField = (TextField) node;
                    ValidateUtil.validateIsNotEmpty(textField.getText(), textField);
                    result.set(validateResult(node));
                    textField.textProperty().addListener((obVal, oldVal, newVal) -> {
                        if (ValidateUtil.validateIsNotEmpty(newVal, textField)) {
                            validateGrrReg(newVal, textField);
                            result.set(validateResult(node));
                        } else {
                            result.set(validateResult(node));
                        }
                    });

                    textField.focusedProperty().addListener((obVal, oldVal, newVal) -> {
                        if (ValidateUtil.validateIsNotEmpty(textField.getText(), textField)) {
                            validateGrrReg(textField.getText(), textField);
                            result.set(validateResult(node));
                        } else {
                            result.set(validateResult(node));
                        }
                    });
                }
                else if (node instanceof ComboBox) {
                    ComboBox comboBox = (ComboBox) node;

                    ValidateUtil.validateIsNotEmpty(comboBox.getValue(), comboBox);
                    result.set(validateResult(node));

                    comboBox.valueProperty().addListener((obVal, oldVal, newVal) -> {
                        ValidateUtil.validateIsNotEmpty(newVal, comboBox);
                        result.set(validateResult(node));
                    });

                    comboBox.focusedProperty().addListener((obVal, oldVal, newVal) -> {
                        ValidateUtil.validateIsNotEmpty(comboBox.getValue(), comboBox);
                        result.set(validateResult(node));
                    });
                }
            }
            return result.get();
        }
        return true;
    }

    private static void validateGrrReg(String msgValue, Node node) {
        String[] params = new String[]{TEXT_GRR_MIN_INT.toString(), TEXT_GRR_MAX_INT.toString()};
        ValidateUtil.validateReg(msgValue, node, grrRegType, FxmlAndLanguageUtils.getString("GLOBAL_VALIDATE_OUT_OF_RANGE_DOUBLE", params));
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
