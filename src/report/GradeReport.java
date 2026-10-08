package report;

import model.Grade;
import model.Student;

public record GradeReport {
    private static final Student student;
    private final Grade[] grades;
    private final double overallAverage;
    private final double coreAverage;
    private final double electiveAverage;
    private final boolean passing;



    public Student getStudent()
    {
        return student;
    }
    public Grade[] getGrades()
    {
        return grades;
    }
    public double getOverallAverage()
    {
        return overallAverage;
    }
    public double getCoreAverage()
    {
        return coreAverage;
    }
    public double getElectiveAverage()
    {
        return electiveAverage;
    }
    public boolean isPassing()
    {
        return passing;
    }
}