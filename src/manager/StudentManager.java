package manager;

import model.Student;

import java.util.Arrays;

public class StudentManager {

    private final Student[] students = new Student[50]; //In case use ArrayList to store dynamic number of students.
    private int studentCount;

    public void addStudent(Student student) {
        if (studentCount >= students.length) {
            throw new IllegalStateException("model.Student capacity has been reached");
        }
        students[studentCount++] = java.util.Objects.requireNonNull(
                student, "model.Student cannot be null");
    }

    public Student findStudent(String studentId) {
        for (int index = 0; index < studentCount; index++) {
            if (students[index].getStudentId().equalsIgnoreCase(studentId)) {
                return students[index];
            }
        }
        return null;
    }

    public Student[] getStudents() {
        return Arrays.copyOf(students, studentCount);
    }

    public int getStudentCount() {
        return studentCount;
    }
}
