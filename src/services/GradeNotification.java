package services;

/**
 * Receives notifications produced by scheduled GPA processing.
 */
@FunctionalInterface
public interface GradeNotification {
    void notify(StudentGPAResult result);
}
