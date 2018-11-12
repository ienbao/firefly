package com.dmsoft.firefly.gui;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TestButton extends Button {

    public TestButton() {
        getStyleClass().add("btn-primary");
    }

    public void change(Boolean loading){
        if (loading){
            this.setGraphic(new RotatePic(new Image("/images/icon_loading_gray.png")));
        }else{
            this.setGraphic(null);
        }
    }
}
