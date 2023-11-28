package com.gradecalculator.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ReadFromClipboard {

    private static String getText() throws UnsupportedFlavorException {
        try {
            return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (IOException e) {
            throw new RuntimeException("B");
        }
    }

    public static BufferedImage getImage() {
        try {
            return (BufferedImage) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.imageFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            throw new RuntimeException("Kein Bild in der Zwischenablage vorhanden!");
        }
    }

    public static String getContents() {
        try {
            return getText();
        } catch (UnsupportedFlavorException e) {
            ImageDataExtractor imageDataExtractor = new ImageDataExtractor(getImage());
            return imageDataExtractor.extractGradeAmounts();
        }
    }

    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }
}
