/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.utils;

import com.dmsoft.firefly.gui.components.utils.FxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.utils.ValidateUtil;
import com.google.common.collect.Lists;
import javafx.scene.Node;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Ethan.Yang on 2018/3/19.
 */
public class SpcSettingValidateUtil {

    private static final Integer ANALYSIS_SETTING_MIN_INT = 0;
    private static final Integer ANALYSIS_SETTING_MAX_INT = 2147483647;
    private static final String ANALYSIS_SETTING_REG_TYPE = "^([1-9]|[1-1][0-9]|20)$";

    private static final String RULE_NUMBER_REG = "^[-+]?\\d+(\\.\\d+)?$";

    private AtomicBoolean result = new AtomicBoolean(true);
    private static SpcSettingValidateUtil instance;

    /**
     * instance
     *
     * @return object
     */
    public static SpcSettingValidateUtil newInstance() {
        if (instance == null) {
            instance = new SpcSettingValidateUtil();
        }
        return instance;
    }

    /**
     * validate spc analysis setting
     *
     * @param nodes nodes
     */
    public void validateSpcAnalysisSetting(Node... nodes) {
        if (nodes != null && nodes.length > 0) {
            for (Node node : nodes) {
                if (node instanceof TextField) {
                    TextField textField = (TextField) node;
                    boolean isNotEmpty = ValidateUtil.validateIsNotEmpty(textField.getText(), textField);
                    result.set(isNotEmpty);
                    textField.textProperty().addListener((obVal, oldVal, newVal) -> {
                        if (ValidateUtil.validateIsNotEmpty(newVal, textField)) {
                            boolean isInteger = validateIsInteger(newVal, textField);
                            result.set(isInteger);
                        } else {
                            result.set(false);
                        }
                    });

                    textField.focusedProperty().addListener((obVal, oldVal, newVal) -> {
                        if (ValidateUtil.validateIsNotEmpty(textField.getText(), textField)) {
                            boolean isInteger = validateIsInteger(textField.getText(), textField);
                            result.set(isInteger);
                        } else {
                            result.set(false);
                        }
                    });
                }
            }
        }
    }


    /**
     * validate Spc alarm setting
     *
     * @param bindNodes bindNodes
     */
    public void validateSpcAlarmSetting(BindNode... bindNodes) {
        if (bindNodes != null && bindNodes.length > 0) {
            for (BindNode bindNode : bindNodes) {
                String sort = bindNode.getSort();
                List<Node> nodeList = bindNode.getNodeList();
                for (Node node : nodeList) {
                    if (node instanceof TextField) {
                        TextField textField = (TextField) node;
                        boolean isNotEmpty = ValidateUtil.validateIsNotEmpty(textField.getText(), textField);
                        result.set(isNotEmpty);
                        int index = nodeList.indexOf(node);

                        textField.textProperty().addListener((obVal, oldVal, newVal) -> {
                            if (ValidateUtil.validateIsNotEmpty(newVal, textField)) {
                                String lastValue = index == 0 ? null : ((TextField) nodeList.get(index - 1)).getText();
                                String nextValue = index == nodeList.size() - 1 ? null : ((TextField) nodeList.get(index + 1)).getText();
                                boolean isInteger = validateRule(newVal, lastValue, nextValue, textField, sort);
                                result.set(isInteger);
                            } else {
                                result.set(false);
                            }
                        });
                        textField.focusedProperty().addListener((obVal, oldVal, newVal) -> {
                            if (ValidateUtil.validateIsNotEmpty(textField.getText(), textField)) {
                                String lastValue = index == 0 ? null : ((TextField) nodeList.get(index - 1)).getText();
                                String nextValue = index == nodeList.size() - 1 ? null : ((TextField) nodeList.get(index + 1)).getText();
                                boolean isInteger = validateRule(textField.getText(), lastValue, nextValue, textField, sort);
                                result.set(isInteger);
                            } else {
                                result.set(false);
                            }
                        });
                    }
                }
            }
        }
    }

    private static boolean validateRule(String msgValue, String lastValue, String nextValue, Node node, String sort) {
        boolean isNumber = ValidateUtil.validateReg(msgValue, node, RULE_NUMBER_REG, FxmlAndLanguageUtils.getString("GLOBAL_VALIDATE_NOT_NUMBER_MSG"));
        if (!isNumber) {
            return isNumber;
        } else {
            Double msgValueD = Double.parseDouble(msgValue);
            Double lastValueD = lastValue == null ? null : Double.parseDouble(lastValue);
            Double nextValueD = nextValue == null ? null : Double.parseDouble(nextValue);
            if (sort.equals(BindNode.DESC)) {
                if (lastValueD != null && msgValueD >= lastValueD) {
                    setErrorStyle(node, SpcFxmlAndLanguageUtils.getString(ResourceMassages.ALARM_RULE_ORDER));
                    return false;
                } else if (nextValue != null && msgValueD <= nextValueD) {
                    setErrorStyle(node, SpcFxmlAndLanguageUtils.getString(ResourceMassages.ALARM_RULE_ORDER));
                    return false;
                }
            } else {
                if (lastValueD != null && msgValueD <= lastValueD) {
                    setErrorStyle(node, SpcFxmlAndLanguageUtils.getString(ResourceMassages.ALARM_RULE_ORDER));
                    return false;
                } else if (nextValue != null && msgValueD >= nextValueD) {
                    setErrorStyle(node, SpcFxmlAndLanguageUtils.getString(ResourceMassages.ALARM_RULE_ORDER));
                    return false;
                }
            }
            return true;
        }

    }

    private static void setErrorStyle(Node node, String message) {
        node.getStyleClass().add(ValidateUtil.TEXT_FIELD_ERROR_STYLE);
        TooltipUtil.installWarnTooltip(node, message);
    }


    private static boolean validateIsInteger(String msgValue, Node node) {
        String[] params = new String[]{ANALYSIS_SETTING_MIN_INT.toString(), ANALYSIS_SETTING_MAX_INT.toString()};
        boolean isInteger = ValidateUtil.validateReg(msgValue, node, ANALYSIS_SETTING_REG_TYPE, FxmlAndLanguageUtils.getString("GLOBAL_VALIDATE_OUT_OF_RANGE_DOUBLE", params));
        return isInteger;
    }

    public static boolean validateResult(Node... nodes) {
        if (nodes != null && nodes.length > 0) {
            for (Node node : nodes) {
                if (node.getStyleClass().contains(ValidateUtil.TEXT_FIELD_ERROR_STYLE)
                        || node.getStyleClass().contains(ValidateUtil.COMBO_BOX_ERROR_STYLE)) {
                    return false;
                }
            }
        }
        return true;
    }

    public class BindNode {
        public static final String DESC = "desc";
        public static final String ASC = "asc";
        private List<Node> nodeList = Lists.newArrayList();
        private String sort = DESC;

        public BindNode(Node... nodes) {
            if (nodes != null && nodes.length > 0) {
                for (Node node : nodes) {
                    nodeList.add(node);
                }
            }
        }

        public BindNode(String sort, Node... nodes) {
            this(nodes);
            this.sort = sort;
        }

        public List<Node> getNodeList() {
            return nodeList;
        }

        public void setNodeList(List<Node> nodeList) {
            this.nodeList = nodeList;
        }

        public String getSort() {
            return sort;
        }
    }
}
