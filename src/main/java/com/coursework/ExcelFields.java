package com.coursework;

import java.util.ArrayList;
import java.util.List;

public class ExcelFields {

    public final ArrayList<String> fieldValues;
    private final ArrayList<String> fieldNames;

    public ExcelFields(ArrayList<String> fieldNames, ArrayList<String> fieldValues) {
        this.fieldNames = fieldNames;
        this.fieldValues = fieldValues;
    }

    public static boolean isFieldNamesEquals(String name1, String name2) {
        return minimizeString(name1).equals(minimizeString(name2));
    }

    public String getByFirstExistingNameOrByIndex(List<String> possibleFieldNames, int index) {
        String ans = "";
        for (String fieldName : possibleFieldNames) {
            ans = getByFieldName(fieldName);
            if (!ans.equals("")) {
                return ans;
            }
        }
        return getByIndex(index);
    }

    public String getByFieldName(String fieldName) {
        int index = -1;
        for (int i = 0; i < fieldNames.size(); i++) {
            if (isFieldNamesEquals(fieldName, fieldNames.get(i))) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return "";
        }
        return fieldValues.get(index);
    }

    public String getByIndex(int i) {
        if (i < fieldValues.size()) {
            return fieldValues.get(i);
        } else {
            return "";
        }
    }

    private static String minimizeString(String s) {
        while (s.contains("  ")) {
            s = s.replaceAll(" {2}", " ");
        }
        s = s.trim();
        s = s.toLowerCase();
        return s;
    }

}