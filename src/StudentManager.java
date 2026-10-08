import java.util.Arrays;

public class StudentManager {

    private final Student[] students = new Student[50];
    private int studentCount;

    public void addStudent(Student student) {
        if (studentCount >= students.length) {
            throw new IllegalStateException("Student capacity has been reached");
        }
        students[studentCount++] = java.util.Objects.requireNonNull(
                student, "Student cannot be null");
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
