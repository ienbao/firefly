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
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.dto.UserPreferenceDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dai.service.TemplateService;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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

    private void initTable() {
        delete.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_del_normal.png")));
        TooltipUtil.installNormalTooltip(delete, GuiFxmlAndLanguageUtils.getString(ResourceMassages.DELETE_SOURCE));
        errorInfo.getStyleClass().add("message-tip-warn-mark");
        errorInfo.setStyle("-fx-background-color: #F38400");
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
                            HBox hBox = new HBox();
                            hBox.setSpacing(5);
                            hBox.setAlignment(Pos.CENTER);
                            Label textField = new Label(item.getValue());
                            textField.setStyle("-fx-border-width: 0 0 0 0");
                            textField.setPrefWidth(400);
                            textField.getStyleClass().add("table-text-field");
                            if (item.isImport() || item.isError()) {
                                textField.setDisable(true);
                                item.getSelector().getCheckbox().setSelected(false);
                                item.getSelector().getCheckbox().setDisable(true);
                            } else {
                                textField.setDisable(false);
                                item.getSelector().getCheckbox().setDisable(false);
                            }
                            ProgressBar progressBar = new ProgressBar(0);
                            if (item.isError()) {
                                progressBar.getStyleClass().setAll("progress-bar-lg-red");
                            } else {
                                progressBar.getStyleClass().setAll("progress-bar-lg-green");
                            }
                            progressBar.setPrefWidth(70);
                            progressBar.setMinWidth(70);
                            progressBar.setMaxHeight(3);
                            progressBar.setPrefHeight(3);
                            progressBar.setMinHeight(3);
                            Button rename = new Button();
                            rename.getStyleClass().add("btn-icon");
                            rename.setStyle("-fx-padding: 0 4 0 4; -fx-border-insets: -3 0 0 0; -fx-background-insets: -3 0 0 0");
                            TooltipUtil.installNormalTooltip(rename, GuiFxmlAndLanguageUtils.getString(ResourceMassages.RENAME_DATA_SOURCE));
                            rename.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_rename_normal.png")));
                            Button deleteOne = new Button();
                            deleteOne.getStyleClass().add("btn-icon");
                            TooltipUtil.installNormalTooltip(deleteOne, GuiFxmlAndLanguageUtils.getString(ResourceMassages.DELETE_SOURCE));
                            deleteOne.setStyle("-fx-padding: 0 4 0 4; -fx-background-insets: -3 0 0 0; -fx-border-insets: -3 0 0 0");

                            rename.setVisible(false);
                            deleteOne.setVisible(false);
                            if (!item.isError()) {
                                progressBar.setVisible(item.isImport());
                            } else {
                                progressBar.setVisible(true);
                            }
                            if (item.getProgress() != 0) {
                                progressBar.setProgress(item.getProgress());
                            }
                            deleteOne.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_del_normal.png")));
                            hBox.getChildren().add(textField);
                            hBox.getChildren().add(progressBar);
                            hBox.getChildren().add(rename);
                            hBox.getChildren().add(deleteOne);
                            hBox.setStyle("-fx-background-insets: -1 0 0 0; -fx-border-insets: -1 0 0 0");
                            HBox.setHgrow(textField, Priority.ALWAYS);
                            HBox.setHgrow(progressBar, Priority.NEVER);
                            HBox.setHgrow(rename, Priority.NEVER);
                            HBox.setHgrow(deleteOne, Priority.NEVER);
                            hBox.setOnMouseEntered(event -> {
                                rename.setVisible(true);
                                deleteOne.setVisible(true);
                            });
                            hBox.setOnMouseExited(event -> {
                                rename.setVisible(false);
                                deleteOne.setVisible(false);
                            });
                            rename.setOnAction(event -> {
                                Pane root = null;
                                Stage renameStage = null;
                                NewNameController renameTemplateController = null;
                                try {
                                    FXMLLoader loader = new FXMLLoader(GuiApplication.class.getClassLoader().getResource("view/new_template.fxml"), ResourceBundle.getBundle("i18n.message_en_US_GUI"));
                                    renameTemplateController = new NewNameController();
                                    renameTemplateController.setPaneName("renameProject");

                                    loader.setController(renameTemplateController);
                                    root = loader.load();
                                    NewNameController finalRenameTemplateController = renameTemplateController;
                                    renameTemplateController.getOk().setOnAction(renameEvent -> {
                                        TextField n = finalRenameTemplateController.getName();
                                        if (StringUtils.isNotEmpty(n.getText()) && !n.getText().equals(item.getValue().toString())) {
                                            String newString = DAPStringUtils.filterSpeCharsFile(n.getText());
                                            sourceDataService.renameProject(item.getValue(), newString);
                                            item.setValue(newString);
                                            dataSourceTable.refresh();
                                            updateProjectOrder();
                                        }
                                        StageMap.closeStage("renameProject");
                                    });
                                    renameStage = WindowFactory.createOrUpdateSimpleWindowAsModel("renameProject", "Rename Project", root);
                                } catch (Exception e) {

                                }
                                renameTemplateController.getName().setText(item.getValue());
                                renameStage.toFront();
                                renameStage.show();
                            });
                            deleteOne.setOnAction(event -> {
                                if (!item.isImport()) {
                                    WindowMessageController controller = WindowMessageFactory.createWindowMessageHasOkAndCancel("Delete DataSource", "Are you sure to delete this file?");
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
                            this.setGraphic(hBox);
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
            MenuFactory.getMainController().resetMain();
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
            WindowMessageController controller = WindowMessageFactory.createWindowMessageHasOkAndCancel(FxmlAndLanguageUtils.getString(ResourceMassages.DELETE_DATA_SOURCE),
                    FxmlAndLanguageUtils.getString(ResourceMassages.DELETE_DATA_SOURCE_CONFIRM));
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
