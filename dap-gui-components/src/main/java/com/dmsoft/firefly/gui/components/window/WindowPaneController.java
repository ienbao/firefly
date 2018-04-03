package com.dmsoft.firefly.gui.components.window;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class WindowPaneController {
    private WindowPane windowPane;
    private Stage stage;
    private SimpleBooleanProperty maximizeProperty;
    private SimpleBooleanProperty shadowProperty;
    private BoundingBox savedBounds;
    private double initX = -1;
    private double initY = -1;
    private double newX = -1;
    private double newY = -1;

    public WindowPaneController(WindowPane windowPane) {
        this.windowPane = windowPane;
        this.stage = windowPane.getStage();
        maximizeProperty = new SimpleBooleanProperty(false);
        maximizeProperty.addListener((ov, t, t1) -> {
            switchMaximize();
        });
        shadowProperty = new SimpleBooleanProperty(true);
        shadowProperty.addListener((ov, t, t1) -> {
            windowPane.shadowVisible(t1);
        });
    }

    protected void setStageDraggable() {
        stage.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.isPrimaryButtonDown()) {
                initX = event.getScreenX();
                initY = event.getScreenY();
            } else {
                initX = -1;
                initY = -1;
            }
        });

        windowPane.getTitlePane().addEventFilter(MouseEvent.MOUSE_DRAGGED, getDragEvent());
    }

    protected EventHandler getDragEvent() {
        EventHandler<? super MouseEvent> draggedEvent = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!event.isPrimaryButtonDown() || initX == -1) {
                    return;
                }
                if (event.isStillSincePress()) {
                    return;
                }
                if (maximizeProperty.get()) {
                    maximizeProperty.set(false);
                    stage.setX(event.getScreenX() - stage.getWidth() / 2);
                    stage.setY(event.getScreenY() - WindowPane.SHADOW_WIDTH);
                }
                newX = event.getScreenX();
                newY = event.getScreenY();
                double deltaX = newX - initX;
                double deltaY = newY - initY;
                initX = newX;
                initY = newY;
                windowPane.getTitlePane().setCursor(Cursor.HAND);
                ObservableList<Screen> screensForRectangle = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
                Screen screen = screensForRectangle.get(0);
                Rectangle2D visualBounds = screen.getVisualBounds();
                setStageX(stage.getX() + deltaX, visualBounds);
                setStageY(stage.getY() + deltaY, visualBounds);
                event.consume();
            }
        };
        return draggedEvent;
    }

    protected void setStageResizable() {
        stage.getScene().addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
            stage.getScene().setCursor(Cursor.DEFAULT);
            if (maximizeProperty.get()) {
                return;
            }
            if (!stage.isResizable()) {
                return;
            }
            if (isRightEdge(event)) {
                if (isTopEdge(event)) {
                    stage.getScene().setCursor(Cursor.NE_RESIZE);
                } else if (isBottomEdge(event)) {
                    stage.getScene().setCursor(Cursor.SE_RESIZE);
                } else {
                    stage.getScene().setCursor(Cursor.E_RESIZE);
                }
            } else if (isBottomEdge(event)) {
                if (isLeftEdge(event)) {
                    stage.getScene().setCursor(Cursor.SW_RESIZE);
                } else {
                    stage.getScene().setCursor(Cursor.S_RESIZE);
                }
            } else if (isLeftEdge(event)) {
                if (isTopEdge(event)) {
                    stage.getScene().setCursor(Cursor.NW_RESIZE);
                } else {
                    stage.getScene().setCursor(Cursor.W_RESIZE);
                }
            } else if (isTopEdge(event)) {
                stage.getScene().setCursor(Cursor.N_RESIZE);
            }
        });
        stage.getScene().addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            if (!event.isPrimaryButtonDown() || (initX == -1 && initY == -1)) {
                return;
            }
            if (maximizeProperty.get()) {
                return;
            }
            if (event.isStillSincePress()) {
                return;
            }
            newX = event.getScreenX();
            newY = event.getScreenY();
            double deltaX = newX - initX;
            double deltaY = newY - initY;

            Cursor cursor = stage.getScene().getCursor();
            ObservableList<Screen> screensForRectangle = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
            Screen screen = screensForRectangle.get(0);
            Rectangle2D visualBounds = screen.getVisualBounds();
            if (Cursor.E_RESIZE.equals(cursor)) {
                setStageWidth(stage.getWidth() + deltaX);
                event.consume();
            } else if (Cursor.NE_RESIZE.equals(cursor)) {
                if (setStageHeight(stage.getHeight() - deltaY)) {
                    setStageY(stage.getY() + deltaY, visualBounds);
                }
                setStageWidth(stage.getWidth() + deltaX);
                event.consume();
            } else if (Cursor.SE_RESIZE.equals(cursor)) {
                setStageWidth(stage.getWidth() + deltaX);
                setStageHeight(stage.getHeight() + deltaY);
                event.consume();
            } else if (Cursor.S_RESIZE.equals(cursor)) {
                setStageHeight(stage.getHeight() + deltaY);
                event.consume();
            } else if (Cursor.W_RESIZE.equals(cursor)) {
                if (setStageWidth(stage.getWidth() - deltaX)) {
                    setStageX(stage.getX() + deltaX, visualBounds);
                }
                event.consume();
            } else if (Cursor.SW_RESIZE.equals(cursor)) {
                if (setStageWidth(stage.getWidth() - deltaX)) {
                    setStageX(stage.getX() + deltaX, visualBounds);
                }
                setStageHeight(stage.getHeight() + deltaY);
                event.consume();
            } else if (Cursor.NW_RESIZE.equals(cursor)) {
                if (setStageWidth(stage.getWidth() - deltaX)) {
                    setStageX(stage.getX() + deltaX, visualBounds);
                }
                if (setStageHeight(stage.getHeight() - deltaY)) {
                    setStageY(stage.getY() + deltaY, visualBounds);
                }
                event.consume();
            } else if (Cursor.N_RESIZE.equals(cursor)) {
                if (setStageHeight(stage.getHeight() - deltaY)) {
                    setStageY(stage.getY() + deltaY, visualBounds);
                }
                event.consume();
            }
        });
    }

    private boolean setStageWidth(double width) {
        if (width >= stage.getMinWidth() + 2 * WindowPane.SHADOW_WIDTH) {
            stage.setWidth(width);
            initX = newX;
            return true;
        }
        stage.setWidth(stage.getMinWidth() + 2 * WindowPane.SHADOW_WIDTH);
        return false;
    }

    private boolean setStageHeight(double height) {
        if (windowPane.getTitlePane() != null) {
            if (height >= stage.getMinHeight() + WindowPane.BORDER_TITLE_HEIGHT + 2 * WindowPane.SHADOW_WIDTH) {
                stage.setHeight(height);
                initY = newY;
                return true;
            }
            stage.setHeight(stage.getMinHeight() + 2 * WindowPane.SHADOW_WIDTH + WindowPane.BORDER_TITLE_HEIGHT);
        } else {
            if (height >= stage.getMinHeight() + 2 * WindowPane.SHADOW_WIDTH) {
                stage.setHeight(height);
                initY = newY;
                return true;
            }
            stage.setHeight(stage.getMinHeight() + 2 * WindowPane.SHADOW_WIDTH);
        }
        return false;
    }

    private boolean isLeftEdge(MouseEvent event) {
        return event.getSceneX() < WindowPane.SHADOW_WIDTH;
    }

    private boolean isRightEdge(MouseEvent event) {
        return event.getSceneX() > stage.getWidth() - WindowPane.SHADOW_WIDTH;
    }

    private boolean isTopEdge(MouseEvent event) {
        return event.getSceneY() < WindowPane.SHADOW_WIDTH;
    }

    private boolean isBottomEdge(MouseEvent event) {
        return event.getSceneY() > stage.getHeight() - WindowPane.SHADOW_WIDTH;
    }

    private void setStageX(double x, Rectangle2D visualBounds) {
        if (stage.getWidth() + x - WindowPane.DRAG_PADDING > 0 && WindowPane.DRAG_PADDING + x < visualBounds.getMaxX()) {
            stage.setX(x);
        }
    }

    private void setStageY(double y, Rectangle2D visualBounds) {
        if (y > WindowPane.DRAG_PADDING && y + WindowPane.DRAG_PADDING < visualBounds.getMaxY()) {
            stage.setY(y);
        }
    }

    private void switchMaximize() {
        if (maximizeProperty.get()) {
            // false to true
            savedBounds = new BoundingBox(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
            shadowProperty.set(false);
            ObservableList<Screen> screensForRectangle = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
            Screen screen = screensForRectangle.get(0);
            Rectangle2D visualBounds = screen.getVisualBounds();
            stage.setX(visualBounds.getMinX());
            stage.setY(visualBounds.getMinY());
            stage.setWidth(visualBounds.getWidth());
            stage.setHeight(visualBounds.getHeight());
            if (windowPane.getMaximizeBtn() != null) {
                windowPane.getMaximizeBtn().getStyleClass().remove(WindowPane.maximizeBtnStyleClass);
                windowPane.getMaximizeBtn().getStyleClass().add(WindowPane.restoreBtnStyleClass);
            }
        } else {
            // true to false
            restoreSavedBounds();
            shadowProperty.set(true);
            if (windowPane.getMaximizeBtn() != null) {
                windowPane.getMaximizeBtn().getStyleClass().remove(WindowPane.restoreBtnStyleClass);
                windowPane.getMaximizeBtn().getStyleClass().add(WindowPane.maximizeBtnStyleClass);
            }
        }
    }

    public SimpleBooleanProperty maximizePropertyProperty() {
        return maximizeProperty;
    }

    private void restoreSavedBounds() {
        stage.setX(savedBounds.getMinX());
        stage.setY(savedBounds.getMinY());
        stage.setWidth(savedBounds.getWidth());
        stage.setHeight(savedBounds.getHeight());
        savedBounds = null;

    }
}
