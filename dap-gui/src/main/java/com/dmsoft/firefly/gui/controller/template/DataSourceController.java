/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.core.utils.DataFormat;
import com.dmsoft.firefly.gui.GuiApplication;
import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.window.WindowCustomListener;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.components.window.WindowMessageController;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.gui.model.ChooseTableRowData;
import com.dmsoft.firefly.gui.utils.MenuFactory;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TemplateSettingDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.dto.UserPreferenceDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dai.service.TemplateService;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
import com.google.common.collect.Lists;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
    private Button addFile, ok, cancel, search, delete;

    @FXML
    private TableView dataSourceTable;

    @FXML
    private TextField filterTf;

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

    private JsonMapper mapper = JsonMapper.defaultMapper();

    private void initTable() {
        search.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_basic_search_normal.png")));
        delete.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_del_normal.png")));

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
                            ProgressBar progressBar = new ProgressBar();
                            progressBar.getStyleClass().setAll("progress-bar-lg-green");
                            progressBar.setPrefWidth(70);
                            progressBar.setMinWidth(70);
                            progressBar.setMaxHeight(3);
                            progressBar.setPrefHeight(3);
                            progressBar.setMinHeight(3);
                            Button rename = new Button();
                            rename.getStyleClass().add("btn-icon");
                            rename.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_rename_normal.png")));
                            Button deleteOne = new Button();
                            deleteOne.getStyleClass().add("btn-icon");
                            deleteOne.getStyleClass().add("delete-icon");

                            rename.setVisible(false);
                            deleteOne.setVisible(false);
                            progressBar.setVisible(item.isImport());
                            if (item.getProgress() != 0) {
                                progressBar.setProgress(item.getProgress() / (double) 100);
                            }
                            deleteOne.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_del_normal.png")));
                            hBox.getChildren().add(textField);
                            hBox.getChildren().add(progressBar);
                            hBox.getChildren().add(rename);
                            hBox.getChildren().add(deleteOne);
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

                                            //TODO 改变数据库里面的名字

                                            item.setValue(n.getText());
                                            dataSourceTable.refresh();
                                            updateProjectOrder();
                                        }
                                        StageMap.closeStage("renameProject");
                                    });
                                    renameStage = WindowFactory.createOrUpdateSimpleWindowAsModel("renameProject", "Rename Project", root);
                                } catch (Exception e) {

                                }
                                renameTemplateController.getName().setText(item.getValue());
                                renameStage.show();
                            });
                            deleteOne.setOnAction(event -> {
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
                                        activeProject.remove(item.getValue());
                                        deleteProjects.add(item.getValue());
                                        sourceDataService.deleteProject(deleteProjects);
                                        envService.setActivatedProjectName(activeProject);
                                        chooseTableRowDataObservableList.remove(item);
                                        updateProjectOrder();
                                        return false;
                                    }
                                });

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
       TemplateSettingDto templateSettingDto =  envService.findActivatedTemplate();
        ok.setOnAction(event -> {
            List<String> selectProject = Lists.newArrayList();
            List<String> projectOrder = Lists.newArrayList();
            chooseTableRowDataObservableList.forEach(v -> {
                if (v.getSelector().isSelected()) {
                    selectProject.add(v.getValue());
                }
                projectOrder.add(v.getValue());
            });
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
        search.setOnAction(event -> {
            getFilterTextFieldEvent();
        });
        List<String> deleteProjects = Lists.newArrayList();
        delete.setOnAction(event -> {
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
            buildDataSourceDialog();
        });
    }

    private void updateProjectOrder() {
        UserPreferenceDto userPreferenceDto = new UserPreferenceDto();
        userPreferenceDto.setUserName("admin");
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
            FXMLLoader loader = new FXMLLoader(GuiApplication.class.getClassLoader().getResource("view/resolver.fxml"), ResourceBundle.getBundle("i18n.message_en_US_GUI"));
            loader.setController(new ResolverSelectController(this));
            root = loader.load();
            Stage stage = WindowFactory.createSimpleWindowAsModel("resolver", "select Resolver", root, getResource("css/platform_app.css").toExternalForm());
            stage.setResizable(false);
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
        } else {
            List<String> value = Lists.newArrayList();
            value.addAll(sourceDataService.findAllProjectName());
            value.forEach(v -> {
                ChooseTableRowData chooseTableRowData = null;
                if (selectProject != null && selectProject.contains(v)) {
                    chooseTableRowData = new ChooseTableRowData(true, v);
                } else {
                    chooseTableRowData = new ChooseTableRowData(false, v);
                }
                chooseTableRowDataList.add(chooseTableRowData);
            });
        }
        setTableData(chooseTableRowDataList);
    }

    private void refreshMainDataSource(List<String> selectProject) {
        MenuFactory.getMainController().updateDataSourceText(selectProject.size());
        ObservableList<String> dataSourceList = FXCollections.observableArrayList(selectProject);
        MenuFactory.getMainController().refreshDataSource(dataSourceList);
    }

    public void setTableData(List<ChooseTableRowData> chooseTableRowDataList) {
        chooseTableRowDataObservableList.clear();
        chooseTableRowDataObservableList.addAll(chooseTableRowDataList);
    }

    private void getFilterTextFieldEvent() {
        chooseTableRowDataFilteredList.setPredicate(p ->
                p.containsRex(filterTf.getText())
        );
    }

    public TableView getDataSourceTable() {
        return dataSourceTable;
    }

    public ObservableList<ChooseTableRowData> getChooseTableRowDataObservableList() {
        return chooseTableRowDataObservableList;
    }
}
