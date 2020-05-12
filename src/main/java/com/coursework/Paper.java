package com.coursework;

import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.List;

public class Paper {

    public final String title;
    public final ExcelFields excelFields;
    private final ArrayList<CCS.Subject> subjectAreas;
    private final ArrayList<Reviewer> blacklist = new ArrayList<>();
    private Reviewer reviewer;

    public Paper(ExcelFields excelFields) {
        this.excelFields = excelFields;
        title = excelFields.getByFirstExistingNameOrByIndex(
                List.of("Работа", "Название", "Название работы"), 0).trim();
        String subjectsString = excelFields.getByFirstExistingNameOrByIndex(List.of("ACM CCS", "CCS"), 2);
        subjectAreas = CCS.parseSubjects(subjectsString);
    }

    public ArrayList<Reviewer> getBlacklist() {
        return blacklist;
    }

    public ArrayList<CCS.Subject> getSubjectAreas() {
        return subjectAreas;
    }

    public void removeFromBlacklist(Reviewer reviewer) {
        blacklist.remove(reviewer);
    }

    public void addTobBlacklist(Reviewer reviewer) {
        if (!blacklist.contains(reviewer)) {
            blacklist.add(reviewer);
        }
    }

    public boolean isCorrect() {
        return (title != null) && !title.isEmpty();
    }


    public void setReviewer(Reviewer newReviewer) {
        if (reviewer != null) {
            reviewer.removePaper(this);
        }
        reviewer = newReviewer;
    }

    public void printToRow(Row row) {
        int i = 0;
        row.createCell(i++);
        row.createCell(i++).setCellValue(title);
        String level = excelFields.getByFirstExistingNameOrByIndex(List.of("Уровень"), 1);
        if (!level.equals("")) {
            row.createCell(i++).setCellValue(level);
        }
        String subjectsString = excelFields.getByFirstExistingNameOrByIndex(List.of("ACM CCS", "CCS"), 6);
        if (!subjectsString.equals("")) {
            row.createCell(i).setCellValue(subjectsString);
        }
    }

    @Override
    public String toString() {
        return "«" + title + "»";
    }

}
