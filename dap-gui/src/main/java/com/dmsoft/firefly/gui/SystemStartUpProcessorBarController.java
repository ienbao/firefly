package com.dmsoft.firefly.gui;

import com.dmsoft.firefly.gui.components.utils.StageMap;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;

import javax.swing.*;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * Created by QiangChen on 2017/4/8.
 */

public class SystemStartUpProcessorBarController {
    public static final int TOTAL_LOAD_CLASS = 11400;
    private static final String BGPATH = "/images/initialize_bg.png";
    private static final String LOGOPATH = "/images/initialize_logo.png";

    @FXML
    private ProgressBar progrossBar;

    @FXML
    private ImageView imageViewLogo;
    private boolean loadDone = false;

    @FXML
    private void initialize() {
        imageViewLogo.setImage(new Image(LOGOPATH));
        System.out.println("init");
    }

    public void updateProcessorBar() {
        Service<Integer> service = new Service<Integer>() {

            @Override
            protected Task<Integer> createTask() {
                return new Task<Integer>() {

                    @Override
                    protected Integer call() throws Exception {
                       /* int i = 0;
                        while (i++ < 100) {
                            updateProgress(i, 100);
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }*/
                        while (!isLoadDone()) {
                            Thread.sleep(500);
                            ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
                            updateProgress((int) (classLoadingMXBean.getLoadedClassCount() * 1.0 / TOTAL_LOAD_CLASS * 100), 100);
                        }
                        return null;
                    };
                };
            }

        };
        progrossBar.setProgress(0);
        progrossBar.progressProperty().bind(service.progressProperty());
        /*if (progrossBar.getProgress() == 100) {
            StageMap.closeStage("template");
        }*/
        service.restart();
    }

    public ProgressBar getProgrossBar() {
        return progrossBar;
    }

    public void setProgrossBar(ProgressBar progrossBar) {
        this.progrossBar = progrossBar;
    }

    /* *//**
     * Used to update the login progress.
     *//*
    public void updateProcessorBar() {
        final SwingWorker swingWorker = new SwingWorker<Void, Object>() {

            @Override
            protected Void doInBackground() throws Exception {

                try {
                    while (!isLoadDone()) {
                        Thread.sleep(500);
                        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
                        publish((int) (classLoadingMXBean.getLoadedClassCount() * 1.0 / TOTAL_LOAD_CLASS * 100));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void process(List<Object> chunks) {
               // if (systemStartUpProcessorBar != null) {
                progrossBar.setProgress(Double.valueOf((int) chunks.get(0)));
                    if ((int) chunks.get(0) >= 100) {
                        setLoadDone(true);
                        progrossBar.setProgress(100);
                    } else {
                    setLoadDone(true);
                }
            }

            @Override
            protected void done() {
                super.done();
            }
        };
        swingWorker.execute();
    }*/


    public boolean isLoadDone() {
        return loadDone;
    }

    public void setLoadDone(boolean loadDone) {
        this.loadDone = loadDone;
    }


    /*    Task task = new Task<Void>() {
            @Override public Void call() {
                static final int max = 1000000;
                for (int i=1; i<=max; i++) {
                    if (isCancelled()) {
                        break;
                    }
                    updateProgress(i, max);
                }
                return null;
            }
        };
        ProgressBar bar = new ProgressBar();
    bar.progressProperty().bind(task.progressProperty());
    new Thread(task).start();*/


    /*Task<Integer> task = new Task<Integer>() {
        @Override protected Integer call() throws Exception {
            int iterations = 0;
            for (iterations = 0; iterations < 100000; iterations++) {
                if (isCancelled()) {
                    break;
                }
                System.out.println("Iteration " + iterations);
            }
            return iterations;
        }

        @Override protected void succeeded() {
            super.succeeded();
            updateMessage("Done!");
        }

        @Override protected void cancelled() {
            super.cancelled();
            updateMessage("Cancelled!");
        }

        @Override protected void failed() {
            super.failed();
            updateMessage("Failed!");
        }
    };*/
}