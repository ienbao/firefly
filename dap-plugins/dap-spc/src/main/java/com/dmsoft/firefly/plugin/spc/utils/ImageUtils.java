/*
 * Copyright (c) 2017. For Intelligent Group.
 */
package com.dmsoft.firefly.plugin.spc.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

/**
 * Created by Ethan.Yang on 2018/2/5.
 */
public class ImageUtils {
    private static final double IMAGE_WIDTH = 16;
    private static final double IMAGE_HEIGHT = 16;


    public static final ImageView getImageView(InputStream url){
        ImageView imageReset = new ImageView(new Image(url));
        imageReset.setFitHeight(IMAGE_HEIGHT);
        imageReset.setFitWidth(IMAGE_WIDTH);
        return imageReset;
    }
}
