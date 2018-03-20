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
        //init
        if (!ValidateUtils.validateNotEmpty(textField.getText())) {
            textField.getStyleClass().add(validateRule.getErrorStyle());
            TooltipUtil.installWarnTooltip(textField, validateRule.getEmptyErrorMsg());
        } else if (!ValidateUtils.validateWithinRange(textField.getText(), validateRule.getMaxValue(), validateRule.getMinValue())) {
            textField.getStyleClass().add(validateRule.getErrorStyle());
            TooltipUtil.installWarnTooltip(textField, validateRule.getRangErrorMsg());
        }
        textField.textProperty().addListener((ov, s1, s2) -> {
            if (!(ValidateUtils.validatePattern(s2, validateRule.getPattern())
                    && ValidateUtils.validateWithLength(s2, validateRule.getMaxLength()))) {
                textField.setText(s1);
            } else if (!ValidateUtils.validateNotEmpty(s2)) {
                textField.getStyleClass().add(validateRule.getErrorStyle());
                TooltipUtil.installWarnTooltip(textField, validateRule.getEmptyErrorMsg());
            } else if (!ValidateUtils.validateWithinRange(s2, validateRule.getMaxValue(), validateRule.getMinValue())) {
                textField.getStyleClass().add(validateRule.getErrorStyle());
                TooltipUtil.installWarnTooltip(textField, validateRule.getRangErrorMsg());
            } else {
                textField.getStyleClass().removeAll(validateRule.getErrorStyle());
                TooltipUtil.uninstallWarnTooltip(textField);
            }
        });
        return textField;
    }
}
