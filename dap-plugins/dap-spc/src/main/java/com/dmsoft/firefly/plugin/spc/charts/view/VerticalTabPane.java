package com.dmsoft.firefly.plugin.spc.charts.view;

import com.dmsoft.firefly.gui.components.pane.ContentStackPane;
import com.dmsoft.firefly.plugin.spc.utils.SpcFxmlAndLanguageUtils;
import com.dmsoft.firefly.plugin.spc.utils.UIConstant;
import com.google.common.collect.Maps;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

import java.util.Map;

/**
 * Created by cherry on 2018/3/6.
 */
public class VerticalTabPane extends AnchorPane {

    private VBox leftBox;
    private ContentStackPane contentPane;
    private Map<Integer, String> nodeIdMap = Maps.newHashMap();

    private final double LEFT_WIDTH = 100;
    private final double RIGHT_WIDTH = 450;
    private final double HEIGHT = 278;
    private final double BUTTON_HEIGHT = 20;

    private final String BORDER_COLOR = "#e7e7e7";
    private final String BACKGROUND_COLOR = "#f0f0f0";

    public VerticalTabPane() {
        this.initComponents();
        this.initComponentsRender();
        this.initEvent();
    }

    public void initComponents() {

        VBox box = new VBox();
        leftBox = new VBox();
        ndcBtn = new Button(SPC_CHART_NAME[0]);
        runBtn = new Button(SPC_CHART_NAME[1]);
        xBarBtn = new Button(SPC_CHART_NAME[2]);
        rangeBtn = new Button(SPC_CHART_NAME[3]);
        sdBtn = new Button(SPC_CHART_NAME[4]);
        medianBtn = new Button(SPC_CHART_NAME[5]);
        boxBtn = new Button(SPC_CHART_NAME[6]);
        mrBtn = new Button(SPC_CHART_NAME[7]);
        leftBox.getChildren().add(ndcBtn);
        leftBox.getChildren().add(runBtn);
        leftBox.getChildren().add(xBarBtn);
        leftBox.getChildren().add(rangeBtn);
        leftBox.getChildren().add(sdBtn);
        leftBox.getChildren().add(medianBtn);
        leftBox.getChildren().add(boxBtn);
        leftBox.getChildren().add(mrBtn);
        leftBox.getChildren().add(box);

        contentPane = new ContentStackPane();
        this.getChildren().addAll(leftBox, contentPane);
        AnchorPane.setBottomAnchor(leftBox, 0.1);
        AnchorPane.setTopAnchor(leftBox, 0.1);
        AnchorPane.setBottomAnchor(contentPane, 0.1);
        AnchorPane.setRightAnchor(contentPane, 0.1);
        AnchorPane.setTopAnchor(contentPane, 0.1);
        AnchorPane.setLeftAnchor(contentPane, LEFT_WIDTH);
    }

    public void addNode(Node node, int index) {
        if (node != null && node.getId() != null) {
            contentPane.add(node);
            nodeIdMap.put(index, node.getId());
        }
    }

    public void activeTabByIndex(int index) {
        if (index < 0 || !nodeIdMap.keySet().contains(index)) {
            return;
        } else {
            contentPane.navTo(nodeIdMap.get(index));
            Node node = leftBox.getChildren().get(index);
            if (node instanceof Button) {
                node.getStyleClass().removeAll("btn-tab-active");
                node.getStyleClass().add("btn-tab-active");
            }
        }
    }

    public void initComponentsRender() {
        leftBox.setPrefWidth(LEFT_WIDTH);
        leftBox.setMinWidth(LEFT_WIDTH);
        leftBox.setMaxWidth(LEFT_WIDTH);
        contentPane.setPrefWidth(RIGHT_WIDTH);
        leftBox.setPrefHeight(HEIGHT);
        leftBox.setStyle("-fx-background-color: " + BACKGROUND_COLOR);
        for (int i = 0; i < leftBox.getChildren().size(); i++) {
            Node node = leftBox.getChildren().get(i);
            if (node instanceof Button) {
                setButtonStyle((Button) node);
            }
            if (node instanceof VBox) {
                setBoxStyle((VBox) node);
            }
        }
    }

    public void initEvent() {
        for (int i = 0; i < leftBox.getChildren().size(); i++) {
            Node node = leftBox.getChildren().get(i);
            if (node instanceof Button) {
                leftBox.getChildren().get(i).setOnMouseClicked(buttonClickEvent(i));
            }
        }
    }

    private void setButtonStyle(Button button) {

        button.getStyleClass().removeAll("btn-tab");
        button.getStyleClass().add("btn-tab");
        button.setPrefWidth(LEFT_WIDTH);
        button.setMinWidth(LEFT_WIDTH);
        button.setMaxHeight(LEFT_WIDTH);
        button.setPrefHeight(BUTTON_HEIGHT);
        button.setMinHeight(BUTTON_HEIGHT);
        button.setMaxHeight(BUTTON_HEIGHT);
    }

    private void setBoxStyle(VBox box) {
        box.setPrefHeight(500);
        box.setStyle("-fx-border-width: 0px 1px 0px 0px; -fx-border-color: " + BORDER_COLOR);
    }

    private EventHandler buttonClickEvent(int index) {
        return event -> {
            setCurrentActiveStyle(index);
            switchContentPane(index);
        };
    }

    private void switchContentPane(int curPageIndex) {
        Node node = leftBox.getChildren().get(curPageIndex);
        if (node instanceof Button) {
            Button button = (Button) node;
            String text = button.getText();
            if (SPC_CHART_NAME[0].equals(text)) {
                contentPane.navTo(nodeIdMap.get(0));
            } else if (SPC_CHART_NAME[1].equals(text)) {
                contentPane.navTo(nodeIdMap.get(1));
            } else if (SPC_CHART_NAME[2].equals(text)) {
                contentPane.navTo(nodeIdMap.get(2));
            } else if (SPC_CHART_NAME[3].equals(text)) {
                contentPane.navTo(nodeIdMap.get(3));
            } else if (SPC_CHART_NAME[4].equals(text)) {
                contentPane.navTo(nodeIdMap.get(4));
            } else if (SPC_CHART_NAME[5].equals(text)) {
                contentPane.navTo(nodeIdMap.get(5));
            } else if (SPC_CHART_NAME[6].equals(text)) {
                contentPane.navTo(nodeIdMap.get(6));
            } else if (SPC_CHART_NAME[7].equals(text)) {
                contentPane.navTo(nodeIdMap.get(7));
            }
        }
    }

    private void setCurrentActiveStyle(int curPageIndex) {
        for (int i = 0; i < leftBox.getChildren().size(); i++) {
            if (curPageIndex == i) {
                leftBox.getChildren().get(i).getStyleClass().removeAll("btn-tab-active");
                leftBox.getChildren().get(i).getStyleClass().add("btn-tab-active");
            } else {
                leftBox.getChildren().get(i).getStyleClass().removeAll("btn-tab-active");
            }
        }
    }

    private String[] SPC_CHART_NAME = new String[]{
            SpcFxmlAndLanguageUtils.getString("SPC_CHART_NDC"),
            SpcFxmlAndLanguageUtils.getString("SPC_CHART_RUN"),
            SpcFxmlAndLanguageUtils.getString("SPC_CHART_BAR"),
            SpcFxmlAndLanguageUtils.getString("SPC_CHART_RANGE"),
            SpcFxmlAndLanguageUtils.getString("SPC_CHART_SD"),
            SpcFxmlAndLanguageUtils.getString("SPC_CHART_MEDIAN"),
            SpcFxmlAndLanguageUtils.getString("SPC_CHART_BOX"),
            SpcFxmlAndLanguageUtils.getString("SPC_CHART_MR")};

    private Button ndcBtn;
    private Button runBtn;
    private Button xBarBtn;
    private Button rangeBtn;
    private Button sdBtn;
    private Button medianBtn;
    private Button boxBtn;
    private Button mrBtn;
}
