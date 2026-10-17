package manager;

import model.Student;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Stores and searches students.
 */
public class StudentManager implements Searchable {

    private final LinkedHashMap<String, Student> students = new LinkedHashMap<>();

    /**
     * Adds a student to the manager.
     *
     * @param student student to add
     * @throws NullPointerException if {@code student} is {@code null}
     */
    public void addStudent(Student student) {
        Student value = Objects.requireNonNull(student, "Student cannot be null");
        students.put(value.getStudentId().toUpperCase(Locale.ROOT), value);
    }

    /**
     * Finds a student by identifier, ignoring case.
     *
     * @param studentId identifier to find
     * @return the matching student, or {@code null} when absent
     */
    @Override
    public Student findStudentById(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            return null;
        }
        return students.get(studentId.trim().toUpperCase(Locale.ROOT));
    }

    /**
     * Finds students whose names contain the supplied query.
     *
     * @param name name query
     * @return matching students
     */
    @Override
    public Student[] findStudentsByName(String name) {
        if (name == null || name.isBlank()) {
            return new Student[0];
        }
        String query = name.trim().toLowerCase(Locale.ROOT);
        List<Student> matches = new ArrayList<>();
        for (Student student : students.values()) {
            if (student.getName().toLowerCase(Locale.ROOT).contains(query)) {
                matches.add(student);
            }
        }
        return matches.toArray(new Student[0]);
    }

    /**
     * Finds students whose averages fall within an inclusive range.
     *
     * @param minimum minimum average
     * @param maximum maximum average
     * @return matching students
     */
    @Override
    public Student[] findStudentsByAverage(double minimum, double maximum) {
        List<Student> matches = new ArrayList<>();
        for (Student student : students.values()) {
            double average = student.calculateAverageGrade();
            if (average >= minimum && average <= maximum) {
                matches.add(student);
            }
        }
        return matches.toArray(new Student[0]);
    }

    /**
     * Finds students by category, ignoring case.
     *
     * @param studentType category to find
     * @return matching students
     */
    @Override
    public Student[] findStudentsByType(String studentType) {
        if (studentType == null || studentType.isBlank()) {
            return new Student[0];
        }
        String query = studentType.trim();
        List<Student> matches = new ArrayList<>();
        for (Student student : students.values()) {
            if (student.getStudentType().equalsIgnoreCase(query)) {
                matches.add(student);
            }
        }
        return matches.toArray(new Student[0]);
    }

    /**
     * Returns all stored students.
     *
     * @return a copy of the stored students
     */
    public Student[] getStudents() {
        return students.values().toArray(new Student[0]);
    }

    /**
     * Returns the number of stored students.
     *
     * @return the student count
     */
    public int getStudentCount() {
        return students.size();
    }
}
