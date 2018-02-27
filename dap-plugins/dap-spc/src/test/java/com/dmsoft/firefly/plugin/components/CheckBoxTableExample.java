package com.dmsoft.firefly.plugin.components;

import com.dmsoft.firefly.gui.components.table.TableViewWrapper;
import com.dmsoft.firefly.plugin.spc.charts.model.CheckTableModel;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.google.common.collect.Lists;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * Created by cherry on 2018/2/8.
 */
public class CheckBoxTableExample extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {

        TableView<String> tableView = new TableView<>();
        CheckTableModel tableModel = new CheckTableModel();
        tableModel.setCheckIndex(0);
        tableView.setEditable(true);
        tableModel.getRowKeyArray().addAll(UIConstant.SPC_CHART_XBAR_EXTERN_MENU);
        tableModel.getHeaderArray().addAll(Lists.newArrayList("1", "2"));
        TableViewWrapper wrapper = new TableViewWrapper(tableView, tableModel);
        Scene scene = new Scene(wrapper.getWrappedTable(), 100, 50);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        primaryStage.setTitle("CheckBox Table Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
