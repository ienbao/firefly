package com.dmsoft.firefly.gui.components.chart;

import javafx.scene.chart.NumberAxis;
import javafx.util.StringConverter;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;
import java.util.function.Function;

/**
 * Created by cherry on 2018/3/27.
 */
public class ChartOperatorUtils {

    /**
     * Save images for jpg formatter
     *
     * @param image    buffered image
     * @param filePath filePath
     * @param quality  quality
     * @throws Exception IOException
     */
    public static void saveImageUsingJPGWithQuality(BufferedImage image,
                                                    File filePath, float quality) throws Exception {

        BufferedImage newBufferedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        newBufferedImage.getGraphics().drawImage(image, 0, 0, null);
        Iterator iterator = ImageIO.getImageWritersByFormatName("jpeg");
        ImageWriter imageWriter = (ImageWriter) iterator.next();
        ImageWriteParam iwp = imageWriter.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(quality);

        FileImageOutputStream fileImageOutput = new FileImageOutputStream(filePath);
        imageWriter.setOutput(fileImageOutput);
        IIOImage jpgImage = new IIOImage(newBufferedImage, null, null);
        imageWriter.write(null, jpgImage, iwp);
        imageWriter.dispose();
    }

    /**
     * Update axis tick unit
     *
     * @param axis chart number axis
     */
    public static void updateAxisTickUnit(NumberAxis axis) {
        double size = Math.abs(axis.getUpperBound() - axis.getLowerBound());
        double tick = Math.ceil(size / 10);
        if (tick > 100) {
            tick = tick - (tick % 100);
        } else if (tick > 10) {
            tick = tick - (tick % 10);
        } else if (tick > 5) {
            tick = tick - (tick % 5);
        } else if (tick > 2) {
            tick = tick - (tick % 2);
        }
        axis.setTickUnit(tick);
    }

    /**
     * Set tick label formatter
     *
     * @param axis chart number axis
     */
    public static void setTickLabelFormatter(NumberAxis axis) {

        axis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                Function<Double, String> convertData = buildConvertDataFunc();
                return (object instanceof Double) ? convertData.apply((Double) object) : String.valueOf(object);
            }

            @Override
            public Number fromString(String string) {
                return 0D;
            }
        });
    }

    private static Function<Double, String> buildConvertDataFunc() {
        return aDouble -> {
            String value = aDouble == null ? "" : String.valueOf(aDouble);
            return value;
        };
    }
}
