package com.dmsoft.firefly.sdk.ui.window;

import com.dmsoft.bamboo.common.utils.base.Platforms;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class WindowPane extends GridPane {
    private Stage stage;
    private GridPane titlePane;
    private Pane bodyPane;
    private GridPane contentPane;

    private Button minimizeBtn;
    private Button maximizeBtn;
    private Button closeBtn;

    private WindowPaneController controller;

    protected static String windowButtonClass = "";
    protected static String minimizeBtnStyleClass = "";
    protected static String maximizeBtnStyleClass = "";
    protected static String restoreBtnStyleClass = "";
    protected static String closeBtnStyleClass = "";

    protected static final int TITLE_LEFT_PADDING_WIN = 10;
    protected static final int TITLE_LEFT_PADDING_MAC = 15;
    protected static final int SHADOW_WIDTH = 10;
    protected static final int BORDER_TITLE_HEIGHT = 30;

    protected static final int DRAG_PADDING = 10;
    protected static final int WINDOW_BUTTON_WIDTH = 30;

    private Effect shadowEffect = new DropShadow(BlurType.TWO_PASS_BOX, new Color(0, 0, 0, 0.2),
            10, 0, 0, 0);

    static {
        if (Platforms.IS_MAC_OSX || Platforms.IS_MAC) {
            windowButtonClass = "mac-window-button";
            minimizeBtnStyleClass = "mac-button-minimize";
            maximizeBtnStyleClass = "mac-button-maximize";
            restoreBtnStyleClass = "mac-button-restore";
            closeBtnStyleClass = "mac-button-close";
        } else {
            windowButtonClass = "btn-icon";
            minimizeBtnStyleClass = "window-button-minimize";
            maximizeBtnStyleClass = "window-button-maximize";
            restoreBtnStyleClass = "window-button-restore";
            closeBtnStyleClass = "window-button-close";
        }
    }

    public WindowPane(Stage stage, String title, Pane body) {
        this.stage = stage;
        this.bodyPane = body;
        controller = new WindowPaneController(this);
        initContentPane();
        initTitlePane(new Label(title));
        initBodyPane();

        this.contentPane.setStyle("-fx-background-color: white");
        this.setStyle("-fx-background-color: transparent");
    }

    public WindowPane(Stage stage, Pane title, Pane body) {
        this.stage = stage;
        this.bodyPane = body;
        controller = new WindowPaneController(this);
        initContentPane();
        initTitlePane(title);
        initBodyPane();

        this.contentPane.setStyle("-fx-background-color: white");
        this.setStyle("-fx-background-color: transparent");
    }


    private void initContentPane() {
        RowConstraints r0 = new RowConstraints(SHADOW_WIDTH, SHADOW_WIDTH, SHADOW_WIDTH);
        r0.setVgrow(Priority.NEVER);
        RowConstraints r1 = new RowConstraints();
        r1.setVgrow(Priority.ALWAYS);
        RowConstraints r2 = new RowConstraints(SHADOW_WIDTH, SHADOW_WIDTH, SHADOW_WIDTH);
        r2.setVgrow(Priority.NEVER);

        getRowConstraints().addAll(r0, r1, r2);

        ColumnConstraints c0 = new ColumnConstraints(SHADOW_WIDTH, SHADOW_WIDTH, SHADOW_WIDTH);
        c0.setHgrow(Priority.NEVER);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setHgrow(Priority.ALWAYS);
        ColumnConstraints c2 = new ColumnConstraints(SHADOW_WIDTH, SHADOW_WIDTH, SHADOW_WIDTH);
        c2.setHgrow(Priority.NEVER);
        getColumnConstraints().addAll(c0, c1, c2);
        Menu menu = new Menu();

        this.contentPane = new GridPane();
        RowConstraints cr0 = new RowConstraints();
        cr0.setVgrow(Priority.NEVER);
        RowConstraints cr1 = new RowConstraints();
        cr1.setVgrow(Priority.ALWAYS);
        ColumnConstraints cc0 = new ColumnConstraints();

        this.contentPane.getRowConstraints().addAll(cr0, cr1);
        cc0.setHgrow(Priority.ALWAYS);

        this.contentPane.getColumnConstraints().add(cc0);

        this.contentPane.setEffect(shadowEffect);

        this.add(contentPane, 1, 1);
    }

    private void initTitlePane() {
        this.titlePane = new GridPane();
        RowConstraints r0 = new RowConstraints();
        r0.setVgrow(Priority.NEVER);
        this.titlePane.getRowConstraints().add(r0);

        ColumnConstraints c0 = new ColumnConstraints();
        c0.setHgrow(Priority.ALWAYS);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setHgrow(Priority.NEVER);

        this.titlePane.getColumnConstraints().addAll(c0, c1);
        this.titlePane.add(buildWindowBtn(), 0, 0);
        this.contentPane.add(titlePane, 0, 0);
    }

    private void initTitlePane(Node title) {
        this.titlePane = new GridPane();
        RowConstraints r0 = new RowConstraints();
        r0.setVgrow(Priority.NEVER);
        this.titlePane.getRowConstraints().add(r0);

        ColumnConstraints c0 = new ColumnConstraints();
        c0.setHgrow(Priority.NEVER);

        if (Platforms.IS_MAC_OSX) {
            c0.setPrefWidth(TITLE_LEFT_PADDING_MAC);
            c0.setMaxWidth(TITLE_LEFT_PADDING_MAC);
            c0.setMinWidth(TITLE_LEFT_PADDING_MAC);

            ColumnConstraints c1 = new ColumnConstraints();
            c1.setHgrow(Priority.NEVER);
            ColumnConstraints c2 = new ColumnConstraints();
            c2.setHgrow(Priority.ALWAYS);
            this.titlePane.getColumnConstraints().addAll(c0, c1, c2);

            this.titlePane.add(buildWindowBtn(), 1, 0);
            this.titlePane.add(title, 2, 0);

        } else {
            c0.setPrefWidth(TITLE_LEFT_PADDING_WIN);
            c0.setMaxWidth(TITLE_LEFT_PADDING_WIN);
            c0.setMinWidth(TITLE_LEFT_PADDING_WIN);

            ColumnConstraints c1 = new ColumnConstraints();
            c1.setHgrow(Priority.ALWAYS);
            ColumnConstraints c2 = new ColumnConstraints();
            c2.setHgrow(Priority.NEVER);
            this.titlePane.getColumnConstraints().addAll(c0, c1, c2);

            this.titlePane.add(title, 1, 0);
            this.titlePane.add(buildWindowBtn(), 2, 0);
        }

        this.contentPane.add(titlePane, 0, 0);
    }

    private Pane buildWindowBtn() {
        if (Platforms.IS_MAC_OSX) {
            return buildMacBtn();
        }

        HBox btnHbox = new HBox();
        btnHbox.setAlignment(Pos.CENTER_LEFT);
        if (!Modality.APPLICATION_MODAL.equals(stage.getModality())) {
            minimizeBtn = new Button();
            minimizeBtn.getStyleClass().add(windowButtonClass);
            minimizeBtn.getStyleClass().add(minimizeBtnStyleClass);
            minimizeBtn.setPrefSize(WINDOW_BUTTON_WIDTH, WINDOW_BUTTON_WIDTH);
            minimizeBtn.setOnAction(event -> stage.setIconified(true));
            btnHbox.getChildren().add(minimizeBtn);
        }
        if (stage.isResizable()) {
            maximizeBtn = new Button();
            maximizeBtn.setPrefSize(WINDOW_BUTTON_WIDTH, WINDOW_BUTTON_WIDTH);
            maximizeBtn.getStyleClass().add(windowButtonClass);
            maximizeBtn.getStyleClass().add(maximizeBtnStyleClass);
            maximizeBtn.setOnAction(event -> controller.maximizePropertyProperty().set(!controller.maximizePropertyProperty().get()));
            btnHbox.getChildren().add(maximizeBtn);
        }
        closeBtn = new Button();
        closeBtn.setPrefSize(WINDOW_BUTTON_WIDTH, WINDOW_BUTTON_WIDTH);
        closeBtn.getStyleClass().add(WindowPane.windowButtonClass);
        closeBtn.getStyleClass().add(WindowPane.closeBtnStyleClass);
        closeBtn.setOnAction(event -> stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST)));
        btnHbox.getChildren().add(closeBtn);
        return btnHbox;
    }

    private Pane buildMacBtn() {

        HBox btnHbox = new HBox();
        btnHbox.setAlignment(Pos.CENTER_LEFT);
        btnHbox.setSpacing(8);
        final int btnPreSize = 12;
        closeBtn = new Button();
        closeBtn.setFocusTraversable(false);
        closeBtn.setPrefSize(btnPreSize, btnPreSize);
        closeBtn.getStyleClass().add(windowButtonClass);
        closeBtn.getStyleClass().add(closeBtnStyleClass);
        closeBtn.setOnAction(event -> stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST)));
        closeBtn.setOnMouseEntered(event -> btnHoverState(true));
        closeBtn.setOnMouseExited(event -> btnHoverState(false));
        btnHbox.getChildren().add(closeBtn);

        if (!Modality.APPLICATION_MODAL.equals(stage.getModality())) {
            minimizeBtn = new Button();
            minimizeBtn.setFocusTraversable(false);
            minimizeBtn.getStyleClass().add(windowButtonClass);
            minimizeBtn.getStyleClass().add(minimizeBtnStyleClass);
            minimizeBtn.setPrefSize(btnPreSize, btnPreSize);
            minimizeBtn.setOnAction(event -> stage.setIconified(true));

            minimizeBtn.setOnMouseEntered(event -> btnHoverState(true));
            minimizeBtn.setOnMouseExited(event -> btnHoverState(false));
            btnHbox.getChildren().add(minimizeBtn);
        }
        if (stage.isResizable()) {
            maximizeBtn = new Button();
            maximizeBtn.setFocusTraversable(false);
            maximizeBtn.setPrefSize(btnPreSize, btnPreSize);
            maximizeBtn.getStyleClass().add(windowButtonClass);
            maximizeBtn.getStyleClass().add(maximizeBtnStyleClass);
            maximizeBtn.setOnAction(event -> controller.maximizePropertyProperty().set(!controller.maximizePropertyProperty().get()));
            maximizeBtn.setOnMouseEntered(event -> btnHoverState(true));
            maximizeBtn.setOnMouseExited(event -> btnHoverState(false));
            btnHbox.getChildren().add(maximizeBtn);
        }

        return btnHbox;
    }

    private void btnHoverState(boolean hover) {
        closeBtn.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), hover);
        maximizeBtn.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), hover);
        minimizeBtn.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), hover);
    }

    private void initBodyPane() {
        if (bodyPane != null) {
            this.contentPane.add(bodyPane, 0, 1);
        }
    }

    public void shadowVisible(boolean value) {
        RowConstraints r0 = getRowConstraints().get(0);
        RowConstraints r2 = getRowConstraints().get(2);
        ColumnConstraints c0 = getColumnConstraints().get(0);
        ColumnConstraints c2 = getColumnConstraints().get(2);
        if (value) {
            r0.setPrefHeight(WindowPane.SHADOW_WIDTH);
            r0.setMinHeight(WindowPane.SHADOW_WIDTH);
            r2.setPrefHeight(WindowPane.SHADOW_WIDTH);
            r2.setMinHeight(WindowPane.SHADOW_WIDTH);
            c0.setPrefWidth(WindowPane.SHADOW_WIDTH);
            c0.setMinWidth(WindowPane.SHADOW_WIDTH);
            c2.setPrefWidth(WindowPane.SHADOW_WIDTH);
            c2.setMinWidth(WindowPane.SHADOW_WIDTH);
            this.setEffect(shadowEffect);
        } else {
            r0.setPrefHeight(0);
            r0.setMinHeight(0);
            r2.setPrefHeight(0);
            r2.setMinHeight(0);
            c0.setPrefWidth(0);
            c0.setMinWidth(0);
            c2.setPrefWidth(0);
            c2.setMinWidth(0);
            this.setEffect(null);
        }
    }


    public void setBodyPane(Pane bodyPane) {
        this.bodyPane = bodyPane;

    }

    public void initEvent() {
        if (this.titlePane != null) {
            controller.setStageDraggable();
        }
        if (stage.isResizable()) {
            controller.setStageResizable();
        }
    }

    public EventHandler getDragEvent() {
        return this.controller.getDragEvent();
    }

    public Stage getStage() {
        return stage;
    }

    public GridPane getTitlePane() {
        return titlePane;
    }

    public Button getMaximizeBtn() {
        return maximizeBtn;
    }

    public Button getMinimizeBtn() {
        return minimizeBtn;
    }

    public Button getCloseBtn() {
        return closeBtn;
    }

    public WindowPaneController getController() {
        return controller;
    }
}

class WindowPaneController {
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
        windowPane.getTitlePane().setOnMouseClicked(event -> {
            if (stage.isResizable() && event.getClickCount() > 1) {
                maximizeProperty.set(!maximizeProperty.get());
                event.consume();
            }
        });


        windowPane.getTitlePane().setOnMouseClicked(event -> {
            if (stage.isResizable() && event.getClickCount() > 1) {
                maximizeProperty.set(!maximizeProperty.get());
                event.consume();
            }
        });
        windowPane.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                initX = event.getScreenX();
                initY = event.getScreenY();
                event.consume();
            } else {
                initX = -1;
                initY = -1;
            }
        });

        windowPane.getTitlePane().setOnMouseDragged(getDragEvent());
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

