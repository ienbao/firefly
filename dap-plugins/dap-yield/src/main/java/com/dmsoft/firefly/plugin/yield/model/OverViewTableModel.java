package com.dmsoft.firefly.plugin.yield.model;

import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.utils.ValidateUtils;
import com.dmsoft.firefly.plugin.yield.dto.OverviewAlarmDto;
import com.dmsoft.firefly.plugin.yield.dto.YieldOverviewResultAlarmDto;
import com.dmsoft.firefly.plugin.yield.utils.*;
import com.dmsoft.firefly.sdk.utils.ColorUtils;
import com.dmsoft.firefly.sdk.utils.DAPDoubleUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OverViewTableModel  implements TableModel{
    private static final String[] YIELD_OVERVIEW_FIX_COLUMN = UIConstant.YIELD_OVERVIEW_FIX_COLUMN;
    private static final String[] YIELD_TITLE = UIConstant.YIELD_SR_ALL;
    private ObservableList<String> columnKey = FXCollections.observableArrayList();
    private ObservableList<String> rowKey = FXCollections.observableArrayList();
    private Map<String, SourceObjectProperty<String>> valueMap = Maps.newLinkedHashMap();
    private Map<String, SimpleObjectProperty<Boolean>> checkMap = Maps.newLinkedHashMap();
    private ObjectProperty<Boolean> allChecked = new SimpleObjectProperty<>(false);
    private Set<String> falseSet = new HashSet<>();
    private List<YieldOverviewResultAlarmDto> overviewResultAlarmDtoList;

    private FilteredList<String> statisticalTableRowDataFilteredList;
    private SortedList<String> statisticalTableRowDataSortedList;
    private Map<String, YieldOverviewResultAlarmDto> keyToStatsDtoMap = Maps.newLinkedHashMap();

    private List<TableMenuRowEvent> menuRowEvents;
    private CheckBox allCheckBox;

    private TableView<String> tableView;
    private Set<String> emptyResultKeys = new HashSet<>();

    private Map<String, Color> colorCache = Maps.newLinkedHashMap();

    private Set<String> editorCell = new HashSet<>();
    private List<String> editorRowKey = Lists.newArrayList();

    private Set<String> errorEditorCell = new HashSet<>();

    private boolean isTimer = false;
    private List<String> columnList;


    /**
     * constructor
     */
    public OverViewTableModel() {
        this.columnKey.addAll(YIELD_OVERVIEW_FIX_COLUMN);
        statisticalTableRowDataFilteredList = rowKey.filtered(p -> true);
        statisticalTableRowDataSortedList = new SortedList<>(statisticalTableRowDataFilteredList);
        this.menuRowEvents = Lists.newArrayList();
    }

    /**
     * init model data
     *
     * @param overviewResultAlarmDtoList data list
     */
    public void initData(List<YieldOverviewResultAlarmDto> overviewResultAlarmDtoList) {
        tableView.getSortOrder().clear();
        tableView.sort();
        this.overviewResultAlarmDtoList = overviewResultAlarmDtoList;
        this.clearTableData();
        if (overviewResultAlarmDtoList != null) {
            int m = 0;
            for (YieldOverviewResultAlarmDto dto : overviewResultAlarmDtoList) {
                rowKey.add(dto.getKey());
                keyToStatsDtoMap.put(dto.getKey(), dto);
                if (this.isEmptyResult(dto.getOverviewAlarmDtoMap())) {
                    emptyResultKeys.add(dto.getKey());
                } else {
                    colorCache.put(dto.getKey(), ColorUtils.getTransparentColor(Colur.RAW_VALUES[m % 10], 0.8));
                    m++;
                }
            }
        }
    }

//    public void setSelect(List<String> selectRowList) {
//        if (selectRowList == null || overviewResultAlarmDtoList == null) {
//            return;
//        }
//        for (SpcStatisticalResultAlarmDto dto : overviewResultAlarmDtoList) {
//            SimpleObjectProperty<Boolean> b = new SimpleObjectProperty<>(false);
//            if (selectRowList.contains(dto.getKey())) {
//                b.set(true);
//            } else {
//                falseSet.add(dto.getKey());
//                allChecked.setValue(false);
//            }
//            checkMap.put(dto.getKey(), b);
//            b.addListener((ov, b1, b2) -> {
//                if (!b2) {
//                    falseSet.add(dto.getKey());
//                    allChecked.setValue(false);
//                } else {
//                    falseSet.remove(dto.getKey());
//                    if (falseSet.isEmpty()) {
//                        allChecked.setValue(true);
//                    }
//                }
//            });
//        }
//
//    }
//
    /**
     * init column
     *
     * @param columnList column list
     */
    public void initColumn(List<String> columnList) {
        this.columnList = columnList;
        columnKey.remove(3, columnKey.size());
//        columnKey.addAll(Arrays.asList(SPC_STATISTICAL_FIX_COLUMN));
        columnKey.addAll(columnList);
    }
//
//    /**
//     * refresh spc statistical data
//     *
//     * @param spcStatsDtoList the refresh data
//     */
//    public void refreshData(List<SpcStatisticalResultAlarmDto> spcStatsDtoList) {
//        if (spcStatsDtoList == null) {
//            return;
//        }
//        editorCell.clear();
//        editorRowKey.clear();
//        errorEditorCell.clear();
//        int m = 0;
//        for (SpcStatisticalResultAlarmDto statisticalResultAlarmDto : spcStatsDtoList) {
//            String key = statisticalResultAlarmDto.getKey();
//            SpcStatisticalResultAlarmDto resultAlarmDto = keyToStatsDtoMap.get(key);
//            if (resultAlarmDto != null) {
//                Map<String, StatisticalAlarmDto> statisticalAlarmDtoMap = statisticalResultAlarmDto.getStatisticalAlarmDtoMap();
//                resultAlarmDto.setStatisticalAlarmDtoMap(statisticalAlarmDtoMap);
//            }
//            this.refreshValue(resultAlarmDto);
//            if (this.isEmptyResult(statisticalResultAlarmDto.getStatisticalAlarmDtoMap())) {
//                emptyResultKeys.add(key);
//                if (colorCache.containsKey(key)) {
//                    colorCache.remove(key);
//                }
//            } else {
//                if (emptyResultKeys.contains(statisticalResultAlarmDto.getKey())) {
//                    emptyResultKeys.remove(statisticalResultAlarmDto.getKey());
//                }
//                colorCache.put(key, ColorUtils.getTransparentColor(Colur.RAW_VALUES[m % 10], 0.8));
//                m++;
//            }
//        }
//        tableView.refresh();
//    }
//
//    private void refreshValue(SpcStatisticalResultAlarmDto spcStatsDto) {
//        String value = "";
//        if (spcStatsDto != null) {
//            String spcStatsDtoKey = spcStatsDto.getKey();
//            for (int i = 0; i < YIELD_TITLE.length; i++) {
//                String columnName = YIELD_TITLE[i];
//                if (i == 0) {
//                    value = spcStatsDto.getItemName();
//                } else if (i == 1) {
//                    value = DAPStringUtils.isBlank(spcStatsDto.getCondition()) ? "All" : spcStatsDto.getCondition();
//                } else {
//                    Map<String, StatisticalAlarmDto> statisticalAlarmDtoMap = spcStatsDto.getStatisticalAlarmDtoMap();
//                    if (statisticalAlarmDtoMap == null) {
//                        value = "-";
//                    } else {
//                        String key = columnName;
//                        if (i == 16) {
//                            key = SpcStatisticalResultKey.CA.getCode();
//                        }
//                        value = showValue(key, statisticalAlarmDtoMap.get(key));
//                    }
//                }
//                SourceObjectProperty valueProperty = new SourceObjectProperty<>(value);
//                if (columnName.equals(YIELD_TITLE[7]) || columnName.equals(YIELD_TITLE[8])) {
//                    valueProperty.addListener((ov, b1, b2) -> {
//                        if (DAPStringUtils.isBlank((String) b2) || !DAPStringUtils.isNumeric((String) b2)) {
//                            return;
//                        }
//                        if (!DAPStringUtils.isEqualsString((String) valueProperty.getSourceValue(), (String) b2)) {
//                            editorCell.add(spcStatsDtoKey + "-" + columnName);
//                            editorRowKey.add(spcStatsDtoKey);
//                        } else {
//                            editorCell.remove(spcStatsDtoKey + "-" + columnName);
//                            editorRowKey.remove(spcStatsDtoKey);
//                        }
//                        if (errorEditorCell.contains(spcStatsDtoKey + "-" + columnName)) {
//                            valueProperty.setError(true);
//                            return;
//                        }
//                        spcStatsDto.getStatisticalAlarmDtoMap().get(columnName).setValue(Double.valueOf((String) b2));
//                    });
//                }
//                valueMap.put(spcStatsDtoKey + "-" + columnName, valueProperty);
//            }
//        }
//    }

    /**
     * clear table
     */
    public void clearTableData() {
        rowKey.clear();
        valueMap.clear();
        checkMap.clear();
        falseSet.clear();
        colorCache.clear();
        emptyResultKeys.clear();
        editorCell.clear();
        allChecked.setValue(false);
        editorRowKey.clear();
        keyToStatsDtoMap.clear();
        errorEditorCell.clear();
    }

    /**
     * filter testItem
     *
     * @param filterTf filter text
     */
    public void filterTestItem(String filterTf) {
        statisticalTableRowDataFilteredList.setPredicate(p -> {
            if (keyToStatsDtoMap.get(p) == null) {
                return false;
            }
            String testItem = keyToStatsDtoMap.get(p).getItemName();
            return testItem.toLowerCase().contains(filterTf.toLowerCase());
        });
    }


//    /**
//     * get select data
//     *
//     * @return the list of SpcStatsDto
//     */
//    public List<SpcStatisticalResultAlarmDto> getSelectData() {
//        List<SpcStatisticalResultAlarmDto> selectStatsDtoList = Lists.newArrayList();
//        for (Map.Entry<String, SimpleObjectProperty<Boolean>> entry : checkMap.entrySet()) {
//            String key = entry.getKey().toString();
//            boolean isSelect = entry.getValue().getValue();
//            if (isSelect) {
//                SpcStatisticalResultAlarmDto spcStatsDto = keyToStatsDtoMap.get(key);
//                selectStatsDtoList.add(spcStatsDto);
//            }
//        }
//        return selectStatsDtoList;
//    }
//
//    /**
//     * get select row key
//     *
//     * @return row key
//     */
//    public List<String> getSelectRowKey() {
//        List<String> rowList = Lists.newArrayList();
//        for (Map.Entry<String, SimpleObjectProperty<Boolean>> entry : checkMap.entrySet()) {
//            String key = entry.getKey().toString();
//            boolean isSelect = entry.getValue().getValue();
//            if (isSelect) {
//                rowList.add(key);
//            }
//        }
//        return rowList;
//    }
//
//    /**
//     * get editor row key
//     *
//     * @return the row keys
//     */
//    public List<String> getEditorRowKey() {
//        if (editorRowKey == null) {
//            return null;
//        }
//        List<String> rowKeyList = Lists.newArrayList();
//        for (String key : editorRowKey) {
//            if (!rowKeyList.contains(key)) {
//                rowKeyList.add(key);
//            }
//        }
//        return rowKeyList;
//    }
//
//    /**
//     * get edit row data
//     *
//     * @return the row data
//     */
//    public List<SpcStatisticalResultAlarmDto> getEditRowData() {
//        List<String> rowKeyList = getEditorRowKey();
//        if (rowKeyList == null) {
//            return null;
//        }
//        List<SpcStatisticalResultAlarmDto> editRowDataList = Lists.newArrayList();
//        for (String key : rowKeyList) {
//            editRowDataList.add(keyToStatsDtoMap.get(key));
//        }
//        return editRowDataList;
//    }

    @Override
    public ObservableList<String> getHeaderArray() {
        return columnKey;
    }

    @Override
    public ObjectProperty<String> getCellData(String rowKey, String columnName) {
        if (valueMap.get(rowKey + "-" + columnName) == null && overviewResultAlarmDtoList != null) {
            this.setValueMap(rowKey, columnName);
        }
        return valueMap.get(rowKey + "-" + columnName);
    }

    @Override
    public ObservableList<String> getRowKeyArray() {
        return statisticalTableRowDataSortedList;
    }

    @Override
    public boolean isEditableTextField(String columnName) {
        if (!isTimer && (columnName.equals(YIELD_TITLE[1]) || columnName.equals(YIELD_TITLE[2]))) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isCheckBox(String columnName) {
        if (columnName.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public ObjectProperty<Boolean> getCheckValue(String rowKey, String columnName) {
        if (checkMap.get(rowKey) == null) {
            SimpleObjectProperty<Boolean> b = new SimpleObjectProperty<>(false);
            checkMap.put(rowKey, b);
            falseSet.add(rowKey);
            allChecked.setValue(false);
            b.addListener((ov, b1, b2) -> {
                if (!b2) {
                    falseSet.add(rowKey);
                    allChecked.setValue(false);
                } else {
                    falseSet.remove(rowKey);
                    if (falseSet.isEmpty()) {
                        allChecked.setValue(true);
                    }
                }
            });
        }
        return checkMap.get(rowKey);
    }


    @Override
    public ObjectProperty<Boolean> getAllCheckValue(String columnName) {
        return allChecked;
    }

    @Override
    public List<TableMenuRowEvent> getMenuEventList() {
        return menuRowEvents;
    }

    @Override
    public <T> TableCell<String, T> decorate(String rowKey, String column, TableCell<String, T> tableCell) {
        if (!this.isEditableTextField(column)) {
            tableCell.setEditable(false);
        } else {
            tableCell.setEditable(true);
        }
        tableCell.setStyle(null);
        tableCell.getStyleClass().remove("error");
        if (DAPStringUtils.isBlank(column)) {
            Color color = colorCache.get(rowKey);
            if (color != null) {
                tableCell.setStyle("-fx-background-color:" + ColorUtils.toHexFromColor(color));
            }
            if (emptyResultKeys.contains(rowKey)) {
                tableCell.getStyleClass().add("error");
            }
        }
        if (editorCell.contains(rowKey + "-" + column)) {
            tableCell.setStyle("-fx-text-fill: #f38400");
        }
        if (errorEditorCell.contains(rowKey + "-" + column)) {
            tableCell.setStyle("-fx-border-color: #ea2028;-fx-border-with:1 1 1 1");
        }

        YieldOverviewResultAlarmDto spcStatsDto = keyToStatsDtoMap.get(rowKey);
        Map<String, OverviewAlarmDto> statisticalAlarmDtoMap = spcStatsDto.getOverviewAlarmDtoMap();
        if (statisticalAlarmDtoMap != null) {
            if (column.equals(YIELD_TITLE[8])) {
                column = YieldOverviewKey.FPYPER.getCode();
            }else if (column.equals(YIELD_TITLE[9])) {
                column = YieldOverviewKey.NTFPER.getCode();
            }else if(column.equals(YIELD_TITLE[10])) {
                column = YieldOverviewKey.NGPER.getCode();
            }
            if (statisticalAlarmDtoMap.get(column) == null) {
                return tableCell;
            }
            String level = statisticalAlarmDtoMap.get(column).getLevel();
            if (level != null) {
                Color bgColor = getAlarmBackgroundColor(level);
                Color fgColor = Color.BLACK;
                if (Colur.isUsingLightFont(bgColor)) {
                    fgColor = Colur.WHITE;
                }
                tableCell.setStyle("-fx-background-color:" + ColorUtils.toHexFromColor(bgColor) + ";-fx-text-fill:" + ColorUtils.toHexFromColor(fgColor));
            }
        }


        return tableCell;
    }

    @Override
    public void setTableView(TableView<String> tableView) {
        this.tableView = tableView;
    }

    public CheckBox getAllCheckBox() {
        return allCheckBox;
    }

    @Override
    public void setAllCheckBox(CheckBox checkBox) {
        this.allCheckBox = checkBox;
    }

    /**
     * add menu event
     *
     * @param tableMenuRowEvent event
     */
    public void addTableMenuEvent(TableMenuRowEvent tableMenuRowEvent) {
        menuRowEvents.add(tableMenuRowEvent);
    }

    private void setValueMap(String rowKey, String columnName) {
        String value = "";
        YieldOverviewResultAlarmDto overviewResultAlarmDto = keyToStatsDtoMap.get(rowKey);
        if (overviewResultAlarmDto != null) {
            if (columnName.equals(YIELD_TITLE[0])) {
                value = overviewResultAlarmDto.getItemName();
                if (value == null){value = "-";}
            }else if(columnName.equals(YIELD_TITLE[1])){
                value = overviewResultAlarmDto.getLslOrFail();
                if (value == null){value = "-";}
            }else if(columnName.equals(YIELD_TITLE[2])){
                value = overviewResultAlarmDto.getUslOrPass();
                if (value == null){value = "-";}
            }else if(columnName.equals(YIELD_TITLE[3])){
                value = overviewResultAlarmDto.getTotalSamples()+"";
                if (overviewResultAlarmDto.getTotalSamples() == null){value = "-";}
            }else if(columnName.equals(YIELD_TITLE[4])){
                value = overviewResultAlarmDto.getFpySamples()+"";
                if (overviewResultAlarmDto.getFpySamples() == null){value = "-";}
            } else if(columnName.equals(YIELD_TITLE[5])){
                value = overviewResultAlarmDto.getPassSamples()+"";
                if (overviewResultAlarmDto.getPassSamples() == null){value = "-";}
            }else if(columnName.equals(YIELD_TITLE[6])){
                value = overviewResultAlarmDto.getNtfSamples()+"";
                if (overviewResultAlarmDto.getNtfSamples() == null){value = "-";}
            }else if(columnName.equals(YIELD_TITLE[7])){
                value = overviewResultAlarmDto.getNgSamples()+"";
                if (overviewResultAlarmDto.getNgSamples() == null){value = "-";}
            } else {
                Map<String, OverviewAlarmDto> overviewAlarmDtoMap = overviewResultAlarmDto.getOverviewAlarmDtoMap();
                if (overviewAlarmDtoMap == null) {
                    value = "-";
                } else {
                    String key = columnName;
                    if (columnName.equals(YIELD_TITLE[8])) {
                        key = YieldOverviewKey.FPYPER.getCode();
                    }else if (columnName.equals(YIELD_TITLE[9])){
                        key = YieldOverviewKey.NTFPER.getCode();
                    }else if (columnName.equals(YIELD_TITLE[10])){
                        key = YieldOverviewKey.NGPER.getCode();
                    }
                    value = showValue(key, overviewAlarmDtoMap.get(key));
                }
            }
        }
        SourceObjectProperty valueProperty = new SourceObjectProperty<>(value);
        if (columnName.equals(YIELD_TITLE[1]) || columnName.equals(YIELD_TITLE[2])) {
            valueProperty.addListener((ov, b1, b2) -> {
                if (DAPStringUtils.isBlank((String) b2) || !DAPStringUtils.isNumeric((String) b2)) {
                    return;
                }
                if (!DAPStringUtils.isEqualsString((String) valueProperty.getSourceValue(), (String) b2)) {
                    editorCell.add(rowKey + "-" + columnName);
                    editorRowKey.add(rowKey);
                } else {
                    editorCell.remove(rowKey + "-" + columnName);
                    editorRowKey.remove(rowKey);
                }
                if (errorEditorCell.contains(rowKey + "-" + columnName)) {
                    valueProperty.setError(true);
                    return;
                }
                overviewResultAlarmDto.getOverviewAlarmDtoMap().get(columnName).setValue(Double.valueOf((String) b2));
            });
        }
        valueMap.put(rowKey + "-" + columnName, valueProperty);
    }

    public SortedList<String> getStatisticalTableRowDataSortedList() {
        return statisticalTableRowDataSortedList;
    }

    public Map<String, SimpleObjectProperty<Boolean>> getCheckMap() {
        return checkMap;
    }

    /**
     * set row background color
     *
     * @param rowKey row key
     * @param color  color
     */
    public void setRowColor(String rowKey, Color color) {
        colorCache.put(rowKey, color);
    }

    public Set<String> getEmptyResultKeys() {
        return emptyResultKeys;
    }

    private boolean isEmptyResult(Map<String, OverviewAlarmDto> statisticalAlarmDtoMap) {
        OverviewAlarmDto simpleDto = statisticalAlarmDtoMap.get(YIELD_TITLE[2]);
        if (simpleDto == null || simpleDto.getValue() == null
                || simpleDto.getValue() == 0) {
            return true;
        }
        return false;
    }

    private String showValue(String key, OverviewAlarmDto overviewAlarmDto) {
        DigNumInstance.newInstance().setDigNum(2);
        if (overviewAlarmDto == null || DAPDoubleUtils.isSpecialNumber(overviewAlarmDto.getValue())) {
            return "-";
        }
        if (key.equals(YIELD_TITLE[8]) || key.equals(YIELD_TITLE[9]) || key.equals(YIELD_TITLE[10])) {
         String str = DAPStringUtils.formatDouble(overviewAlarmDto.getValue(), DigNumInstance.newInstance().getDigNum());
         NumberFormat num = NumberFormat.getPercentInstance();
         return num.format(Double.valueOf(str));
        }
        if (key.equals(YIELD_TITLE[2])) {
            DecimalFormat df = new DecimalFormat("######0");
            return Integer.valueOf(df.format(overviewAlarmDto.getValue())).toString();
        }

        return overviewAlarmDto.getValue().toString();
    }
//
//    public Map<String, Color> getColorCache() {
//        return colorCache;
//    }
//
    private Color getAlarmBackgroundColor(String level) {
        Color color = Color.WHITE;
        if (level.equals(YieldOverviewKey.EXCELLENT.getCode())) {
            color = Colur.GREEN;
        } else if (level.equals(YieldOverviewKey.ADEQUATE.getCode())) {
            color = Colur.LEVEL_B;
        } else if (level.equals(YieldOverviewKey.MARGINAL.getCode())) {
            color = Colur.LEVEL_C;
        } else if (level.equals(YieldOverviewKey.BAD.getCode())) {
            color = Colur.LEVEL_D;
        }
//        else if (level.equals(YieldOverviewKey.PASS.getCode())) {
//            color = Colur.GREEN;
//        } else if (level.equals(YieldOverviewKey.FAIL.getCode())) {
//            color = Colur.LEVEL_D;
//        }
        return color;
    }

//    /**
//     * get spc statistical data
//     *
//     * @return the list of data
//     */
//    public List<SpcStatisticalResultAlarmDto> getOverviewResultAlarmDtoList() {
//        if (keyToStatsDtoMap == null) {
//            return null;
//        }
//        List<SpcStatisticalResultAlarmDto> spcStatisticalResultAlarmDtoList = Lists.newArrayList();
//        for (Map.Entry<String, SpcStatisticalResultAlarmDto> entry : keyToStatsDtoMap.entrySet()) {
//            spcStatisticalResultAlarmDtoList.add(entry.getValue());
//        }
//        return spcStatisticalResultAlarmDtoList;
//    }

    /**
     * is menu event enable
     *
     * @param rowKey row key
     * @return boolean
     */
    public boolean isMenuEventEnable(String rowKey) {
        return !emptyResultKeys.contains(rowKey);
    }

//    @Override
//    public boolean isTextInputError(TextField textField, String oldText, String newText, String rowKey, String columnName) {
//        if (newText.length() > 255) {
//            textField.setText(oldText);
//            return true;
//        }
//        if (!ValidateUtils.validatePattern(newText, ValidateUtils.DOUBLE_PATTERN)) {
//            textField.setText(oldText);
//            return true;
//        }
//        if (DAPStringUtils.isBlank(newText)) {
//            errorEditorCell.add(rowKey + "-" + columnName);
//            if (!textField.getStyleClass().contains("text-field-error")) {
//                textField.getStyleClass().add("text-field-error");
//            }
//            TooltipUtil.installWarnTooltip(textField, SpcFxmlAndLanguageUtils.getString(ResourceMassages.SPC_STATISTICAL_USL_LSL_EMPTY));
//            return true;
//        }
//        SpcStatisticalResultAlarmDto spcStatsDto = keyToStatsDtoMap.get(rowKey);
//        Map<String, StatisticalAlarmDto> statisticalAlarmDtoMap = spcStatsDto.getStatisticalAlarmDtoMap();
//        if (columnName.equals(YIELD_TITLE[7])) {
//            SourceObjectProperty uslProperty = valueMap.get(rowKey + "-" + YIELD_TITLE[8]);
//            if (!DAPStringUtils.isNumeric((String) uslProperty.getValue())) {
//                return false;
//            }
//            StatisticalAlarmDto statisticalAlarmDto = statisticalAlarmDtoMap.get(SpcStatisticalResultKey.USL.getCode());
//            Double usl = Double.valueOf((String) uslProperty.getValue());
//            if (!DAPStringUtils.isNumeric(newText) || Double.valueOf(newText) >= usl) {
//                errorEditorCell.add(rowKey + "-" + columnName);
//                if (!textField.getStyleClass().contains("text-field-error")) {
//                    textField.getStyleClass().add("text-field-error");
//                }
//                TooltipUtil.installWarnTooltip(textField, SpcFxmlAndLanguageUtils.getString(ResourceMassages.SPC_STATISTICAL_LSL_MORE_THEN_USL));
//                return true;
//            } else if (errorEditorCell.contains(rowKey + "-" + YIELD_TITLE[8])) {
//                errorEditorCell.remove(rowKey + "-" + YIELD_TITLE[8]);
//                if (uslProperty.isError()) {
//                    uslProperty.setError(false);
//                    statisticalAlarmDto.setValue(usl);
//                }
//            }
//        } else if (columnName.equals(YIELD_TITLE[8])) {
//            SourceObjectProperty lslProperty = valueMap.get(rowKey + "-" + YIELD_TITLE[7]);
//            if (!DAPStringUtils.isNumeric((String) lslProperty.getValue())) {
//                return false;
//            }
//            StatisticalAlarmDto statisticalAlarmDto = statisticalAlarmDtoMap.get(SpcStatisticalResultKey.LSL.getCode());
//            Double lsl = Double.valueOf((String) lslProperty.getValue());
//            if (Double.valueOf(newText) <= lsl) {
//                errorEditorCell.add(rowKey + "-" + columnName);
//                if (!textField.getStyleClass().contains("text-field-error")) {
//                    textField.getStyleClass().add("text-field-error");
//                }
//                TooltipUtil.installWarnTooltip(textField, SpcFxmlAndLanguageUtils.getString(ResourceMassages.SPC_STATISTICAL_USL_LESS_THEN_LSL));
//                return true;
//            } else if (errorEditorCell.contains(rowKey + "-" + YIELD_TITLE[7])) {
//                errorEditorCell.remove(rowKey + "-" + YIELD_TITLE[7]);
//                if (lslProperty.isError()) {
//                    lslProperty.setError(false);
//                    statisticalAlarmDto.setValue(lsl);
//                }
//            }
//        }
//        if (errorEditorCell.contains(rowKey + "-" + columnName)) {
//            errorEditorCell.remove(rowKey + "-" + columnName);
//            textField.getStyleClass().removeAll("text-field-error");
//            TooltipUtil.uninstallWarnTooltip(textField);
//            return false;
//        }
//        textField.getStyleClass().removeAll("text-field-error");
//        TooltipUtil.uninstallWarnTooltip(textField);
//        return false;
//    }

    public boolean hasErrorEditValue() {
        return errorEditorCell.size() != 0;
    }

    public void setTimer(boolean timer) {
        isTimer = timer;
    }

    public List<String> getColumnList() {
        return columnList;
    }
}
