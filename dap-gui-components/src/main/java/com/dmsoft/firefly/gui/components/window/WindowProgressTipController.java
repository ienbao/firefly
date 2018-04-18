package com.dmsoft.firefly.gui.components.window;

import com.dmsoft.firefly.gui.components.utils.CommonResourceMassages;
import com.dmsoft.firefly.gui.components.utils.FxmlAndLanguageUtils;
import com.dmsoft.firefly.gui.components.utils.StageMap;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
    @FXML
    private TextArea errorTxt;
    @FXML
    private Label analysisLB;
    @FXML
    private VBox vbox;
    @FXML
    private HBox hbox;

    private Stage stage;

    private WindowCustomListener windowCustomListener;
    private boolean autoHide = true;

    @FXML
    private void initialize() {
        initCancelBtn();
        errorTxt.setEditable(false);
        errorTxt.setVisible(false);
        errorTxt.setWrapText(true);
        errorTxt.setMaxHeight(0);
        vbox.getChildren().remove(hbox);
        taskProgress.getStyleClass().setAll("progress-bar-lg-green");
        taskProgress.setProgress(0);
        taskProgress.progressProperty().addListener(e -> {
            if (taskProgress.getProgress() >= 1 && autoHide) {
                closeDialog();
            }
        });
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

    public void updateFailProgress(double progressValue) {
        updateFailProgress(progressValue, "");
        /*if (progressValue >= 100) {
            closeDialog();
        }*/
    }

    public void updateFailProgress(String errorText) {
        updateFailProgress(taskProgress.getProgress() * 100, errorText);
        /*if (progressValue >= 100) {
            closeDialog();
        }*/
    }

    public void updateFailProgressNextLine(String errorText) {
        updateFailProgress(taskProgress.getProgress() * 100, "\n" + errorText);
        /*if (progressValue >= 100) {
            closeDialog();
        }*/
    }

    public void updateFailProgress(double progressValue, String errorText) {
        Stage stage1 = StageMap.getStage(CommonResourceMassages.COMPONENT_STAGE_WINDOW_PROGRESS_TIP);
        WindowPane windowPane = null;
        if (stage1.getScene().getRoot() instanceof WindowPane) {
            windowPane = (WindowPane) stage1.getScene().getRoot();
        }
        if (windowPane != null) {
            windowPane.getCloseBtn().setOnAction(event -> stage1.fireEvent(new WindowEvent(stage1, WindowEvent.WINDOW_CLOSE_REQUEST)));
        }
        taskProgress.getStyleClass().setAll("progress-bar-lg-red");
        taskProgress.setProgress(progressValue / 100);
        Platform.runLater(() -> {
            if (!vbox.getChildren().contains(hbox)) {
                vbox.getChildren().add(2, hbox);
            }
        });
        errorTxt.setVisible(true);
        errorTxt.setMinHeight(245);
        errorTxt.appendText(errorText);
        Platform.runLater(() -> {
            stage1.setMaxHeight(380);
            stage1.setMinHeight(380);
        });
        /*if (progressValue >= 100) {
            closeDialog();
        }*/
    }

    public void onShowingRequest() {
        boolean isOverride = false;
        if (windowCustomListener != null) {
            isOverride = windowCustomListener.onShowCustomEvent();
        }

        if (!isOverride) {
            Stage stage1 = StageMap.getStage(CommonResourceMassages.PLATFORM_STAGE_MAIN);
            if (stage1 != null && stage1.getScene() != null && stage1.getScene().lookup("#grpContent") != null) {
                stage1.getScene().lookup("#grpContent").setDisable(true);
                stage1.getScene().lookup("#tbaSystem").setDisable(true);
                stage1.getScene().getRoot().getScene().lookup("#menuPane").setDisable(true);
            }
        }
    }

    private void initCancelBtn() {
        cancelBtn.setText(FxmlAndLanguageUtils.getString("GLOBAL_BTN_CANCEL"));
        cancelBtn.setOnAction(event -> setCancelingText());
    }

    public void closeDialog() {
        boolean isOverride = false;
        if (windowCustomListener != null) {
            isOverride = windowCustomListener.onCloseAndCancelCustomEvent();
        }
        if (!isOverride) {
            StageMap.closeStage(CommonResourceMassages.COMPONENT_STAGE_WINDOW_PROGRESS_TIP);
            Stage stage1 = StageMap.getStage(CommonResourceMassages.PLATFORM_STAGE_MAIN);
            if (stage1 != null && stage1.getScene() != null && stage1.getScene().lookup("#grpContent") != null) {
                stage1.getScene().lookup("#grpContent").setDisable(false);
                stage1.getScene().lookup("#tbaSystem").setDisable(false);
                stage1.getScene().lookup("#menuPane").setDisable(false);
            }
        }
    }

    public void addProcessMonitorListener(WindowCustomListener windowCustomListener) {
        this.windowCustomListener = windowCustomListener;
    }

    public ProgressBar getTaskProgress() {
        return taskProgress;
    }

    public Button getCancelBtn() {
        return cancelBtn;
    }

    public Label getAnalysisLB() {
        return analysisLB;
    }

    public void setAutoHide(boolean autoHide) {
        this.autoHide = autoHide;
    }

    /**
     * method to set canceling text
     */
    public void setCancelingText() {
        if (FxmlAndLanguageUtils.getString("GLOBAL_BTN_CANCEL").equals(cancelBtn.getText())) {
            cancelBtn.setText(FxmlAndLanguageUtils.getString("CANCELING"));
        }
    }
}
