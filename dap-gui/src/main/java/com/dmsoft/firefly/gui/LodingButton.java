package com.dmsoft.firefly.gui;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LodingButton extends Button {

    public LodingButton() {
        getStyleClass().add("btn-primary");
    }

    public void change(Boolean loading){
        if (loading){
            this.setGraphic(new RotatePic(new Image("/images/loading.svg")));
        }else{
            this.setGraphic(null);
        }
    }
}
