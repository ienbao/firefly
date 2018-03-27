package com.dmsoft.firefly.gui.components.chart;

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
public class ChartSaveUtils {

    public static void saveImageUsingJPGWithQuality(BufferedImage image,
                                                     File filePath, float quality) throws Exception {

        BufferedImage newBufferedImage = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        newBufferedImage.getGraphics().drawImage(image, 0, 0, null);

        Iterator iter = ImageIO
                .getImageWritersByFormatName("jpeg");

        ImageWriter imageWriter = (ImageWriter) iter.next();
        ImageWriteParam iwp = imageWriter.getDefaultWriteParam();

        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(quality);

        FileImageOutputStream fileImageOutput = new FileImageOutputStream(filePath);
        imageWriter.setOutput(fileImageOutput);
        IIOImage jpgimage = new IIOImage(newBufferedImage, null, null);
        imageWriter.write(null, jpgimage, iwp);
        imageWriter.dispose();
    }
}
