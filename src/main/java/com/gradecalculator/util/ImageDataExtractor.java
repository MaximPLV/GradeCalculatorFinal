package com.gradecalculator.util;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.Word;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ImageDataExtractor {

    private final String imageAsString;

    private final List<Word> lines;

    private final List<Word> charSequences;

    public ImageDataExtractor(BufferedImage imageFile) {
        try {
            Tesseract tesseract = new Tesseract();
            //Points to the directory containing the .traineddata files
            tesseract.setDatapath("src/main/resources");
            //tesseract.setLanguage("digits");
            tesseract.setLanguage("deu_best");

            //tesseract.setVariable("tessedit_char_whitelist", "Anzahl0123456789- ");

            this.imageAsString = tesseract.doOCR(imageFile);
                /*
            gets all the words at page iterator lvl 2
            which means that every line is a word
             */
            this.lines = tesseract.getWords(imageFile, 2);
            /*
            gets all the words at page iterator lvl 3
            which means that every charSequence is a word
             */
            this.charSequences = tesseract.getWords(imageFile, 3);

        } catch (TesseractException e) {
            throw new RuntimeException(e);
        }
    }

    public String getString() {
        return this.imageAsString;
    }


    public static String getString1(BufferedImage imageFile) {
        try {
            Tesseract tesseract = new Tesseract();
            //Points to the directory containing the .traineddata files
            tesseract.setDatapath("src/main/resources");
            //tesseract.setLanguage("digits");
            tesseract.setLanguage("deu_best");

            //tesseract.setVariable("tessedit_char_whitelist", "Anzahl0123456789- ");

            //Performs OCR on the given image
            System.out.println(tesseract.doOCR(imageFile));
            /*
            gets all the words at page iterator lvl 2
            which means that every line is a word
             */
            List<Word> lines = tesseract.getWords(imageFile, 2);
            /*
            gets all the words at page iterator lvl 3
            which means that every charSequence is a word
             */
            List<Word> charSequences = tesseract.getWords(imageFile, 3);

            Rectangle boundingBox1 = null;
            for (Word word : lines) {
                if (word.getText().matches("Anzahl\\s.+\\s.+\\s.+\\s.+\\s.+\\s.+\\s.+\\s.+\\s.+\\s.+\\s.+\\n*")){
                    boundingBox1 = word.getBoundingBox();
                    break;
                }
            }
            if (boundingBox1 == null) throw new RuntimeException("Pixel height of the characters is too low");


            Rectangle boundingBox2 = new Rectangle();
            for (Word word : charSequences) {
                if (word.getText().equals("Anzahl")) {
                    boundingBox2 = word.getBoundingBox();
                    break;
                }
            }

            boundingBox1.setLocation(boundingBox1.x + boundingBox2.width, boundingBox1.y);
            boundingBox1.setSize(boundingBox1.width - boundingBox2.width, boundingBox1.height);

            tesseract.setLanguage("digits");
            String tmp = tesseract.doOCR(imageFile, boundingBox1);
            System.out.println("smallerBox "+tmp);
            return tmp;

        } catch (TesseractException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Deprecated
    //not needed in current version
    public String extractGradeAmounts() {
        String data = getString();
        int startIndex = data.indexOf("Anzahl") + "Anzahl".length();
        int endIndex = data.indexOf("Durchschnitt");
        if (startIndex == (-1 + "Anzahl".length()) || endIndex == -1) {
            startIndex = 0;
            endIndex = data.length();
        }
        return data.substring(startIndex, endIndex);
    }


    public String extractAverageGrade() {
        String data = getString();
        Pattern pattern = Pattern.compile("\\d,\\d{3}");
        Matcher matcher = pattern.matcher(data);
        //noinspection ResultOfMethodCallIgnored
        matcher.find();
        String result = matcher.group();
        return result.replace(",", ".");
    }

    private BufferedImage preprocessImage(BufferedImage originalImage, boolean process) {

        BufferedImage bwImage1 = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        BufferedImage bwImage2 = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        double percentageOfWhitePixels = 0;
        long totalAmountOfPixels = (long) originalImage.getHeight() * originalImage.getWidth();
        if(process) {

            for (int y = 0; y < originalImage.getHeight(); y++) {
                for (int x = 0; x < originalImage.getWidth(); x++) {

                    int color = originalImage.getRGB(x, y);
                    int red = (color >> 16) & 0xff;
                    int green = (color >> 8) & 0xff;
                    int blue = color & 0xff;

                    int gray = (red + green + blue) / 3;

                    if (gray > 240) percentageOfWhitePixels += 1/(double) totalAmountOfPixels;

                    int threshold = 165;
                    if (gray > threshold) {
                        gray = 255;
                    } else {
                        gray = 0;
                    }

                    int newColor1 = new Color(gray, gray, gray).getRGB();
                    int newColor2 = new Color(255 - gray, 255 - gray, 255 - gray).getRGB();

                    bwImage1.setRGB(x, y, newColor1);
                    bwImage2.setRGB(x, y, newColor2);
                }
            }
        }
        try {
            ImageIO.write(bwImage1, "png", new File("image1.png"));
            ImageIO.write(bwImage2, "png", new File("image2.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return percentageOfWhitePixels > 0.5 ? bwImage1 : bwImage2;
    }
}
