/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.core.utils.*;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.model.ChooseTableRowData;
import com.dmsoft.firefly.gui.model.PluginTableRowData;
import com.dmsoft.firefly.gui.utils.FileUtils;
import com.dmsoft.firefly.gui.utils.GuiConst;
import com.dmsoft.firefly.gui.utils.KeyValueDto;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.plugin.PluginInfo;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

    private ObservableList<PluginTableRowData> pluginTableRowDataObservableList;
    private FilteredList<PluginTableRowData> pluginTableRowDataFilteredList;
    private SortedList<PluginTableRowData> pluginTableRowDataSortedList;
    private final String parentPath = ApplicationPathUtil.getPath(GuiConst.CONFIG_PATH);
    private JsonMapper mapper = JsonMapper.defaultMapper();
    private boolean isEdit = false;
    private List<String> deleteList = Lists.newArrayList();
    private Map<String, Boolean> validateMap = Maps.newHashMap();

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
                    isEdit = true;
                    ok.setText("restart");
                    updateProjectOrder();
                }
            });

            row.setOnMouseClicked(event -> {
                System.out.println("xx");
                if (pluginTable.getSelectionModel().getSelectedIndex() != -1) {
                    PluginTableRowData pluginTableRowData = pluginTableRowDataObservableList.get(pluginTable.getSelectionModel().getSelectedIndex());
                    explain.setText(pluginTableRowData.getInfo().getDescription());
                }
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
        List<KeyValueDto> activePlugin = Lists.newArrayList();
        pluginTableRowDataObservableList.forEach(v -> {
            activePlugin.add(new KeyValueDto(v.getInfo().getId(), v.getSelector().isSelected()));
        });
        JsonFileUtil.writeJsonFile(activePlugin, parentPath, GuiConst.ACTIVE_PLUGIN);
    }

    private void initEvent() {
        ok.setOnAction(event -> {
            validateMapChange();
            if (isEdit) {
                List<KeyValueDto> activePlugin = Lists.newArrayList();
                pluginTableRowDataObservableList.forEach(v -> {
                    activePlugin.add(new KeyValueDto(v.getInfo().getId(), v.getSelector().isSelected()));
                });
                JsonFileUtil.writeJsonFile(activePlugin, parentPath, "activePlugin");
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        try {
                            StringBuilder stringBuilder = new StringBuilder("java -jar dap-restart-1.0.0.jar");
                            deleteList.forEach(v -> {
                                stringBuilder.append(" " + v);
                            });
                            System.out.println(stringBuilder.toString());
                            Runtime.getRuntime().exec(stringBuilder.toString());
                        } catch (IOException e) {
                            System.out.println("restart failed.");
                        }
                    }
                });
                System.exit(0);
            } else {
                StageMap.closeStage("pluginManage");
            }
        });
        installPlugin.setOnAction(event -> {
            String str = System.getProperty("user.home");
//            if (!StringUtils.isEmpty(path.getText())) {
//                str = path.getText();
//            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open");
            fileChooser.setInitialDirectory(new File(str));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("ZIP", "*.zip")
            );
            Stage fileStage = null;
            File file = fileChooser.showOpenDialog(fileStage);
            if (file != null) {
                //TODO
                String propertiesURL = ApplicationPathUtil.getPath("application.properties");
                InputStream inputStream = null;
                String pluginFolderPath = null;
                try {
                    inputStream = new BufferedInputStream(new FileInputStream(propertiesURL));
                    Properties properties = new Properties();
                    properties.load(inputStream);
                    pluginFolderPath = PropertiesUtils.getPluginsPath(properties);

                    //validate
                    FileUtils.unZipFiles(file, pluginFolderPath + "/temp/" + file.getName() + "/");
                    PluginContext context = RuntimeContext.getBean(PluginContext.class);
                    List<PluginInfo> scannedPlugins = PluginScanner.scanPluginByPath(pluginFolderPath + "/temp/" + file.getName() + "/");
                    PluginInfo installPlugins = scannedPlugins.get(0);

                    Map<String, PluginInfo> allInstallPlugins = context.getAllInstalledPluginInfo() == null ? Maps.newHashMap() : context.getAllInstalledPluginInfo();

                    if (isExists(scannedPlugins.get(0), allInstallPlugins)) {
                        //TODO 存在相同的插件时对应的逻辑
                        return;
                    }

                    FileUtils.unZipFiles(file, pluginFolderPath + "/");
                    FileUtils.deleteFolder(pluginFolderPath + "/temp/" + file.getName());

                    PluginTableRowData chooseTableRowData = new PluginTableRowData(false, installPlugins.getName(), installPlugins);
                    pluginTableRowDataObservableList.add(chooseTableRowData);
                    isEdit = true;
                    ok.setText("restart");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        unInstallPlugin.setOnAction(event -> {
            if (pluginTable.getSelectionModel().getSelectedIndex() != -1) {
                PluginTableRowData pluginTableRowData = pluginTableRowDataObservableList.get(pluginTable.getSelectionModel().getSelectedIndex());
                PluginContext context = RuntimeContext.getBean(PluginContext.class);
                String url = pluginTableRowData.getInfo().getFolderPath();
                System.out.println(url);
                deleteList.add(url);
                context.uninstallPlugin(pluginTableRowData.getInfo().getId());
                pluginTableRowDataObservableList.remove(pluginTableRowData);
                isEdit = true;
                ok.setText("restart");
                updateProjectOrder();
            }
        });
    }

    private boolean isExists(PluginInfo pluginInfo, Map<String, PluginInfo> allInstallPlugins) {

        if (allInstallPlugins.containsKey(pluginInfo.getId())) {
            PluginInfo exist = allInstallPlugins.get(pluginInfo.getId());
            if (exist.getVersion().equals(pluginInfo.getVersion())) {
                return true;
            }
        }
        return false;
    }

    private void validateMapChange() {
        pluginTableRowDataObservableList.forEach(v -> {
            if (validateMap.containsKey(v.getInfo().getId())) {
                if (validateMap.get(v.getInfo().getId()).booleanValue() != v.getSelector().isSelected()) {
                    isEdit = true;
                    return;
                }
            } else {
                isEdit = true;
                return;
            }
        });
    }

    private void initDataSourceTableData() {
        PluginContext context = RuntimeContext.getBean(PluginContext.class);
        Map<String, PluginInfo> map = context.getAllInstalledPluginInfo();

        String json = JsonFileUtil.readJsonFile(parentPath, GuiConst.ACTIVE_PLUGIN);
        List<KeyValueDto> activePlugin = Lists.newArrayList();
        if (DAPStringUtils.isNotBlank(json)) {
            activePlugin = mapper.fromJson(json, mapper.buildCollectionType(List.class, KeyValueDto.class));
        }
        if (activePlugin != null) {
            activePlugin.forEach(v -> {
                PluginTableRowData chooseTableRowData = new PluginTableRowData((Boolean) v.getValue(), map.get(v.getKey()).getName(), map.get(v.getKey()));
                chooseTableRowData.setOnAction(event -> {
                    isEdit = true;
                    ok.setText("restart");
                });
                pluginTableRowDataObservableList.add(chooseTableRowData);
                validateMap.put(v.getKey(), (Boolean) v.getValue());
            });
        }
    }
}
