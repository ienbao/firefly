/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.bamboo.common.monitor.ProcessResult;
import com.dmsoft.bamboo.common.utils.mapper.JsonMapper;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.model.ChooseTableRowData;
import com.dmsoft.firefly.gui.utils.GuiConst;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.UserPreferenceDto;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
import com.dmsoft.firefly.sdk.job.Job;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.dmsoft.firefly.sdk.plugin.PluginClass;
import com.dmsoft.firefly.sdk.plugin.PluginClassType;
import com.dmsoft.firefly.sdk.plugin.PluginImageContext;
import com.dmsoft.firefly.sdk.plugin.apis.IDataParser;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Garen.Pang on 2018/3/1.
 */
public class ResolverSelectController implements Initializable {

    private DataSourceController controller;
    private ObservableList resolverData;

    public ResolverSelectController(DataSourceController controller) {
        this.controller = controller;
    }

    @FXML
    private ComboBox resolver;

    @FXML
    private Button nextStep;

    @FXML
    private CheckBox defaultTemplate;

    private UserPreferenceService userPreferenceService = RuntimeContext.getBean(UserPreferenceService.class);
private JsonMapper mapper = JsonMapper.defaultMapper();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initEvent();
        initComBox();
    }

    private void initComBox() {
        PluginImageContext pluginImageContext = RuntimeContext.getBean(PluginImageContext.class);
        List<PluginClass> pluginClasses = pluginImageContext.getPluginClassByType(PluginClassType.DATA_PARSER);
        List<String> name = Lists.newArrayList();
        pluginClasses.forEach(v -> {
            name.add(((IDataParser) v.getInstance()).getName());
        });
        resolverData = FXCollections.observableArrayList(name);
        resolver.setItems(resolverData);
        if (name.size() > 0) {
            String defaultResolver = userPreferenceService.findPreferenceByUserId("defaultResolver", "admin");
            String resolverName;
            if (DAPStringUtils.isNotBlank(defaultResolver)) {
                resolverName = mapper.fromJson(defaultResolver, String.class);
                if (DAPStringUtils.isNotBlank(resolverName)) {
                    resolver.getSelectionModel().select(resolverName);
                } else {
                    resolver.getSelectionModel().select(0);
                }
            } else {
                resolver.getSelectionModel().select(0);
            }
        }
    }

    private void initEvent() {
        nextStep.setOnAction(event -> {

            String resolverName = resolver.getSelectionModel().getSelectedItem().toString();

            UserPreferenceDto userPreferenceDto = new UserPreferenceDto();
            userPreferenceDto.setUserName("admin");
            userPreferenceDto.setCode("defaultResolver");
            userPreferenceDto.setValue(resolverName);
            userPreferenceService.updatePreference(userPreferenceDto);

            if (DAPStringUtils.isEmpty(resolverName)) {
                //TODO 出错交互
            }
            StageMap.closeStage("resolver");

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
                importDataSource(file.getPath(), file.getName(), resolverName);
            }
        });
    }

    private void importDataSource(String filPath, String fileName, String resolverName) {

        ChooseTableRowData chooseTableRowData = new ChooseTableRowData(false, fileName);
        chooseTableRowData.setImport(true);
        JobManager manager = RuntimeContext.getBean(JobManager.class);
        Job job = new Job(GuiConst.DATASOURCE_IMPORT);
        job.addProcessMonitorListener(event -> {
            chooseTableRowData.setProgress(event.getPoint());
            controller.getDataSourceTable().refresh();
        });
        Platform.runLater(() -> {
            manager.doJobASyn(job, returnValue -> {
                if (returnValue != null && returnValue instanceof Throwable) {
                    //TODO 弹出警告窗口
                    controller.getChooseTableRowDataObservableList().remove(chooseTableRowData);
                } else {
                    chooseTableRowData.setImport(false);
                    controller.getDataSourceTable().refresh();
                }
            }, filPath, resolverName);
        });
        controller.getChooseTableRowDataObservableList().add(chooseTableRowData);
    }
}
