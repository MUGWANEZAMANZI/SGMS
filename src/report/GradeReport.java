package report;

import model.Grade;
import model.Student;

/**
 * Contains the data needed to present a student's grade report.
 *
 * @param student student represented by the report
 * @param grades student's grades
 * @param overallAverage overall percentage average
 * @param coreAverage core-subject average
 * @param electiveAverage elective-subject average
 * @param passing whether the student is passing
 */
public record GradeReport(
        Student student,
        Grade[] grades,
        double overallAverage,
        double coreAverage,
        double electiveAverage,
        boolean passing)
{

    /**
     * Generates a one-line text representation of the report.
     *
     * @return the formatted report text
     */
    public String generateTextReport()
    {
        return "Student: " + student.getName() + "," +
               "Overall Average: " + overallAverage + "," +
               "Core Average: " + coreAverage + "," +
               "Elective Average: " + electiveAverage + "," +
               "Passing: " + passing + "\n";
    }

    public String generateJsonReport()
    {
    return "{\n" +
           "  \"student\": \"" + student.getName() + "\",\n" +
           "  \"overallAverage\": " + overallAverage + ",\n" +
           "  \"coreAverage\": " + coreAverage + ",\n" +
           "  \"electiveAverage\": " + electiveAverage + ",\n" +
           "  \"passing\": " + passing + "\n" +
           "}";
    }

    public String generateCsvReport()
    {
        return "Student,Overall Average,Core Average,Elective Average,Passing\n" +
               student.getName() + "," +
               overallAverage + "," +
               coreAverage + "," +
               electiveAverage + "," +
               passing + "\n";
    }
}