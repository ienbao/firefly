package com.dmsoft.firefly.plugin.yield.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

public class ImageUtils {
    private static final double IMAGE_WIDTH = 16;
    private static final double IMAGE_HEIGHT = 16;


    public static final ImageView getImageView(InputStream url) {
        ImageView imageReset = new ImageView(new Image(url));
        imageReset.setFitHeight(IMAGE_HEIGHT);
        imageReset.setFitWidth(IMAGE_WIDTH);
        return imageReset;
    }
}
