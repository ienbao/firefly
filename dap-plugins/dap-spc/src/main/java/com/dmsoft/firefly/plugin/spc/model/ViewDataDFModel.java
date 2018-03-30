package com.dmsoft.firefly.plugin.spc.model;

import com.dmsoft.firefly.gui.components.table.TableMenuRowEvent;
import com.dmsoft.firefly.gui.components.table.TableModel;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.spc.controller.SpcMainController;
import com.dmsoft.firefly.plugin.spc.controller.ViewDataDetailController;
import com.dmsoft.firefly.plugin.spc.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.spc.utils.ResourceMassages;
import com.dmsoft.firefly.plugin.spc.utils.SpcExceptionCode;
import com.dmsoft.firefly.plugin.spc.utils.SpcFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.spc.utils.ViewResource;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.exception.ApplicationException;
import com.dmsoft.firefly.sdk.utils.DAPDoubleUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.dmsoft.firefly.sdk.utils.RangeUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * view data model with data frame
 *
 * @author Can Guan
 */
public class ViewDataDFModel implements TableModel {
    private static Logger logger = LoggerFactory.getLogger(ViewDataDFModel.class);
    private SearchDataFrame dataFrame;
    private ObservableList<String> headerArray;
    private ObservableList<String> rowKeyArray;
    private Map<String, ObjectProperty<Boolean>> checkValueMap;
    private SimpleObjectProperty<Boolean> allCheck;
    private List<TableMenuRowEvent> menuRowEvents;
    private Set<String> highLightRowKeys;
    private CheckBox allCheckBox;
    private TableView<String> tableView;
    private SpcMainController mainController;
    private List<String> initSelectedRowKeys;
    private List<SearchConditionDto> statisticalSearchConditionDtoList;
    private Map<String,TestItemWithTypeDto> testItemDtoMap;

    /**
     * constructor
     *
     * @param dataFrame       search data frame
     * @param selectedRowKeys selected row keys
     */
    public ViewDataDFModel(SearchDataFrame dataFrame, List<String> selectedRowKeys) {
        this.dataFrame = dataFrame;
        this.initSelectedRowKeys = selectedRowKeys;
        this.headerArray = FXCollections.observableArrayList(dataFrame.getAllTestItemName());
        this.headerArray.add(0, "CheckBox");
        this.rowKeyArray = FXCollections.observableArrayList(dataFrame.getAllRowKeys());
        this.checkValueMap = Maps.newLinkedHashMap();
        this.allCheck = new SimpleObjectProperty<>(true);
        this.highLightRowKeys = Sets.newLinkedHashSet();
        this.menuRowEvents = Lists.newArrayList();
        TableMenuRowEvent highLight = new TableMenuRowEvent() {
            @Override
            public String getMenuName() {
                return SpcFxmlAndLanguageUtils.getString(ResourceMassages.HIGH_LIGHT_TABLE_MENU);
            }

            @Override
            public void handleAction(String rowKey, ActionEvent event) {
                if (highLightRowKeys.contains(rowKey)) {
                    highLightRowKeys.remove(rowKey);
                } else {
                    highLightRowKeys.add(rowKey);
                }
                tableView.refresh();
            }

            @Override
            public Node getMenuNode() {
                return null;
            }
        };
        TableMenuRowEvent detail = new TableMenuRowEvent() {
            @Override
            public String getMenuName() {
                return SpcFxmlAndLanguageUtils.getString(ResourceMassages.DETAIL_TABLE_MENU);
            }

            @Override
            public Node getMenuNode() {
                return null;
            }

            @Override
            public void handleAction(String rowKey, ActionEvent event) {
                ViewDataDetailController controller = new ViewDataDetailController();
                controller.setRowDataDto(RuntimeContext.getBean(SourceDataService.class).findTestData(rowKey));
//                List<TestItemWithTypeDto> typeDtoList = RuntimeContext.getBean(EnvService.class).findTestItems();
//                for (int j = 0; j < typeDtoList.size(); j++) {
//                    TestItemWithTypeDto typeDto = typeDtoList.get(j);
//                    if (dataFrame.getTestItemWithTypeDto(typeDto.getTestItemName()) != null) {
//                        int i = typeDtoList.indexOf(typeDto);
//                        typeDtoList.remove(i);
//                        typeDtoList.add(i, dataFrame.getTestItemWithTypeDto(typeDto.getTestItemName()));
//                    }
//                }
                controller.setTestItemDtoMap(testItemDtoMap);
                FXMLLoader loader = SpcFxmlAndLanguageUtils.getLoaderFXML(ViewResource.SPC_VIEW_DATA_DETAIL);
                loader.setController(controller);
                if (StageMap.getStage("Spc_detail") == null) {
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setResizable(false);
                    stage.initStyle(StageStyle.TRANSPARENT);
                    Scene scene = new Scene(new Pane());
                    stage.setScene(scene);
                    StageMap.addStage("Spc_detail", stage);
                }
                try {
                    Pane root = loader.load();
                    Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("Spc_detail",
                            SpcFxmlAndLanguageUtils.getString(ResourceMassages.DETAIL_TABLE_MENU), root,
                            getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
                    stage.show();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    throw new ApplicationException(SpcFxmlAndLanguageUtils.getString(SpcExceptionCode.ERR_20001));
                }
            }
        };
        TableMenuRowEvent remove = new TableMenuRowEvent() {
            @Override
            public String getMenuName() {
                return SpcFxmlAndLanguageUtils.getString(ResourceMassages.REMOVE_TABLE_MENU);
            }

            @Override
            public Node getMenuNode() {
                return null;
            }

            @Override
            public void handleAction(String rowKey, ActionEvent event) {
                RuntimeContext.getBean(SourceDataService.class).changeRowDataInUsed(Lists.newArrayList(rowKey), false);
                if (mainController != null) {
                    mainController.removeDataFrameRow(rowKey);
                }
            }
        };
        this.menuRowEvents.add(highLight);
        this.menuRowEvents.add(detail);
        this.menuRowEvents.add(remove);
    }

    @Override
    public ObservableList<String> getHeaderArray() {
        return headerArray;
    }

    @Override
    public ObjectProperty<String> getCellData(String rowKey, String columnName) {
        return new SimpleObjectProperty<>(dataFrame.getCellValue(rowKey, columnName));
    }

    @Override
    public ObservableList<String> getRowKeyArray() {
        return rowKeyArray;
    }

    @Override
    public boolean isEditableTextField(String columnName) {
        return false;
    }

    @Override
    public boolean isCheckBox(String columnName) {
        return "CheckBox".equals(columnName);
    }

    @Override
    public ObjectProperty<Boolean> getCheckValue(String rowKey, String columnName) {
        if (this.checkValueMap.get(rowKey) == null) {
            if (this.initSelectedRowKeys != null && this.initSelectedRowKeys.contains(rowKey)) {
                this.checkValueMap.put(rowKey, new SimpleObjectProperty<>(true));
            } else {
                this.checkValueMap.put(rowKey, new SimpleObjectProperty<>(false));
            }
        }
        return this.checkValueMap.get(rowKey);
    }

    @Override
    public ObjectProperty<Boolean> getAllCheckValue(String columnName) {
        return allCheck;
    }

    @Override
    public List<TableMenuRowEvent> getMenuEventList() {
        return this.menuRowEvents;
    }

    @Override
    public <T> TableCell<String, T> decorate(String rowKey, String column, TableCell<String, T> tableCell) {
        tableCell.setStyle(null);
        if (testItemDtoMap != null && !RangeUtils.isPass(dataFrame.getCellValue(rowKey, column), testItemDtoMap.get(column))) {
            tableCell.setStyle("-fx-background-color: #ea2028; -fx-text-fill: white");
        } else if (dataFrame.getCellValue(rowKey, column) != null && !DAPStringUtils.isNumeric(dataFrame.getCellValue(rowKey, column)) && this.highLightRowKeys.contains(rowKey)) {
            tableCell.setStyle("-fx-background-color: #f8d251; -fx-text-fill: #aaaaaa");
        } else if (this.highLightRowKeys.contains(rowKey)) {
            tableCell.setStyle("-fx-background-color: #f8d251");
        } else if ((dataFrame.getCellValue(rowKey, column) != null && !DAPStringUtils.isNumeric(dataFrame.getCellValue(rowKey, column))) || (testItemDtoMap != null && !testItemDtoMap.containsKey(column))) {
            tableCell.setStyle("-fx-text-fill: #aaaaaa");
        }
        return tableCell;
    }

    public CheckBox getAllCheckBox() {
        return allCheckBox;
    }

    @Override
    public void setAllCheckBox(CheckBox checkBox) {
        this.allCheckBox = checkBox;
    }

    @Override
    public void setTableView(TableView<String> tableView) {
        this.tableView = tableView;
    }

    public void setMainController(SpcMainController mainController) {
        this.mainController = mainController;
    }

    /**
     * method to get selected row keys
     *
     * @return list of selected row key
     */
    public List<String> getSelectedRowKeys() {
        List<String> result = Lists.newArrayList(dataFrame.getAllRowKeys());
        for (String s : this.checkValueMap.keySet()) {
            if (!this.checkValueMap.get(s).get()) {
                result.remove(s);
            }
        }
        return result;
    }

    public List<SearchConditionDto> getStatisticalSearchConditionDtoList() {
        return statisticalSearchConditionDtoList;
    }

    public void setStatisticalSearchConditionDtoList(List<SearchConditionDto> statisticalSearchConditionDtoList) {
        this.statisticalSearchConditionDtoList = statisticalSearchConditionDtoList;
        if(statisticalSearchConditionDtoList == null){
            testItemDtoMap = null;
            return;
        }
        testItemDtoMap = Maps.newHashMap();
        for(SearchConditionDto searchConditionDto : statisticalSearchConditionDtoList){
            String testName = searchConditionDto.getItemName();
            String lsl = searchConditionDto.getCusLsl();
            String usl = searchConditionDto.getCusUsl();
            if(testItemDtoMap.containsKey(testName)){
                TestItemWithTypeDto testItemDto = testItemDtoMap.get(testName);
                if(DAPStringUtils.isNumeric(lsl)){
                    if((DAPStringUtils.isNumeric(testItemDto.getLsl()) && Double.valueOf(lsl) < Double.valueOf(testItemDto.getLsl())) || !DAPStringUtils.isNumeric(testItemDto.getLsl())){
                        testItemDto.setLsl(lsl);
                    }
                }
                if(DAPStringUtils.isNumeric(usl)){
                    if((DAPStringUtils.isNumeric(testItemDto.getUsl()) && Double.valueOf(usl) > Double.valueOf(testItemDto.getUsl())) || !DAPStringUtils.isNumeric(testItemDto.getUsl())){
                        testItemDto.setUsl(usl);
                    }
                }
            } else {
                TestItemWithTypeDto testItemDto = new TestItemWithTypeDto();
                testItemDto.setUsl(usl);
                testItemDto.setLsl(lsl);
                testItemDto.setTestItemName(testName);
                testItemDtoMap.put(testName,testItemDto);
            }
        }
    }
}
