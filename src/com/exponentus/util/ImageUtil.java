package com.exponentus.util;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class ImageUtil {

    private ImageUtil() {
    }

    public static void createJpegThumbnail(InputStream inputStream, String outputFile) throws IOException {
        float compressionQuality = 0.03f;
        createImageThumbnail(inputStream, compressionQuality, "jpeg", outputFile);
    }

    private static void createImageThumbnail(InputStream inputStream, float compressionQuality, String formatName, String outputFile) throws IOException {
        ImageWriter writer = null;
        FileImageOutputStream output = null;
        try {
            Iterator iterator = ImageIO.getImageWritersByFormatName(formatName);

            writer = (ImageWriter) iterator.next();

            ImageWriteParam iwp = writer.getDefaultWriteParam();
            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            iwp.setCompressionQuality(compressionQuality);

            File file = new File(outputFile);
            output = new FileImageOutputStream(file);
            writer.setOutput(output);

            BufferedImage sourceImage = ImageIO.read(inputStream);
            IIOImage image = new IIOImage(sourceImage, null, null);
            writer.write(null, image, iwp);
        } finally {
            if (writer != null) writer.dispose();
            if (output != null) output.close();
        }
    }

    public static void main(String[] args) {
        try {
            FileInputStream is = new FileInputStream(new File("/home/medin/temp/2002704.jpg"));
            String outFile = "/home/medin/temp/2002704-thumb.jpg";
            ImageUtil.createJpegThumbnail(is, outFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
