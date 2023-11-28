package com.gradecalculator;

import com.gradecalculator.core.GradeOverview;
import com.gradecalculator.util.FileUtils;
import com.gradecalculator.util.ImageDataExtractor;
import com.gradecalculator.util.ReadFromClipboard;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;


public class Main {
    public static void main(String[] args) {
        //sample grade report: "30	29	40	46	52	6	53	56	42	28	333"
        runGradeOverview();
        //test();
    }

    public static void runGradeOverview() {
        //System.out.println(ReadFromClipboard.getContents());
        GradeOverview gradeOverview = new GradeOverview();
        gradeOverview.generateHTMLDoc();
        gradeOverview.generatePDFDoc();
        //FileUtils.openInBrowser("src/main/resources/GradeOverview.html");
        FileUtils.openInBrowser("src/main/resources/GradeOverview.pdf");
        System.out.println(Arrays.toString(gradeOverview.getAmounts()));

        //if (gradeOverview.getRoundedAverageGrade() != Double.parseDouble(ImageDataExtractor.extractAverageGrade(ReadFromClipboard.getImage())))
        //    throw new RuntimeException("Output PDF is probably wrong due to the low resolution of the original screenshot");
    }

    public static void test() {
        BufferedImage image = ReadFromClipboard.getImage();
        File outputfile = new File("image.png");
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("src/main/resources");

        tesseract.setLanguage("eng_best");
        //tesseract.setLanguage("digits");
        //tesseract.setLanguage("digits_comma");
        //tesseract.setVariable("tessedit_char_whitelist", "0123456789-. ");
        try {
            ImageIO.write(image, "png", outputfile);
            System.out.println(1+": "+tesseract.doOCR(outputfile));
        } catch (IOException | TesseractException e) {
            throw new RuntimeException(e);
        }
    }
}