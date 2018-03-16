package com.dmsoft.firefly.plugin.grr.controller;

import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.dmsoft.firefly.plugin.grr.dto.GrrDataFrameDto;
import com.dmsoft.firefly.plugin.grr.dto.SearchConditionDto;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dataframe.SearchDataFrame;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by cherry on 2018/3/11.
 */
public class GrrMainController implements Initializable {

    private SearchDataFrame dataFrame;
    private GrrDataFrameDto grrDataFrame;
    private List<String> includeRows;
    private List<String> excludeRows;
    @FXML
    private GrrItemController grrItemController;
    @FXML
    private GrrResultController grrResultController;
    @FXML
    private GrrViewDataController grrViewDataController;

    private JobManager manager = RuntimeContext.getBean(JobManager.class);

    @FXML
    private Button exportBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        grrItemController.init(this);
        grrResultController.init(this);
        //grrViewDataController.init(this);
        initBtnIcon();
        initComponentEvents();
    }

    public void grrAnalyzeResult() {
        grrResultController.analyzeGrrResult(grrDataFrame, includeRows, getSearchConditionDto());
    }

    private void initBtnIcon() {
        exportBtn.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/btn_export_normal.png")));
    }

    private void initComponentEvents() {
        exportBtn.setOnAction(event -> {
            getExportBtnEvent();
        });
    }

    private void getExportBtnEvent() {
        Pane root = null;
        try {
            FXMLLoader fxmlLoader = GrrFxmlAndLanguageUtils.getLoaderFXML("view/grr_export.fxml");
            root = fxmlLoader.load();
            Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("grrExport", "Grr Export", root, getClass().getClassLoader().getResource("css/grr_app.css").toExternalForm());
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setDataFrame(SearchDataFrame dataFrame) {
        this.dataFrame = dataFrame;
    }

    public SearchDataFrame getDataFrame() {
        return dataFrame;
    }

    public List<String> getIncludeRows() {
        return includeRows;
    }

    public void setIncludeRows(List<String> includeRows) {
        this.includeRows = includeRows;
    }

    public List<String> getExcludeRows() {
        return excludeRows;
    }

    public void setExcludeRows(List<String> excludeRows) {
        this.excludeRows = excludeRows;
    }

    public GrrDataFrameDto getGrrDataFrame() {
        return grrDataFrame;
    }

    public void setGrrDataFrame(GrrDataFrameDto grrDataFrame) {
        this.grrDataFrame = grrDataFrame;
    }

    public SearchConditionDto getSearchConditionDto() {
        return grrItemController.getSearchConditionDto();
    }
}
