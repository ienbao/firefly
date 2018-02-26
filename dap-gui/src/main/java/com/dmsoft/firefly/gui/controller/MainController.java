package com.dmsoft.firefly.gui.controller;

import com.dmsoft.firefly.gui.GuiApplication;
import com.dmsoft.firefly.gui.component.ContentStackPane;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.gui.utils.ResourceBundleUtils;
import com.dmsoft.firefly.gui.utils.ResourceMassages;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.ui.PluginUIContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;
import java.util.Set;

import static com.google.common.io.Resources.getResource;

public class MainController {

    private final Logger logger = LoggerFactory.getLogger(MainController.class);

    @FXML
    private GridPane grpContent;

    @FXML
    private ToolBar tbaSystem;

    @FXML
    private Button dataSourceBtn;

    @FXML
    private Button templateBtn;

    @FXML
    private Label dataSourceLbl;

    @FXML
    private Label templateLbl;

    private ContentStackPane contentStackPane;
    private TabPane tabPane;


    @FXML
    private void initialize() {
        tabPane = new TabPane();
        contentStackPane = new ContentStackPane();
        grpContent.add(contentStackPane, 0, 1);
        this.initToolBar();
        this.initComponentEvent();
        System.out.println("init");
    }



    private void initToolBar() {
        PluginUIContext pc = RuntimeContext.getBean(PluginUIContext.class);
        Set<String> names = pc.getAllMainBodyNames();

        names.forEach(name -> {
            Pane pane = pc.getMainBodyPane(name).getNewPane();
            Button btn = new Button(name);
            pane.setId(name);
            if (StringUtils.isBlank(tabPane.getId())) {
                tabPane = new TabPane();
                tabPane.setId(name);
            } else if (StringUtils.isNotBlank(tabPane.getId()) && !name.equals(tabPane.getId())) {
                tabPane = new TabPane();
                tabPane.setId(name);
            }
            initTab(name, pane, tabPane);
            contentStackPane.add(tabPane);
            btn.setOnAction(event -> {
                System.out.println(contentStackPane);
                contentStackPane.navTo(name);
                System.out.println(name);

            });
            tbaSystem.getItems().add(btn);
        });
    }

    private void initTab(String name, Pane pane, TabPane tabPane) {
        if (name.equals(tabPane.getId())) {
            Tab tab = new Tab();
            tab.setText(name + 1);
            tab.setContent(pane);
            tabPane.getTabs().add(tab);
        }
    }

    private void initComponentEvent() {
        dataSourceBtn.setOnAction(event -> this.getDataSourceBtnEvent());
        templateBtn.setOnAction(event -> this.getTemplateBtnEvent());
        dataSourceLbl.setOnMouseEntered(event -> this.getDataSourceLblEvent());
        templateLbl.setOnMouseEntered(event -> this.getTemplateLblEvent());
    }

    private void getDataSourceBtnEvent(){
        buildDataSourceDialog();
        logger.debug("Data source btn event.");
    }

    private void getDataSourceLblEvent(){
        logger.debug("Data source lbl event.");
        if (!dataSourceLbl.isDisable()) {

        }
    }

    private void getTemplateBtnEvent(){
        logger.debug("Template btn event.");
    }

    private void getTemplateLblEvent(){
        logger.debug("Template lbl event.");
        if (!templateLbl.isDisable()) {

        }
    }

    /**
     * disable state bar False
     */
    public void disableStateBarFalse() {
        dataSourceBtn.setDisable(false);
        dataSourceLbl.setDisable(false);
        templateBtn.setDisable(false);
        templateLbl.setDisable(false);
    }

    /**
     * disable state bar True
     */
    public void disableStateBarTrue() {
        dataSourceBtn.setDisable(true);
        dataSourceLbl.setDisable(true);
        templateBtn.setDisable(true);
        templateLbl.setDisable(true);
    }

    private void buildDataSourceDialog() {
        Pane root = null;
        try {
            root = FXMLLoader.load(GuiApplication.class.getClassLoader().getResource("view/data_source.fxml"), ResourceBundle.getBundle("i18n.message_en_US_GUI"));
            Stage stage = WindowFactory.createSimpleWindowAsModel("dataSource", ResourceBundleUtils.getString(ResourceMassages.DATASOURCE), root, getResource("css/platform_app.css").toExternalForm());
            stage.setResizable(false);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
