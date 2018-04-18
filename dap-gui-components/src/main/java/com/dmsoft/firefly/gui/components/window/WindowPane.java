package com.dmsoft.firefly.gui.components.window;

import com.dmsoft.bamboo.common.utils.base.Platforms;
import com.dmsoft.firefly.gui.components.utils.ImageUtils;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.HPos;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class WindowPane extends GridPane {
    public static final int WINDOW_MODEL_FULL = 1;
    public static final int WINDOW_MODEL_X = 2;
    public static final int WINDOW_MODEL_NONE = 3;

    protected static final int TITLE_LEFT_PADDING_WIN = 10;
    protected static final int TITLE_LEFT_PADDING_MAC = 15;
    protected static final int STANDARD_MARGIN = 10;
    protected static final int SHADOW_WIDTH = 10;
    protected static final int BORDER_TITLE_HEIGHT = 28;
    protected static final int DRAG_PADDING = 10;
    protected static final int WINDOW_BUTTON_WIDTH = 28;
    protected static final int WINDOW_TITLE_HEIGHT = 28;
    protected static String windowButtonClass = "";
    protected static String minimizeBtnStyleClass = "";
    protected static String maximizeBtnStyleClass = "";
    protected static String restoreBtnStyleClass = "";
    protected static String closeBtnStyleClass = "";

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

    private Stage stage;
    private GridPane titlePane;
    private Pane bodyPane;
    private GridPane contentPane;
    private Button minimizeBtn;
    private Button maximizeBtn;
    private Button closeBtn;
    private Object cusTitle;
    private WindowPaneController controller;
    private Effect shadowEffect = new DropShadow(BlurType.TWO_PASS_BOX, new Color(0, 0, 0, 0.2),
            10, 0, 0, 0);
    private int WINDOW_MODEL = WINDOW_MODEL_FULL;

    public WindowPane(Stage stage, String title, Pane body) {
        this.stage = stage;
        this.bodyPane = body;
        this.cusTitle = title;
    }

    public WindowPane(Stage stage, Pane title, Pane body) {
        this.stage = stage;
        this.bodyPane = body;
        this.cusTitle = title;
    }

    public WindowPane(Pane title, Pane body) {
        this(null, title, body);
    }

    public WindowPane(String title, Pane body) {
        this(null, title, body);
    }


    public void setWindowsModel(int model) {
        WINDOW_MODEL = model;
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
        r0.setPrefHeight(WINDOW_TITLE_HEIGHT);
        r0.setMaxHeight(WINDOW_TITLE_HEIGHT);
        r0.setMinHeight(WINDOW_TITLE_HEIGHT);
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
        r0.setPrefHeight(WINDOW_TITLE_HEIGHT);
        r0.setMaxHeight(WINDOW_TITLE_HEIGHT);
        r0.setMinHeight(WINDOW_TITLE_HEIGHT);
        this.titlePane.getRowConstraints().add(r0);

        ColumnConstraints c0 = new ColumnConstraints();
        c0.setHgrow(Priority.NEVER);

        if (Platforms.IS_MAC_OSX) {
            if (WINDOW_MODEL != WINDOW_MODEL_NONE) {
                c0.setPrefWidth(TITLE_LEFT_PADDING_MAC);
                c0.setMaxWidth(TITLE_LEFT_PADDING_MAC);
                c0.setMinWidth(TITLE_LEFT_PADDING_MAC);
            }

            ColumnConstraints c1 = new ColumnConstraints();
            c1.setHgrow(Priority.NEVER);
            ColumnConstraints c2 = new ColumnConstraints();
            c2.setHgrow(Priority.NEVER);
            c2.setPrefWidth(STANDARD_MARGIN);
            c2.setMaxWidth(STANDARD_MARGIN);
            c2.setMinWidth(STANDARD_MARGIN);

            ColumnConstraints c3 = new ColumnConstraints();
            c3.setHgrow(Priority.ALWAYS);
            this.titlePane.getColumnConstraints().addAll(c0, c1, c2, c3);

            this.titlePane.add(buildWindowBtn(), 1, 0);
            this.titlePane.add(title, 3, 0);

        } else {
            if (WINDOW_MODEL != WINDOW_MODEL_NONE) {
                c0.setPrefWidth(TITLE_LEFT_PADDING_WIN);
                c0.setMaxWidth(TITLE_LEFT_PADDING_WIN);
                c0.setMinWidth(TITLE_LEFT_PADDING_WIN);
            }

            ColumnConstraints c1 = new ColumnConstraints();
            c1.setHgrow(Priority.ALWAYS);
            ColumnConstraints c2 = new ColumnConstraints();
            c2.setHgrow(Priority.NEVER);
            c2.setPrefWidth(STANDARD_MARGIN);
            c2.setMinWidth(STANDARD_MARGIN);

            ColumnConstraints c3 = new ColumnConstraints();
            c3.setHgrow(Priority.NEVER);

            this.titlePane.getColumnConstraints().addAll(c0, c1, c2, c3);

            this.titlePane.add(title, 1, 0);
            this.titlePane.add(buildWindowBtn(), 3, 0);
        }

        this.contentPane.add(titlePane, 0, 0);
    }

    private Pane buildWindowBtn() {
        if (Platforms.IS_MAC_OSX) {
            return buildMacBtn();
        }

        HBox btnHbox = new HBox();
        btnHbox.setAlignment(Pos.CENTER_LEFT);
        if (!Modality.APPLICATION_MODAL.equals(stage.getModality()) && (WINDOW_MODEL == WINDOW_MODEL_FULL)) {
            minimizeBtn = new Button();
            minimizeBtn.getStyleClass().add(windowButtonClass);
            minimizeBtn.getStyleClass().add(minimizeBtnStyleClass);
            minimizeBtn.setPrefSize(WINDOW_BUTTON_WIDTH, WINDOW_BUTTON_WIDTH);
            minimizeBtn.setOnAction(event -> stage.setIconified(true));
            btnHbox.getChildren().add(minimizeBtn);
        }
        if (stage.isResizable() && (WINDOW_MODEL == WINDOW_MODEL_FULL)) {
            maximizeBtn = new Button();
            maximizeBtn.setPrefSize(WINDOW_BUTTON_WIDTH, WINDOW_BUTTON_WIDTH);
            maximizeBtn.getStyleClass().add(windowButtonClass);
            maximizeBtn.getStyleClass().add(maximizeBtnStyleClass);
            maximizeBtn.setOnAction(event -> controller.maximizePropertyProperty().set(!controller.maximizePropertyProperty().get()));
            btnHbox.getChildren().add(maximizeBtn);
        }
        if (WINDOW_MODEL != WINDOW_MODEL_NONE) {
            closeBtn = new Button();
            closeBtn.setPrefSize(WINDOW_BUTTON_WIDTH, WINDOW_BUTTON_WIDTH);
            closeBtn.getStyleClass().add(WindowPane.windowButtonClass);
            closeBtn.getStyleClass().add(WindowPane.closeBtnStyleClass);
            closeBtn.setOnAction(event -> stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST)));
            btnHbox.getChildren().add(closeBtn);
        }

        return btnHbox;
    }

    private Pane buildMacBtn() {

        HBox btnHbox = new HBox();
        btnHbox.setAlignment(Pos.CENTER_LEFT);
        btnHbox.setSpacing(8);

        final int btnPreSize = 12;
        if (WINDOW_MODEL != WINDOW_MODEL_NONE) {
            closeBtn = new Button();
            closeBtn.setFocusTraversable(false);
            closeBtn.setPrefSize(btnPreSize, btnPreSize);
            closeBtn.getStyleClass().add(windowButtonClass);
            closeBtn.getStyleClass().add(closeBtnStyleClass);
            closeBtn.setOnAction(event -> stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST)));
            closeBtn.setOnMouseEntered(event -> btnHoverState(true));
            closeBtn.setOnMouseExited(event -> btnHoverState(false));
            btnHbox.getChildren().add(closeBtn);
        }

        if (!Modality.APPLICATION_MODAL.equals(stage.getModality()) && (WINDOW_MODEL == WINDOW_MODEL_FULL)) {
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
        if (stage.isResizable() && (WINDOW_MODEL == WINDOW_MODEL_FULL)) {
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
        if (maximizeBtn != null) {
            maximizeBtn.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), hover);
        }
        if (minimizeBtn != null) {
            minimizeBtn.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), hover);
        }
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

    public void init() {
        initContentPane();
        if (cusTitle instanceof String) {
            Label label = new Label((String) cusTitle);
            label.setStyle("-fx-border-insets: 0 0 0 10");
            label.setStyle("-fx-background-insets: 0 0 0 10;-fx-font-weight: bold");
            initTitlePane(label);
        } else {
            if (Platforms.IS_WINDOWS) {
                GridPane gridPane = new GridPane();
                RowConstraints r0 = new RowConstraints(WINDOW_TITLE_HEIGHT, WINDOW_TITLE_HEIGHT, WINDOW_TITLE_HEIGHT);
                ColumnConstraints c0 = new ColumnConstraints(27, 27, 27);
                c0.setHalignment(HPos.LEFT);
                ColumnConstraints c1 = new ColumnConstraints();
                c1.setHgrow(Priority.ALWAYS);
                gridPane.getRowConstraints().add(r0);
                gridPane.getColumnConstraints().addAll(c0, c1);
                ImageView logo = ImageUtils.getImageView(getClass().getResourceAsStream("/images/top_title_logo.png"));
                gridPane.add(logo, 0, 0);
                gridPane.add((Pane) cusTitle, 1, 0);
                initTitlePane(gridPane);
            } else {
                initTitlePane((Pane) cusTitle);
            }
        }

        initBodyPane();

        this.contentPane.setStyle("-fx-background-color: white");
        this.setStyle("-fx-background-color: transparent");

        controller = new WindowPaneController(this);
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

    public void setStage(Stage stage) {
        this.stage = stage;
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



