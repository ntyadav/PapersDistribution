package com.coursework;

import javafx.scene.image.Image;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class CCS {

    static  Subject root;
    static  ArrayList<Subject> lcaDfsSubjectsOrder;

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
        for (String subjectName : subjectNames) {
            var subjectPath = subjectName.split(edgeSymbol);
            String shortSubjectName = subjectPath[subjectPath.length - 1];
            Subject subject = getSubject(shortSubjectName);
            if (subject != null) {
                subjects.add(subject);
            }
        }
        return subjects;
    }

    public static void printTree(Subject root, Integer depth) {
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
    }

    public static int suitabilityOfPaperToReviewerFunction(ArrayList<Subject> reviewerSubjects,
                                                           ArrayList<Subject> paperSubjects) {
        double percent = 0;
        for (Subject paperSubject : paperSubjects) {
            for (Subject reviewerSubject : reviewerSubjects) {
                Subject lca = lastCommonAncestor(reviewerSubject, paperSubject);
                if (lca == root) {
                    continue;
                }
                int maxDepth = reviewerSubject.getMaxChildHeight();
                int maxPath = maxDepth * 2;
                if (lca == reviewerSubject) {
                    percent += 1 - ((double) paperSubject.height - reviewerSubject.height) / (maxDepth * 1.5);
                } else {
                    percent += Math.sqrt(1 - ((double) reviewerSubject.height - lca.height +
                            paperSubject.height - lca.height) / (maxPath * 1.5));
                }

            }
        }
        return (int) Math.round(percent * 1000);
    }

    public static Subject getSubject(String subjectName) {
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
    }

    private static void preprocessingLcaDfs(Subject subtreeRoot, int height) {
        lcaDfsSubjectsOrder.add(subtreeRoot);
        subtreeRoot.height = height;
        for (Subject child : subtreeRoot.children) {
            preprocessingLcaDfs(child, height + 1);
            lcaDfsSubjectsOrder.add(subtreeRoot);
        }
    }

    private static boolean isNamesEquals(String name1, String name2) {
        return minimizeString(name1).equals(minimizeString(name2));
    }

    private static String minimizeString(String s) {
        while (s.contains("(") && s.contains(")")) {
            s = s.substring(0, s.indexOf('(')) + s.substring(s.indexOf(')') + 1);
        }
        while (s.contains("  ")) {
            s = s.replaceAll(" {2}", " ");
        }
        s = s.trim();
        s = s.toLowerCase();
        return s;
    }

    public static class Subject {
        private final String name;
        private final ArrayList<Subject> children = new ArrayList<>();
        private Subject parent = null;
        private int height, maxChildHeight = -1;

        public Subject(String name) {
            this.name = name;
        }

        public int getMaxChildHeight() {
            if (maxChildHeight != -1) {
                return maxChildHeight;
            }
            if (children.isEmpty()) {
                return height;
            }
            int max = height;
            for (Subject subject : children) {
                max = Math.max(max, subject.getMaxChildHeight());
            }
            maxChildHeight = max;
            return max;
        }

        public ArrayList<Subject> getChildren() {
            return children;
        }

        private void addChild(Subject subject) {
            if (!children.contains(subject)) {
                children.add(subject);
                subject.parent = this;
            }
        }

/*        public Subject getChild(String childName) {
            Subject res = null;
            for (Subject subject : children) {
                if (subject.name.equals(childName)) {
                    res = subject;
                }
            }
            return res;
        }*/
    }
}
