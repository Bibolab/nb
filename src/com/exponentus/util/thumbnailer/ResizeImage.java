package com.exponentus.util.thumbnailer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ResizeImage {

    private BufferedImage inputImage;
    private BufferedImage outputImage;
    private boolean isProcessed = false;

    private int imageWidth;
    private int imageHeight;
    private int thumbWidth;
    private int thumbHeight;
    private int scaledWidth;
    private int scaledHeight;

    public ResizeImage(int thumbWidth, int thumbHeight) {
        this.thumbWidth = thumbWidth;
        this.thumbHeight = thumbHeight;
    }

    public void setInputImage(InputStream is) throws IOException {
        BufferedImage image = ImageIO.read(is);
        setInputImage(image);
    }

    private void setInputImage(BufferedImage input) {
        this.inputImage = input;
        isProcessed = false;
        imageWidth = inputImage.getWidth(null);
        imageHeight = inputImage.getHeight(null);
    }

    public void writeOutput(File output) throws IOException {
        if (!isProcessed)
            process();

        ImageIO.write(outputImage, "PNG", output);
    }

    private void process() {
        if (imageWidth == thumbWidth && imageHeight == thumbHeight)
            outputImage = inputImage;
        else {
            calcDimensions();
            paint();
        }

        isProcessed = true;
    }

    private void calcDimensions() {
        double resizeRatio = Math.min(((double) thumbWidth) / imageWidth, ((double) thumbHeight) / imageHeight);

        scaledWidth = (int) Math.round(imageWidth * resizeRatio);
        scaledHeight = (int) Math.round(imageHeight * resizeRatio);

        thumbWidth = scaledWidth;
        thumbHeight = scaledHeight;
    }

    private void paint() {
        outputImage = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = outputImage.createGraphics();

        graphics2D.setBackground(new Color(0, 0, 0, 1));
        graphics2D.setPaint(new Color(0, 0, 0, 1));
        graphics2D.fillRect(0, 0, thumbWidth, thumbHeight);
        graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        ThumbnailReadyObserver observer = new ThumbnailReadyObserver(Thread.currentThread());
        boolean scalingComplete = graphics2D.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, observer);

        if (!scalingComplete) {
            // ImageObserver must wait for ready
            while (!observer.ready) {
                System.err.println("Waiting 0.4 sec...");
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                }
            }
        }

        graphics2D.dispose();
    }
}
