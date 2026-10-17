package services;

import input.InputReader;
import manager.Searchable;
import model.Student;
import logging.ApplicationLogger;
import view.StudentView;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import exceptions.FileExportException;
import exceptions.InvalidInputException;

import java.util.Objects;
import cache.ThreadSafeLruCache;
import java.time.Duration;

/**
 * Provides interactive student searching and result export.
 */
public final class StudentSearchService {
    private final Searchable studentSearch;
    private final InputReader inputReader;
    private final ThreadSafeLruCache<String, Student[]> patternCache =
            new ThreadSafeLruCache<>(100, Duration.ofMinutes(5));

    /**
     * Creates a student search service.
     *
     * @param studentSearch search implementation to use
     * @param inputReader reader used for interactive input
     */
    public StudentSearchService(Searchable studentSearch, InputReader inputReader) {
        this.studentSearch = Objects.requireNonNull(
                studentSearch, "Student search cannot be null");
        this.inputReader = Objects.requireNonNull(
                inputReader, "Input reader cannot be null");
    }

    /**
     * Executes a case-insensitive regex query against student names.
     *
     * @param pattern regular expression
     * @return matching students
     */
    public Student[] searchByPattern(String pattern) {
        Student[] cached = patternCache.get(pattern);
        if (cached != null) {
            return cached.clone();
        }
        Student[] results = studentSearch.findStudentsByPattern(pattern);
        patternCache.put(pattern, results.clone());
        return results;
    }

    /** Clears cached search results after student data changes. */
    public void clearSearchCache() {
        patternCache.clear();
    }

    /** Interactively searches for students and presents the results. */
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
            default -> throw new InvalidInputException(
                    "Unsupported search option: " + option);
        }

        displayResults(results);
        if (results.length > 0) {
            IO.println("\nActions: 1. View details  2. Export results  3. New search  4. Return");
            int action = inputReader.readInt("Select action: ", 1, 4);
            if (action == 1) {
                new StudentView().display(results[0]);
            } else if (action == 2) {
                exportResults(results);
            } else if (action == 3) {
                search();
            }
        }
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

    private static void exportResults(Student[] students) {
        try {
            Path directory = Path.of("grade_reports");
            Files.createDirectories(directory);
            Path file = directory.resolve("search-results.csv");
            StringBuilder content = new StringBuilder("studentId,name,type,average\n");
            for (Student student : students) {
                content.append(student.getStudentId()).append(',')
                        .append(student.getName()).append(',')
                        .append(student.getStudentType()).append(',')
                        .append(student.calculateAverageGrade()).append('\n');
            }
            Files.writeString(file, content);
            IO.println("Search results exported to " + file.toAbsolutePath());
            ApplicationLogger.info("Search results exported to " + file);
        } catch (IOException exception) {
            ApplicationLogger.error("Could not export search results", exception);
            throw new FileExportException("Could not export search results", exception);
        }
    }
}
