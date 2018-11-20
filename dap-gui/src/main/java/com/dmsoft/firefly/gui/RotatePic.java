package com.dmsoft.firefly.gui;

import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class RotatePic extends ImageView {
    public RotatePic(){
    }

    public RotatePic(Image image){
        this.setImage(image);
        this.setFitWidth(16);
        this.setFitHeight(16);
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(1000),this);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(Timeline.INDEFINITE);
        rotateTransition.setAutoReverse(false);
        rotateTransition.play();
    }

}
