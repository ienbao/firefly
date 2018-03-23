package com.dmsoft.firefly.plugin.grr.model;

import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.utils.ValidateUtils;
import com.dmsoft.firefly.plugin.grr.dto.GrrSummaryDto;
import com.dmsoft.firefly.plugin.grr.utils.DigNumInstance;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.grr.utils.SourceObjectProperty;
import com.dmsoft.firefly.plugin.grr.utils.UIConstant;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;

import java.util.*;

/**
 * Created by cherry on 2018/3/22.
 */
public class GrrSummaryModel implements TableModel {

    private Map<String, SourceObjectProperty<String>> valueMap = new HashMap<>();
    private Map<String, SimpleObjectProperty<Boolean>> checkMap = new HashMap<>();
    private Map<String, GrrSummaryDto> rowKeyDataMap = new HashMap<>();
    private Map<String, RadioButton> summaryRadioButton = Maps.newHashMap();
    private Map<RadioButton, String> radioButtonRowKey = Maps.newHashMap();
    private ObservableList<String> headerArray;
    private ObservableList<String> rowKeyArray;
    private List<GrrSummaryDto> summaryDtos;
    private List<Double> rules;
    private FilteredList<String> filterRowKeyArray;
    private ToggleGroup group = new ToggleGroup();
    private String radioKey = "   ";
    private int analysisType = 1;

    private Set<String> editorCell = new HashSet<>();
    private Set<String> errorEditorCell = new HashSet<>();
    private Set<String> errorEditorRow = new HashSet<>();
    private List<String> editorRowKey = Lists.newArrayList();
    private List<TestItemWithTypeDto> editTestItem = Lists.newArrayList();
    private String selectedItemName = "";

    private SummaryRadioClickListener radioClickListener;

    public GrrSummaryModel() {
        headerArray = FXCollections.observableArrayList();
        rowKeyArray = FXCollections.observableArrayList();
        filterRowKeyArray = rowKeyArray.filtered(s -> true);
        summaryDtos = Lists.newArrayList();
        rules = Lists.newArrayList();
    }

    public void setData(List<GrrSummaryDto> summaryDtos, String selectedRowKey) {
        this.clearTableData();
        if (summaryDtos != null) {
            this.selectedItemName = selectedRowKey;
            this.summaryDtos = summaryDtos;
            this.summaryDtos.forEach(summaryDto -> {
                rowKeyArray.add(summaryDto.getItemName());
                rowKeyDataMap.put(summaryDto.getItemName(), summaryDto);
                RadioButton radioButton = new RadioButton();
                radioButton.setToggleGroup(group);
                if (this.selectedItemName.equals(summaryDto.getItemName())) {
                    radioButton.setSelected(true);
                }
                radioButton.setOnAction(event -> {
                    if (radioClickListener != null) {
                        selectedItemName = summaryDto.getItemName();
                        String valueKey = summaryDto.getItemName() + UIConstant.SPLIT_FLAG + headerArray.get(headerArray.size() - 1);
                        if (!valueMap.containsKey(valueKey) || (valueMap.containsKey(valueKey) && "-".equals(valueMap.get(valueKey).getValue()))) {
                            return;
                        }
                        if (!errorEditorRow.contains(summaryDto.getItemName())) {
                            String tolerence = valueMap.get(selectedItemName + UIConstant.SPLIT_FLAG + UIConstant.GRR_SUMMARY_TITLE[3]).getValue();
                            radioClickListener.executeAnalyzeDetail(summaryDto, tolerence);
                        }
                    }
                });
                radioButtonRowKey.put(radioButton, summaryDto.getItemName());
                summaryRadioButton.put(summaryDto.getItemName(), radioButton);
            });
        }
    }

    /**
     * clear table
     */
    public void clearTableData() {
        summaryDtos.clear();
        rowKeyArray.clear();
        editTestItem.clear();
        valueMap.clear();
        checkMap.clear();
        rowKeyDataMap.clear();
        summaryRadioButton.clear();
        filterRowKeyArray.clear();
        editorCell.clear();
        editorRowKey.clear();
        errorEditorCell.clear();
        errorEditorRow.clear();
    }

    /**
     * init column
     *
     * @param columnList column list
     */
    public void initColumn(List<String> columnList) {
        headerArray.clear();
        headerArray.add(radioKey);
        headerArray.addAll(columnList);
    }

    /**
     * Filter test item
     *
     * @param filterTf filter text
     */
    public void filterTestItem(String filterTf) {
        filterRowKeyArray.setPredicate(p -> p.toLowerCase().contains(filterTf.toLowerCase()));
    }

    @Override
    public ObservableList<String> getHeaderArray() {
        return headerArray;
    }

    @Override
    public ObjectProperty<String> getCellData(String rowKey, String columnName) {

        if (columnName.equals(radioKey)) {
            return null;
        }
        if (valueMap.get(rowKey + UIConstant.SPLIT_FLAG + columnName) == null && summaryDtos != null) {
            this.setValueMap(rowKey, columnName);
        }
        return valueMap.get(rowKey + UIConstant.SPLIT_FLAG + columnName);
    }

    private void updateValueMapByAnalysisType() {
        int digNum = DigNumInstance.newInstance().getDigNum();
        int percentDigNum = digNum - 2 >= 0 ? digNum - 2 : 0;
        for (Map.Entry<String, SourceObjectProperty<String>> sourceObjectPropertyEntry: valueMap.entrySet()) {
            String key = sourceObjectPropertyEntry.getKey();
            SourceObjectProperty<String> sourceObjectProperty = sourceObjectPropertyEntry.getValue();
            String columnName = key.split(UIConstant.SPLIT_FLAG)[1];
            String rowName = key.split(UIConstant.SPLIT_FLAG)[0];
            GrrSummaryDto summaryDto = rowKeyDataMap.get(rowName);
            if (UIConstant.GRR_SUMMARY_TITLE[4].equals(columnName)) {
                Double repeatability = analysisType == 1 ?
                        summaryDto.getSummaryResultDto().getRepeatabilityOnTolerance() :
                        summaryDto.getSummaryResultDto().getRepeatabilityOnContribution();
                String value = this.formatterPercentValue(repeatability, percentDigNum);
                sourceObjectProperty.setValue(value);
            } else if (UIConstant.GRR_SUMMARY_TITLE[5].equals(columnName)) {

                Double reproducibility = analysisType == 1 ?
                        summaryDto.getSummaryResultDto().getReproducibilityOnTolerance() :
                        summaryDto.getSummaryResultDto().getReproducibilityOnContribution();
                String value = this.formatterPercentValue(reproducibility, percentDigNum);
                sourceObjectProperty.setValue(value);
            } else if (UIConstant.GRR_SUMMARY_TITLE[6].equals(columnName)) {

                Double grr = analysisType == 1 ?
                        summaryDto.getSummaryResultDto().getGrrOnTolerance() :
                        summaryDto.getSummaryResultDto().getGrrOnContribution();
                String value = this.formatterPercentValue(grr, percentDigNum);
                sourceObjectProperty.setValue(value);
            }
        }
    }

    private void setValueMap(String rowKey, String columnName) {
        String value = "";
        int digNum = DigNumInstance.newInstance().getDigNum();
        int percentDigNum = digNum - 2 >= 0 ? digNum - 2 : 0;
        GrrSummaryDto summaryDto = rowKeyDataMap.get(rowKey);
        if (summaryDto != null) {
            if (UIConstant.GRR_SUMMARY_TITLE[0].equals(columnName)) {

                value = summaryDto.getItemName();
            } else if (UIConstant.GRR_SUMMARY_TITLE[1].equals(columnName)) {

                Double lsl = summaryDto.getSummaryResultDto().getLsl();
                value = DAPStringUtils.isInfinityAndNaN(lsl) ? "-" : String.valueOf(lsl);
            } else if (UIConstant.GRR_SUMMARY_TITLE[2].equals(columnName)) {

                Double usl = summaryDto.getSummaryResultDto().getUsl();
                value = DAPStringUtils.isInfinityAndNaN(usl) ? "-" : String.valueOf(usl);
            } else if (UIConstant.GRR_SUMMARY_TITLE[3].equals(columnName)) {

                Double tolerance = summaryDto.getSummaryResultDto().getTolerance();
                value = this.formatterNormalValue(tolerance, digNum);
            } else if (UIConstant.GRR_SUMMARY_TITLE[4].equals(columnName)) {

                Double repeatability = analysisType == 1 ?
                        summaryDto.getSummaryResultDto().getRepeatabilityOnTolerance() :
                        summaryDto.getSummaryResultDto().getRepeatabilityOnContribution();
                value = this.formatterPercentValue(repeatability, percentDigNum);
            } else if (UIConstant.GRR_SUMMARY_TITLE[5].equals(columnName)) {

                Double reproducibility = analysisType == 1 ?
                        summaryDto.getSummaryResultDto().getReproducibilityOnTolerance() :
                        summaryDto.getSummaryResultDto().getReproducibilityOnContribution();
                value = this.formatterPercentValue(reproducibility, percentDigNum);
            } else if (UIConstant.GRR_SUMMARY_TITLE[6].equals(columnName)) {

                Double grr = analysisType == 1 ?
                        summaryDto.getSummaryResultDto().getGrrOnTolerance() :
                        summaryDto.getSummaryResultDto().getGrrOnContribution();
                value = this.formatterPercentValue(grr, percentDigNum);
            }
        }
        SourceObjectProperty valueProperty = new SourceObjectProperty<>(value);
        if (UIConstant.GRR_SUMMARY_TITLE[1].equals(columnName) || UIConstant.GRR_SUMMARY_TITLE[2].equals(columnName)) {
            valueProperty.addListener((ov, b1, b2) -> {
                if (!valueProperty.getSourceValue().equals(b2)) {
                    editorCell.add(rowKey + UIConstant.SPLIT_FLAG + columnName);
                    editorRowKey.add(rowKey);
                } else {
                    editorCell.remove(rowKey + UIConstant.SPLIT_FLAG + columnName);
                    editorRowKey.remove(rowKey);
                }
                if (errorEditorCell.contains(rowKey + UIConstant.SPLIT_FLAG + columnName)) {
                    return;
                }
                if (UIConstant.GRR_SUMMARY_TITLE[1].equals(columnName)) {
                    summaryDto.getSummaryResultDto().setLsl(Double.valueOf((String) b2));
                }
                if (UIConstant.GRR_SUMMARY_TITLE[2].equals(columnName)) {
                    summaryDto.getSummaryResultDto().setUsl(Double.valueOf((String) b2));
                }
                TestItemWithTypeDto testItemWithTypeDto = new TestItemWithTypeDto();
                testItemWithTypeDto.setTestItemName(summaryDto.getItemName());
                testItemWithTypeDto.setUsl(summaryDto.getSummaryResultDto().getUsl() + "");
                testItemWithTypeDto.setLsl(summaryDto.getSummaryResultDto().getLsl() + "");
                editTestItem.add(testItemWithTypeDto);
            });
        }
        valueMap.put(rowKey + UIConstant.SPLIT_FLAG + columnName, valueProperty);
    }

    private String formatterNormalValue(Double value, int digNum) {
        String valueStr = DAPStringUtils.isInfinityAndNaN(value) ? "-" : DAPStringUtils.formatDouble(value, digNum);
        valueStr = DAPStringUtils.isBlankWithSpecialNumber(valueStr) ? "-" : valueStr;
        return valueStr;
    }

    private String formatterPercentValue(Double value, int digNum) {
        String valueStr = DAPStringUtils.isInfinityAndNaN(value) ? "-" : DAPStringUtils.formatDouble(value, digNum);
        valueStr = DAPStringUtils.isBlankWithSpecialNumber(valueStr) ? "-" : valueStr + "%";
        return valueStr;
    }

    @Override
    public ObservableList<String> getRowKeyArray() {
        return filterRowKeyArray;
    }

    @Override
    public boolean isEditableTextField(String columnName) {
        if (UIConstant.GRR_SUMMARY_TITLE[1].equals(columnName) || UIConstant.GRR_SUMMARY_TITLE[2].equals(columnName)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isCheckBox(String columnName) {
        return false;
    }

    @Override
    public ObjectProperty<Boolean> getCheckValue(String rowKey, String columnName) {
        return null;
    }

    @Override
    public ObjectProperty<Boolean> getAllCheckValue(String columnName) {
        return null;
    }

    @Override
    public List<TableMenuRowEvent> getMenuEventList() {
        return null;
    }

    @Override
    public <T> TableCell<String, T> decorate(String rowKey, String column, TableCell<String, T> tableCell) {
        tableCell.setStyle(null);
        if (radioKey.equals(column)) {
            tableCell.setText(null);
            tableCell.setGraphic(summaryRadioButton.get(rowKey));
            return tableCell;
        }
        if (editorCell.contains(rowKey + UIConstant.SPLIT_FLAG + column)) {
            tableCell.setStyle("-fx-text-fill: #f38400");
        }
        if (errorEditorCell.contains(rowKey + UIConstant.SPLIT_FLAG + column)) {
            tableCell.setStyle("-fx-border-color: #ea2028;-fx-border-with:1 1 1 1");
        }
        if (column.equals(UIConstant.GRR_SUMMARY_TITLE[6])) {
            int size = rules.size();
            String grrStr = tableCell.getText();
            grrStr = grrStr.substring(0, grrStr.length() - 1);
            if (DAPStringUtils.isBlankWithSpecialNumber(grrStr)) {
                tableCell.setStyle("-fx-background-color: " + ColorUtils.toHexFromFXColor(UIConstant.COLOR_EXCELLENT));
            } else {
                double grr = Double.valueOf(grrStr);
                if (rules != null && size >= 1 && grr <= rules.get(0)) {
                    tableCell.setStyle("-fx-background-color: " + ColorUtils.toHexFromFXColor(UIConstant.COLOR_EXCELLENT));
                } else if (size >= 2 && grr > rules.get(0) && grr < rules.get(1)) {
                    tableCell.setStyle("-fx-background-color: " + ColorUtils.toHexFromFXColor(UIConstant.COLOR_GOOD));
                } else if (size >= 3 && grr >= rules.get(1) && grr < rules.get(2)) {
                    tableCell.setStyle("-fx-background-color: " + ColorUtils.toHexFromFXColor(UIConstant.COLOR_ACCEPTABLE));
                } else {
                    tableCell.setStyle("-fx-background-color: " + ColorUtils.toHexFromFXColor(UIConstant.COLOR_RECTIFICATION));
                }
            }
        }
        return tableCell;
    }

    @Override
    public void setAllCheckBox(CheckBox checkBox) {

    }

    @Override
    public void setTableView(TableView<String> tableView) {

    }

    public void setAnalysisType(int analysisType) {
        this.analysisType = analysisType;
        this.updateValueMapByAnalysisType();
    }

    public void setRadioClickListener(SummaryRadioClickListener radioClickListener) {
        this.radioClickListener = radioClickListener;
    }

    @Override
    public boolean isTextInputError(TextField textField, String oldText, String newText, String rowKey, String columnName) {
        if (newText.length() > 255) {
            textField.setText(oldText);
            errorEditorRow.add(rowKey);
            return true;
        }
        if (!ValidateUtils.validatePattern(newText, ValidateUtils.DOUBLE_PATTERN)) {
            textField.setText(oldText);
            errorEditorRow.add(rowKey);
            return true;
        }
        if (DAPStringUtils.isBlank(newText) || "-".equals(newText)) {
            errorEditorCell.add(rowKey + "-" + columnName);
            if (!textField.getStyleClass().contains("text-field-error")) {
                textField.getStyleClass().add("text-field-error");
            }
            TooltipUtil.installWarnTooltip(textField, GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_USL_LSL_EMPTY"));
            errorEditorRow.add(rowKey);
            return true;
        }
        GrrSummaryDto summaryDto = rowKeyDataMap.get(rowKey);
        if (columnName.equals(UIConstant.GRR_SUMMARY_TITLE[1])) {
            Double usl = summaryDto.getSummaryResultDto().getUsl();
            if (Double.valueOf(newText) >= usl) {
                errorEditorCell.add(rowKey + "-" + columnName);
                if (!textField.getStyleClass().contains("text-field-error")) {
                    textField.getStyleClass().add("text-field-error");
                }
                TooltipUtil.installWarnTooltip(textField, GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_LSL_MORE_THEN_USL"));
                errorEditorRow.add(rowKey);
                return true;
            }
        } else if (columnName.equals(UIConstant.GRR_SUMMARY_TITLE[2])) {
            Double lsl = summaryDto.getSummaryResultDto().getLsl();
            if (Double.valueOf(newText) <= lsl) {
                errorEditorCell.add(rowKey + "-" + columnName);
                if (!textField.getStyleClass().contains("text-field-error")) {
                    textField.getStyleClass().add("text-field-error");
                }
                TooltipUtil.installWarnTooltip(textField, GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_USL_LESS_THEN_LSL"));
                errorEditorRow.add(rowKey);
                return true;
            }
        }
        if (errorEditorCell.contains(rowKey + "-" + columnName)) {
            errorEditorCell.remove(rowKey + "-" + columnName);
            textField.getStyleClass().removeAll("text-field-error");
            TooltipUtil.uninstallWarnTooltip(textField);
            return false;
        }
        textField.getStyleClass().removeAll("text-field-error");
        TooltipUtil.uninstallWarnTooltip(textField);
        return false;
    }

    public boolean hasErrorEditValue() {
        return errorEditorCell.size() != 0;
    }

    public Map<String, GrrSummaryDto> getRowKeyDataMap() {
        return rowKeyDataMap;
    }

    public List<TestItemWithTypeDto> getEditTestItem() {
        return editTestItem;
    }

    public String getSelectedItemName() {
        return selectedItemName;
    }

    public void setRules(List<Double> rules) {
        this.rules.clear();
        this.rules = rules;
    }
}
