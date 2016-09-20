package com.exponentus.common.service;

import com.exponentus.common.dao.AttachmentDAO;
import com.exponentus.common.model.Attachment;
import com.exponentus.common.model.AttachmentThumbnail;
import com.exponentus.env.Environment;
import com.exponentus.exception.SecureException;
import com.exponentus.scripting._Session;
import com.exponentus.util.thumbnailer.ResizeImage;
import org.apache.commons.io.FileUtils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class AttachmentThumbnailService {

    private static final List<String> THUMBNAIL_SUPPORTED_FORMAT = Arrays.asList("jpeg", "jpg", "png", "gif");

    private static boolean isSupported(Attachment att) {
        if (att.getExtension() != null) {
            return THUMBNAIL_SUPPORTED_FORMAT.contains(att.getExtension());
        }
        return THUMBNAIL_SUPPORTED_FORMAT.contains(getExtension(att.getRealFileName()));
    }

    public static File createThumbnailFileIfSupported(_Session session, Attachment att) {
        if (!isSupported(att)) {
            return null;
        }

        String outFile = Environment.tmpDir + File.separator + "thumbnails" + File.separator + att.getIdentifier() + ".png";
        File attFile = new File(outFile);
        attFile.getParentFile().mkdirs();

        FileOutputStream fos = null;
        File tf = null;
        try {
            // return thumb if exists
            if (att.getAttachmentThumbnail() != null) {
                FileUtils.writeByteArrayToFile(attFile, att.getAttachmentThumbnail().getFile());
                return attFile;
            }
            // create
            byte[] thumbFileBytes;
            byte[] attBytes = att.getFile();
            int minFileSizeForThumbnailProcessStart = 80000; // 80KB
            if (attBytes.length > minFileSizeForThumbnailProcessStart) {
                ByteArrayInputStream bis = new ByteArrayInputStream(attBytes);
                //
                tf = createResizedImageThumbnail(bis, attFile);
                // tf = createJpegThumbnail(bis, outFile);
                //
                Path path = Paths.get(tf.getAbsolutePath());
                thumbFileBytes = Files.readAllBytes(path);
            } else {
                thumbFileBytes = attBytes;
                fos = new FileOutputStream(attFile);
                fos.write(thumbFileBytes);
                tf = attFile;
            }

            AttachmentThumbnail attachmentThumbnail = att.getAttachmentThumbnail();
            if (attachmentThumbnail == null) {
                attachmentThumbnail = new AttachmentThumbnail(att, thumbFileBytes);
            } else {
                attachmentThumbnail.setFile(thumbFileBytes);
            }
            att.setHasThumbnail(true);
            att.setAttachmentThumbnail(attachmentThumbnail);

            AttachmentDAO attachmentDAO = new AttachmentDAO(session);
            attachmentDAO.update(att);
        } catch (SecureException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return tf;
    }

    private static String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    private static File createJpegThumbnail(InputStream inputStream, String outputFile) throws IOException {
        float compressionQuality = 0.5f;
        return createImageThumbnail(inputStream, compressionQuality, "jpeg", outputFile);
    }

    private static File createImageThumbnail(InputStream inputStream, float compressionQuality, String formatName, String outputFile) throws IOException {
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

            return file;
        } finally {
            if (writer != null) writer.dispose();
            if (output != null) output.close();
        }
    }

    private static File createResizedImageThumbnail(InputStream inputStream, File outFile) throws IOException {
        ResizeImage resizeImage = new ResizeImage(200, 200);
        resizeImage.setInputImage(inputStream);
        resizeImage.writeOutput(outFile);
        return outFile;
    }

    public static void main(String[] args) {
        try {
            File inputFile = new File("/home/medin/temp/Screenshot+2016-09-19+16.35.47.png");
            File outFile = new File("/home/medin/temp/_thumb.png");

            ResizeImage resizeImage = new ResizeImage(230, 230);
            resizeImage.setInputImage(new FileInputStream(inputFile));
            resizeImage.writeOutput(outFile);

            // FileInputStream fis = new FileInputStream(outFile);
            // createJpegThumbnail(fis, "/home/medin/temp/_thumb-.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
