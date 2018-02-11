package com.dmsoft.firefly.gui.components.window;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WindowFactory {

    private static final String PLATFORM_CSS_PATH = WindowFactory.class.getClassLoader().getResource("css/redfall/main.css").toExternalForm();

    public static Stage createFullWindow(String name, String title, Pane bodyPane, String style) {
        WindowPane windowPane = new WindowPane(title, bodyPane);
       // windowPane.setWindowsModel(WindowPane.WINDOW_MODEL_FULL);


        if (StageMap.loadStage(name, windowPane, false, Stream.of(PLATFORM_CSS_PATH, style).collect(Collectors.toList()), StageStyle.TRANSPARENT)) {
            Stage stage = StageMap.getStage(name);
            windowPane.setStage(StageMap.getStage(name));
            windowPane.init();
            return stage;
        }

        return null;
    }

    public static Stage createFullWindow(String name, Pane title, Pane bodyPane, String style) {
        WindowPane windowPane = new WindowPane(title, bodyPane);
        //windowPane.setWindowsModel(WindowPane.WINDOW_MODEL_FULL);

        if (StageMap.loadStage(name, windowPane, false, Stream.of(PLATFORM_CSS_PATH, style).collect(Collectors.toList()), StageStyle.TRANSPARENT)) {
            Stage stage = StageMap.getStage(name);
            windowPane.setStage(stage);
            windowPane.init();
            return stage;
        }

        return null;
    }

    public static Stage createFullWindowAsModel(String name, String title, Pane bodyPane, String style) {
        WindowPane windowPane = new WindowPane(title, bodyPane);
        windowPane.setWindowsModel(WindowPane.WINDOW_MODEL_FULL);

        if (StageMap.loadStage(name, windowPane, true, Stream.of(PLATFORM_CSS_PATH, style).collect(Collectors.toList()), StageStyle.TRANSPARENT)) {
            Stage stage = StageMap.getStage(name);
            windowPane.setStage(stage);
            windowPane.init();
            return stage;
        }

        return null;
    }

    public static Stage createFullWindowAsModel(String name, Pane title, Pane bodyPane, String style) {
        WindowPane windowPane = new WindowPane(title, bodyPane);
        windowPane.setWindowsModel(WindowPane.WINDOW_MODEL_FULL);

        if (StageMap.loadStage(name, windowPane, true, Stream.of(PLATFORM_CSS_PATH, style).collect(Collectors.toList()), StageStyle.TRANSPARENT)) {
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

        if (StageMap.loadStage(name, windowPane, true, Stream.of(PLATFORM_CSS_PATH, style).collect(Collectors.toList()), StageStyle.TRANSPARENT)) {
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

        if (StageMap.loadStage(name, windowPane, true, Stream.of(PLATFORM_CSS_PATH, style).collect(Collectors.toList()), StageStyle.TRANSPARENT)) {
            Stage stage = StageMap.getStage(name);
            windowPane.setStage(stage);
            windowPane.init();
            return stage;
        }

        return null;
    }
}
