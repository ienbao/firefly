package com.dmsoft.firefly.gui.components.window;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.utils.enums.LanguageType;
import com.google.common.collect.Lists;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Collections;
import java.util.List;

public class WindowFactory {

    private static final String PLATFORM_CSS_PATH = WindowFactory.class.getClassLoader().getResource("css/redfall/main.css").toExternalForm();
    private static final String PLATFORM_CSS_PATH_ZN = WindowFactory.class.getClassLoader().getResource("css/redfall/main_cn.css").toExternalForm();

    public static List<String> checkStyles(String... styles) {
        List<String> list = Lists.newArrayList();

        //TODO yuanwen 代码结构优化中重建
//        if (LanguageType.ZH.equals(RuntimeContext.getBean(EnvService.class).getLanguageType())) {
//            list.add(PLATFORM_CSS_PATH_ZN);
//        } else {
            list.add(PLATFORM_CSS_PATH);
//        }
        if (styles != null && styles.length > 0) {
            Collections.addAll(list, styles);
        }
        return list;
    }

    public static Stage createFullWindow(String name, String title, Pane bodyPane, String... styles) {
        WindowPane windowPane = new WindowPane(title, bodyPane);

        if (StageMap.loadStage(name, windowPane, false, checkStyles(styles), StageStyle.TRANSPARENT)) {
            Stage stage = StageMap.getStage(name);
            windowPane.setStage(StageMap.getStage(name));
            windowPane.init();
            return stage;
        }

        return null;
    }

    public static Stage createFullWindow(String name, Pane title, Pane bodyPane, String... styles) {
        WindowPane windowPane = new WindowPane(title, bodyPane);

        if (StageMap.loadStage(name, windowPane, false, checkStyles(styles), StageStyle.TRANSPARENT)) {
            Stage stage = StageMap.getStage(name);
            windowPane.setStage(stage);
            windowPane.init();
            return stage;
        }

        return null;
    }

    public static Stage createFullWindowAsModel(String name, String title, Pane bodyPane, String... styles) {
        WindowPane windowPane = new WindowPane(title, bodyPane);
        windowPane.setWindowsModel(WindowPane.WINDOW_MODEL_FULL);

        if (StageMap.loadStage(name, windowPane, true, checkStyles(styles), StageStyle.TRANSPARENT)) {
            Stage stage = StageMap.getStage(name);
            windowPane.setStage(stage);
            windowPane.init();
            return stage;
        }

        return null;
    }

    public static Stage createFullWindowAsModel(String name, Pane title, Pane bodyPane, String... styles) {
        WindowPane windowPane = new WindowPane(title, bodyPane);
        windowPane.setWindowsModel(WindowPane.WINDOW_MODEL_FULL);

        if (StageMap.loadStage(name, windowPane, true, checkStyles(styles), StageStyle.TRANSPARENT)) {
            Stage stage = StageMap.getStage(name);
            windowPane.setStage(stage);
            windowPane.init();
            return stage;
        }

        return null;
    }

//    public static Stage createSimpleWindowAsModel(String name, Pane title, Pane bodyPane, String... styles) {
//        WindowPane windowPane = new WindowPane(title, bodyPane);
//        windowPane.setWindowsModel(WindowPane.WINDOW_MODEL_X);
//
//        if (StageMap.loadStage(name, windowPane, true, checkStyles(styles), StageStyle.TRANSPARENT)) {
//            Stage stage = StageMap.getStage(name);
//            windowPane.setStage(stage);
//            windowPane.init();
//            return stage;
//        }
//
//        return null;
//    }

//    public static Stage createSimpleWindowAsModel(String name, String title, Pane bodyPane, String... styles) {
//        WindowPane windowPane = new WindowPane(title, bodyPane);
//        windowPane.setWindowsModel(WindowPane.WINDOW_MODEL_X);
//
//        if (StageMap.loadStage(name, windowPane, true, checkStyles(styles), StageStyle.TRANSPARENT)) {
//            Stage stage = StageMap.getStage(name);
//            windowPane.setStage(stage);
//            windowPane.init();
//            return stage;
//        }
//
//        return null;
//    }


    public static Stage createOrUpdateFullWindow(String name, String title, Pane bodyPane, String... styles) {
        return StageMap.loadAndRefreshStage(name, title, bodyPane, true, WindowPane.WINDOW_MODEL_FULL, checkStyles(styles), StageStyle.TRANSPARENT);
    }

    public static Stage createOrUpdateFullWindow(String name, Pane title, Pane bodyPane, String... styles) {
        return StageMap.loadAndRefreshStage(name, title, bodyPane, true, WindowPane.WINDOW_MODEL_FULL, checkStyles(styles), StageStyle.TRANSPARENT);
    }


    public static Stage createOrUpdateSimpleWindowAsModel(String name, String title, Pane bodyPane, String... styles) {
        return StageMap.loadAndRefreshStage(name, title, bodyPane, true, WindowPane.WINDOW_MODEL_X, checkStyles(styles), StageStyle.TRANSPARENT);
    }

    public static Stage createOrUpdateSimpleWindowAsModel(String name, Pane title, Pane bodyPane, String... styles) {
        return StageMap.loadAndRefreshStage(name, title, bodyPane, true, WindowPane.WINDOW_MODEL_X, checkStyles(styles), StageStyle.TRANSPARENT);

    }

    public static Stage createOrUpdateWindowAsModelNoBtn(String name, String title, Pane bodyPane, String... styles) {
        return StageMap.loadAndRefreshStage(name, title, bodyPane, true, WindowPane.WINDOW_MODEL_NONE, checkStyles(styles), StageStyle.TRANSPARENT);
    }

    public static Stage createNoManagedStage(String title, Pane bodyPane, String... styles) {
        return StageMap.createNoManagedStage(title, bodyPane, true, checkStyles(styles), StageStyle.TRANSPARENT);
    }

}
