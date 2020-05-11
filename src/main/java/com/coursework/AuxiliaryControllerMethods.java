package com.coursework;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.io.File;

public class AuxiliaryControllerMethods {

    public static String getExtension(File file) {
        String fileName = file.getName();
        String extension = "";
        int lastDotPosition = fileName.lastIndexOf('.');
        if (lastDotPosition > -1) {
            extension = fileName.substring(lastDotPosition);
        }
        return extension.toLowerCase();
    }

    public static void showAlertWindow(String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        Label label = new Label(content);
        label.setWrapText(true);
        alert.getDialogPane().setContent(label);
        alert.showAndWait();
    }

    public static boolean isNamesEquals(String name1, String name2) {
        return minimizeString(name1).equals(minimizeString(name2));
    }

    public static String minimizeString(String s) {
        while (s.contains("(") && s.contains(")")) {
            s = s.substring(0, s.indexOf('(')) + s.substring(s.indexOf(')') + 1);
        }
        while (s.contains("\t")) {
            s = s.replaceAll("\t", " ");
        }
        while (s.contains("  ")) {
            s = s.replaceAll(" {2}", " ");
        }
        s = s.trim();
        s = s.toLowerCase();
        return s;
    }
}
