package com.dmsoft.firefly.plugin.spc.charts.view;

import com.dmsoft.firefly.gui.components.utils.TextFieldFilter;
import com.dmsoft.firefly.plugin.spc.charts.ClearCallBack;
import com.dmsoft.firefly.plugin.spc.charts.model.SimpleItemCheckModel;
import com.dmsoft.firefly.plugin.spc.utils.ImageUtils;
import com.dmsoft.firefly.sdk.utils.DAPStringUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import java.util.List;

/**
 * Created by cherry on 2018/2/27.
 */
public class ChartAnnotationButton extends Button {

    private Popup popup;
    private VBox vBox;
    private Button clearBtn;
    private Button editBtn;
    private TextFieldFilter itemFilter;
    private ImageView imageReset;
    private ListView<SimpleItemCheckModel> dataListView;
    private ObservableList<SimpleItemCheckModel> dataModels = FXCollections.observableArrayList();
    private FilteredList<SimpleItemCheckModel> filteredData = new FilteredList(dataModels, s -> true);
    private ClearCallBack clearCallBack;

    private Object currentSelectItem;
    private boolean showAnnotation = false;

    private final int defaultSelectedIndex = 0;
    public final Double MAX_HEIGHT = 275.0;
    private final double threshold = 4;

    private String listViewBorderStyle = "-fx-border-width: 1px 0px 1px 0px;";

    public ChartAnnotationButton() {

        this.initComponent();
        this.initRender();
        this.initEvent();
        this.initData();
    }

    public void setData(List<String> data) {
        this.setData(data, defaultSelectedIndex);
    }

    public void setData(List<String> data, int selectedIndex) {
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                boolean selected = (selectedIndex == i);
                dataModels.add(new SimpleItemCheckModel(data.get(i), selected));
            }
        }
        dataListView.setItems(filteredData);
    }

    private void initComponent() {

        popup = new Popup();
        vBox = new VBox();
        clearBtn = new Button();
        editBtn = new Button();
        itemFilter = new TextFieldFilter();
        imageReset = new ImageView(new Image("/images/icon_choose_one_gray.png"));
        dataListView = new ListView<>();
        dataListView.setCellFactory(e -> this.buildListCell());
        HBox hBox = new HBox();
        HBox blankBox = new HBox();
        blankBox.setPrefHeight(20);
        blankBox.setPrefWidth(20);
        hBox.getChildren().addAll(editBtn, clearBtn, blankBox);
        BorderPane borderPane = new BorderPane();
        borderPane.setPrefHeight(20);
        borderPane.setMinHeight(20);
        borderPane.setMaxHeight(20);
        borderPane.setRight(hBox);
        vBox.getChildren().add(itemFilter);
        vBox.getChildren().add(dataListView);
        vBox.getChildren().add(borderPane);
        popup.getContent().add(vBox);
        vBox.setMargin(borderPane, new Insets(2, 0, 0, 0));
    }

    private void initEvent() {

        itemFilter.getTextField().textProperty().addListener(obs -> {
            String filter = itemFilter.getTextField().getText();
            if (filter == null || filter.length() == 0) {
                filteredData.setPredicate(s -> true);
            } else {
                filteredData.setPredicate(s -> s.getItemName().contains(filter));
            }
        });

        dataListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        dataListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            newValue.setIsChecked(true);
            if (oldValue != null) {
                oldValue.setIsChecked(false);
            }
            dataListView.refresh();
            editCurrentSelectItem(newValue.getItemName());
        });

        Button button = this;
        button.setOnMouseClicked(event -> {
            Double preHeight = vBox.getPrefHeight();
            if (preHeight >= MAX_HEIGHT) {
                preHeight = MAX_HEIGHT;
            }
            double x = button.getScene().getWindow().getX() +
                    button.getScene().getX() + button.localToScene(0, 0).getX();
            double y = button.getScene().getWindow().getY() +
                    button.getScene().getY() + button.localToScene(0, 0).getY() - preHeight - threshold;
            x -= vBox.getPrefWidth();
            x += button.getPrefWidth();

            popup.show(button, x, y);
        });

        vBox.setOnMouseExited(event -> {
            if (popup.isShowing()) {
                popup.hide();
            }
        });

        clearBtn.setOnMouseClicked(event -> {
            if (dataModels == null || dataModels.isEmpty()) {
                return;
            }
            reset();
        });

        editBtn.setOnMouseClicked(event -> {
            showAnnotation = false;
        });
    }

    private void reset() {
        dataModels.get(0).setIsChecked(true);
        dataListView.refresh();
        showAnnotation = false;
        editCurrentSelectItem(dataModels.get(0).getItemName());
        if (clearCallBack != null) {
            clearCallBack.execute();
        }
    }

    private void initRender() {

        vBox.setStyle("-fx-border-width: 1px; -fx-border-color: #cccccc; -fx-background-color: white;");
        clearBtn.setGraphic(ImageUtils.getImageView(getClass()
                .getResourceAsStream("/images/btn_remove_tracing_point_normal.png")));
        editBtn.setGraphic(ImageUtils.getImageView(getClass()
                .getResourceAsStream("/images/btn_cancel_tracing_point_normal.png")));
        clearBtn.getStyleClass().addAll("btn-icon-b");
        editBtn.getStyleClass().addAll("btn-icon-b");
        this.getStyleClass().addAll("btn-icon-b");

        vBox.setMargin(itemFilter, new Insets(3, 3, 2, 3));
        dataListView.setStyle(listViewBorderStyle);
        itemFilter.getTextField().setFocusTraversable(false);
        clearBtn.setPrefWidth(25);
        clearBtn.setMinWidth(25);
        clearBtn.setMaxWidth(25);
        clearBtn.setPrefHeight(20);
        clearBtn.setMinHeight(20);
        clearBtn.setMaxHeight(20);
        editBtn.setPrefHeight(20);
        editBtn.setMinHeight(20);
        editBtn.setMaxHeight(20);
        editBtn.setPrefWidth(25);
        editBtn.setMinWidth(25);
        editBtn.setMaxWidth(25);
        imageReset.setFitHeight(16);
        imageReset.setFitWidth(16);
        vBox.setPrefWidth(160);
        vBox.setPrefHeight(MAX_HEIGHT);
        vBox.setMaxHeight(MAX_HEIGHT);
        vBox.setMinHeight(MAX_HEIGHT);
        dataListView.setMaxHeight(220);
        itemFilter.getTextField().setPromptText("Filter");
        this.setPrefWidth(20);
        this.setMinWidth(20);
        this.setMaxWidth(20);
    }

    private void editCurrentSelectItem(Object currentSelectItem) {

        this.currentSelectItem = currentSelectItem;
        if (DAPStringUtils.isNotBlank(this.currentSelectItem + "")) {
            this.activeAnnotation();
        }
    }

    private void activeAnnotation() {
        showAnnotation = true;
    }

    private void initData() {
        dataListView.setItems(dataModels);
    }

    private ListCell<SimpleItemCheckModel> buildListCell() {

        ListCell<SimpleItemCheckModel> listCell = new ListCell<SimpleItemCheckModel>() {
            @Override
            public void updateItem(SimpleItemCheckModel item, boolean empty) {

                super.updateItem(item, empty);
                if (item == null || empty == true) {
                    setGraphic(null);
                    setText(null);
                } else {
                    HBox cell;
                    Label label = new Label(item.getItemName());
                    if (item.isIsChecked()) {
                        cell = new HBox(imageReset, label);
                        dataListView.getSelectionModel().select(item);
                    } else {
                        Label label1 = new Label("");
                        label1.setPrefWidth(16);
                        label1.setPrefHeight(16);
                        cell = new HBox(label1, label);
                    }
                    setGraphic(cell);
                }
            }
        };
        return listCell;
    }

    public Object getCurrentSelectItem() {
        return currentSelectItem;
    }

    public boolean isShowAnnotation() {
        return showAnnotation;
    }

    public void setClearCallBack(ClearCallBack clearCallBack) {
        this.clearCallBack = clearCallBack;
    }
}
