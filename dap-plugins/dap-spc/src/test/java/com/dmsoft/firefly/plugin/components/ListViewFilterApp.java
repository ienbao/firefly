package com.dmsoft.firefly.plugin.components;

import com.dmsoft.firefly.plugin.spc.charts.model.SimpleItemCheckModel;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Created by cherry on 2018/3/4.
 */
public class ListViewFilterApp extends Application {

    private ListView<SimpleItemCheckModel> listView = new ListView<>();
    private TextField filterInput = new TextField("1");
    private ObservableList<SimpleItemCheckModel> data = FXCollections.observableArrayList();

    private FilteredList<SimpleItemCheckModel> filterData = new FilteredList(data, s-> true);

    @Override
    public void start(Stage primaryStage) throws Exception {
        for (int i = 0; i < 2; i++) {
            boolean selected = (i == 0) ? true : false;
            SimpleItemCheckModel model = new SimpleItemCheckModel("itemName" + i, selected);
            data.add(model);
        }
        System.out.println("julia");
        //data.add(new SimpleItemCheckModel("julia", true));

        filterInput.textProperty().addListener((ChangeListener) (observable, oldVal, newVal) -> {
                   /* String filter = filterInput.getText();
                    if (filter == null || filter.length() == 0) {
                        filterData.clear();

                        listView.setItems(data);
//                filterData.setPredicate(s -> true);
                    } else {
//                filterData.setPredicate(
//                        new Predicate<SimpleItemCheckModel>() {
//                            @Override
//                            public boolean test(SimpleItemCheckModel simpleItemCheckModel) {
//                                filterData.clear();
//
//                                return false;
//                            }
//                        });
                        filterData.clear();
                        data.forEach(dataOneData -> {
                            if (dataOneData.getItemName().contains(filter)) {
                                filterData.add(dataOneData);
                            }
                        });
                        System.out.println("filterData" +filterData.size());
                        listView.setItems(null);
                        listView.setItems(filterData);*/
            //}
            //filterData.remove(2);
            // filterData.clear();
            String filter = filterInput.getText();
            filterData.setPredicate(simpleItemCheckModel -> simpleItemCheckModel.getItemName().contains(filter));
//            filterData.setAll(FXCollections.observableArrayList());
//            System.out.println(data.size() + "fdsf");
//            data.forEach(dataOneData -> {
//                if (dataOneData.getItemName().contains(filter)) {
//                    filterData.add(dataOneData);
//                }
//            });
//            listView.setItems(null);
//            System.out.println(filterData.size());
//
//            listView.setItems(filterData);
//            System.out.println(listView.getItems().size() + "99999");

        });

        listView.setItems(filterData);

        System.out.println(listView.getItems().size());

        ImageView imageReset = new ImageView(new Image("/images/icon_choose_one_gray.png"));
        listView.setCellFactory(e -> new ListCell<SimpleItemCheckModel>() {
            @Override
            public void updateItem(SimpleItemCheckModel item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty == true) {
                    setGraphic(null);
                    setText(null);
                } else {
                    HBox cell = null;
                    Label label = new Label(item.getItemName());
                    if (item.isIsChecked()) {
                        cell = new HBox(imageReset, label);
                        listView.getSelectionModel().select(item);
                    } else {
                        Label label1 = new Label("");
                        label1.setPrefWidth(16);
                        label1.setPrefHeight(16);
                        cell = new HBox(label1, label);
                    }
                    setGraphic(cell);
                }
            }
        });

        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            newValue.setIsChecked(true);
            if (oldValue != null) {
                oldValue.setIsChecked(false);
            }
            listView.refresh();
        });


        BorderPane content = new BorderPane(listView);
        content.setBottom(filterInput);

        Scene scene = new Scene(content, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
