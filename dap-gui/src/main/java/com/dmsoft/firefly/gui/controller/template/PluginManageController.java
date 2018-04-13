/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.bamboo.common.utils.collection.ListUtil;
import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.core.utils.*;
import com.dmsoft.firefly.gui.components.utils.CommonResourceMassages;
import com.dmsoft.firefly.gui.components.utils.FxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.gui.components.window.WindowCustomListener;
import com.dmsoft.firefly.gui.components.window.WindowMessageController;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.gui.model.ChooseTableRowData;
import com.dmsoft.firefly.gui.model.PluginTableRowData;
import com.dmsoft.firefly.gui.utils.*;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.plugin.PluginContext;
import com.dmsoft.firefly.sdk.plugin.PluginInfo;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.dmsoft.firefly.sdk.utils.PropertyConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Created by Garen.Pang on 2018/3/7.
 */
public class PluginManageController implements Initializable {

    private static String restartStr = FxmlAndLanguageUtils.getString(CommonResourceMassages.RESTART);
    private final String parentPath = ApplicationPathUtil.getPath(GuiConst.CONFIG_PATH);
    @FXML
    private Button ok, installPlugin, unInstallPlugin;
    @FXML
    private TextFieldFilter filterTf;
    @FXML
    private TableView pluginTable;
    @FXML
    private TextFlow explain;
    @FXML
    private TableColumn<PluginTableRowData, CheckBox> pluginActivated;
    @FXML
    private TableColumn<PluginTableRowData, String> pluginName;
    private ObservableList<PluginTableRowData> pluginTableRowDataObservableList;
    private FilteredList<PluginTableRowData> pluginTableRowDataFilteredList;
    private SortedList<PluginTableRowData> pluginTableRowDataSortedList;
    private JsonMapper mapper = JsonMapper.defaultMapper();
    private boolean isEdit = false;
    private List<String> deleteList = Lists.newArrayList();
    private List<String> coverList = Lists.newArrayList();
    private Map<String, Boolean> validateMap = Maps.newHashMap();
    private String pluginFolderPath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTable();
        initEvent();
        initDataSourceTableData();
        filterTf.getTextField().setPromptText(FxmlAndLanguageUtils.getString(ResourceMassages.FILTER));
    }

    private void initTable() {

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
                    ok.setText(restartStr);
                    updateProjectOrder();
                }
            });

            row.setOnMouseClicked(event -> {
                if (pluginTable.getSelectionModel().getSelectedIndex() != -1) {
                    PluginTableRowData pluginTableRowData = pluginTableRowDataObservableList.get(pluginTable.getSelectionModel().getSelectedIndex());
                    explain.getChildren().remove(0, explain.getChildren().size());
                    Text version = new Text("Version: " + pluginTableRowData.getInfo().getVersion() + "\n");
                    Text description = new Text((DAPStringUtils.isEmpty(pluginTableRowData.getInfo().getDescription()) ? "" : pluginTableRowData.getInfo().getDescription()));
                    Text name = new Text(pluginTableRowData.getInfo().getName() + "\n");
                    name.setStyle("-fx-font-weight: bold");
                    explain.getChildren().addAll(name, version, description);
                    explain.setLineSpacing(5);
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
                showRestart();
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
                Platform.runLater(() -> {
                    pluginFolderPath = null;
                    try {
                        String propertiesURL = ApplicationPathUtil.getPath("application.properties");
                        Properties properties = PropertyConfig.getProperties(propertiesURL);
                        pluginFolderPath = PropertiesUtils.getPluginsPath(properties);
                        //validate
                        String fileNameZip = file.getName();
                        String fileName = fileNameZip.substring(fileNameZip.lastIndexOf(File.separator) + 1, fileNameZip.lastIndexOf('.'));
                        FileUtils.unZipFiles(file, pluginFolderPath + "/temp/" + fileName + "/");
                        PluginContext context = RuntimeContext.getBean(PluginContext.class);
                        List<PluginInfo> scannedPlugins = PluginScanner.scanPluginByPath(pluginFolderPath + "/temp/" + fileName + "/");
                        PluginInfo installPlugins = ListUtil.isEmpty(scannedPlugins) ? null : scannedPlugins.get(0);

                        if (installPlugins == null) {
                            WindowMessageFactory.createWindowMessageNoBtnHasOk(FxmlAndLanguageUtils.getString(CommonResourceMassages.INSTALL_ERROR), FxmlAndLanguageUtils.getString(CommonResourceMassages.ILLEGAL_PLUGIN));
                            return;
                        }

                        Map<String, PluginInfo> allInstallPlugins = context.getAllEnabledPluginInfo() == null ? Maps.newHashMap() : context.getAllEnabledPluginInfo();

                        if (isExists(scannedPlugins.get(0), allInstallPlugins)) {
                            WindowMessageFactory.createWindowMessageNoBtnHasOk(FxmlAndLanguageUtils.getString(CommonResourceMassages.INSTALL_ERROR), FxmlAndLanguageUtils.getString(CommonResourceMassages.PLUGIN_EXISTS));
                            FileUtils.deleteFolder(pluginFolderPath + "/temp/" + fileName);
                            return;
                        }

                        PluginInfo pluginInfo = isExist(scannedPlugins.get(0), allInstallPlugins);
                        if (pluginInfo != null) {
                            coverList.add(file.getPath() + ":coverPath:" + pluginInfo.getFolderPath());
                        } else {
                            FileUtils.unZipFiles(file, pluginFolderPath + "/");
                            context.installPlugin(scannedPlugins.get(0));
                            PluginTableRowData chooseTableRowData = new PluginTableRowData(false, installPlugins.getName(), installPlugins);
                            pluginTableRowDataObservableList.add(chooseTableRowData);
                        }

                        FileUtils.deleteFolder(pluginFolderPath + "/temp/" + fileName);
                        isEdit = true;
                        ok.setText(restartStr);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
        unInstallPlugin.setOnAction(event -> {
            if (pluginTable.getSelectionModel().getSelectedIndex() != -1) {
                PluginTableRowData pluginTableRowData = pluginTableRowDataObservableList.get(pluginTable.getSelectionModel().getSelectedIndex());
                PluginContext context = RuntimeContext.getBean(PluginContext.class);
                String url = pluginTableRowData.getInfo().getFolderPath();
                System.out.println(url);
                if (url.contains("/temp/") || url.contains("\\temp\\")) {
                    List<PluginInfo> scannedPlugins = PluginScanner.scanPluginByPath(pluginFolderPath + "/");
                    if (scannedPlugins != null) {
                        scannedPlugins.forEach(v -> {
                            if (v.getId().equals(pluginTableRowData.getInfo().getId()) && v.getName().equals(pluginTableRowData.getInfo().getName())) {
                                deleteList.add(v.getFolderPath());
                            }
                        });
                    }
                } else {
                    deleteList.add(url);
                }
                context.uninstallPlugin(pluginTableRowData.getInfo().getId());
                pluginTableRowDataObservableList.remove(pluginTableRowData);
                isEdit = true;
                ok.setText(restartStr);
                updateProjectOrder();
            }
        });

        filterTf.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            pluginTableRowDataFilteredList.setPredicate(p -> {
                return p.getValue().contains(filterTf.getTextField().getText());
            });
        });
    }

    private void showRestart() {
        WindowMessageController controller = WindowMessageFactory.createWindowMessageHasOk(FxmlAndLanguageUtils.getString(CommonResourceMassages.RESTART_APPLICATION),
                FxmlAndLanguageUtils.getString(CommonResourceMassages.RESTART_APPLICATION_INFO));
        controller.addProcessMonitorListener(new WindowCustomListener() {
            @Override
            public boolean onShowCustomEvent() {
                restart();
                return false;
            }

            @Override
            public boolean onCloseAndCancelCustomEvent() {
                restart();
                return false;
            }

            @Override
            public boolean onOkCustomEvent() {
                restart();
                return false;
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void restart() {
        List<KeyValueDto> activePlugin = Lists.newArrayList();
        pluginTableRowDataObservableList.forEach(v -> {
            activePlugin.add(new KeyValueDto(v.getInfo().getId(), v.getSelector().isSelected()));
        });
        JsonFileUtil.writeJsonFile(activePlugin, parentPath, "activePlugin");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    String propertiesURL = ApplicationPathUtil.getPath("application.properties");
                    Properties properties = PropertyConfig.getProperties(propertiesURL);
                    pluginFolderPath = PropertiesUtils.getPluginsPath(properties);

                    String run = "." + File.separator + "restart.sh";
                    String runUrl = ApplicationPathUtil.getPath("restart.sh");
                    if (ApplicationPathUtil.OS_NAME.toLowerCase().startsWith(ApplicationPathUtil.OS_WIN)) {
                        run = "." + File.separator + "restart.bat";
                        runUrl = ApplicationPathUtil.getPath("restart.bat");
                    }
                    com.dmsoft.firefly.sdk.utils.FileUtils.changeFileAuthority(runUrl);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(run);
                    stringBuilder.append(" pluginFolderPath:").append(pluginFolderPath);
                    deleteList.forEach(v -> stringBuilder.append(" delete:").append(v));
                    coverList.forEach(v -> stringBuilder.append(" cover:").append(v));
                    System.out.println(stringBuilder.toString());
                    Process proc = Runtime.getRuntime().exec(stringBuilder.toString());
                    StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "Error");
                    StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "Output");
                    errorGobbler.start();
                    outputGobbler.start();

                } catch (IOException e) {
                    System.out.println("restart failed.");
                }
            }
        });
        System.exit(0);
    }

    private boolean isExists(PluginInfo pluginInfo, Map<String, PluginInfo> allInstallPlugins) {

        if (allInstallPlugins.containsKey(pluginInfo.getId())) {
            PluginInfo exist = allInstallPlugins.get(pluginInfo.getId());
            if (!exist.getName().equals(pluginInfo.getName())) {
                return true;
            }
        }

        for (PluginInfo pluginInfo1 : allInstallPlugins.values()) {
            if (pluginInfo1.getName().equals(pluginInfo.getName()) && !pluginInfo1.getId().equals(pluginInfo.getId())) {
                return true;
            }
        }
        return false;
    }

    private PluginInfo isExist(PluginInfo pluginInfo, Map<String, PluginInfo> allInstallPlugins) {
        if (allInstallPlugins.containsKey(pluginInfo.getId())) {
            PluginInfo exist = allInstallPlugins.get(pluginInfo.getId());
            if (exist.getName().equals(pluginInfo.getName())) {
                return exist;
            }
        }
        return null;
    }

    private void validateMapChange() {
        pluginTableRowDataObservableList.forEach(v -> {
            if (validateMap.containsKey(v.getInfo().getId())) {
                if (validateMap.get(v.getInfo().getId()).booleanValue() != v.getSelector().isSelected()) {
                    isEdit = true;
                }
            } else {
                isEdit = true;
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
                    ok.setText(restartStr);
                });
                pluginTableRowDataObservableList.add(chooseTableRowData);
                validateMap.put(v.getKey(), (Boolean) v.getValue());
            });
        }
    }

    public EventHandler<WindowEvent> getOnCloseRequest() {
        return new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                validateMapChange();
                if (isEdit) {
                    showRestart();
                }
            }
        };
    }
}
