package com.exponentus.common.service;

import com.exponentus.common.dao.AttachmentDAO;
import com.exponentus.common.model.Attachment;
import com.exponentus.common.model.AttachmentThumbnail;
import com.exponentus.env.Environment;
import com.exponentus.exception.SecureException;
import com.exponentus.scripting._Session;
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

        String outFile = Environment.tmpDir + File.separator + "thumbnails" + File.separator + att.getIdentifier() + ".jpg";
        File attFile = new File(outFile);
        attFile.getParentFile().mkdirs();

        File tf = null;
        try {
            // return thumb if exists
            if (att.getAttachmentThumbnail() != null) {
                FileUtils.writeByteArrayToFile(attFile, att.getAttachmentThumbnail().getFile());
                return attFile;
            }
            // create
            AttachmentDAO attachmentDAO = new AttachmentDAO(session);
            ByteArrayInputStream bis = new ByteArrayInputStream(att.getFile());
            tf = createJpegThumbnail(bis, outFile);
            Path path = Paths.get(tf.getAbsolutePath());
            byte[] thumbFileBytes = Files.readAllBytes(path);

            AttachmentThumbnail attachmentThumbnail = att.getAttachmentThumbnail();
            if (attachmentThumbnail == null) {
                attachmentThumbnail = new AttachmentThumbnail(att, thumbFileBytes);
            } else {
                attachmentThumbnail.setFile(thumbFileBytes);
            }
            att.setHasThumbnail(true);
            att.setAttachmentThumbnail(attachmentThumbnail);

            attachmentDAO.update(att);
        } catch (SecureException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
        float compressionQuality = 0.03f;
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

    public static void main(String[] args) {
        try {
            FileInputStream is = new FileInputStream(new File("/home/medin/temp/2002704.jpg"));
            String outFile = "/home/medin/temp/2002704-thumb.jpg";
            createJpegThumbnail(is, outFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
