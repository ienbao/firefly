package com.dmsoft.firefly.gui;

import javafx.scene.control.Button;

public class TestButton extends Button {

    public TestButton() {
        getStyleClass().add("btn-primary");
    }

    public void change(Boolean loading){
        if (loading){
            this.getStyleClass().removeAll("btn-primary");
            this.getStyleClass().add("btn-primary-loading");
        }else{
            this.getStyleClass().removeAll("btn-primary-loading");
            this.getStyleClass().add("btn-primary");
        }
    }
}
