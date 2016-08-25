package com.exponentus.util;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class ImageUtil {

    private ImageUtil() {
    }

    public static void createImageThumbnail(File imageFile, float compressionQuality, String outputFile) throws IOException {
        ImageWriter writer = null;
        FileImageOutputStream output = null;
        try {
            Iterator iterator = ImageIO.getImageWritersByFormatName("jpeg");

            writer = (ImageWriter) iterator.next();

            ImageWriteParam iwp = writer.getDefaultWriteParam();
            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            iwp.setCompressionQuality(compressionQuality);

            File file = new File(outputFile);
            output = new FileImageOutputStream(file);
            writer.setOutput(output);

            BufferedImage sourceImage = ImageIO.read(imageFile);
            IIOImage image = new IIOImage(sourceImage, null, null);
            writer.write(null, image, iwp);
            writer.dispose();
        } finally {
            if (writer != null) writer.dispose();
            if (output != null) output.close();
        }
    }

    public static void main(String[] args) {
        File imageFile = new File("/home/medin/temp/wallhaven-5370.jpg");
        String outFile = "/home/medin/temp/2002704-thumb.jpg";

        try {
            ImageUtil.createImageThumbnail(imageFile, 0.03f, outFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
