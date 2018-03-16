package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class GrrViewDataController implements Initializable {
    @FXML
    private TextFieldFilter analysisFilterLB;

    @FXML
    private TextFieldFilter exchangeFilterLB;

    @FXML
    private TableView<String> analysisDataTB;

    @FXML
    private TableView<String> exchangeFilterTB;

    @FXML
    private Label exchangeableLB;

    @FXML
    private Button exchangeBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        analysisFilterLB.getTextField().setPromptText(GrrFxmlAndLanguageUtils.getString("TEST_ITEM"));
        exchangeFilterLB.getTextField().setPromptText(GrrFxmlAndLanguageUtils.getString("TEST_ITEM"));
    }
}
