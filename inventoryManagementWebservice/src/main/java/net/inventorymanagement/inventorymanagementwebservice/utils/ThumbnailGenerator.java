package net.inventorymanagement.inventorymanagementwebservice.utils;

import lombok.extern.log4j.Log4j2;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j2
public class ThumbnailGenerator {

    static final int MAX_WIDTH = 300;
    static final int MAX_HEIGHT = 300;
    static final float QUALITY = 0.95f;

    private void writeJpeg(BufferedImage image, String outputPath) throws IOException {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(QUALITY);
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(new File(outputPath))) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }
    }

    public String generateThumbnail(File file, String destinationFolder) {
        BufferedImage sourceImage;
        String in = file.getName();
        try {
            // load image
            sourceImage = ImageIO.read(file);
            if (sourceImage != null) {
                if (sourceImage.getWidth() <= MAX_WIDTH && sourceImage.getHeight() <= MAX_HEIGHT) {
                    // no need to create thumbnail of an image that is already smaller
                    return null;
                }
                BufferedImage bi = createThumb(sourceImage);
                String ext = in.substring(in.lastIndexOf('.') + 1);
                if (!ext.equalsIgnoreCase("jpg")) {
                    ext = "jpg";
                }
                String out = in.replaceFirst("(?i).([a-z0-9]+)$", "_thumb." + ext);
                Path destinationPath = Paths.get(destinationFolder, out);
                try {
                    writeJpeg(bi, destinationPath.toString());
                    return destinationPath.toString();
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to write thumbnail file for " + out, ex);
                }
            } else {
                log.warn("Unable to load image for thumbnail creation: {}", file.getName());
            }
        } catch (IOException | RuntimeException ex) {
            log.error("Failed to generate thumbnail from " + file.getName(), ex);
        }
        return null;
    }

    private BufferedImage createThumb(BufferedImage bufferedImage) {
        int width = MAX_WIDTH;
        int height = MAX_HEIGHT;
        // scale w, h to keep aspect constant
        double outputAspect = 1.0 * width / height;
        double inputAspect = 1.0 * bufferedImage.getWidth() / bufferedImage.getHeight();
        if (outputAspect < inputAspect) {
            // width is limiting factor; adjust height to keep aspect
            height = (int) (width / inputAspect);
        } else {
            // height is limiting factor; adjust width to keep aspect
            width = (int) (height * inputAspect);
        }
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.drawImage(bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, width, height, null);
        g2.dispose();
        return bi;
    }
}
