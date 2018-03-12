package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.plugin.grr.utils.UIConstant;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by cherry on 2018/3/12.
 */
public class GrrResultController implements Initializable {

    /****** Summary *****/
    @FXML
    private HBox itemFilterHBox;
    @FXML
    private ComboBox resultBasedCmb;
    @FXML
    private TableView summaryTb;

    private TextFieldFilter summaryItemTf;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initComponents();
        this.initComponentsRender();
        this.initComponentEvents();
    }

    private void initComponents() {
        summaryItemTf = new TextFieldFilter();
        summaryItemTf.getTextField().setPromptText("Test Item");
        itemFilterHBox.getChildren().setAll(summaryItemTf);
        resultBasedCmb.getItems().addAll(UIConstant.GRR_RESULT_TYPE);
        resultBasedCmb.setValue(UIConstant.GRR_RESULT_TYPE[0]);
    }

    private void initComponentsRender() {
        final double INPUT_WIDTH = 200;
        itemFilterHBox.setMargin(summaryItemTf, new Insets(4, 0, 4, 0));
        summaryItemTf.getTextField().setPrefWidth(INPUT_WIDTH);
        summaryItemTf.getTextField().setFocusTraversable(false);
    }

    private void initComponentEvents() {
        resultBasedCmb.setOnAction(event -> this.fireResultBasedCmbEvent());
        summaryItemTf.getTextField().textProperty().addListener(observable -> this.fireSummaryItemTfEvent());
    }

    private void fireResultBasedCmbEvent() {
        String type = resultBasedCmb.getSelectionModel().getSelectedItem().toString();
        //TODO   refresh analyze summary
    }

    private void fireSummaryItemTfEvent() {
        String textValule = summaryItemTf.getTextField().getText();
        //TODO   filter summary table
    }
}
