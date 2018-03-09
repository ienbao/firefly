package com.dmsoft.firefly.gui.components.window;

import com.dmsoft.firefly.gui.components.utils.FxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.components.utils.ResourceMassages;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;

/**
 * Created by Julia on 2018/03/09.
 */
public class WindowProgressTipController {

    @FXML
    private Button cancelBtn;
    @FXML
    private GridPane progressPane;
    @FXML
    private ProgressBar taskProgress;

    private WindowCustomListener windowCustomListener;

    @FXML
    private void initialize(){
        initCancelBtn();
        taskProgress.setProgress(0);
    }

    public void refreshProgress(double progressValue) {
        taskProgress.getStyleClass().setAll("progress-bar-lg-green");
        taskProgress.setProgress(progressValue);
        if (progressValue >= 100) {
            closeDialog();
        }
    }

    private void initCancelBtn() {
        cancelBtn.setText(FxmlAndLanguageUtils.getString("GLOBAL_BTN_CANCEL"));
        cancelBtn.setOnAction(event -> {
            closeDialog();
        });
    }

    public void closeDialog() {
        if (windowCustomListener != null) {
            windowCustomListener.onCloseAndCancelCustomEvent();
        }
        StageMap.closeStage(ResourceMassages.COMPONENT_STAGE_WINDOW_PROGRESS_TIP);
    }

    public void addProcessMonitorListener(WindowCustomListener windowCustomListener) {
        this.windowCustomListener = windowCustomListener;
    }
}
