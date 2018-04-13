package com.dmsoft.firefly.gui.controller;

import com.dmsoft.firefly.gui.components.utils.JsonFileUtil;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.model.ExportSettingModel;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.TemplateService;
import com.dmsoft.firefly.sdk.plugin.PluginClass;
import com.dmsoft.firefly.sdk.plugin.PluginClassType;
import com.dmsoft.firefly.sdk.plugin.PluginImageContext;
import com.dmsoft.firefly.sdk.plugin.apis.IConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by GuangLi on 2018/3/8.
 */
public class ExportSettingController {
    private final Logger logger = LoggerFactory.getLogger(ExportSettingController.class);

    @FXML
    private TableView settingTable;
    @FXML
    private TableColumn<ExportSettingModel, CheckBox> selector;
    @FXML
    private TableColumn<ExportSettingModel, String> settings;
    @FXML
    private Button exportBtn;
    @FXML
    private Button cancelBtn;
    private CheckBox box;
    private ObservableList<ExportSettingModel> items = FXCollections.observableArrayList();

    private PluginImageContext pluginImageContext = RuntimeContext.getBean(PluginImageContext.class);
    private List<PluginClass> pluginClasses = pluginImageContext.getPluginClassByType(PluginClassType.CONFIG);
    private TemplateService templateService = RuntimeContext.getBean(TemplateService.class);

    @FXML
    private void initialize() {
        settingTable.setOnMouseEntered(event -> {
            settingTable.focusModelProperty();
        });
        box = new CheckBox();
        box.setOnAction(event -> {
            if (items != null) {
                for (ExportSettingModel model : items) {
                    model.getSelector().setValue(box.isSelected());
                }
            }
        });
        selector.setGraphic(box);
        selector.setCellValueFactory(cellData -> cellData.getValue().getSelector().getCheckBox());
        settings.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        initData();

        exportBtn.setOnAction(event -> {
            List<String> names = getSelect();
            if (names.size() > 0) {
                exportAllConfig(names);
            }
        });
        cancelBtn.setOnAction(event -> StageMap.closeStage("exportSetting"));
    }

    private void initData() {
        items.add(new ExportSettingModel(GuiFxmlAndLanguageUtils.getString(templateService.getConfigName())));
        pluginClasses.forEach(v -> {
            IConfig service = (IConfig) v.getInstance();
            String configName = service.getConfigName();
            if (StringUtils.isNotEmpty(configName)) {
                items.add(new ExportSettingModel(GuiFxmlAndLanguageUtils.getString(configName)));
            }
        });
        settingTable.setItems(items);
    }

    public void exportAllConfig(List<String> names) {
        Map<String, String> config = Maps.newHashMap();
        String templateConfigName = GuiFxmlAndLanguageUtils.getString(templateService.getConfigName());
        if (names != null && !names.isEmpty() && names.contains(templateConfigName)) {
            config.put(templateConfigName, new String(templateService.exportConfig()));
        }

        pluginClasses.forEach(v -> {
            IConfig service = (IConfig) v.getInstance();
//            name.add(((IConfig) v.getInstance()).getConfigName());
            String name = GuiFxmlAndLanguageUtils.getString(service.getConfigName());
            if (StringUtils.isNotEmpty(name) && names.contains(name)) {
                config.put(name, new String(service.exportConfig()));
            }
        });
        String str = System.getProperty("user.home");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(GuiFxmlAndLanguageUtils.getString("GLOBAL_TITLE_EXPORT_CONFIG"));
        fileChooser.setInitialDirectory(new File(str));
        fileChooser.setInitialFileName("DAPConfig.json");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        File file = fileChooser.showSaveDialog(StageMap.getStage("exportSetting"));

        if (file != null) {
            if (config != null) {
                JsonFileUtil.writeJsonFile(config, file);
                logger.debug("Export success");
            }
        }
    }

    private List<String> getSelect() {
        List<String> names = Lists.newArrayList();
        items.forEach(value -> {
            if (value.getSelector().isSelected()) {
                names.add(value.getName());
            }
        });
        return names;
    }
}
