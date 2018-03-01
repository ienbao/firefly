package com.dmsoft.firefly.gui.controller;

import com.dmsoft.firefly.gui.components.messagetip.MessageTipFactory;
import com.dmsoft.firefly.gui.components.utils.NodeMap;
import com.dmsoft.firefly.sdk.message.IMessageManager;
import com.dmsoft.firefly.sdk.message.MessageTipTyp;
import com.google.common.collect.Lists;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Popup;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MessageController implements IMessageManager{

    private List<Popup> popupList = Lists.newLinkedList();
    private List<Double> popupHeightList = Lists.newLinkedList();
    private Timer timer = new Timer();
    private AtomicBoolean isShow = new AtomicBoolean(false);

    public MessageController() {
        timer.schedule(new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (!isShow.get()) {
                            if (popupHeightList.size() > 0) {
                                popupHeightList.remove(popupHeightList.size() - 1);
                            }

                            if (popupList.size() > 0) {
                                int dd = popupList.size() - 1;
                                popupList.get(dd).hide();
                                popupList.remove(dd);
                            }
                        }
                    }

                });
            }
        }, 2000, 3000);
    }

    @Override
    public void addMsg(String title, String msg, MessageTipTyp type) {
        if (type != null) {
            switch (type) {
                case SUCCESS:
                    initSuccessTip(title, msg);
                    break;
                case  WARN:
                    initWarnTip(title, msg);
                    break;
                case  INFO:
                    initNormalTip(title, msg);
                    break;
                default :
                    initNormalTip(title, msg);
                    break;
            }
        }
    }

    private void initSuccessTip(String title, String msg) {
       Popup popup = MessageTipFactory.getSuccessTip(title, msg);
        doPopup(popup, msg);
    }

    private void initWarnTip(String title, String msg) {
        Popup popup = MessageTipFactory.getWarnTip(title, msg);
        doPopup(popup, msg);
    }

    private void initNormalTip(String title, String msg) {
        Popup  popup = MessageTipFactory.getNormalTip(title, msg);
        doPopup(popup, msg);
    }

    private void doPopup(Popup popup, String msg) {
        isShow = new AtomicBoolean(false);
        GridPane pane = MessageTipFactory.getMessageTipPane();
        Double preHeight = 0.0;
        if (StringUtils.isNotBlank(msg)) {
            Double length = Double.valueOf(msg.getBytes().length);
            preHeight = Math.ceil((double) (length/30.0)) * 16 + 40;
            popupHeightList.add(preHeight);
        }
        Double exPreHeight = 0.0;
        System.out.println("----------99999--------------" + popupHeightList.size());

        for (Double d : popupHeightList) {
            exPreHeight += d;
        }
        System.out.println("----------00000--------------" + exPreHeight);

        setLocation(popup, pane, exPreHeight);
        pane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            System.out.println("cl");
            if (popupHeightList.size() > 0) {
                popupHeightList.remove(popupHeightList.size() - 1);
            }
            popup.hide();
            popupList.remove(popup);
        });

        pane.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            System.out.println("enter");
            isShow.set(true);
        });

        pane.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            System.out.println("ex");
            isShow.set(false);
        });
    }

    private void setLocation(Popup popup, GridPane pane, Double preHeight) {
        Node node  = NodeMap.getNode("platform_main");
        popupList.add(popup);
        double screenX = node.getScene().getWindow().getX() + node.getBoundsInLocal().getMaxX() - 240;
        double screenY = node.getScene().getWindow().getY() + node.getBoundsInLocal().getMaxY() - preHeight - 12;
        popup.show(NodeMap.getNode("platform_main"), screenX, screenY);
    }

}
