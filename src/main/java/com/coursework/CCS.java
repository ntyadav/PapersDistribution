package com.coursework;

import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class CCS {

    public static final int INF = 10000;
    static Subject root;
    static ArrayList<Subject> lcaDfsSubjectsOrder;

    static {
        root = new Subject("root");
        try {
            InputStream in1 = CCS.class.getClassLoader().getResourceAsStream("assets/CCS.txt");
            InputStreamReader in2 = new InputStreamReader(in1);
            final int levelIndent = 4;
            BufferedReader in = new BufferedReader(in2);
            String s;
            int depth = 0;
            Stack<Subject> nodesHierarchyStack = new Stack<>();
            nodesHierarchyStack.add(root);
            Subject prevSubject = root;
            while ((s = in.readLine()) != null) {
                int newDepth = 0;
                while (!s.isEmpty() && s.charAt(0) == ' ') {
                    newDepth++;
                    s = s.substring(1);
                }
                newDepth /= levelIndent;
                Subject newSubject = new Subject(s.trim());
                if (newDepth > depth) {
                    nodesHierarchyStack.add(prevSubject);
                } else if (newDepth < depth) {
                    for (int i = newDepth; i < depth; i++) {
                        nodesHierarchyStack.pop();
                    }
                }
                nodesHierarchyStack.peek().addChild(newSubject);
                prevSubject = newSubject;
                depth = newDepth;
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lcaDfsSubjectsOrder = new ArrayList<>();
        preprocessingLcaDfs(root, 0);
    }

    public static Subject lastCommonAncestor(Subject subject1, Subject subject2) {
        int i = 0;
        while (i < lcaDfsSubjectsOrder.size() && lcaDfsSubjectsOrder.get(i) != subject1) {
            i++;
        }
        if (i == lcaDfsSubjectsOrder.size()) {
            return root;
        }
        Subject ancestor = subject1;
        while (i < lcaDfsSubjectsOrder.size()) {
            Subject subject = lcaDfsSubjectsOrder.get(i);
            if (subject.height < ancestor.height) {
                ancestor = subject;
            }
            if (lcaDfsSubjectsOrder.get(i) == subject2) {
                return ancestor;
            }
            i++;
        }
        return root;
    }

    public static ArrayList<Subject> parseSubjects(String s) {
        ArrayList<Subject> subjects = new ArrayList<>();
        String separator, edgeSymbol;
        if (s.contains("•") || s.contains("~")) {
            separator = "•";
            edgeSymbol = "~";
        } else {
            separator = ";";
            edgeSymbol = "→";
        }
        String[] subjectNames = s.split(separator);
        for (String subjectFullName : subjectNames) {
            var subjectPath = subjectFullName.split(edgeSymbol);
            Subject subject = root;
            for (String subjectName : subjectPath) {
                if (AuxiliaryControllerMethods.minimizeString(subjectName).isEmpty()) {
                    continue;
                }
                subject = subject.getFirstFoundChildByName(subjectName);
                if (subject == null) {
                    AuxiliaryControllerMethods.showAlertWindow("Некорректный формат темы",
                            "Не удалось определить тему " + "« " + subjectFullName + " »", Alert.AlertType.ERROR);

                    break;
                }
            }
            if (subject != root && subject != null) {
                subjects.add(subject);
            }
        }
        return subjects;
    }

/*    public static void printTree(Subject root, Integer depth) {
        if (root == null || depth == null) {
            root = CCS.root;
            depth = 0;
            for (Subject child : root.getChildren()) {
                printTree(child, depth);
            }
            return;
        }
        System.out.println(" ".repeat(depth * 4) + root.name);
        for (Subject child : root.getChildren()) {
            printTree(child, depth + 1);
        }
    }*/

    public static int paperToReviewerSuitabilityFunction(Reviewer reviewer, Paper paper) {
        if (reviewer.getStudentPapers().contains(paper)) {
            return INF;
        }
        if (paper.getBlacklist().contains(reviewer)) {
            return INF / 2;
        }
        int min = INF / 3;
        final int k = 3;
        for (Subject paperSubject : paper.getSubjectAreas()) {
            for (Subject reviewerSubject : reviewer.getSubjectAreas()) {
                Subject lca = lastCommonAncestor(reviewerSubject, paperSubject);
                int f = INF / 3;
                if (lca == paperSubject) {
                    f = (reviewerSubject.height - lca.height) * k;
                } else if (lca == reviewerSubject) {
                    f = paperSubject.height - lca.height;
                }
                min = Math.min(min, f);
            }
        }
        return min;
    }

/*    public static Subject getSubject(String subjectName) {
        Queue<Subject> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Subject cur = queue.remove();
            queue.addAll(cur.children);
            for (Subject child : cur.children) {
                if (isNamesEquals(subjectName, child.name)) {
                    return child;
                }
            }
        }
        return null;
    }*/

    private static void preprocessingLcaDfs(Subject subtreeRoot, int height) {
        lcaDfsSubjectsOrder.add(subtreeRoot);
        subtreeRoot.height = height;
        for (Subject child : subtreeRoot.children) {
            preprocessingLcaDfs(child, height + 1);
            lcaDfsSubjectsOrder.add(subtreeRoot);
        }
    }

    public static class Subject {
        private final String name;
        private final ArrayList<Subject> children = new ArrayList<>();
        private Subject parent = null;
        private int height;

        public Subject(String name) {
            this.name = name;
        }


        public Subject getFirstFoundChildByName(String childName) {
            Queue<Subject> queue = new LinkedList<>();
            queue.add(this);
            while (!queue.isEmpty()) {
                Subject subject = queue.remove();
                for (Subject child : subject.children) {
                    queue.add(child);
                    if (AuxiliaryControllerMethods.isNamesEquals(child.name, childName)) {
                        return subject;
                    }
                }
            }
            return null;
        }

        private void addChild(Subject subject) {
            if (!children.contains(subject)) {
                children.add(subject);
                subject.parent = this;
            }
        }
    }
}
