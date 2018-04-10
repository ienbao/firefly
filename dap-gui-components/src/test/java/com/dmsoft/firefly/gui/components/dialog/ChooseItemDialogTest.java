package com.dmsoft.firefly.gui.components.dialog;

import com.dmsoft.firefly.gui.components.window.WindowFactory;
import com.google.common.collect.Lists;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

public class ChooseItemDialogTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        List<String> item = Lists.newArrayList();
        for (int i = 0; i < 101; i++) {
            item.add("item" + i);
        }
        ChooseTestItemDialog dialog = new ChooseTestItemDialog(item, null, 50);
        dialog.show();
    }
}
