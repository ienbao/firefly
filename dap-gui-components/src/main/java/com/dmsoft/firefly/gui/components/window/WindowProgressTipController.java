package com.dmsoft.firefly.gui.components.window;

import com.dmsoft.firefly.gui.components.utils.FxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.components.utils.ResourceMassages;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

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

    public void updateCancelBtn(String msg) {
        cancelBtn.setText(msg);
    }

    public void disabledNode(Node node) {
        if (node != null) {
            node.setDisable(true);
        }
    }

    public void enableNode(Node node) {
        if (node != null) {
            node.setDisable(false);
        }
    }

    public void refreshProgress(double progressValue) {
        Platform.runLater(() -> {
            taskProgress.getStyleClass().setAll("progress-bar-lg-green");
            taskProgress.setProgress(progressValue);
            if (progressValue >= 100) {
                closeDialog();
            }
        });
    }

    public void onShowingRequest() {
        boolean isOverride = false;
        if (windowCustomListener != null) {
            isOverride = windowCustomListener.onShowCustomEvent();
        }

        if (!isOverride) {
            Platform.runLater(() -> {
                Stage stage = StageMap.getStage(ResourceMassages.PLARTFORM_STAGE_MAIN);
                if (stage != null && stage.getScene() != null && stage.getScene().lookup("#grpContent") != null) {
                    stage.getScene().lookup("#grpContent").setDisable(true);
                    stage.getScene().lookup("#tbaSystem").setDisable(true);
                    stage.getScene().getRoot().getScene().lookup("#menuPane").setDisable(true);
                }
            });
        }
    }

    private void initCancelBtn() {
        cancelBtn.setText(FxmlAndLanguageUtils.getString("GLOBAL_BTN_CANCEL"));
        cancelBtn.setOnAction(event -> {
            closeDialog();
        });
    }

    public void closeDialog() {
        boolean isOverride = false;
        if (windowCustomListener != null) {
            isOverride = windowCustomListener.onCloseAndCancelCustomEvent();
        }
        if (!isOverride) {
            StageMap.closeStage(ResourceMassages.COMPONENT_STAGE_WINDOW_PROGRESS_TIP);
            Stage stage = StageMap.getStage(ResourceMassages.PLARTFORM_STAGE_MAIN);
            if (stage != null &&  stage.getScene() != null &&  stage.getScene().lookup("#grpContent") != null) {
                stage.getScene().lookup("#grpContent").setDisable(false);
                stage.getScene().lookup("#tbaSystem").setDisable(false);
                stage.getScene().lookup("#menuPane").setDisable(false);
            }
        }
    }

    public void addProcessMonitorListener(WindowCustomListener windowCustomListener) {
        this.windowCustomListener = windowCustomListener;
    }
}
