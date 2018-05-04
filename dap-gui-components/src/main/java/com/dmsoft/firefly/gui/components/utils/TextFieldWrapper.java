package com.dmsoft.firefly.gui.components.utils;

import javafx.scene.control.TextField;

/**
 * decorator class for text field
 *
 * @author Can Guan
 */
public class TextFieldWrapper {
    /**
     * method to decorate text field
     *
     * @param textField    text field
     * @param validateRule validate rule
     * @return decorated text field
     */
    public static TextField decorate(TextField textField, ValidateRule validateRule) {
        if (validateRule == null) {
            return textField;
        }
        //init
        if (!ValidateUtils.validateNotEmpty(textField.getText()) && Boolean.FALSE.equals(validateRule.getAllowEmpty())) {
            if (!textField.getStyleClass().contains(validateRule.getErrorStyle())) {
                textField.getStyleClass().add(validateRule.getErrorStyle());
            }
            TooltipUtil.installWarnTooltip(textField, validateRule.getEmptyErrorMsg());
        } else if (!ValidateUtils.validateWithinRange(textField.getText(), validateRule.getMaxValue(), validateRule.getMinValue())) {
            if (!textField.getStyleClass().contains(validateRule.getErrorStyle())) {
                textField.getStyleClass().add(validateRule.getErrorStyle());
            }
            TooltipUtil.installWarnTooltip(textField, validateRule.getRangErrorMsg());
        }
        textField.textProperty().addListener((ov, s1, s2) -> {
            if (!(ValidateUtils.validatePattern(s2, validateRule.getPattern())
                    && ValidateUtils.validateWithLength(s2, validateRule.getMaxLength()))) {
                textField.setText(s1);
            } else if (!ValidateUtils.validateNotEmpty(s2) && Boolean.FALSE.equals(validateRule.getAllowEmpty())) {
                if (!textField.getStyleClass().contains(validateRule.getErrorStyle())) {
                    textField.getStyleClass().add(validateRule.getErrorStyle());
                }
                TooltipUtil.installWarnTooltip(textField, validateRule.getEmptyErrorMsg());
            } else if (validateRule.getMaxValue() != null && validateRule.getMinValue() != null
                    && !ValidateUtils.validateWithinRange(s2, validateRule.getMaxValue(), validateRule.getMinValue())) {
                if (!textField.getStyleClass().contains(validateRule.getErrorStyle())) {
                    textField.getStyleClass().add(validateRule.getErrorStyle());
                }
                TooltipUtil.installWarnTooltip(textField, validateRule.getRangErrorMsg());
            } else if (validateRule.getValidateFunc() != null && !validateRule.getValidateFunc().apply(s2)) {
                 textField.setText(s1);
            } else {
                textField.getStyleClass().removeAll(validateRule.getErrorStyle());
                TooltipUtil.uninstallWarnTooltip(textField);
            }
        });
        return textField;
    }
}
