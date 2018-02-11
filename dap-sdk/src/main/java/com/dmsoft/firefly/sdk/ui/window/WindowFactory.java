package com.dmsoft.firefly.sdk.ui.window;

import com.dmsoft.firefly.sdk.utils.StageMap;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class WindowFactory {

    public static Stage createFullWindow(String name, String title, Pane bodyPane, String style) {
        WindowPane windowPane = new WindowPane(title, bodyPane);
        if (StageMap.loadStage(name, windowPane, false, style, StageStyle.TRANSPARENT)) {
            Stage stage = StageMap.getStage(name);
            windowPane.setStage(StageMap.getStage(name));
            windowPane.init();
            return stage;
        }

        return null;
    }

    public static Stage createFullWindow(String name, Pane title, Pane bodyPane, String style) {
        WindowPane windowPane = new WindowPane(title, bodyPane);

        if (StageMap.loadStage(name, windowPane, false, style, StageStyle.TRANSPARENT)) {
            Stage stage = StageMap.getStage(name);
            windowPane.setStage(stage);
            windowPane.init();
            return stage;
        }

        return null;
    }

    public static Stage createFullWindowAsModel(String name, String title, Pane bodyPane, String style) {
        WindowPane windowPane = new WindowPane(title, bodyPane);

        if (StageMap.loadStage(name, windowPane, true, style, StageStyle.TRANSPARENT)) {
            Stage stage = StageMap.getStage(name);
            windowPane.setStage(stage);
            windowPane.init();
            return stage;
        }

        return null;
    }

    public static Stage createFullWindowAsModel(String name, Pane title, Pane bodyPane, String style) {
        WindowPane windowPane = new WindowPane(title, bodyPane);
        if (StageMap.loadStage(name, windowPane, true, style, StageStyle.TRANSPARENT)) {
            Stage stage = StageMap.getStage(name);
            windowPane.setStage(stage);
            windowPane.init();
            return stage;
        }

        return null;
    }

    public static Stage createSimpleWindowAsModel(String name, Pane title, Pane bodyPane, String style) {
        WindowPane windowPane = new WindowPane(title, bodyPane);
        windowPane.setWindowsModel(WindowPane.WINDOW_MODEL_X);

        if (StageMap.loadStage(name, windowPane, true, style, StageStyle.TRANSPARENT)) {
            Stage stage = StageMap.getStage(name);
            windowPane.setStage(stage);
            windowPane.init();
            return stage;
        }

        return null;
    }

    public static Stage createSimpleWindowAsModel(String name, String title, Pane bodyPane, String style) {
        WindowPane windowPane = new WindowPane(title, bodyPane);
        windowPane.setWindowsModel(WindowPane.WINDOW_MODEL_X);

        if (StageMap.loadStage(name, windowPane, true, style, StageStyle.TRANSPARENT)) {
            Stage stage = StageMap.getStage(name);
            windowPane.setStage(stage);
            windowPane.init();
            return stage;
        }

        return null;
    }
}
