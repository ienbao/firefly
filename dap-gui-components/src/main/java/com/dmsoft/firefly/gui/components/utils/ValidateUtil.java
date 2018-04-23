package com.dmsoft.firefly.gui.components.utils;

import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.regex.Pattern;

/**
 * validate tool
 *
 * @author Julia
 */
public class ValidateUtil {
    public static final String TEXT_FIELD_ERROR_STYLE = "text-field-error";
    public static final String COMBO_BOX_ERROR_STYLE = "combo-box-error";

    public static boolean validateIsNotEmpty(Object msg, Node node) {
        boolean result = false;
        if (msg == null || DAPStringUtils.isBlank((String) msg)) {
            if (node instanceof TextField) {
                node.getStyleClass().add(TEXT_FIELD_ERROR_STYLE);
            } else if (node instanceof ComboBox) {
                node.getStyleClass().add(COMBO_BOX_ERROR_STYLE);
            }
            TooltipUtil.installWarnTooltip(node, FxmlAndLanguageUtils.getString(ValidationAnno.GLOBAL_VALIDATE_NOT_BE_EMPTY));
        } else {
            TooltipUtil.uninstallWarnTooltip(node);
            if (node instanceof TextField) {
                node.getStyleClass().removeAll(TEXT_FIELD_ERROR_STYLE);
            } else if (node instanceof ComboBox) {
                node.getStyleClass().removeAll(COMBO_BOX_ERROR_STYLE);
            }
            result = true;
        }
        return result;

    }

    public static boolean validateSizeMinOrMax(Object msg, Node node, Integer minInt, Integer maxInt) {
        if (msg != null && DAPStringUtils.isNotBlank(msg.toString())) {
            String msgValue = (String) msg;
            return validateSizeMinOrMax(msgValue, node, minInt, maxInt);
        } else {
            return validateSizeMinOrMax(null, node, minInt, maxInt);
        }
    }

    public static boolean validateReg(String msgValue, Node node, String regType, String errorMsg) {
        boolean result = false;
        if (DAPStringUtils.isNotBlank(msgValue) && DAPStringUtils.isNotBlank(regType) && !Pattern.matches(regType, msgValue)) {
            node.getStyleClass().add(TEXT_FIELD_ERROR_STYLE);
            TooltipUtil.installWarnTooltip(node, errorMsg);
        } else {
            node.getStyleClass().removeAll(TEXT_FIELD_ERROR_STYLE);
            TooltipUtil.uninstallWarnTooltip(node);
            result = true;
        }
        return result;
    }

    public static boolean validateConfirm(String newValue, String confirmValue, Node node) {
        boolean result = false;
        if (DAPStringUtils.isBlank(newValue) && DAPStringUtils.isBlank(confirmValue)) {
            node.getStyleClass().add(TEXT_FIELD_ERROR_STYLE);
            TooltipUtil.installWarnTooltip(node, FxmlAndLanguageUtils.getString("CHANGE_VALIDATE_CONFIRM_NOT_MATCH"));
        } else if (DAPStringUtils.isNotBlank(newValue) && DAPStringUtils.isNotBlank(confirmValue) && (newValue.length() < 6 || newValue.length() >= 13) && (confirmValue.length() < 6 || confirmValue.length() >= 13)) {
            node.getStyleClass().add(TEXT_FIELD_ERROR_STYLE);
            TooltipUtil.installWarnTooltip(node, FxmlAndLanguageUtils.getString("CHANGE_VALIDATE_CONFIRM_NOT_MATCH"));
        } else if (!newValue.equals(confirmValue)) {
            node.getStyleClass().add(TEXT_FIELD_ERROR_STYLE);
            TooltipUtil.installWarnTooltip(node, FxmlAndLanguageUtils.getString("CHANGE_VALIDATE_CONFIRM_NOT_MATCH"));
        } else {
            node.getStyleClass().removeAll(TEXT_FIELD_ERROR_STYLE);
            TooltipUtil.uninstallWarnTooltip(node);
            result = true;
        }
        return result;
    }

    private static boolean validateSizeMinOrMax(String msgValue, Node node, Integer minInt, Integer maxInt) {
        boolean result = false;
        if (DAPStringUtils.isNotBlank(msgValue)) {
            if (minInt != null && maxInt != null && (msgValue.length() < minInt || msgValue.length() > maxInt)) {
                node.getStyleClass().add(TEXT_FIELD_ERROR_STYLE);
                String[] params = new String[]{minInt.toString(), maxInt.toString()};
                TooltipUtil.installWarnTooltip(node, FxmlAndLanguageUtils.getString("GLOBAL_VALIDATE_OUT_OF_RANGE_DOUBLE", params));
            } else if (minInt != null && maxInt == null && msgValue.length() < minInt) {
                node.getStyleClass().add(TEXT_FIELD_ERROR_STYLE);
                String[] params = new String[]{minInt.toString()};
                TooltipUtil.installWarnTooltip(node, FxmlAndLanguageUtils.getString("GLOBAL_VALIDATE_MIN_INT", params));
            } else if (minInt == null && maxInt != null && msgValue.length() > maxInt) {
                node.getStyleClass().add(TEXT_FIELD_ERROR_STYLE);
                String[] params = new String[]{maxInt.toString()};
                TooltipUtil.installWarnTooltip(node, FxmlAndLanguageUtils.getString("GLOBAL_VALIDATE_MAX_INT", params));
            } else {
                node.getStyleClass().removeAll(TEXT_FIELD_ERROR_STYLE);
                TooltipUtil.uninstallWarnTooltip(node);
                result = true;
            }
        } else {
            if (minInt != null && maxInt != null) {
                node.getStyleClass().add(TEXT_FIELD_ERROR_STYLE);
                String[] params = new String[]{minInt.toString(), maxInt.toString()};
                TooltipUtil.installWarnTooltip(node, FxmlAndLanguageUtils.getString("GLOBAL_VALIDATE_OUT_OF_RANGE_DOUBLE", params));
            } else if (minInt != null && maxInt == null) {
                node.getStyleClass().add(TEXT_FIELD_ERROR_STYLE);
                String[] params = new String[]{minInt.toString()};
                TooltipUtil.installWarnTooltip(node, FxmlAndLanguageUtils.getString("GLOBAL_VALIDATE_MIN_INT", params));
            } else if (minInt == null && maxInt != null) {
                node.getStyleClass().add(TEXT_FIELD_ERROR_STYLE);
                String[] params = new String[]{maxInt.toString()};
                TooltipUtil.installWarnTooltip(node, FxmlAndLanguageUtils.getString("GLOBAL_VALIDATE_MAX_INT", params));
            } else {
                node.getStyleClass().removeAll(TEXT_FIELD_ERROR_STYLE);
                TooltipUtil.uninstallWarnTooltip(node);
                result = true;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String reg = "^([1-9]|[1-1][0-9]|20)$";
//        System.out.println(!Pattern.matches(reg, ""));
//        System.out.println(!Pattern.matches(reg, " "));
//        System.out.println(!Pattern.matches(reg, "null"));
//        System.out.println(!Pattern.matches(reg, "sfs"));
//        System.out.println(!Pattern.matches(reg, "0"));
//        System.out.println(!Pattern.matches(reg, "1"));
//        System.out.println(!Pattern.matches(reg, "5"));
//        System.out.println(!Pattern.matches(reg, "20"));
//        System.out.println(!Pattern.matches(reg, "21"));
    }
}
