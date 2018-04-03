package com.dmsoft.firefly.plugin.chart;

import com.dmsoft.firefly.gui.components.chart.ChartSaveUtils;
import com.dmsoft.firefly.plugin.spc.charts.BoxPlotChart;
import com.dmsoft.firefly.plugin.spc.charts.data.BoxPlotChartData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IBoxAndWhiskerData;
import com.dmsoft.firefly.plugin.spc.charts.data.basic.IPoint;
import com.google.common.collect.Lists;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
import java.util.List;

/**
 * Created by cherry on 2018/4/2.
 */
public class BoxPlotChartApp extends Application {

    private Button saveBtn;
    private BoxPlotChart boxPlotChart;

    private Parent getContent() {
        VBox vBox = new VBox();
        List<BoxPlotChartData> boxPlotChartData = buildBoxPlotChartData();
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        boxPlotChart = new BoxPlotChart(xAxis, yAxis);
        boxPlotChart.setData(boxPlotChartData, null);
        saveBtn = new Button("Save as");
        this.initSaveBtnEvent();
        vBox.getChildren().add(saveBtn);
        vBox.getChildren().add(boxPlotChart);
        return vBox;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Scene scene = new Scene(getContent(), 415, 315);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/charts.css").toExternalForm());
        primaryStage.show();
    }

    private List<BoxPlotChartData> buildBoxPlotChartData() {

        List<BoxPlotChartData> data = Lists.newArrayList();
        Double x[] = new Double[]{2D, 3D, 4D, 5D, 6D, 7D, 8D, 9D};
        Double mean[] = new Double[]{28D, 29D, 27D, 28D, 30D, 36D, 35D, 42D};
        Double q3[] = new Double[] {26D, 30D, 24D, 26D, 28D, 36D, 30D, 40D};
        Double q1[] = new Double[] {30D, 38D, 30D, 36D, 38D, 30D, 18D, 50D};
        Double max[] = new Double[] {33D, 40D, 34D, 40D, 45D, 44D, 36D, 52D};
        Double min[] = new Double[] {22D, 20D, 22D, 24D, 25D, 28D, 16D, 36D};
        Double median[] = new Double[] {25D, 32D, 30D, 32D, 34D, 39D, 31D, 41D};

        BoxPlotChartData boxPlotChartData = new BoxPlotChartData() {
            @Override
            public IBoxAndWhiskerData getBoxAndWhiskerData() {
                return new IBoxAndWhiskerData() {
                    @Override
                    public Double getXPosByIndex(int index) {
                        return x[index];
                    }

                    @Override
                    public Double getMeanByIndex(int index) {
                        return mean[index];
                    }

                    @Override
                    public Double getMedianByIndex(int index) {
                        return median[index];
                    }

                    @Override
                    public Double getQ1ByIndex(int index) {
                        return q1[index];
                    }

                    @Override
                    public Double getQ3ByIndex(int index) {
                        return q3[index];
                    }

                    @Override
                    public Double getMinRegularValueByIndex(int index) {
                        return min[index];
                    }

                    @Override
                    public Double getMaxRegularValueByIndex(int index) {
                        return max[index];
                    }

                    @Override
                    public int getLen() {
                        return x.length;
                    }
                };
            }

            @Override
            public IPoint getPoints() {
                return null;
            }

            @Override
            public Color getColor() {
                return Color.BLUE;
            }

            @Override
            public String getUniqueKey() {
                return "box plot";
            }

            @Override
            public Double getXLowerBound() {
                return null;
            }

            @Override
            public Double getXUpperBound() {
                return null;
            }

            @Override
            public Double getYLowerBound() {
                return null;
            }

            @Override
            public Double getYUpperBound() {
                return null;
            }
        };
        data.add(boxPlotChartData);
        return data;
    }

    private void initSaveBtnEvent() {
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
                    WritableImage writableImage = boxPlotChart.snapshot(new SnapshotParameters(), null);

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

                    ChartSaveUtils.saveImageUsingJPGWithQuality(SwingFXUtils.fromFXImage(writableImage, null), file, 0.9f);
                    System.out.println(file.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
