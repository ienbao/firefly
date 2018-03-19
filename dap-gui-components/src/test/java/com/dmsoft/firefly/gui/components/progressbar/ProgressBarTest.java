package com.dmsoft.firefly.gui.components.progressbar;

import com.dmsoft.firefly.gui.components.utils.FxmlAndLanguageUtils;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ProgressBarTest extends Application {

    static {
        FxmlAndLanguageUtils.setIsDebug(true);
    }

    @Override
    public void start(Stage primaryStage) {
        double y = 15;
        final double SPACING = 15;
        primaryStage.setTitle("ProgressBar");
        Group root = new Group();

        ProgressBar smRed = new ProgressBar();
        smRed.getStyleClass().add("progress-bar-sm-red");
        smRed.setProgress(0.2);
        //smRed.setLayoutY(y);

        y += SPACING;
        ProgressBar mdRed = new ProgressBar();
        mdRed.getStyleClass().add("progress-bar-md-red");
        mdRed.setProgress(0.4);
        mdRed.setLayoutY(y);

        y += SPACING;
        ProgressBar mdGreed = new ProgressBar();
        mdGreed.getStyleClass().add("progress-bar-md-green");
        mdGreed.setProgress(0.6);
        mdGreed.setLayoutY(y);

        y += SPACING;
        ProgressBar lgRed = new ProgressBar();
        lgRed.getStyleClass().add("progress-bar-lg-red");
        lgRed.setProgress(80);
        lgRed.setLayoutY(y);

        y += SPACING;
        ProgressBar lgGreen = new ProgressBar();
        lgGreen.getStyleClass().add("progress-bar-lg-green");
        lgGreen.setProgress(0.8);
        lgGreen.setLayoutY(y);


        root.getChildren().addAll(smRed, mdRed, mdGreed, lgRed, lgGreen);
        Scene scene = new Scene(root, 1000, 250, Color.WHITE);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
