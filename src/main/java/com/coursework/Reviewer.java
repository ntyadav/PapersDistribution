package com.coursework;

import org.apache.poi.ss.usermodel.Row;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

class Reviewer {

    public final int maxPapersNum;
    public final ExcelFields excelFields;

    private final ArrayList<CCS.Subject> subjectAreas;
    private final ArrayList<Paper> studentPapers = new ArrayList<>();

    public String getName() {
        if (excelFields == null)
            return "";
        return excelFields.getFieldValue(ExcelFields.ReviewerField.NAME);
    }


    public Reviewer(ExcelFields excelFields) {
        this.excelFields = excelFields;
        int maxPapersNum;
        try {
            maxPapersNum = Integer.parseInt(excelFields.getFieldValue(ExcelFields.ReviewerField.MAX_PAPER_NUM));
        } catch (NumberFormatException exception) {
            maxPapersNum = -1;
        }
        this.maxPapersNum = maxPapersNum;
        String subjectsString = excelFields.getFieldValue(ExcelFields.ReviewerField.ACM_CCS);
        subjectAreas = CCS.parseSubjects(subjectsString);
    }

    @Override
    public boolean equals(Object o) {
        Reviewer other = (Reviewer)o;
        return other.excelFields.getFieldValue(ExcelFields.ReviewerField.NAME).equals(excelFields.getFieldValue(ExcelFields.ReviewerField.NAME)) &&
                other.excelFields.getFieldValue(ExcelFields.ReviewerField.UNIT).equals(excelFields.getFieldValue(ExcelFields.ReviewerField.UNIT)) &&
                other.excelFields.getFieldValue(ExcelFields.ReviewerField.MAX_PAPER_NUM).equals(excelFields.getFieldValue(ExcelFields.ReviewerField.MAX_PAPER_NUM)) &&
                other.excelFields.getFieldValue(ExcelFields.ReviewerField.ACM_CCS).equals(excelFields.getFieldValue(ExcelFields.ReviewerField.ACM_CCS)) &&
        other.excelFields.getFieldValue(ExcelFields.ReviewerField.EMAIL).equals(excelFields.getFieldValue(ExcelFields.ReviewerField.EMAIL));
    }

    public boolean isCorrect() {
        return !getName().isEmpty() && (maxPapersNum != -1);
    }

    public ArrayList<Paper> getStudentPapers() {
        return studentPapers;
    }

    public void addPaper(Paper paper) {
        if (!studentPapers.contains(paper)) {
            paper.addReviewer(this);
            studentPapers.add(paper);
        }
    }

    public void clearPapers() {
        for (int i = 0; i < studentPapers.size(); i++) {
            studentPapers.get(i).removeReviwer(this);
        }
        studentPapers.clear();
    }

/*    public void removePaper(Paper paper) {
        studentPapers.remove(paper);
    }*/


    public boolean hasMaxPapersNum() {
        return studentPapers.size() == maxPapersNum;
    }


    public ArrayList<CCS.Subject> getSubjectAreas() {
        return subjectAreas;
    }

    public void printToRow(Row row) {
        String email = excelFields.getFieldValue(ExcelFields.ReviewerField.EMAIL).trim();
        int i = 0;
        row.createCell(i++).setCellValue(getName());
        row.createCell(i++).setCellValue(email);
        String subdivision = excelFields.getFieldValue(ExcelFields.ReviewerField.BRANCH);
        if (!subdivision.equals("")) {
            row.createCell(i++).setCellValue(subdivision);
        }
        String subjectsString = excelFields.getFieldValue(ExcelFields.ReviewerField.ACM_CCS);
        if (!subjectsString.equals("")) {
            row.createCell(i).setCellValue(subjectsString);
        }
    }

    @Override
    public String toString() {
        String email = excelFields.getFieldValue(ExcelFields.ReviewerField.EMAIL).trim();
        return getName() + " " + email + " (" + studentPapers.size() + " / " + maxPapersNum + ")";
    }
}