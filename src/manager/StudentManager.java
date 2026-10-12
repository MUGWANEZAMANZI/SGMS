package manager;

import exceptions.CapacityExceededException;
import model.Student;

import java.util.Arrays;

/**
 * Stores and searches students.
 */
public class StudentManager implements Searchable {

    private final Student[] students = new Student[50]; //In case use ArrayList to store dynamic number of students.
    private int studentCount;

    /**
     * Adds a student to the manager.
     *
     * @param student student to add
     * @throws CapacityExceededException if the student capacity is reached
     * @throws NullPointerException if {@code student} is {@code null}
     */
    public void addStudent(Student student) {
        if (studentCount >= students.length) {
            throw new CapacityExceededException("model.Student capacity has been reached");
        }
        students[studentCount++] = java.util.Objects.requireNonNull(
                student, "model.Student cannot be null");
    }

    /**
     * Finds a student by identifier, ignoring case.
     *
     * @param studentId identifier to find
     * @return the matching student, or {@code null} when absent
     */
    @Override
    public Student findStudentById(String studentId) {
        for (int index = 0; index < studentCount; index++) {
            if (students[index].getStudentId().equalsIgnoreCase(studentId)) {
                return students[index];
            }
        }
            return null;
    }

    /**
     * Finds students whose names contain the supplied query.
     *
     * @param name name query
     * @return matching students
     */
    @Override
    public Student[] findStudentsByName(String name) {
        String query = name.trim().toLowerCase();
        Student[] matches = new Student[studentCount];
        int matchCount = 0;

        for (int index = 0; index < studentCount; index++) {
            if (students[index].getName().toLowerCase().contains(query)) {
                matches[matchCount++] = students[index];
            }
        }

        return Arrays.copyOf(matches, matchCount);
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
        Student[] matches = new Student[studentCount];
        int matchCount = 0;

        for (int index = 0; index < studentCount; index++) {
            double average = students[index].calculateAverageGrade();
            if (average >= minimum && average <= maximum) {
                matches[matchCount++] = students[index];
            }
        }

        return Arrays.copyOf(matches, matchCount);
    }

    /**
     * Finds students by category, ignoring case.
     *
     * @param studentType category to find
     * @return matching students
     */
    @Override
    public Student[] findStudentsByType(String studentType) {
        String query = studentType.trim();
        Student[] matches = new Student[studentCount];
        int matchCount = 0;

        for (int index = 0; index < studentCount; index++) {
            if (students[index].getStudentType().equalsIgnoreCase(query)) {
                matches[matchCount++] = students[index];
            }
        }

        return Arrays.copyOf(matches, matchCount);
    }

    /**
     * Returns all stored students.
     *
     * @return a copy of the stored students
     */
    public Student[] getStudents() {
        return Arrays.copyOf(students, studentCount);
    }

    /**
     * Returns the number of stored students.
     *
     * @return the student count
     */
    public int getStudentCount() {
        return studentCount;
    }
}
