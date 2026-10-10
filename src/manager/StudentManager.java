package manager;

import model.Student;

import java.util.Arrays;

public class StudentManager implements Searchable {

    private final Student[] students = new Student[50]; //In case use ArrayList to store dynamic number of students.
    private int studentCount;

    public void addStudent(Student student) {
        if (studentCount >= students.length) {
            throw new IllegalStateException("model.Student capacity has been reached");
        }
        students[studentCount++] = java.util.Objects.requireNonNull(
                student, "model.Student cannot be null");
    }

    @Override
    public Student findStudentById(String studentId) {
        for (int index = 0; index < studentCount; index++) {
            if (students[index].getStudentId().equalsIgnoreCase(studentId)) {
                return students[index];
            }
        }
        return null;
    }

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

    public Student[] getStudents() {
        return Arrays.copyOf(students, studentCount);
    }

    public int getStudentCount() {
        return studentCount;
    }
}
