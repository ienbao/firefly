package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by cherry on 2018/3/11.
 */
public class GrrMainController implements Initializable {

    private SearchDataFrame dataFrame;
    private JobManager manager = RuntimeContext.getBean(JobManager.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setDataFrame(SearchDataFrame dataFrame) {
        this.dataFrame = dataFrame;
    }
}
