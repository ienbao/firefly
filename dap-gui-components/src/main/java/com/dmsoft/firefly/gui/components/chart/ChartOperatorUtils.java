package com.dmsoft.firefly.gui.components.chart;

import javafx.scene.chart.NumberAxis;

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
}
