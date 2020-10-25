package com.coursework;

import org.apache.poi.ss.usermodel.Row;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Paper {

    public final ExcelFields excelFields;
    private final ArrayList<CCS.Subject> subjectAreas;
    private final ArrayList<Reviewer> blacklist = new ArrayList<>();
    private final ArrayList<Reviewer> reviewers = new ArrayList<>();

    public String getTitle() {
        if (excelFields == null)
            return "";
        return excelFields.getFieldValue(ExcelFields.PaperField.TITLE);
    }

    public int getReviewersSize() {
        return reviewers.size();
    }

    public Paper(ExcelFields excelFields) {
        this.excelFields = excelFields;
        String subjectsString = excelFields.getFieldValue(ExcelFields.PaperField.ACM_CCS);
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

    public void removeReviwer(Reviewer reviewer) {
        reviewers.remove(reviewer);
        reviewer.getStudentPapers().remove(this);
    }

    public boolean isCorrect() {
        return (getTitle() != null) && !getTitle().isEmpty();
    }


    public void addReviewer(Reviewer newReviewer) {
        if (newReviewer != null && !reviewers.contains(newReviewer)) {
            reviewers.add(newReviewer);
        }
    }

    public void printRow(Row row, int k) {
        int i = 0;
        row.createCell(i++).setCellValue(getTitle());
        row.createCell(i++).setCellValue(excelFields.getFieldValue(ExcelFields.PaperField.ACM_CCS));
        Reviewer reviewer = reviewers.get(k);
        row.createCell(i++).setCellValue(reviewer.excelFields.getFieldValue(ExcelFields.ReviewerField.NAME));
        row.createCell(i++).setCellValue(reviewer.excelFields.getFieldValue(ExcelFields.ReviewerField.ACM_CCS));
        row.createCell(i++).setCellValue(
                Integer.parseInt(reviewer.excelFields.getFieldValue(ExcelFields.ReviewerField.MAX_PAPER_NUM)));

    }

/*    public void printToRow(Row row) {
        int i = 0;
        row.createCell(i++);
        row.createCell(i++).setCellValue(getTitle());
        String level = excelFields.getFieldValue(ExcelFields.PaperField.LEVEL);
        if (!level.equals("")) {
            row.createCell(i++).setCellValue(level);
        }
        String subjectsString = excelFields.getFieldValue(ExcelFields.PaperField.ACM_CCS);
        if (!subjectsString.equals("")) {
            row.createCell(i).setCellValue(subjectsString);
        }
    }*/

    @Override
    public String toString() {
        return "«" + getTitle() + "»";
    }

}
