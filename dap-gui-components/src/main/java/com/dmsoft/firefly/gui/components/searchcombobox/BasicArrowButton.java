package com.dmsoft.firefly.gui.components.searchcombobox;

import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import static com.dmsoft.firefly.gui.components.searchcombobox.BasicArrowButton.Direction.UP;

public class BasicArrowButton extends StackPane {
    public enum Direction {
        UP {
            @Override
            String getStyleClass() {
                return "up-arrow-button";
            }
        }, DOWN {
            @Override
            String getStyleClass() {
                return "down-arrow-button";
            }
        }, LEFT {
            @Override
            String getStyleClass() {
                return "left-arrow-button";
            }
        }, RIGHT {
            @Override
            String getStyleClass() {
                return "right-arrow-button";
            }
        };

        abstract String getStyleClass();
    }

    public BasicArrowButton(Direction direction) {
        Direction dir = direction == null ? UP : direction;
        Region arrow = new Region();
        arrow.setFocusTraversable(false);
        arrow.getStyleClass().setAll("arrow");
        arrow.setMaxWidth(Region.USE_PREF_SIZE);
        arrow.setMaxHeight(Region.USE_PREF_SIZE);
        arrow.setMouseTransparent(true);
        this.setFocusTraversable(false);
        this.getStyleClass().setAll(dir.getStyleClass());
        this.getChildren().add(arrow);
    }
}
