package services;

import input.InputReader;
import manager.Searchable;
import model.Student;

import java.util.Objects;

public final class StudentSearchService {
    private final Searchable studentSearch;
    private final InputReader inputReader;

    public StudentSearchService(Searchable studentSearch, InputReader inputReader) {
        this.studentSearch = Objects.requireNonNull(
                studentSearch, "Student search cannot be null");
        this.inputReader = Objects.requireNonNull(
                inputReader, "Input reader cannot be null");
    }

    public void search() {
        IO.println("\n============= SEARCH STUDENTS =============");
        IO.println("1. By Student ID");
        IO.println("2. By Name");
        IO.println("3. By Grade Range");
        IO.println("4. By Student Type");

        int option = inputReader.readInt("Select search option: ", 1, 4);
        Student[] results;

        switch (option) {
            case 1 -> {
                String id = inputReader.readRequired(
                        "Enter student ID: ");
                Student student = studentSearch.findStudentById(id);
                results = student == null
                        ? new Student[0]
                        : new Student[]{student};
            }
            case 2 -> {
                String name = inputReader.readRequired(
                        "Enter name (partial or full): ");
                results = studentSearch.findStudentsByName(name);
            }
            case 3 -> {
                double minimum = inputReader.readDouble(
                        "Minimum average (0-100): ", 0, 100);
                double maximum = inputReader.readDouble(
                        "Maximum average (0-100): ", minimum, 100);
                results = studentSearch.findStudentsByAverage(minimum, maximum);
            }
            case 4 -> {
                IO.println("1. Regular");
                IO.println("2. Honors");
                int type = inputReader.readInt("Select type: ", 1, 2);
                results = studentSearch.findStudentsByType(
                        type == 1 ? "Regular" : "Honors");
            }
            default -> throw new IllegalStateException(
                    "Unsupported search option: " + option);
        }

        displayResults(results);
    }

    private void displayResults(Student[] students) {
        IO.println("\nSEARCH RESULTS (" + students.length + " found)");
        if (students.length == 0) {
            IO.println("No students matched your search.");
            return;
        }

        IO.println("%-8s | %-20s | %-10s | %-8s".formatted(
                "STU ID", "NAME", "TYPE", "AVERAGE"));
        IO.println("--------------------------------------------------------");
        for (Student student : students) {
            IO.println("%-8s | %-20s | %-10s | %6.2f%%".formatted(
                    student.getStudentId(),
                    student.getName(),
                    student.getStudentType(),
                    student.calculateAverageGrade()));
        }
    }
}
