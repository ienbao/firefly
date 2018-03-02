/*
 * Copyright (c) 2017. For Intelligent Group.
 */

package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.bamboo.common.monitor.ProcessResult;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.model.ChooseTableRowData;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.job.Job;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.dmsoft.firefly.sdk.plugin.PluginClass;
import com.dmsoft.firefly.sdk.plugin.PluginClassType;
import com.dmsoft.firefly.sdk.plugin.PluginImageContext;
import com.dmsoft.firefly.sdk.plugin.apis.IDataParser;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
            resolver.getSelectionModel().select(0);
        }
    }

    private void initEvent() {
        nextStep.setOnAction(event -> {

            String resolverName = resolver.getSelectionModel().getSelectedItem().toString();

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
        PluginImageContext pluginImageContext = RuntimeContext.getBean(PluginImageContext.class);
        List<PluginClass> pluginClasses = pluginImageContext.getPluginClassByType(PluginClassType.DATA_PARSER);
        IDataParser service = null;
        for (int i = 0; i < pluginClasses.size(); i++) {
            if (((IDataParser) pluginClasses.get(0).getInstance()).getName().equals(resolverName)) {
                service = (IDataParser) pluginClasses.get(0).getInstance();
            }
        }
        ChooseTableRowData chooseTableRowData = new ChooseTableRowData(false, fileName);
        chooseTableRowData.setImport(true);
        IDataParser finalService = service;

        JobManager manager = RuntimeContext.getBean(JobManager.class);
        Job job = new Job("import");
        job.addJobEventListener(event -> {
            ProcessResult result = (ProcessResult) event.getObject();
            chooseTableRowData.setProgress(result.getPoint());
            controller.getDataSourceTable().refresh();
        });

        new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {

//                finalService.importFile(filPath);
                manager.doJobSyn(job, filPath);

                chooseTableRowData.setImport(false);
                controller.getDataSourceTable().refresh();
                return null;
            }
        }.execute();

        controller.getChooseTableRowDataObservableList().add(chooseTableRowData);
    }
}
