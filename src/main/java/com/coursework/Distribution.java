package com.coursework;

import org.apache.poi.ss.usermodel.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Distribution {

    private final ArrayList<Reviewer> reviewers = new ArrayList<>();
    private final ArrayList<Paper> papers = new ArrayList<>();

    public ArrayList<Reviewer> getReviewers() {
        return reviewers;
    }

    public boolean distributeTwoTimes() {
        for (Reviewer reviewer : reviewers) {
            reviewer.clearPapers();
        }
        return (hungarianAlgDistribution() && hungarianAlgDistribution());
    }

    public boolean hungarianAlgDistribution() {
        final long timer = System.currentTimeMillis();
        final double timeout = 5000;
        ArrayList<Reviewer> reviewerPlaces = new ArrayList<>();
        for (Reviewer reviewer : reviewers) {
            for (int i = 0; i < reviewer.maxPapersNum - reviewer.getStudentPapers().size(); i++) {
                reviewerPlaces.add(reviewer);
            }
        }
        final int n = papers.size();
        final int m = reviewerPlaces.size();
        int[][] a = new int[n + 1][m + 1];
        arrayRandomShuffle(reviewerPlaces);
        for (int i = 0; i < papers.size(); i++) {
            for (int j = 0; j < reviewerPlaces.size(); j++) {
                int f = CCS.paperToReviewerSuitabilityFunction(reviewerPlaces.get(j), papers.get(i));
                a[i + 1][j + 1] = f;
            }
        }
        int[] u = new int[n + 1], v = new int[m + 1], p = new int[m + 1], way = new int[m + 1];
        for (int i = 1; i <= n; i++) {
            p[0] = i;
            int j0 = 0;
            int[] minv = new int[m + 1];
            Arrays.fill(minv, CCS.INF);
            boolean[] used = new boolean[m + 1];
            Arrays.fill(used, false);
            do {
                if (System.currentTimeMillis() - timer > timeout) {
                    return false;
                }
                used[j0] = true;
                int i0 = p[j0], delta = CCS.INF, j1 = 0;
                for (int j = 1; j <= m; ++j)
                    if (!used[j]) {
                        int cur = a[i0][j] - u[i0] - v[j];
                        if (cur < minv[j]) {
                            minv[j] = cur;
                            way[j] = j0;
                        }
                        if (minv[j] < delta) {
                            delta = minv[j];
                            j1 = j;
                        }
                    }
                for (int j = 0; j <= m; ++j) {
                    if (used[j]) {
                        u[p[j]] += delta;
                        v[j] -= delta;
                    } else {
                        minv[j] -= delta;
                    }
                    j0 = j1;
                }
            } while (p[j0] != 0);
            do {
                int j1 = way[j0];
                p[j0] = p[j1];
                j0 = j1;
            } while (j0 != 0);
        }
        for (int i = 0; i < m; i++) {
            if (p[i + 1] != 0) {
                reviewerPlaces.get(i).addPaper(papers.get(p[i + 1] - 1));
            }
        }
        return true;
    }

    /*    public void randomDistribution() {
            if (reviewers.isEmpty()) {
                return;
            }
            for (Reviewer reviewer : reviewers) {
                reviewer.clearPapers();
            }
            ArrayList<Reviewer> reviewersCopy = new ArrayList<>(reviewers);
            arrayRandomShuffle(reviewersCopy);
            int k = 0;
            for (int i = 0; i < papers.size(); i++) {
                while (k < reviewers.size() && reviewersCopy.get(k).hasMaxPapersNum()) {
                    reviewersCopy.remove(reviewersCopy.get(k));
                }
                if (k >= reviewersCopy.size()) {
                    arrayRandomShuffle(reviewersCopy);
                    k = 0;
                    i--;
                    continue;
                }
                reviewersCopy.get(k).addPaper(papers.get(i));
                k++;
            }
        }*/
    public ArrayList<String> readWorkbookRow(Row row) {
        ArrayList<String> inputStrings = new ArrayList<>();
        for (Cell cell : row) {
            while (cell.getColumnIndex() > inputStrings.size()) {
                inputStrings.add("");
            }
            if (cell.getCellType() == CellType.NUMERIC) {
                inputStrings.add(((Integer) (int) cell.getNumericCellValue()).toString());
            } else if (cell.getCellType() == CellType.STRING) {
                inputStrings.add(cell.getStringCellValue());
            }
        }
        return inputStrings;
    }

    public boolean loadReviewersFromExcelFile(Workbook workbook) {
        reviewers.clear();
        Sheet sheet = workbook.getSheetAt(0);
        ArrayList<String> fieldNames = null;
        for (Row row : sheet) {
            if (fieldNames == null) {
                fieldNames = readWorkbookRow(row);
            } else {
                ExcelFields excelFields = new ExcelFields(fieldNames, readWorkbookRow(row));
                String status = excelFields.getFieldValue(ExcelFields.ReviewerField.STATUS);
                if (AuxiliaryControllerMethods.isNamesEquals("Согласие", status) ||
                        AuxiliaryControllerMethods.isNamesEquals("В комиссии", status)) {
                    Reviewer reviewer = new Reviewer(excelFields);
                    reviewers.add(reviewer);
                    if (!reviewer.isCorrect()) {
                        return false;
                    }
                }
            }
        }
        return fieldNames != null && !fieldNames.isEmpty() && !reviewers.isEmpty();
    }

    public boolean loadDistribution(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0);
        ArrayList<String> fieldNames = null;
        int i = -1;
        while (i < sheet.getLastRowNum()) {
            Row row = sheet.getRow(++i);
            if (fieldNames == null) {
                fieldNames = readWorkbookRow(row);
                continue;
            }
            if (row == null || row.getCell(0) == null)
                continue;
            if (AuxiliaryControllerMethods.isNamesEquals(row.getCell(0).getStringCellValue(), ("Рецензенты без работ:"))) {
                break;
            }
            ExcelFields excelFields = new ExcelFields(fieldNames, readWorkbookRow(row));
            Paper newPaper = new Paper(excelFields);
            for (Paper paper : papers) {
                if (paper.getTitle().equals(newPaper.getTitle())) {
                    newPaper = paper;
                }
            }
            papers.add(newPaper);
            if (!newPaper.isCorrect()) {
                return false;
            }
            Reviewer reviewer = new Reviewer(excelFields);
            boolean b = true;
            for (Reviewer other : reviewers) {
                if (reviewer.equals(other)) {
                    reviewer = other;
                    b = false;
                    break;
                }
            }
            if (b)
                reviewers.add(reviewer);
            reviewer.addPaper(newPaper);
            if (!reviewer.isCorrect()) {
                return false;
            }
        }
        fieldNames = null;
        while (i < sheet.getLastRowNum()) {
            Row row = sheet.getRow(++i);
            if (fieldNames == null) {
                fieldNames = readWorkbookRow(row);
            } else {
                ExcelFields excelFields = new ExcelFields(fieldNames, readWorkbookRow(row));
                    Reviewer reviewer = new Reviewer(excelFields);
                    reviewers.add(reviewer);
                    if (!reviewer.isCorrect()) {
                        return false;
                    }
            }
        }
        return true;
    }

    public boolean loadPapersFromExcelFile(Workbook workbook) {
        papers.clear();
        Sheet sheet = workbook.getSheetAt(0);
        ArrayList<String> fieldNames = null;
        for (Row row : sheet) {
            if (fieldNames == null) {
                fieldNames = readWorkbookRow(row);
            } else {
                ExcelFields excelFields = new ExcelFields(fieldNames, readWorkbookRow(row));
                Paper paper = new Paper(excelFields);
                papers.add(paper);
                if (!paper.isCorrect()) {
                    return false;
                }
            }
        }
        return fieldNames != null && !fieldNames.isEmpty() && !papers.isEmpty();
    }


    private static <T> void arrayRandomShuffle(ArrayList<T> array) {
        Random random = new Random();
        for (int i = array.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            T tmp = array.get(j);
            array.set(j, array.get(i));
            array.set(i, tmp);
        }
    }

}
