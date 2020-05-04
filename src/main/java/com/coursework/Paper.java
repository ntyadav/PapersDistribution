package com.coursework;

import java.util.ArrayList;
import java.util.List;

public class Paper {
    public final String title;
    private Reviewer reviewer;
    public final ExcelFields excelFields;

    public ArrayList<CCS.Subject> getSubjectAreas() {
        return subjectAreas;
    }

    private final ArrayList<CCS.Subject> subjectAreas;
    private final ArrayList<Reviewer> blacklist = new ArrayList<>();

    public void removeFromBlacklist(Reviewer reviewer) {
        blacklist.remove(reviewer);
    }

    public void addTobBlacklist(Reviewer reviewer) {
        if (!blacklist.contains(reviewer)) {
            blacklist.add(reviewer);
        }
    }

    public Paper(ExcelFields excelFields) {
        this.excelFields = excelFields;
        title = excelFields.getByFirstExistingNameOrByIndex(
                List.of("Работа", "Название", "Название работы"), 0).trim();
        String subjectsString = excelFields.getByFirstExistingNameOrByIndex(List.of("ACM CCS", "CCS"), 2);
        subjectAreas = CCS.parseSubjects(subjectsString);
    }

    public boolean isCorrect() {
        return (title != null) && !title.isEmpty();
    }
    public Reviewer getReviewer() {
        return reviewer;
    }

    public void setReviewer(Reviewer newReviewer) {
        if (reviewer != null) {
            reviewer.removePaper(this);
        }
        reviewer = newReviewer;
    }

    @Override
    public String toString() {
        return "«" + title + "»";
    }

}
