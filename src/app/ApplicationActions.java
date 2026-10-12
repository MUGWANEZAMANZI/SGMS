package app;

/**
 * Defines actions exposed by the application menu.
 */
public interface ApplicationActions {
    /** Displays a student's grade report. */
    void viewGradeReport();

    /** Exports a student's grade report. */
    void exportGradeReport();

    /** Calculates and displays a student's GPA. */
    void calculateStudentGPA();

    /** Pauses the application for user input. */
    void pause();
}
