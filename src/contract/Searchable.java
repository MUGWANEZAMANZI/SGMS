package contract;

import model.Student;

public interface Searchable {
    Student findStudentById(String studentId);

    Student[] findStudentsByName(String name);

    Student[] findStudentsByAverage(double minimum, double maximum);

    Student[] findStudentsByType(String studentType);
}
