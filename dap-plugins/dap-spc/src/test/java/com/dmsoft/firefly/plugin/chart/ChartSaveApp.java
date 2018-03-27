package com.dmsoft.firefly.plugin.chart;

import com.dmsoft.firefly.gui.components.chart.ChartSaveUtils;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

/**
 * Created by cherry on 2018/3/27.
 */
public class ChartSaveApp extends Application {

    private XYChart chart;

    public Parent createContent() {
        NumberAxis xAxis = new NumberAxis("X-Axis", 0d, 8.0d, 1.0d);
        NumberAxis yAxis = new NumberAxis("Y-Axis", 0.0d, 5.0d, 1.0d);
        ObservableList<XYChart.Series> data = FXCollections.observableArrayList(
                new ScatterChart.Series("Series 1", FXCollections.<ScatterChart.Data>observableArrayList(
                        new XYChart.Data(0.2, 3.5),
                        new XYChart.Data(0.7, 4.6),
                        new XYChart.Data(1.8, 1.7),
                        new XYChart.Data(2.1, 2.8),
                        new XYChart.Data(4.0, 2.2),
                        new XYChart.Data(4.1, 2.6),
                        new XYChart.Data(4.5, 2.0),
                        new XYChart.Data(6.0, 3.0),
                        new XYChart.Data(7.0, 2.0),
                        new XYChart.Data(7.8, 4.0)
                ))
        );
        chart = new ScatterChart(xAxis, yAxis, data);
        return chart;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        VBox pane = new VBox();
        Button saveBtn = new Button("Save as");
        pane.getChildren().add(saveBtn);
        pane.getChildren().add(createContent());
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/charts.css").toExternalForm());
        primaryStage.show();

        saveBtn.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save as spc chart");
            fileChooser.setInitialDirectory(
                    new File(System.getProperty("user.home"))
            );
            fileChooser.setInitialFileName("sss");
            FileChooser.ExtensionFilter pdfExtensionFilter = new FileChooser.ExtensionFilter("PNG - Portable Network Graphics (.png)", "*.png");
            fileChooser.getExtensionFilters().add(pdfExtensionFilter);
            fileChooser.setSelectedExtensionFilter(pdfExtensionFilter);
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                try {
                    String name = file.getName();
                    String imagePath = file.getAbsolutePath();
                    file = new File(imagePath);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    WritableImage writableImage = chart.snapshot(new SnapshotParameters(), null);
//                    ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);

                    float quality = 0.9f;
                    BufferedImage image = SwingFXUtils.fromFXImage(writableImage, null);
                    BufferedImage newBufferedImage = new BufferedImage(image.getWidth(),
                            image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                    newBufferedImage.getGraphics().drawImage(image, 0, 0, null);

                    Iterator iter = ImageIO
                            .getImageWritersByFormatName("jpeg");

                    ImageWriter imageWriter = (ImageWriter) iter.next();
                    ImageWriteParam iwp = imageWriter.getDefaultWriteParam();

                    iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    iwp.setCompressionQuality(quality);

                    FileImageOutputStream fileImageOutput = new FileImageOutputStream(file);
                    imageWriter.setOutput(fileImageOutput);
                    IIOImage jpgimage = new IIOImage(newBufferedImage, null, null);
                    imageWriter.write(null, jpgimage, iwp);
                    imageWriter.dispose();

//                    ChartSaveUtils.saveImageUsingJPGWithQuality(SwingFXUtils.fromFXImage(writableImage, null), file, 0.9f);
                    System.out.println(file.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
