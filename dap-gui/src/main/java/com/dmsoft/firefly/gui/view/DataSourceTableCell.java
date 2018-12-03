package com.dmsoft.firefly.gui.view;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.gui.components.utils.TooltipUtil;
import com.dmsoft.firefly.gui.components.window.WindowCustomListener;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.components.window.WindowMessageController;
import com.dmsoft.firefly.gui.components.window.WindowMessageFactory;
import com.dmsoft.firefly.gui.controller.template.NewNameController;
import com.dmsoft.firefly.gui.model.ChooseTableRowData;
import com.dmsoft.firefly.gui.utils.GuiFxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.utils.ResourceMassages;
import com.dmsoft.firefly.sdk.dai.dto.UserPreferenceDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import com.google.common.collect.Lists;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
/**
 * 创建dataSource表格中每一行为一个组件
 * @author Tod
 */
public class DataSourceTableCell extends HBox implements Initializable {

    @FXML
    private Label textField;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button rename;
    @FXML
    private Button deleteOne;

    private String renameStr = GuiFxmlAndLanguageUtils.getString(ResourceMassages.RENAME_DATA_SOURCE);
    private String delStr = GuiFxmlAndLanguageUtils.getString(ResourceMassages.DELETE_SOURCE);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public DataSourceTableCell(ChooseTableRowData item, SourceDataService sourceDataService, TableView<ChooseTableRowData> dataSourceTable, EnvService envService,ObservableList<ChooseTableRowData> chooseTableRowDataObservableList) throws IOException {
        initView(item);
        initEvent(item,sourceDataService,dataSourceTable,envService,chooseTableRowDataObservableList);
    }

    private void initView(ChooseTableRowData item) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getClassLoader().getResource("view/data_source_cell.fxml"));
        getStylesheets().add("css/platform_app.css");
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
        textField.setText(item.getValue());
        if (item.isImport() || item.isError()) {
            textField.setDisable(true);
            item.getSelector().getCheckbox().setSelected(false);
            item.getSelector().getCheckbox().setDisable(true);
        } else {
            textField.setDisable(false);
            item.getSelector().getCheckbox().setDisable(false);
        }
        progressBar.setProgress(0);
        if (item.isError()) {
            progressBar.getStyleClass().setAll("progress-bar-lg-red");
        } else {
            progressBar.getStyleClass().setAll("progress-bar-lg-green");
        }
        TooltipUtil.installNormalTooltip(rename, renameStr);
        TooltipUtil.installNormalTooltip(deleteOne, delStr);
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
        this.setHgrow(textField, Priority.ALWAYS);
        this.setHgrow(progressBar, Priority.NEVER);
        this.setHgrow(rename, Priority.NEVER);
        this.setHgrow(deleteOne, Priority.NEVER);
    }

    private void initEvent(ChooseTableRowData item, SourceDataService sourceDataService, TableView<ChooseTableRowData> dataSourceTable, EnvService envService, ObservableList<ChooseTableRowData> chooseTableRowDataObservableList){
        this.setOnMouseExited(event -> {
            this.rename.setVisible(false);
            this.deleteOne.setVisible(false);
        });
        this.setOnMouseEntered(event -> {
            this.rename.setVisible(true);
            this.deleteOne.setVisible(true);
        });
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
                        fireEvent(new ActionEvent());
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
                        fireEvent(new ActionEvent());
//                        UserPreferenceDto userPreferenceDto = new UserPreferenceDto();
//                        userPreferenceDto.setUserName(envService.getUserName());
//                        userPreferenceDto.setCode("projectOrder");
//                        List<String> order = Lists.newArrayList();
//                        chooseTableRowDataObservableList.forEach(v -> {
//                            order.add(v.getValue());
//                        });
//                        userPreferenceDto.setValue(order);
//                        userPreferenceService.updatePreference(userPreferenceDto);
                        return false;
                    }
                });
            }
        });
    }
    //    private HBox hBox;
//    private TextField textField;
//    private ProgressBar progressBar;
//    private Button rename;
//    private Button deleteOne;

//    public HBox createHbox(){
//        HBox hBox = null;
//        Label textField = null;
//        ProgressBar progressBar = null;
//        Button rename = null;
//        Button deleteOne = null;
//        try {
//            hBox = GuiFxmlAndLanguageUtils.getLoaderFXML("view/data_source_cell.fxml").load();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        textField = (Label) hBox.getChildren().get(0);
//        progressBar = (ProgressBar) hBox.getChildren().get(1);
//        rename = (Button) hBox.getChildren().get(2);
//        deleteOne = (Button) hBox.getChildren().get(3);
//        textField.setText(item.getValue());
//        if (item.isImport() || item.isError()) {
//            textField.setDisable(true);
//            item.getSelector().getCheckbox().setSelected(false);
//            item.getSelector().getCheckbox().setDisable(true);
//        } else {
//            textField.setDisable(false);
//            item.getSelector().getCheckbox().setDisable(false);
//        }
//        progressBar.setProgress(0);
//        if (item.isError()) {
//            progressBar.getStyleClass().setAll("progress-bar-lg-red");
//        } else {
//            progressBar.getStyleClass().setAll("progress-bar-lg-green");
//        }
//        TooltipUtil.installNormalTooltip(rename, renameStr);
//        TooltipUtil.installNormalTooltip(deleteOne, delStr);
//        rename.setVisible(false);
//        deleteOne.setVisible(false);
//        if (!item.isError()) {
//            progressBar.setVisible(item.isImport());
//        } else {
//            progressBar.setVisible(true);
//        }
//        if (item.getProgress() != 0) {
//            progressBar.setProgress(item.getProgress());
//        }
//        HBox.setHgrow(textField, Priority.ALWAYS);
//        HBox.setHgrow(progressBar, Priority.NEVER);
//        HBox.setHgrow(rename, Priority.NEVER);
//        HBox.setHgrow(deleteOne, Priority.NEVER);
//        Button finalRename = rename;
//        Button finalDeleteOne = deleteOne;
//        hBox.setOnMouseEntered(event -> {
//            finalRename.setVisible(true);
//            finalDeleteOne.setVisible(true);
//        });
//        Button finalRename1 = rename;
//        Button finalDeleteOne1 = deleteOne;
//        hBox.setOnMouseExited(event -> {
//            finalRename1.setVisible(false);
//            finalDeleteOne1.setVisible(false);
//        });
//        rename.setOnAction(event -> {
//            Pane root = null;
//            Stage renameStage = null;
//            NewNameController renameTemplateController = null;
//            try {
//                FXMLLoader loader = GuiFxmlAndLanguageUtils.getLoaderFXML("view/new_template.fxml");
//                renameTemplateController = new NewNameController();
//                renameTemplateController.setPaneName("renameProject");
//                renameTemplateController.setInitName(item.getValue());
//
//                loader.setController(renameTemplateController);
//                root = loader.load();
//
//                NewNameController finalRenameTemplateController = renameTemplateController;
//                renameTemplateController.getOk().setOnAction(renameEvent -> {
//                    if (finalRenameTemplateController.isError()) {
//                        //WindowMessageFactory.createWindowMessageHasOk(GuiFxmlAndLanguageUtils.getString(ResourceMassages.WARN_HEADER), GuiFxmlAndLanguageUtils.getString(ResourceMassages.TEMPLATE_NAME_EMPTY_WARN));
//                        return;
//                    }
//                    TextField n = finalRenameTemplateController.getName();
//                    if (StringUtils.isNotEmpty(n.getText()) && !n.getText().equals(item.getValue().toString())) {
//                        String newString = DAPStringUtils.filterSpeChars4Mongo(n.getText());
//                        sourceDataService.renameProject(item.getValue(), newString);
//                        item.setValue(newString);
//                        dataSourceTable.refresh();
//                        updateProjectOrder();
//                    }
//                    StageMap.closeStage("renameProject");
//                });
//                renameStage = WindowFactory.createOrUpdateSimpleWindowAsModel("renameProject", GuiFxmlAndLanguageUtils.getString("RENAME_DATA_SOURCE"), root);
//                renameTemplateController.getName().setText(item.getValue());
//                renameStage.toFront();
//                renameStage.show();
//            } catch (Exception ignored) {
//            }
//        });
//        deleteOne.setOnAction(event -> {
//            if (!item.isImport()) {
//                WindowMessageController controller = WindowMessageFactory.createWindowMessageHasOkAndCancel(GuiFxmlAndLanguageUtils.getString("DELETE_SOURCE"), GuiFxmlAndLanguageUtils.getString("DELETE_DATA_SOURCE_CONFIRM"));
//                controller.addProcessMonitorListener(new WindowCustomListener() {
//                    @Override
//                    public boolean onShowCustomEvent() {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onCloseAndCancelCustomEvent() {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onOkCustomEvent() {
//                        List<String> deleteProjects = Lists.newArrayList();
//                        List<String> activeProject = envService.findActivatedProjectName();
//                        if (activeProject != null && activeProject.contains(item.getValue())) {
//                            activeProject.remove(item.getValue());
//                        }
//                        deleteProjects.add(item.getValue());
//                        if (!item.isError()) {
//                            sourceDataService.deleteProject(deleteProjects);
//                        }
//                        envService.setActivatedProjectName(activeProject);
//                        chooseTableRowDataObservableList.remove(item);
//                        updateProjectOrder();
//                        return false;
//                    }
//                });
//            }
//
//        });
//
//        return hBox;
//
//    }
}
