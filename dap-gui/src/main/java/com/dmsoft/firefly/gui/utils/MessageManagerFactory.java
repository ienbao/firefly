package com.dmsoft.firefly.gui.utils;

import com.dmsoft.firefly.gui.components.messagetip.MessageTipFactory;
import com.dmsoft.firefly.gui.components.utils.NodeMap;
import com.dmsoft.firefly.sdk.message.IMessageManager;
import com.dmsoft.firefly.sdk.message.MessageTipTyp;
import com.google.common.collect.Lists;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class MessageManagerFactory implements IMessageManager{

    private List<Popup> popupList = Lists.newLinkedList();
    private List<Double> popupHeightList = Lists.newLinkedList();

    public MessageManagerFactory() {
    }

    @Override
    public void showMsg(String title, String msg, MessageTipTyp type) {
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

    @Override
    public void showSuccessMsg(String title, String msg) {
        initSuccessTip(title, msg);
    }

    @Override
    public void showWarnMsg(String title, String msg) {
        initWarnTip(title, msg);
    }

    @Override
    public void showInfoMsg(String title, String msg) {
        initNormalTip(title, msg);
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
        AtomicBoolean isShow = new AtomicBoolean(false);
        Pane pane = MessageTipFactory.getMessageTipPane();

        initEvent(pane, popup, isShow);

        setLocation(popup, msg);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (!isShow.get()) {
                            if (popupHeightList.size() > 0) {
                                popupHeightList.remove(0);
                            }

                            if (popupList.size() > 0) {
                                popup.hide();
                                popupList.remove(popup);
                                timer.cancel();
                            }
                        }
                    }

                });
            }
        }, 3000, 3000);
    }

    private void initEvent(Pane pane, Popup popup, AtomicBoolean isShow) {

        pane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (popupHeightList.size() > 0) {
                popupHeightList.remove(popupHeightList.size() - 1);
            }
            popup.hide();
            popupList.remove(popup);
        });

        pane.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            isShow.set(true);
        });

        pane.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            isShow.set(false);
        });
    }

    private void setLocation(Popup popup, String msg) {
        if (StringUtils.isNotBlank(msg)) {
            Double length = Double.valueOf(msg.getBytes().length);
            Double preHeight = Math.ceil((double) (length/30.0)) * 16 + 40;
            popupHeightList.add(preHeight);
        }
        Double exPreHeight = 0.0;
        for (Double d : popupHeightList) {
            exPreHeight += d;
        }

        Node node  = NodeMap.getNode("platform_main");
        popupList.add(popup);
        double screenX = node.getScene().getWindow().getX() + node.getBoundsInLocal().getMaxX() - 240;
        double screenY = node.getScene().getWindow().getY() + node.getBoundsInLocal().getMaxY() - exPreHeight - 12;
        popup.show(NodeMap.getNode("platform_main"), screenX, screenY);
    }


}
