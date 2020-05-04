package com.coursework;

import org.apache.poi.ss.usermodel.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Distribution {

    private final ArrayList<Reviewer> reviewers = new ArrayList<>();
    private final ArrayList<Paper> papers = new ArrayList<>();

    public ArrayList<Reviewer> getReviewers() {
        return reviewers;
    }

    public boolean hungarianAlgorithmDistribution() {
        final long timer = System.currentTimeMillis();
        final double timeout = 5000;
        for (Reviewer reviewer : reviewers) {
            reviewer.clearPapers();
        }
        ArrayList<Reviewer> reviewerPlaces = new ArrayList<>();
        for (Reviewer reviewer : reviewers) {
            for (int i = 0; i < reviewer.maxPapersNum; i++) {
                reviewerPlaces.add(reviewer);
            }
        }
        final int n = papers.size();
        final int m = reviewerPlaces.size();
        int[][] a = new int[n + 1][m + 1];
        int reviewerPlacesCounter = 0;
        for (Reviewer reviewer : reviewers) {
            for (int i = 0; i < papers.size(); i++) {
                Paper paper = papers.get(i);
                int f = CCS.suitabilityOfPaperToReviewerFunction(reviewer.getSubjectAreas(),
                        paper.getSubjectAreas());
                for (int k = 0; k < reviewer.maxPapersNum; k++) {
                    a[i + 1][reviewerPlacesCounter + k + 1] = f * -1;
                }
            }
            reviewerPlacesCounter += reviewer.maxPapersNum;
        }
        int[] u = new int[n + 1], v = new int[m + 1], p = new int[m + 1], way = new int[m + 1];
        for (int i = 1; i <= n; i++) {
            p[0] = i;
            int j0 = 0;
            int[] minv = new int[m + 1];
            boolean[] used = new boolean[m + 1];
            Arrays.fill(used, false);
            do {
                if (System.currentTimeMillis() - timer > timeout) {
                    return false;
                }
                used[j0] = true;
                int i0 = p[j0], delta = 1, j1 = 0;
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

    public void randomDistribution() {
        if (reviewers.isEmpty()) {
            return;
        }
        for (Reviewer reviewer : reviewers) {
            reviewer.resetPapers();
        }
        ArrayList<Reviewer> reviewersCopy = new ArrayList<>(reviewers);
        shuffleArray(reviewersCopy);
        int k = 0;
        for (int i = 0; i < papers.size(); i++) {
            while (k < reviewers.size() && reviewersCopy.get(k).hasMaxPapersNum()) {
                reviewersCopy.remove(reviewersCopy.get(k));
            }
            if (k >= reviewersCopy.size()) {
                shuffleArray(reviewersCopy);
                k = 0;
                i--;
                continue;
            }
            reviewersCopy.get(k).addPaper(papers.get(i));
            k++;
        }
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
                String status = excelFields.getByFirstExistingNameOrByIndex(List.of("Статус"), 4);
                if (ExcelFields.isFieldNamesEquals("Согласие", status) || ExcelFields.isFieldNamesEquals("В комиссии", status)) {
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

    private ArrayList<String> readWorkbookRow(Row row) {
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

    private static <T> void shuffleArray(ArrayList<T> array) {
        Random random = new Random();
        for (int i = array.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            T tmp = array.get(j);
            array.set(j, array.get(i));
            array.set(i, tmp);
        }
    }

}
