package report;

import java.util.List;

/**
 * Format-neutral data read from a grade-report file.
 *
 * @param studentName imported student name
 * @param overallAverage imported overall average
 * @param coreAverage imported core average
 * @param electiveAverage imported elective average
 * @param passing imported passing status
 * @param grades imported grade rows
 */
public record ImportedGradeReport(
        String studentName,
        double overallAverage,
        double coreAverage,
        double electiveAverage,
        boolean passing,
        List<ImportedGrade> grades) {

    public ImportedGradeReport {
        grades = List.copyOf(grades);
    }

    /**
     * Imported grade row.
     *
     * @param subjectName subject name
     * @param subjectCode subject code
     * @param subjectType subject type
     * @param value percentage value
     */
    public record ImportedGrade(
            String subjectName,
            String subjectCode,
            String subjectType,
            double value) {
    }
}
