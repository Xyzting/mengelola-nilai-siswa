package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StudentManager {
    private List<Student> students = new ArrayList<>();

    public void loadFromFile(String filename) throws IOException {
        String pathfile = "latihan_uts/" + filename;
        students.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(pathfile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    students.add(new Student(parts[1], parts[0],
                        Double.parseDouble(parts[2]),
                        Double.parseDouble(parts[3]),
                        Double.parseDouble(parts[4])));
                }
            }
        }
    }

    public void saveToFile(String filename) throws IOException {
      String pathfile = "latihan_uts/" + filename;
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathfile, true))) {
        for (Student s : students) {
            writer.write(s.toString());
            writer.newLine();
        }
      }
    }

    public void addStudent(Student s) {
        students.add(s);
    }

    public List<Student> getStudents() {
        return students;
    }

    public Student binarySearch(String name) {
        List<Student> sorted = new ArrayList<>(students);
        sorted.sort(Comparator.comparing(Student::getName));
        int left = 0, right = sorted.size() - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            int cmp = sorted.get(mid).getName().compareToIgnoreCase(name);
            if (cmp == 0) return sorted.get(mid);
            if (cmp < 0) left = mid + 1;
            else right = mid - 1;
        }
        return null;
    }

    public double totalNilai(int index) {
        if (index == students.size()) return 0;
        return students.get(index).getAverage() + totalNilai(index + 1);
    }

    public double averageNilai() {
        if (students.isEmpty()) return 0;
        return totalNilai(0) / students.size();
    }

    public int totalSiswa() {
        return countStudents(0);
    }

    private int countStudents(int index) {
        if (index == students.size()) return 0;
        return 1 + countStudents(index + 1);
    }
}