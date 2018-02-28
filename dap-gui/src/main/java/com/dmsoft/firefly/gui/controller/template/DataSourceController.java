/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.model.ChooseTableRowData;
import com.dmsoft.firefly.gui.model.DataAndProgress;
import com.dmsoft.firefly.gui.utils.ImageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.plugin.PluginClass;
import com.dmsoft.firefly.sdk.plugin.PluginClassType;
import com.dmsoft.firefly.sdk.plugin.PluginImageContext;
import com.dmsoft.firefly.sdk.plugin.apis.IDataParser;
import com.google.common.collect.Lists;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.*;


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
    private TableColumn<ChooseTableRowData, DataAndProgress> chooseValueColumn;

    private CheckBox allCheckBox;
    private ObservableList<ChooseTableRowData> chooseTableRowDataObservableList;
    private FilteredList<ChooseTableRowData> chooseTableRowDataFilteredList;
    private SortedList<ChooseTableRowData> chooseTableRowDataSortedList;

    private SourceDataService sourceDataService = RuntimeContext.getBean(SourceDataService.class);
    private EnvService envService = RuntimeContext.getBean(EnvService.class);
    private JsonMapper mapper = JsonMapper.defaultMapper();


    private void initTable() {
        search.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_basic_search_normal.png")));
        delete.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_del_normal.png")));

        allCheckBox = new CheckBox();
        chooseCheckBoxColumn.setGraphic(allCheckBox);

        chooseCheckBoxColumn.setCellValueFactory(cellData -> cellData.getValue().getSelector().getCheckBox());
        chooseValueColumn.setCellValueFactory(cellData -> {
            DataAndProgress dataAndProgress = new DataAndProgress(cellData.getValue().valueProperty().getValue(), cellData.getValue().getProgress(), cellData.getValue().isSelect(), cellData.getValue().isOver());
            return new SimpleObjectProperty<>(dataAndProgress);
        });
        chooseValueColumn.setCellFactory(new Callback<TableColumn<ChooseTableRowData, DataAndProgress>, TableCell<ChooseTableRowData, DataAndProgress>>() {
            @Override
            public TableCell<ChooseTableRowData, DataAndProgress> call(TableColumn<ChooseTableRowData, DataAndProgress> param) {
                TableCell tableCell =  new TableCell<ChooseTableRowData, DataAndProgress>() {

                    @Override
                    protected void updateItem(DataAndProgress item, boolean empty) {
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
                            progressBar.setProgress(item.getProgress());
                            progressBar.setPrefWidth(70);
                            progressBar.setMinWidth(70);
                            progressBar.setMaxHeight(5);
                            progressBar.setPrefHeight(5);
                            progressBar.setMinHeight(5);
                            progressBar.setProgress(50);
                            Button rename = new Button();
                            rename.getStyleClass().add("btn-icon");
                            rename.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_rename_normal.png")));
                            Button deleteOne = new Button();
                            deleteOne.getStyleClass().add("btn-icon");
                            deleteOne.getStyleClass().add("delete-icon");

                            deleteOne.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_del_normal.png")));
                            hBox.getChildren().add(textField);
                            hBox.getChildren().add(progressBar);
                            hBox.getChildren().add(rename);
                            hBox.getChildren().add(deleteOne);
                            HBox.setHgrow(textField, Priority.ALWAYS);
                            HBox.setHgrow(progressBar, Priority.NEVER);
                            HBox.setHgrow(rename, Priority.NEVER);
                            HBox.setHgrow(deleteOne, Priority.NEVER);
                            this.setGraphic(hBox);

                        }
                    }

                };
                return tableCell;
            }
        });
        chooseTableRowDataObservableList = FXCollections.observableArrayList();
        chooseTableRowDataFilteredList = chooseTableRowDataObservableList.filtered(p -> true);
        chooseTableRowDataSortedList = new SortedList<>(chooseTableRowDataFilteredList);
        dataSourceTable.setItems(chooseTableRowDataSortedList);
        chooseTableRowDataSortedList.comparatorProperty().bind(dataSourceTable.comparatorProperty());
    }

    private void initEvent() {
        ok.setOnAction(event -> {
            List<String> selectProject = Lists.newArrayList();
            chooseTableRowDataObservableList.forEach(v -> {
                if (v.getSelector().isSelected()) {
                    selectProject.add(v.getValue());
                }
            });
            Map<String, TestItemDto> testItemDtoMap = sourceDataService.findAllTestItem(selectProject);

            envService.setTestItems(new ArrayList(testItemDtoMap.values()));
            envService.setActivatedProjectName(selectProject);
            envService.setActivatedTemplate("default");

            //TODO notify refresh event

            StageMap.closeStage("dataSource");
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
            Iterator<ChooseTableRowData> iterable = chooseTableRowDataObservableList.iterator();
            while (iterable.hasNext()) {
                ChooseTableRowData rowData = iterable.next();
                if (rowData.getSelector().isSelected()) {
                    deleteProjects.add(rowData.getValue());
                    iterable.remove();
                }
            }
            sourceDataService.deleteProject(deleteProjects);
        });
        addFile.setOnAction(event -> {
            String str = System.getProperty("user.home");
//            if (!StringUtils.isEmpty(path.getText())) {
//                str = path.getText();
//            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open");
            fileChooser.setInitialDirectory(new File(str));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("CSV", "*.csv")
            );
            Stage fileStage = null;
            File file = fileChooser.showOpenDialog(fileStage);
            if (file != null) {
                importDataSource(file.getPath(), file.getName());
            }
        });
        dataSourceTable.setOnMouseMoved(event -> {
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTable();
        initEvent();
        initDataSourceTableData();
    }

    private void importDataSource(String filPath, String fileName) {
        PluginImageContext pluginImageContext = RuntimeContext.getBean(PluginImageContext.class);
        List<PluginClass> pluginClasses = pluginImageContext.getPluginClassByType(PluginClassType.DATA_PARSER);
        IDataParser service = (IDataParser) pluginClasses.get(0).getInstance();
        new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                service.importFile(filPath);
                return null;
            }
        }.execute();

        ChooseTableRowData chooseTableRowData = new ChooseTableRowData(false, fileName);
        chooseTableRowDataObservableList.add(chooseTableRowData);
    }

    private void getAllSelectEvent() {
        if (chooseTableRowDataSortedList != null) {
            for (ChooseTableRowData data : chooseTableRowDataSortedList) {
                data.getSelector().setValue(allCheckBox.isSelected());
            }
        }
    }

    private void initDataSourceTableData() {
        List<String> value = Lists.newArrayList();
        value.addAll(sourceDataService.findAllProjectName());
        List<ChooseTableRowData> chooseTableRowDataList = Lists.newArrayList();
        List<String> selectProject = mapper.fromJson(envService.findPreference("selectProject"), mapper.buildCollectionType(List.class, String.class));
        value.forEach(v -> {
            ChooseTableRowData chooseTableRowData = null;
            if (selectProject != null && selectProject.contains(v)) {
                chooseTableRowData = new ChooseTableRowData(true, v);
            } else {
                chooseTableRowData = new ChooseTableRowData(false, v);
            }
            chooseTableRowDataList.add(chooseTableRowData);
        });
        setTableData(chooseTableRowDataList);
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
}
