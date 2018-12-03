package com.dmsoft.firefly.gui.controller.template;

import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by GuangLi on 2018/2/28.
 */
public class TimePane extends HBox {
    private ComboBox<String> item;
    private Button delete;
    private EnvService envService;

    /**
     * constructor
     *
     * @param selectItem select item
     */
    public TimePane(String selectItem) {
        initView();
        item.setValue(selectItem);
    }

    /**
     * constructor
     */
    public TimePane() {
        initView();
    }

    private void initView() {
        this.setMaxWidth(500);
        item = new ComboBox<>();
        item.setMinWidth(160);
        item.setPrefSize(160, 22);
        item.setStyle("-fx-border-width: 1 0 1 1");
        item.setItems(FXCollections.observableArrayList(envService.findTestItemNames()));
        delete = new Button();
        delete.setMaxWidth(22);
        delete.setMinWidth(22);
        delete.setPrefSize(22, 22);
        delete.setGraphic(ImageUtils.getImageView(getClass().getResourceAsStream("/images/delete.svg")));
        delete.setOnAction(event -> ((VBox) this.getParent()).getChildren().remove(this));
        this.getChildren().add(item);
        this.getChildren().add(delete);
        HBox.setMargin(delete, new Insets(0, 0, 10, 0));
    }

    public String getSelectItem() {
        return item.getValue();
    }
}
