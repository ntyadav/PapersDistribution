package com.coursework;

import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.List;

class Reviewer {

    public final String name;
    public final int maxPapersNum;
    public final ExcelFields excelFields;

    private final ArrayList<CCS.Subject> subjectAreas;
    private final ArrayList<Paper> studentPapers = new ArrayList<>();

    public Reviewer(ExcelFields excelFields) {
        this.excelFields = excelFields;
        name = excelFields.getByFirstExistingNameOrByIndex(List.of("Работа", "Название", "Название работы"), 0).trim();
        int maxPapersNum;
        try {
            maxPapersNum = Integer.parseInt(excelFields.getByFirstExistingNameOrByIndex(List.of("Макс. количество работ",
                    "Максимальное количество работ", "Макс. кол-во работ", "Максимальное кол-во работ"), 5));
        } catch (NumberFormatException exception) {
            maxPapersNum = -1;
        }
        this.maxPapersNum = maxPapersNum;
        String subjectsString = excelFields.getByFirstExistingNameOrByIndex(List.of("ACM CCS", "CCS"), 6);
        subjectAreas = CCS.parseSubjects(subjectsString);
    }

    public boolean isCorrect() {
        return !name.isEmpty() && (maxPapersNum != -1);
    }

    public ArrayList<Paper> getStudentPapers() {
        return studentPapers;
    }

    public void addPaper(Paper paper) {
        if (!studentPapers.contains(paper)) {
            paper.setReviewer(this);
            studentPapers.add(paper);
        }
    }

    public void clearPapers() {
        for (int i = 0; i < studentPapers.size(); i++) {
            studentPapers.get(i).setReviewer(null);
        }
        studentPapers.clear();
    }

    public void removePaper(Paper paper) {
        studentPapers.remove(paper);
    }


    public boolean hasMaxPapersNum() {
        return studentPapers.size() == maxPapersNum;
    }


    public ArrayList<CCS.Subject> getSubjectAreas() {
        return subjectAreas;
    }

    public void printToRow(Row row) {
        String email = excelFields.getByFirstExistingNameOrByIndex(
                List.of("Эл. почта", "Электронная почта", "Почта", "E-mail", "Email"), 1).trim();
        int i = 0;
        row.createCell(i++).setCellValue(name);
        row.createCell(i++).setCellValue(email);
        String subdivision = excelFields.getByFirstExistingNameOrByIndex(List.of("Подразделение"), 3);
        if (!subdivision.equals("")) {
            row.createCell(i++).setCellValue(subdivision);
        }
        String subjectsString = excelFields.getByFirstExistingNameOrByIndex(List.of("ACM CCS", "CCS"), 6);
        if (!subjectsString.equals("")) {
            row.createCell(i).setCellValue(subjectsString);
        }
    }

    @Override
    public String toString() {
        String email = excelFields.getByFirstExistingNameOrByIndex(
                List.of("Эл. почта", "Электронная почта", "Почта", "E-mail", "Email"), 1).trim();
        return name + " " + email + " (" + studentPapers.size() + " / " + maxPapersNum + ")";
    }
}