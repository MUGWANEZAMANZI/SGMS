package manager;

import model.Student;

/**
 * Defines student search operations.
 */
public interface Searchable {
    /**
     * Finds a student by identifier.
     *
     * @param studentId identifier to find
     * @return the matching student, or {@code null} when absent
     */
    Student findStudentById(String studentId);

    /**
     * Finds students whose names contain a query.
     *
     * @param name name query
     * @return matching students
     */
    Student[] findStudentsByName(String name);

    /**
     * Finds students whose averages fall within an inclusive range.
     *
     * @param minimum minimum average
     * @param maximum maximum average
     * @return matching students
     */
    Student[] findStudentsByAverage(double minimum, double maximum);

    /**
     * Finds students by category.
     *
     * @param studentType category to find
     * @return matching students
     */
    Student[] findStudentsByType(String studentType);

    /**
     * Finds students whose names match a regular expression.
     *
     * @param namePattern regular expression applied to student names
     * @return matching students
     */
    default Student[] findStudentsByPattern(String namePattern) {
        throw new UnsupportedOperationException("Pattern search is not supported");
    }
}
