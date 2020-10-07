package com.coursework;

import java.util.ArrayList;
import java.util.List;

public class ExcelFields {

    private final ArrayList<String> fieldNames;
    public final ArrayList<String> fieldValues;

    public ExcelFields(ArrayList<String> fieldNames, ArrayList<String> fieldValues) {
        this.fieldNames = fieldNames;
        while (fieldValues.size() < fieldNames.size())
            fieldValues.add("");
        this.fieldValues = fieldValues;
    }

    public String getFieldValue(PaperField paperField) {
        switch (paperField) {
            case TITLE:
                return getByFirstExistingNameOrByIndex(List.of("Работа", "Название", "Название работы"));
            case ACM_CCS:
                return getByFirstExistingNameOrByIndex(List.of("ACM CCS", "CCS", "ACM CCS работы"));
            case LEVEL:
                return getByFirstExistingNameOrByIndex(List.of("Уровень"));
            default:
                return "";
        }
    }

    public String getFieldValue(ReviewerField reviewerField) {
        switch (reviewerField) {
            case NAME:
                return getByFirstExistingNameOrByIndex(List.of("ФИО", "Имя", "ФИО рецензента"));
            case UNIT:
                return getByFirstExistingNameOrByIndex(List.of("Подразделение"));
            case EMAIL:
                return getByFirstExistingNameOrByIndex(
                        List.of("Эл. почта", "Электронная почта", "Почта", "E-mail", "Email", "Почта рецензента"));
            case BRANCH:
                return getByFirstExistingNameOrByIndex(List.of("Филиал"));
            case STATUS:
                return getByFirstExistingNameOrByIndex(List.of("Статус"));
            case ACM_CCS:
                return getByFirstExistingNameOrByIndex(List.of("ACM CCS", "CCS", "ACM CCS рецензента"));
            case COMMENT:
                return getByFirstExistingNameOrByIndex(List.of("Комментарий"));
            case MAX_PAPER_NUM:
                return getByFirstExistingNameOrByIndex(List.of("Макс. количество работ", "Макс. кол-во работ рецензента",
                        "Максимальное количество работ", "Макс. кол-во работ", "Максимальное кол-во работ"));
            default:
                return "";
        }
    }

    private String getByFirstExistingNameOrByIndex(List<String> possibleFieldNames) {
        String ans = "";
        for (String fieldName : possibleFieldNames) {
            ans = getByFieldName(fieldName);
            if (!ans.equals("")) {
                return ans;
            }
        }
        return ans;
    }

    private String getByFieldName(String fieldName) {
        int index = -1;
        for (int i = 0; i < fieldNames.size(); i++) {
            if (AuxiliaryControllerMethods.isNamesEquals(fieldName, fieldNames.get(i))) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return "";
        }
        return fieldValues.get(index);
    }

    private String getByIndex(int i) {
        if (i < fieldValues.size()) {
            return fieldValues.get(i);
        } else {
            return "";
        }
    }

    enum PaperField {
        TITLE,
        LEVEL,
        ACM_CCS
    }
    enum ReviewerField {
        NAME("ФИО"),
        EMAIL("Эл. почта"),
        BRANCH("Филиал"),
        UNIT("Подразделение"),
        STATUS("Статус"),
        MAX_PAPER_NUM("Макс. количество работ"),
        ACM_CCS("ACM CCS"),
        COMMENT("Комментарий");

        private final String synonym;

        ReviewerField(String synonym) {
            this.synonym = synonym;
        }

        public String getSynonym() {
            return synonym;
        }

    }

}