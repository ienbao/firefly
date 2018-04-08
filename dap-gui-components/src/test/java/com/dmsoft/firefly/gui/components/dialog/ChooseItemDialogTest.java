package com.dmsoft.firefly.gui.components.dialog;

import com.dmsoft.firefly.gui.components.window.WindowFactory;
import javafx.application.Application;
import javafx.stage.Stage;

public class ChooseItemDialogTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        ChooseItemsMainPane pane = new ChooseItemsMainPane(null);
        Stage stage = WindowFactory.createOrUpdateSimpleWindowAsModel("Choose Test Items","Choose Test Items", pane);
        stage.show();
    }
}
