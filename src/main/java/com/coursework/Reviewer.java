package com.coursework;

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

    public void resetPapers() {
        for (Paper paper : studentPapers) {
            paper.setReviewer(null);
        }
        studentPapers.clear();
    }

    public ArrayList<CCS.Subject> getSubjectAreas() {
        return subjectAreas;
    }

    @Override
    public String toString() {
        return name + " " + excelFields.getByFieldName("Эл. почта").trim() + " (" + studentPapers.size() + " / " + maxPapersNum + ")";
    }
}