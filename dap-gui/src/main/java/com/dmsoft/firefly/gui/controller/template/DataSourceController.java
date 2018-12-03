/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.core.utils.DataFormat;
import com.dmsoft.firefly.gui.GuiApplication;
import com.dmsoft.firefly.gui.components.utils.*;
import com.dmsoft.firefly.gui.components.window.WindowCustomListener;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.components.window.WindowMessageController;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.gui.model.ChooseTableRowData;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.utils.MenuFactory;
import com.dmsoft.firefly.gui.utils.ResourceMassages;
import com.dmsoft.firefly.gui.view.DataSourceTableCell;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.dto.UserPreferenceDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dai.service.TemplateService;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
import com.dmsoft.firefly.sdk.event.EventContext;
import com.dmsoft.firefly.sdk.event.EventType;
import com.dmsoft.firefly.sdk.event.PlatformEvent;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static com.google.common.io.Resources.getResource;


/**
 * Created by Garen.Pang on 2018/2/25.
 */
public class DataSourceController implements Initializable {

    @FXML
    private Button addFile, ok, cancel, delete;
    @FXML
    private Label errorInfo;
    @FXML
    private TableView<ChooseTableRowData> dataSourceTable;

    @FXML
    private TextFieldFilter filterTf;

    @FXML
    private TableColumn<ChooseTableRowData, CheckBox> chooseCheckBoxColumn;

    @FXML
    private TableColumn<ChooseTableRowData, ChooseTableRowData> chooseValueColumn;

    private CheckBox allCheckBox;
    private ObservableList<ChooseTableRowData> chooseTableRowDataObservableList;
    private FilteredList<ChooseTableRowData> chooseTableRowDataFilteredList;
    private SortedList<ChooseTableRowData> chooseTableRowDataSortedList;

    private SourceDataService sourceDataService = RuntimeContext.getBean(SourceDataService.class);
    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private TemplateService templateService = RuntimeContext.getBean(TemplateService.class);
    private UserPreferenceService userPreferenceService = RuntimeContext.getBean(UserPreferenceService.class);
    private EventHandler eventHandler;

    private JsonMapper mapper = JsonMapper.defaultMapper();
    private String renameStr = GuiFxmlAndLanguageUtils.getString(ResourceMassages.RENAME_DATA_SOURCE);
    private String delStr = GuiFxmlAndLanguageUtils.getString(ResourceMassages.DELETE_SOURCE);

    private void initTable() {
        filterTf.getTextField().setPromptText(GuiFxmlAndLanguageUtils.getString(ResourceMassages.FILTER));
        TooltipUtil.installNormalTooltip(delete, GuiFxmlAndLanguageUtils.getString(ResourceMassages.DELETE_SOURCE));
        errorInfo.setVisible(false);
        allCheckBox = new CheckBox();
        chooseCheckBoxColumn.setGraphic(allCheckBox);
        chooseCheckBoxColumn.setCellValueFactory(cellData -> cellData.getValue().getSelector().getCheckBox());
        chooseValueColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        chooseValueColumn.setCellFactory(new Callback<TableColumn<ChooseTableRowData, ChooseTableRowData>, TableCell<ChooseTableRowData, ChooseTableRowData>>() {
            @Override
            public TableCell<ChooseTableRowData, ChooseTableRowData> call(TableColumn<ChooseTableRowData, ChooseTableRowData> param) {
                return new TableCell<ChooseTableRowData, ChooseTableRowData>() {
                    @Override
                    protected void updateItem(ChooseTableRowData item, boolean empty) {
                        if (item != null && item.equals(getItem())) {
                            return;
                        }
                        super.updateItem(item, empty);
                        if (item == null) {
                            super.setText(null);
                            super.setGraphic(null);
                        } else {
                            try {
                                DataSourceTableCell dataSourceTableCell = new DataSourceTableCell(item);
                                Button rename = dataSourceTableCell.getRename();
                                Button deleteOne = dataSourceTableCell.getDeleteOne();
                                rename.setOnAction(event -> {
                                    Pane root = null;
                                    Stage renameStage = null;
                                    NewNameController renameTemplateController = null;
                                    try {
                                        FXMLLoader loader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/new_template.fxml");
                                        renameTemplateController = new NewNameController();
                                        renameTemplateController.setPaneName("renameProject");
                                        renameTemplateController.setInitName(item.getValue());

                                        loader.setController(renameTemplateController);
                                        root = loader.load();

                                        NewNameController finalRenameTemplateController = renameTemplateController;
                                        renameTemplateController.getOk().setOnAction(renameEvent -> {
                                            if (finalRenameTemplateController.isError()) {
                                                //WindowMessageFactory.createWindowMessageHasOk(GuiFxmlAndLanguageUtils.getString(ResourceMassages.WARN_HEADER), GuiFxmlAndLanguageUtils.getString(ResourceMassages.TEMPLATE_NAME_EMPTY_WARN));
                                                return;
                                            }
                                            TextField n = finalRenameTemplateController.getName();
                                            if (StringUtils.isNotEmpty(n.getText()) && !n.getText().equals(item.getValue().toString())) {
                                                String newString = DAPStringUtils.filterSpeChars4Mongo(n.getText());
                                                sourceDataService.renameProject(item.getValue(), newString);
                                                item.setValue(newString);
                                                dataSourceTable.refresh();
                                                updateProjectOrder();
                                            }
                                            StageMap.closeStage("renameProject");
                                        });
                                        renameStage = WindowFactory.createOrUpdateSimpleWindowAsModel("renameProject", GuiFxmlAndLanguageUtils.getString("RENAME_DATA_SOURCE"), root);
                                        renameTemplateController.getName().setText(item.getValue());
                                        renameStage.toFront();
                                        renameStage.show();
                                    } catch (Exception ignored) {
                                    }
                                });
                                deleteOne.setOnAction(event -> {
                                    if (!item.isImport()) {
                                        WindowMessageController controller = WindowMessageFactory.createWindowMessageHasOkAndCancel(GuiFxmlAndLanguageUtils.getString("DELETE_SOURCE"), GuiFxmlAndLanguageUtils.getString("DELETE_DATA_SOURCE_CONFIRM"));
                                        controller.addProcessMonitorListener(new WindowCustomListener() {
                                            @Override
                                            public boolean onShowCustomEvent() {
                                                return false;
                                            }

                                            @Override
                                            public boolean onCloseAndCancelCustomEvent() {
                                                return false;
                                            }

                                            @Override
                                            public boolean onOkCustomEvent() {
                                                List<String> deleteProjects = Lists.newArrayList();
                                                List<String> activeProject = envService.findActivatedProjectName();
                                                if (activeProject != null && activeProject.contains(item.getValue())) {
                                                    activeProject.remove(item.getValue());
                                                }
                                                deleteProjects.add(item.getValue());
                                                if (!item.isError()) {
                                                    sourceDataService.deleteProject(deleteProjects);
                                                }
                                                envService.setActivatedProjectName(activeProject);
                                                chooseTableRowDataObservableList.remove(item);
                                                updateProjectOrder();
                                                return false;
                                            }
                                        });
                                    }
                                });
                                dataSourceTableCell.addEventHandler(ActionEvent.ACTION,event -> {updateProjectOrder();});
                                this.setGraphic(dataSourceTableCell);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
            }
        });
        dataSourceTable.setRowFactory(tv -> {
            TableRow<ChooseTableRowData> row = new TableRow<>();

            row.setOnDragDetected(event -> {
                if (!row.isEmpty()) {
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(DataFormat.SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
                }
            });

            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(DataFormat.SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != ((Integer) db.getContent(DataFormat.SERIALIZED_MIME_TYPE)).intValue()) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        event.consume();
                    }
                }
            });

            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(DataFormat.SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(DataFormat.SERIALIZED_MIME_TYPE);
                    ChooseTableRowData draggedPerson = chooseTableRowDataObservableList.remove(draggedIndex);
                    int dropIndex;

                    if (row.isEmpty()) {
                        dropIndex = chooseTableRowDataObservableList.size();
                    } else {
                        dropIndex = row.getIndex();
                    }

                    chooseTableRowDataObservableList.add(dropIndex, draggedPerson);

                    event.setDropCompleted(true);
                    dataSourceTable.getSelectionModel().select(dropIndex);
                    event.consume();

                    updateProjectOrder();
                }
            });

            return row;
        });
        chooseTableRowDataObservableList = FXCollections.observableArrayList();
        chooseTableRowDataFilteredList = chooseTableRowDataObservableList.filtered(p -> true);
        chooseTableRowDataSortedList = new SortedList<>(chooseTableRowDataFilteredList);
        dataSourceTable.setItems(chooseTableRowDataSortedList);
        chooseTableRowDataSortedList.comparatorProperty().bind(dataSourceTable.comparatorProperty());
    }

    private void initEvent() {
        TemplateSettingDto templateSettingDto = envService.findActivatedTemplate();
        ok.setOnAction(event -> {
            List<String> selectProject = Lists.newArrayList();
            List<String> projectOrder = Lists.newArrayList();

            Iterator<ChooseTableRowData> iterator = chooseTableRowDataObservableList.iterator();
            while (iterator.hasNext()) {
                ChooseTableRowData next = iterator.next();
                if (next.isError()) {
                    iterator.remove();
                }
            }
            errorInfo.setVisible(false);

            chooseTableRowDataObservableList.forEach(v -> {
                if (v.getSelector().isSelected()) {
                    selectProject.add(v.getValue());
                }
                projectOrder.add(v.getValue());
            });
            if (selectProject.isEmpty()) {
                WindowMessageFactory.createWindowMessageHasOk(FxmlAndLanguageUtils.getString(CommonResourceMassages.MESSAGE),
                        FxmlAndLanguageUtils.getString(CommonResourceMassages.PLEASE_SELECT_FILE));
                return;
            }
            Map<String, TestItemDto> testItemDtoMap = sourceDataService.findAllTestItem(selectProject);

            envService.setActivatedProjectName(selectProject);
            LinkedHashMap<String, TestItemWithTypeDto> itemWithTypeDtoMap = templateService.assembleTemplate(testItemDtoMap, templateSettingDto.getName());
            envService.setTestItems(itemWithTypeDtoMap);

            //TODO notify refresh event

            StageMap.closeStage("dataSource");

            EventContext eventContext = RuntimeContext.getBean(EventContext.class);
            eventContext.pushEvent(new PlatformEvent(EventType.PLATFORM_RESET_MAIN, null));
            //refreshMainDataSource(selectProject);

        });

        cancel.setOnAction(event -> {
            StageMap.closeStage("dataSource");
        });

        allCheckBox.setOnAction(event -> getAllSelectEvent());
        filterTf.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            getFilterTextFieldEvent();
        });
        List<String> deleteProjects = Lists.newArrayList();
        delete.setOnAction(event -> {
            WindowMessageController controller = WindowMessageFactory.createWindowMessageHasOkAndCancel(GuiFxmlAndLanguageUtils.getString(ResourceMassages.DELETE_DATA_SOURCE),
                    GuiFxmlAndLanguageUtils.getString(ResourceMassages.DELETE_DATA_SOURCE_CONFIRM));
            controller.addProcessMonitorListener(new WindowCustomListener() {
                @Override
                public boolean onShowCustomEvent() {
                    return false;
                }

                @Override
                public boolean onCloseAndCancelCustomEvent() {
                    return false;
                }

                @Override
                public boolean onOkCustomEvent() {
                    List<String> activeProject = envService.findActivatedProjectName();
                    Iterator<ChooseTableRowData> iterable = chooseTableRowDataObservableList.iterator();
                    while (iterable.hasNext()) {
                        ChooseTableRowData rowData = iterable.next();
                        if (rowData.getSelector().isSelected()) {
                            if (activeProject != null) {
                                activeProject.remove(rowData.getValue());
                            }
                            deleteProjects.add(rowData.getValue());
                            iterable.remove();
                        }
                    }
                    envService.setActivatedProjectName(activeProject);
                    sourceDataService.deleteProject(deleteProjects);
                    updateProjectOrder();
                    return false;
                }
            });

        });
        addFile.setOnAction(event -> {
            System.out.println(this);
            buildDataSourceDialog();
        });
    }

    private void updateProjectOrder() {
        UserPreferenceDto userPreferenceDto = new UserPreferenceDto();
        userPreferenceDto.setUserName(envService.getUserName());
        userPreferenceDto.setCode("projectOrder");
        List<String> order = Lists.newArrayList();
        chooseTableRowDataObservableList.forEach(v -> {
            order.add(v.getValue());
        });
        userPreferenceDto.setValue(order);
        userPreferenceService.updatePreference(userPreferenceDto);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTable();
        initEvent();
        initDataSourceTableData();
    }

    private void buildDataSourceDialog() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/resolver.fxml");
            fxmlLoader.setController(new ResolverSelectController(this));
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("resolver", GuiFxmlAndLanguageUtils.getString("DATA_SOURCE_SELECT_RESOLVER"), root, getResource("css/platform_app.css").toExternalForm());
            stage.setResizable(false);
            stage.toFront();
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getAllSelectEvent() {
        if (chooseTableRowDataSortedList != null) {
            for (ChooseTableRowData data : chooseTableRowDataSortedList) {
                data.getSelector().setValue(allCheckBox.isSelected());
            }
        }
    }

    private void initDataSourceTableData() {
//        List<String> value = Lists.newArrayList();
//        value.addAll(sourceDataService.findAllProjectName());
        List<ChooseTableRowData> chooseTableRowDataList = Lists.newArrayList();
        List<String> selectProject = mapper.fromJson(envService.findPreference("selectProject"), mapper.buildCollectionType(List.class, String.class));
        List<String> projectOrder = mapper.fromJson(envService.findPreference("projectOrder"), mapper.buildCollectionType(List.class, String.class));
        if (projectOrder != null && projectOrder.size() != 0) {
            projectOrder.forEach(v -> {
                ChooseTableRowData chooseTableRowData = null;
                if (selectProject != null && selectProject.contains(v)) {
                    chooseTableRowData = new ChooseTableRowData(true, v);
                } else {
                    chooseTableRowData = new ChooseTableRowData(false, v);
                }
                chooseTableRowDataList.add(chooseTableRowData);
            });
        }
        List<String> value = Lists.newArrayList();
        value.addAll(sourceDataService.findAllProjectName());
        value.forEach(v -> {
            if (projectOrder == null || !projectOrder.contains(v)) {
                ChooseTableRowData chooseTableRowData;
                if (selectProject != null && selectProject.contains(v)) {
                    chooseTableRowData = new ChooseTableRowData(true, v);
                } else {
                    chooseTableRowData = new ChooseTableRowData(false, v);
                }
                chooseTableRowDataList.add(chooseTableRowData);
            }
        });
        setTableData(chooseTableRowDataList);
    }

    /**
     * method to set table data
     *
     * @param chooseTableRowDataList list of choose table row data
     */
    public void setTableData(List<ChooseTableRowData> chooseTableRowDataList) {
        chooseTableRowDataObservableList.clear();
        chooseTableRowDataObservableList.addAll(chooseTableRowDataList);
    }

    private void getFilterTextFieldEvent() {
        chooseTableRowDataFilteredList.setPredicate(p ->
                p.containsRex(filterTf.getTextField().getText())
        );
    }

    /**
     * method to get event handler
     *
     * @return event handler
     */
    public EventHandler<WindowEvent> getEventHandler() {
        if (eventHandler == null) {
            eventHandler = new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    Iterator<ChooseTableRowData> iterator = getChooseTableRowDataObservableList().iterator();
                    while (iterator.hasNext()) {
                        ChooseTableRowData next = iterator.next();
                        if (next.isError()) {
                            iterator.remove();
                        }
                    }
                    errorInfo.setVisible(false);
                }
            };
        }
        return eventHandler;
    }

    public Label getErrorInfo() {
        return errorInfo;
    }

    public TableView getDataSourceTable() {
        return dataSourceTable;
    }

    public ObservableList<ChooseTableRowData> getChooseTableRowDataObservableList() {
        return chooseTableRowDataObservableList;
    }
}
