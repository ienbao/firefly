package com.dmsoft.firefly.plugin.grr.model;

import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.gui.components.utils.TableComparatorUtils;
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
    private Map<String, GrrSummaryDto> rowKeyDataMap = Maps.newLinkedHashMap();
    private Map<String, RadioButton> summaryRadioButton = Maps.newLinkedHashMap();
    private ObservableList<String> headerArray;
    private ObservableList<String> rowKeyArray;
    private List<GrrSummaryDto> summaryDtos;
    private List<Double> rules;
    private FilteredList<String> filterRowKeyArray;
    private ToggleGroup group = new ToggleGroup();
    private String radioKey = "   ";
    private int analysisType = 0;

    private Set<String> disabledRowKeys = new LinkedHashSet<>();
    private Set<String> editorCell = new HashSet<>();
    private Set<String> errorEditorCell = new HashSet<>();
    private Set<String> errorEditorRow = new HashSet<>();
    private List<String> editorRowKey = Lists.newArrayList();
    private List<TestItemWithTypeDto> editTestItem = Lists.newArrayList();
    private String selectedItemName = "";

    private SummaryRadioClickListener radioClickListener;

    /**
     * Construct a new GrrSummaryModel.
     */
    public GrrSummaryModel() {
        headerArray = FXCollections.observableArrayList();
        rowKeyArray = FXCollections.observableArrayList();
        filterRowKeyArray = rowKeyArray.filtered(s -> true);
        summaryDtos = Lists.newArrayList();
        rules = Lists.newArrayList();
    }

    /**
     * Set summary model data with given summaryDtos and selectedRowKey.
     *
     * @param summaryDtos    summary model data source
     * @param selectedRowKey current selected row key
     */
    public void setData(List<GrrSummaryDto> summaryDtos, String selectedRowKey) {
        this.clearTableData();
        if (summaryDtos != null) {
            this.summaryDtos = summaryDtos;
            this.summaryDtos.forEach(summaryDto -> {
                rowKeyArray.add(summaryDto.getItemName());
                rowKeyDataMap.put(summaryDto.getItemName(), summaryDto);
                RadioButton radioButton = new RadioButton();
                radioButton.setToggleGroup(group);
                if (!grrResultValid(summaryDto)) {
                    disabledRowKeys.add(summaryDto.getItemName());
                }
                if (DAPStringUtils.isNotBlank(selectedRowKey) && selectedRowKey.equals(summaryDto.getItemName())) {
                    this.selectedItemName = selectedRowKey;
                    radioButton.setSelected(true);
                }
                radioButton.setOnAction(event -> {
                    if (radioClickListener != null) {
                        selectedItemName = summaryDto.getItemName();
                        String valueKey = summaryDto.getItemName() + UIConstant.SPLIT_FLAG + headerArray.get(headerArray.size() - 1);
                        boolean canAnalyze = !errorEditorRow.contains(summaryDto.getItemName());
                        canAnalyze = canAnalyze && valueMap.containsKey(valueKey);
                        canAnalyze = canAnalyze && valueMap.get(valueKey).getValue() != null;
                        radioClickListener.executeAnalyzeDetail(summaryDto, getToleranceCellValue(selectedItemName), canAnalyze);
                    }
                });
                summaryRadioButton.put(summaryDto.getItemName(), radioButton);
            });
        }
    }

    private boolean grrResultValid(GrrSummaryDto summaryDto) {
        boolean valid = true;
        int digNum = DigNumInstance.newInstance().getDigNum();
        int percentDigNum = digNum - 2 >= 0 ? digNum - 2 : 0;
        Double grr = analysisType == 0 ? summaryDto.getSummaryResultDto().getGrrOnTolerance()
                : summaryDto.getSummaryResultDto().getGrrOnContribution();
        String value = this.formatterPercentValue(grr, percentDigNum);
        valid = "-".equals(value) ? false : valid;
        return valid;
    }

    /**
     * clear table
     */
    public void clearTableData() {
        disabledRowKeys.clear();
        summaryDtos.clear();
        rowKeyArray.clear();
        valueMap.clear();
        rowKeyDataMap.clear();
        summaryRadioButton.clear();
        filterRowKeyArray.clear();
        this.clearEditData();
    }

    /**
     * Clear edit data
     */
    public void clearEditData() {
        editTestItem.clear();
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
        for (Map.Entry<String, SourceObjectProperty<String>> sourceObjectPropertyEntry : valueMap.entrySet()) {
            String key = sourceObjectPropertyEntry.getKey();
            SourceObjectProperty<String> sourceObjectProperty = sourceObjectPropertyEntry.getValue();
            String columnName = key.split(UIConstant.SPLIT_FLAG)[1];
            String rowName = key.split(UIConstant.SPLIT_FLAG)[0];
            GrrSummaryDto summaryDto = rowKeyDataMap.get(rowName);
            if (UIConstant.GRR_SUMMARY_TITLE[4].equals(columnName)) {
                Double repeatability = analysisType == 0 ? summaryDto.getSummaryResultDto().getRepeatabilityOnTolerance()
                        : summaryDto.getSummaryResultDto().getRepeatabilityOnContribution();
                String value = this.formatterPercentValue(repeatability, percentDigNum);
                sourceObjectProperty.setValue(value);
            } else if (UIConstant.GRR_SUMMARY_TITLE[5].equals(columnName)) {

                Double reproducibility = analysisType == 0 ? summaryDto.getSummaryResultDto().getReproducibilityOnTolerance()
                        : summaryDto.getSummaryResultDto().getReproducibilityOnContribution();
                String value = this.formatterPercentValue(reproducibility, percentDigNum);
                sourceObjectProperty.setValue(value);
            } else if (UIConstant.GRR_SUMMARY_TITLE[6].equals(columnName)) {

                Double grr = analysisType == 0 ? summaryDto.getSummaryResultDto().getGrrOnTolerance()
                        : summaryDto.getSummaryResultDto().getGrrOnContribution();
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

                Double repeatability = analysisType == 0 ? summaryDto.getSummaryResultDto().getRepeatabilityOnTolerance()
                        : summaryDto.getSummaryResultDto().getRepeatabilityOnContribution();
                value = this.formatterPercentValue(repeatability, percentDigNum);
            } else if (UIConstant.GRR_SUMMARY_TITLE[5].equals(columnName)) {

                Double reproducibility = analysisType == 0 ? summaryDto.getSummaryResultDto().getReproducibilityOnTolerance()
                        : summaryDto.getSummaryResultDto().getReproducibilityOnContribution();
                value = this.formatterPercentValue(reproducibility, percentDigNum);
            } else if (UIConstant.GRR_SUMMARY_TITLE[6].equals(columnName)) {

                Double grr = analysisType == 0 ? summaryDto.getSummaryResultDto().getGrrOnTolerance()
                        : summaryDto.getSummaryResultDto().getGrrOnContribution();
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
                testItemWithTypeDto.setUsl(summaryDto.getSummaryResultDto().getUsl() == null ? null : String.valueOf(summaryDto.getSummaryResultDto().getUsl()));
                testItemWithTypeDto.setLsl(summaryDto.getSummaryResultDto().getLsl() == null ? null : String.valueOf(summaryDto.getSummaryResultDto().getLsl()));
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
        //can not analyze grr, radio button make red
        if (disabledRowKeys != null && disabledRowKeys.contains(rowKey)) {
            tableCell.getStyleClass().add("error");
        } else {
            tableCell.getStyleClass().removeAll("error");
        }
        if (radioKey.equals(column)) {
            tableCell.setText(null);
            tableCell.setGraphic(summaryRadioButton.get(rowKey));
            return tableCell;
        }
        if (editorCell.contains(rowKey + UIConstant.SPLIT_FLAG + column)) {
            tableCell.setStyle("-fx-text-fill: " + ColorUtils.toHexFromFXColor(UIConstant.COLOR_EDIT_CHANGE));
        }
        if (errorEditorCell.contains(rowKey + UIConstant.SPLIT_FLAG + column)) {
            tableCell.setStyle("-fx-border-with:1 1 1 1;-fx-border-color: " + ColorUtils.toHexFromFXColor(UIConstant.COLOR_EDIT_ERROR));
        }
        if (column.equals(UIConstant.GRR_SUMMARY_TITLE[6])) {
            int size = rules.size();
            String grrStr = tableCell.getText();
            grrStr = grrStr.substring(0, grrStr.length() - 1);
            if (DAPStringUtils.isBlankWithSpecialNumber(grrStr)) {
                tableCell.setStyle("-fx-background-color: " + ColorUtils.toHexFromFXColor(UIConstant.COLOR_RECTIFICATION));
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
        if (column.equals(UIConstant.GRR_SUMMARY_TITLE[4]) || column.equals(UIConstant.GRR_SUMMARY_TITLE[5]) || column.equals(UIConstant.GRR_SUMMARY_TITLE[6])) {
            tableCell.getTableColumn().setComparator((Comparator<T>) TableComparatorUtils.getContainsPercentColumnComparator());
        }
        return tableCell;
    }

    @Override
    public void setAllCheckBox(CheckBox checkBox) {

    }

    @Override
    public void setTableView(TableView<String> tableView) {

    }

    /**
     * Set analysis type, need to refresh summary, update disabledRowKeys
     *
     * @param analysisType summary analysis type
     */
    public void setAnalysisType(int analysisType) {
        this.analysisType = analysisType;
        this.updateValueMapByAnalysisType();
        this.updateDisabledRowKeys();
    }

    private void updateDisabledRowKeys() {
        disabledRowKeys.clear();
        rowKeyDataMap.forEach((key, value) -> {
            if (!grrResultValid(value)) {
                disabledRowKeys.add(key);
            }
        });
    }

    public void setRadioClickListener(SummaryRadioClickListener radioClickListener) {
        this.radioClickListener = radioClickListener;
    }

    @Override
    public boolean isTextInputError(TextField textField, String oldText, String newText, String rowKey, String columnName) {
        if (newText.length() > 255) {
            textField.setText(oldText);
            return true;
        }
        if (!ValidateUtils.validatePattern(newText, ValidateUtils.DOUBLE_PATTERN)) {
            textField.setText(oldText);
            return true;
        }
        if (DAPStringUtils.isBlank(newText) || "-".equals(newText)) {
            errorEditorRow.add(rowKey);
            errorEditorCell.add(rowKey + UIConstant.SPLIT_FLAG + columnName);
            if (!textField.getStyleClass().contains("text-field-error")) {
                textField.getStyleClass().add("text-field-error");
            }
            TooltipUtil.installWarnTooltip(textField, GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_USL_LSL_EMPTY"));
            return true;
        }
        GrrSummaryDto summaryDto = rowKeyDataMap.get(rowKey);
        if (columnName.equals(UIConstant.GRR_SUMMARY_TITLE[1])) {
            Double usl = summaryDto.getSummaryResultDto().getUsl();
            if (!DAPStringUtils.isInfinityAndNaN(usl) && Double.valueOf(newText) >= usl) {
                errorEditorRow.add(rowKey);
                errorEditorCell.add(rowKey + UIConstant.SPLIT_FLAG + columnName);
                if (!textField.getStyleClass().contains("text-field-error")) {
                    textField.getStyleClass().add("text-field-error");
                }
                TooltipUtil.installWarnTooltip(textField, GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_LSL_MORE_THEN_USL"));
                return true;
            }
        } else if (columnName.equals(UIConstant.GRR_SUMMARY_TITLE[2])) {
            Double lsl = summaryDto.getSummaryResultDto().getLsl();
            if (!DAPStringUtils.isInfinityAndNaN(lsl) && Double.valueOf(newText) <= lsl) {
                errorEditorRow.add(rowKey);
                errorEditorCell.add(rowKey + UIConstant.SPLIT_FLAG + columnName);
                if (!textField.getStyleClass().contains("text-field-error")) {
                    textField.getStyleClass().add("text-field-error");
                }
                TooltipUtil.installWarnTooltip(textField, GrrFxmlAndLanguageUtils.getString("GRR_SUMMARY_USL_LESS_THEN_LSL"));
                return true;
            }
        }
        if (errorEditorCell.contains(rowKey + UIConstant.SPLIT_FLAG + columnName)) {
            errorEditorCell.remove(rowKey + UIConstant.SPLIT_FLAG + columnName);
            textField.getStyleClass().removeAll("text-field-error");
            TooltipUtil.uninstallWarnTooltip(textField);
            return false;
        }
        textField.getStyleClass().removeAll("text-field-error");
        TooltipUtil.uninstallWarnTooltip(textField);
        return false;
    }

    /**
     * Whether it has error edit value
     *
     * @return Whether it true or false
     */
    public boolean hasErrorEditValue() {
        return errorEditorCell.size() != 0;
    }

    public List<TestItemWithTypeDto> getEditTestItem() {
        return editTestItem;
    }

    public String getSelectedItemName() {
        return selectedItemName;
    }

    /**
     * Set rule data
     *
     * @param rules rule data
     */
    public void setRules(List<Double> rules) {
        this.rules.clear();
        this.rules = rules;
    }

    /**
     * Get tolerance cell value with given row key
     *
     * @param rowKey row key
     * @return tolerance cell value
     */
    public String getToleranceCellValue(String rowKey) {
        int digNum = DigNumInstance.newInstance().getDigNum();
        String value = "-";
        value = rowKeyDataMap.containsKey(rowKey)
                ? this.formatterNormalValue(rowKeyDataMap.get(rowKey).getSummaryResultDto().getTolerance(), digNum) : value;
        return value;
    }
}
