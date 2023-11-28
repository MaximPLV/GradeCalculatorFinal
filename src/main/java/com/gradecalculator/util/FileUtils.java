package com.gradecalculator.util;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FileUtils {
    public static void openInBrowser(String pathname) {
        File htmlFile = new File(pathname);
        try {
            Desktop.getDesktop().browse(htmlFile.toURI());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
