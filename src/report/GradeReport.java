package report;

import model.Grade;
import model.Student;

public record GradeReport(
        Student student,
        Grade[] grades,
        double overallAverage,
        double coreAverage,
        double electiveAverage,
        boolean passing)
{

    public String generateTextReport()
    {
        return "Student: " + student.getName() + "," +
               "Overall Average: " + overallAverage + "," +
               "Core Average: " + coreAverage + "," +
               "Elective Average: " + electiveAverage + "," +
               "Passing: " + passing + "\n";
    }
}