package com.dmsoft.firefly.gui.components.chart;

import com.google.common.collect.Maps;
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
import java.util.Map;
import java.util.function.Function;

/**
 * Created by cherry on 2018/3/27.
 */
public class ChartOperatorUtils {

    public static final String KEY_MAX = "Max";
    public static final String KEY_MIN = "Min";
    public static final String KEY_CORNUMBER = "CorNumber";

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
        fileImageOutput.close();
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

    public static Map<String, Object> getAdjustAxisRangeData(double corMax, double corMin, int corNumber) {
        //        （1）先算出平均步长：
        corNumber = corNumber == 0 ? 1 : corNumber;
        double corStep = (corMax - corMin) / corNumber;
//        （2）然后算出步长的数量级：
        double temp = Math.pow(10, (int) (Math.log(corStep) / Math.log(10))) == corStep
                ? Math.pow(10, (int) (Math.log(corStep) / Math.log(10)))
                : Math.pow(10, ((int) (Math.log(corStep) / Math.log(10)) + 1));
//        （3）将步长归一化：
        double tmpStep = corStep / temp;
        if (tmpStep >= 0 && tmpStep <= 0.1) {
            tmpStep = 0.1;
        } else if (tmpStep >= 0.100001 && tmpStep <= 0.2) {
            tmpStep = 0.2;
        } else if (tmpStep >= 0.200001 && tmpStep <= 0.25) {
            tmpStep = 0.25;
        } else if (tmpStep >= 0.250001 && tmpStep <= 0.5) {
            tmpStep = 0.5;
        } else {
            tmpStep = 1;
        }
//        （4）把规范步长恢复本来数值：
        tmpStep = tmpStep * temp;
//        （5）坐标轴max数值与min数值调整：
        if ((int) (corMin / tmpStep) != (corMin / tmpStep)) {
            if (corMin < 0) {
                corMin = (-1) * Math.ceil(Math.abs(corMin / tmpStep)) * tmpStep;
            } else {
                corMin = (int) (Math.abs(corMin / tmpStep)) * tmpStep;
            }
        }
        corMax = (int) (corMax / tmpStep + 1) * tmpStep;
//        （6）最后求新的corNumber、max、min：
        double tmpNumber = (corMax - corMin) / tmpStep;
        if (tmpNumber < corNumber) {
            double extraNumber = corNumber - tmpNumber;
            tmpNumber = corNumber;
            if (extraNumber % 2 == 0) {
                corMax = corMax + tmpStep * (int) (extraNumber / 2);
            } else {
                corMax = corMax + tmpStep * (int) (extraNumber / 2 + 1);
            }
            corMin = corMin - tmpStep * (int) (extraNumber / 2);
        }
        corNumber = (int) tmpNumber;
        Map<String, Object> axisDomainMap = Maps.newHashMap();
        axisDomainMap.put(KEY_MAX, corMax);
        axisDomainMap.put(KEY_MIN, corMin);
        axisDomainMap.put(KEY_CORNUMBER, corNumber);
        return axisDomainMap;
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
