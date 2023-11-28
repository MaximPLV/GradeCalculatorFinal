package com.gradecalculator.core;

import com.gradecalculator.util.ReadFromClipboard;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfWriter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;

public class GradeOverview {

    /**
     * equals grades.length
     */
    private static final int amountOfGrades = 11;

    private final double[] grades = new double[]{1.0, 1.3, 1.7, 2.0, 2.3, 2.7, 3.0, 3.3, 3.7, 4.0, 5.0};

    private final int[] amounts;

    private final HashMap<Double, Integer> gradesMap;

    private final int totalAmount;

    public GradeOverview() {
        this(ReadFromClipboard.getContents());
    }

    public GradeOverview(String data) {
        //TODO: Check if String has correct format
        this(stringToIntArray(data));
    }

    public GradeOverview(int[] amounts) {
        this.amounts = amounts;

        this.gradesMap = new HashMap<>();
        for (int i = 0; i < grades.length; i++) {
            gradesMap.put(grades[i], amounts[i]);
        }

        int totalAmount1 = 0;
        for (int amount : amounts) {
            totalAmount1 += amount;
        }
        this.totalAmount = totalAmount1;
    }

    public static int[] stringToIntArray(String data) {
        /*
        replaces all "---" (which means that no one has this grade) with 0
        it also replaces every other character except for digits and white spaces,
        because sometimes Tesseract doesn't recognize "---" and reads it as something like "me" or "oo"
        */
        data = data.replaceAll("[^0-9 \t\n]", "0");
        String[] stringAmounts = data.split("\\D");
        return Arrays.stream(stringAmounts).filter(str -> !str.isEmpty()).mapToInt(Integer::parseInt).toArray();
    }

    public int getAmount(double grade) {
        return gradesMap.get(grade);
    }

    public int[] getAmounts() {
        return amounts;
    }

    public double getAverageGrade() {
        double averageGrade = 0;
        for (double grade : grades) {
            averageGrade += grade * gradesMap.get(grade) / (double) totalAmount;
        }
        return averageGrade;
    }

    public double getRoundedAverageGrade() {
        double averageGrade = 0;
        for (double grade : grades) {
            averageGrade += grade * gradesMap.get(grade) / (double) totalAmount;
        }

        averageGrade =  averageGrade * 1000;
        averageGrade = Math.round(averageGrade);
        averageGrade = averageGrade / 1000;
        return averageGrade;
    }

    public double getAverageGradeWithout5() {
        double averageGrade = 0;
        int newTotalAmount = totalAmount - getAmount(5.0);
        for (int i = 0; i < grades.length - 1; i++) {
            averageGrade += grades[i] * gradesMap.get(grades[i]) / (double) newTotalAmount;
        }
        return averageGrade;
    }

    public void generateHTMLDoc() {
        try (Writer writer = new FileWriter("src/main/resources/GradeOverview.html")) {

            String htmlTemplate = new String(Files.readAllBytes(Paths.get("src/main/resources/GradeOverviewTemplate.html")));

            StringBuilder tableAmounts = new StringBuilder();
            //<td>Row 2 Cell 1</td><td>Row 2 Cell 2</td><td>Row 2 Cell 3</td><td>Row 2 Cell 4</td><td>Row 2 Cell 5</td><td>Row 2 Cell 6</td><td>Row 2 Cell 7</td><td>Row 2 Cell 8</td><td>Row 2 Cell 9</td><td>Row 2 Cell 10</td><td>Row 2 Cell 11</td>
            for (int amount : getAmounts()) {
                tableAmounts.append("<td> ");
                tableAmounts.append(amount);
                tableAmounts.append(" </td>");
            }

            String finalHtml = String.format(htmlTemplate, tableAmounts, getAverageGrade(), getAverageGradeWithout5(), totalAmount);

            writer.write(finalHtml);

            //FileUtils.openInBrowser("src/main/resources/GradeOverview.html");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void generatePDFDoc() {
        generateHTMLDoc();
        try (PdfWriter writer = new PdfWriter("src/main/resources/GradeOverview.pdf")) {
            HtmlConverter.convertToPdf(new String(Files.readAllBytes(Paths.get("src/main/resources/GradeOverview.html"))), writer);
            //FileUtils.openInBrowser("src/main/resources/GradeOverview.pdf");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
