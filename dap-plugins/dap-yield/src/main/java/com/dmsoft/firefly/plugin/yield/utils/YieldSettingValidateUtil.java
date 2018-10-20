package com.dmsoft.firefly.plugin.yield.utils;

import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.utils.ValidateUtil;
import com.dmsoft.firefly.gui.components.utils.ValidateUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import javafx.scene.Node;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class YieldSettingValidateUtil {

    public static final Integer ANALYSIS_SETTING_MAX_INT = 255;
    public static final String ANALYSIS_SETTING_REG_TYPE = "^([1-9]|[1-1][0-9]|20)$";

    private static final String RULE_NUMBER_REG = "^[-+]?\\d+(\\.\\d+)?$";

    private AtomicBoolean result = new AtomicBoolean(true);
    private static YieldSettingValidateUtil instance;
    private List<Node> nodeArrayList = Lists.newArrayList();

    /**
     * instance
     *
     * @return object
     */
    public static YieldSettingValidateUtil newInstance() {
        if (instance == null) {
            instance = new YieldSettingValidateUtil();
        }
        return instance;
    }

    /**
     * validate spc analysis setting
     *
     * @param nodes nodes
     */
    public void validateYieldAnalysisSetting(Node... nodes) {
        if (nodes != null && nodes.length > 0) {
            for (Node node : nodes) {
                nodeArrayList.add(node);
                if (node instanceof TextField) {
                    TextField textField = (TextField) node;
                    boolean isNotEmpty = ValidateUtil.validateIsNotEmpty(textField.getText(), textField);
                    result.set(isNotEmpty);
                    textField.textProperty().addListener((obVal, oldVal, newVal) -> {
                        if (ValidateUtil.validateIsNotEmpty(newVal, textField)) {
                            boolean isInteger = validateIsInteger(newVal);
                            if (!isInteger) {
                                textField.setText(oldVal);
                            }
                            result.set(isInteger);
                        } else {
                            result.set(false);
                        }
                    });

                    textField.focusedProperty().addListener((obVal, oldVal, newVal) -> {
                        if (ValidateUtil.validateIsNotEmpty(textField.getText(), textField)) {
                            result.set(true);
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
    public void validateYieldAlarmSetting(BindNode... bindNodes) {
        if (bindNodes != null && bindNodes.length > 0) {
            int i=0;
            for (BindNode bindNode : bindNodes) {
                String sort = i==0?bindNode.getSort():BindNode.ASC;
                i=1;
                List<Node> nodeList = bindNode.getNodeList();
                for (Node node : nodeList) {
                    nodeArrayList.add(node);
                    if (node instanceof TextField) {
                        TextField textField = (TextField) node;
                        boolean isNotEmpty = ValidateUtil.validateIsNotEmpty(textField.getText(), textField);
                        result.set(isNotEmpty);
                        int index = nodeList.indexOf(node);

                        textField.textProperty().addListener((obVal, oldVal, newVal) -> {
                            if (ValidateUtil.validateIsNotEmpty(newVal, textField)) {
                                boolean isDouble = validateIsDouble(newVal);
                                if(sort.equals(BindNode.ASC)) {
                                    isDouble = validateIsPositiveNumber(newVal);//判断是否为正数
                                }
                                if (!isDouble) {
                                    textField.setText(oldVal);
                                    return;
                                }
                                String lastValue = index == 0 ? null : ((TextField) nodeList.get(index - 1)).getText();
                                String nextValue = index == nodeList.size() - 1 ? null : ((TextField) nodeList.get(index + 1)).getText();
                                boolean isInteger = validateRule(newVal, lastValue, nextValue, textField, sort, nodeList);
                                result.set(isInteger);
                            } else {
                                result.set(false);
                            }
                        });
                        textField.focusedProperty().addListener((obVal, oldVal, newVal) -> {
                            if (ValidateUtil.validateIsNotEmpty(textField.getText(), textField)) {
                                String lastValue = index == 0 ? null : ((TextField) nodeList.get(index - 1)).getText();
                                String nextValue = index == nodeList.size() - 1 ? null : ((TextField) nodeList.get(index + 1)).getText();
                                boolean isInteger = validateRule(textField.getText(), lastValue, nextValue, textField, sort, nodeList);
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

    private static boolean validateRule(String msgValue, String lastValue, String nextValue, Node node, String sort, List<Node> nodes) {
        if (!DAPStringUtils.isNumeric(msgValue)){
            setErrorStyle(node, YieldFxmlAndLanguageUtils.getString(ResourceMassages.ALARM_RULE_ORDER));
            return false;
        }
        Double msgValueD = Double.parseDouble(msgValue);
        Double lastValueD = DAPStringUtils.isBlank(lastValue) || !DAPStringUtils.isNumeric(lastValue) ? null : Double.parseDouble(lastValue);
        Double nextValueD = DAPStringUtils.isBlank(nextValue) || !DAPStringUtils.isNumeric(nextValue) ? null : Double.parseDouble(nextValue);
        int index = nodes.indexOf(node);
        if (sort.equals(BindNode.DESC)) {
            if (lastValueD != null && msgValueD >= lastValueD) {
                setErrorStyle(node, YieldFxmlAndLanguageUtils.getString(ResourceMassages.ALARM_RULE_ORDER));
                return false;
            } else if (lastValueD != null && msgValueD < lastValueD) {
                Node lastNode = nodes.get(index - 1);
                if (index < 2) {
                    uninstallErrorStyle(lastNode);
                } else {
                    if (lastNode != null) {
                        Double lastValueV = DAPStringUtils.isBlank(((TextField) lastNode).getText()) ? null : Double.parseDouble(((TextField) lastNode).getText());
                        Node lastNodeD = nodes.get(index - 2);
                        if (lastNodeD != null && lastValueV != null) {
                            Double lastValueDV = DAPStringUtils.isBlank(((TextField) lastNodeD).getText()) ? null : Double.parseDouble(((TextField) lastNodeD).getText());
                            if (lastValueDV != null && lastValueDV > lastValueV) {//前面的数大于后面的数
                                uninstallErrorStyle(lastNode);
                            }
                        }
                    }
                }
            }
            if (nextValueD != null && msgValueD <= nextValueD) {
                setErrorStyle(node, YieldFxmlAndLanguageUtils.getString(ResourceMassages.ALARM_RULE_ORDER));
                return false;
            } else if (nextValueD != null && msgValueD > nextValueD) {
                Node nextNode = nodes.get(index + 1);
                if (index > nodes.size() - 3) {
                    uninstallErrorStyle(nextNode);
                } else {
                    if (nextNode != null) {
                        Double nextValueV = DAPStringUtils.isBlank(((TextField) nextNode).getText()) ? null : Double.parseDouble(((TextField) nextNode).getText());
                        Node nextNodeD = nodes.get(index + 2);
                        if (nextNodeD != null && nextValueV != null) {
                            Double nextValueDV = DAPStringUtils.isBlank(((TextField) nextNodeD).getText()) ? null : Double.parseDouble(((TextField) nextNodeD).getText());
                            if (nextValueDV != null && nextValueDV < nextValueV) {
                                uninstallErrorStyle(nextNode);
                            }
                        }
                    }
                }
            }
        } else {
            if (lastValueD != null && msgValueD <= lastValueD) {
                setErrorStyle(node, YieldFxmlAndLanguageUtils.getString(ResourceMassages.ALARM_RULE_ORDER));
                return false;
            } else if (lastValueD != null && msgValueD > lastValueD) {
                Node lastNode = nodes.get(index - 1);
                if (index < 2) {
                    uninstallErrorStyle(lastNode);
                } else {
                    if (lastNode != null) {
                        Double lastValueV = DAPStringUtils.isBlank(((TextField) lastNode).getText()) ? null : Double.parseDouble(((TextField) lastNode).getText());
                        Node lastNodeD = nodes.get(index - 2);
                        if (lastNodeD != null && lastValueV != null) {
                            Double lastValueDV = DAPStringUtils.isBlank(((TextField) lastNodeD).getText()) ? null : Double.parseDouble(((TextField) lastNodeD).getText());
                            if (lastValueDV != null && lastValueDV < lastValueV) {
                                uninstallErrorStyle(lastNode);
                            }
                        }
                    }
                }
            }
            if (nextValueD != null && msgValueD >= nextValueD) {
                setErrorStyle(node, YieldFxmlAndLanguageUtils.getString(ResourceMassages.ALARM_RULE_ORDER));
                return false;
            } else if (nextValueD != null && msgValueD < nextValueD) {
                Node nextNode = nodes.get(index + 1);
                if (index > nodes.size() - 3) {
                    uninstallErrorStyle(nextNode);
                } else {
                    if (nextNode != null) {
                        Double nextValueV = DAPStringUtils.isBlank(((TextField) nextNode).getText()) ? null : Double.parseDouble(((TextField) nextNode).getText());
                        Node nextNodeD = nodes.get(index + 2);
                        if (nextNodeD != null && nextValueV != null) {
                            Double nextValueDV = DAPStringUtils.isBlank(((TextField) nextNodeD).getText()) ? null : Double.parseDouble(((TextField) nextNodeD).getText());
                            if (nextValueDV != null && nextValueDV > nextValueV) {
                                uninstallErrorStyle(nextNode);
                            }
                        }
                    }
                }
            }
        }
        return true;

    }

    private static void setErrorStyle(Node node, String message) {
        node.getStyleClass().add(ValidateUtil.TEXT_FIELD_ERROR_STYLE);
        TooltipUtil.installWarnTooltip(node, message);
    }

    private static void uninstallErrorStyle(Node node) {
        if (node == null) {
            return;
        }
        if (node.getStyleClass().contains(ValidateUtil.TEXT_FIELD_ERROR_STYLE)) {
            node.getStyleClass().remove(ValidateUtil.TEXT_FIELD_ERROR_STYLE);
        }
        TooltipUtil.uninstallWarnTooltip(node);
    }

    private static boolean validateIsDouble(String value) {
        if (!Pattern.matches(ValidateUtils.DOUBLE_PATTERN, value) || value.length() > ANALYSIS_SETTING_MAX_INT) {
            return false;
        }
        return true;
    }

    private static boolean validateIsPositiveNumber(String value){
        if (!Pattern.matches(ValidateUtils.POSITIVE_DOUBLE_PATTERN, value) || value.length() > ANALYSIS_SETTING_MAX_INT) {
            return false;
        }
        return true;
    }

    private static boolean validateIsInteger(String value) {
        if (!Pattern.matches(ANALYSIS_SETTING_REG_TYPE, value) || value.length() > ANALYSIS_SETTING_MAX_INT) {
            return false;
        }
        return true;
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

    public boolean getResult() {
        return result.get();
    }

    public boolean hasErrorResult() {
        for (Node node : nodeArrayList) {
            if (node.getStyleClass().contains(ValidateUtil.TEXT_FIELD_ERROR_STYLE)
                    || node.getStyleClass().contains(ValidateUtil.COMBO_BOX_ERROR_STYLE)) {
                return true;
            }
        }
        return false;
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