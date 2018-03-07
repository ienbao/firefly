/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.core.utils.JsonFileUtil;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.model.ChooseTableRowData;
import com.dmsoft.firefly.gui.model.PluginTableRowData;
import com.dmsoft.firefly.gui.utils.KeyValueDto;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.plugin.PluginInfo;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Garen.Pang on 2018/3/7.
 */
public class PluginManageController implements Initializable {

    @FXML
    private Button ok, installPlugin, unInstallPlugin;

    @FXML
    private TextFieldFilter filterTf;

    @FXML
    private TableView pluginTable;

    @FXML
    private TextArea explain;

    @FXML
    private TableColumn<PluginTableRowData, CheckBox> pluginActivated;

    @FXML
    private TableColumn<PluginTableRowData, String> pluginName;

    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

    private ObservableList<PluginTableRowData> pluginTableRowDataObservableList;
    private FilteredList<PluginTableRowData> pluginTableRowDataFilteredList;
    private SortedList<PluginTableRowData> pluginTableRowDataSortedList;
    private final String parentPath = this.getClass().getResource("/").getPath() + "config";
    private JsonMapper mapper = JsonMapper.defaultMapper();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTable();
        initEvent();
        initDataSourceTableData();
    }

    private void initTable() {

        explain.setEditable(false);

        pluginActivated.setCellValueFactory(cellData -> cellData.getValue().getSelector().getCheckBox());
        pluginName.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        pluginTable.setRowFactory(tv -> {
            TableRow<ChooseTableRowData> row = new TableRow<>();

            row.setOnDragDetected(event -> {
                if (!row.isEmpty()) {
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
                }
            });

            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != ((Integer) db.getContent(SERIALIZED_MIME_TYPE)).intValue()) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        event.consume();
                    }
                }
            });

            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                    PluginTableRowData draggedPerson = pluginTableRowDataObservableList.remove(draggedIndex);
                    int dropIndex;

                    if (row.isEmpty()) {
                        dropIndex = pluginTableRowDataObservableList.size();
                    } else {
                        dropIndex = row.getIndex();
                    }

                    pluginTableRowDataObservableList.add(dropIndex, draggedPerson);

                    event.setDropCompleted(true);
                    pluginTable.getSelectionModel().select(dropIndex);
                    event.consume();

                    updateProjectOrder();
                }
            });

            row.setOnMouseClicked(event -> {
                PluginTableRowData pluginTableRowData = pluginTableRowDataObservableList.get(pluginTable.getSelectionModel().getSelectedIndex());
                explain.setText(pluginTableRowData.getInfo().getDescription());
                System.out.println(pluginTable.getSelectionModel().getSelectedIndex() + " click");
            });

            return row;
        });

        pluginTableRowDataObservableList = FXCollections.observableArrayList();
        pluginTableRowDataFilteredList = pluginTableRowDataObservableList.filtered(p -> true);
        pluginTableRowDataSortedList = new SortedList<>(pluginTableRowDataFilteredList);
        pluginTable.setItems(pluginTableRowDataSortedList);
        pluginTableRowDataSortedList.comparatorProperty().bind(pluginTable.comparatorProperty());
    }

    private void updateProjectOrder() {

    }

    private void initEvent() {
        ok.setOnAction(event -> {
            List<KeyValueDto> activePlugin = Lists.newArrayList();
            pluginTableRowDataObservableList.forEach(v -> {
                activePlugin.add(new KeyValueDto(v.getInfo().getId(), v.getSelector().isSelected()));
            });
            JsonFileUtil.writeJsonFile(activePlugin, parentPath, "activePlugin");
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        Runtime.getRuntime().exec("java -jar dap-gui-1.0.0.jar");
                    } catch (IOException e) {
                        System.out.println("restart failed.");
                    }
                }
            });
            System.exit(0);
        });
        installPlugin.setOnAction(event -> {

        });
        unInstallPlugin.setOnAction(event -> {

        });
    }

    private void initDataSourceTableData() {
        PluginContext context = RuntimeContext.getBean(PluginContext.class);
        Map<String, PluginInfo> map = context.getAllInstalledPluginInfo();

        String json = JsonFileUtil.readJsonFile(parentPath, "activePlugin");
        List<KeyValueDto> activePlugin = Lists.newArrayList();
        if (DAPStringUtils.isNotBlank(json)) {
            activePlugin = mapper.fromJson(json, mapper.buildCollectionType(List.class, KeyValueDto.class));
        }
        if (activePlugin != null) {
            activePlugin.forEach(v -> {
                PluginTableRowData chooseTableRowData = new PluginTableRowData((Boolean) v.getValue(), map.get(v.getKey()).getName(), map.get(v.getKey()));
                pluginTableRowDataObservableList.add(chooseTableRowData);
            });
        }
    }
}
