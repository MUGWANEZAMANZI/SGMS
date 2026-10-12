package model;

import exceptions.InvalidInputException;

/**
 * Base class for subjects that can receive grades.
 */
public abstract class Subject {

    private final String subjectName;
    private final String subjectCode;

    protected Subject(String subjectName, String subjectCode) {
        if (subjectName == null || subjectName.isBlank()) {
            throw new InvalidInputException("model.Subject name cannot be blank");
        }
        if (subjectCode == null || subjectCode.isBlank()) {
            throw new InvalidInputException("model.Subject code cannot be blank");
        }
        this.subjectName = subjectName.trim();
        this.subjectCode = subjectCode.trim();


    }
    /**
     * Returns the subject name.
     *
     * @return the subject name
     */
    public String getSubjectName() {
        return subjectName;
    }

    /**
     * Returns the subject code.
     *
     * @return the subject code
     */
    public String getSubjectCode() {
        return subjectCode;
    }

    /**
     * Returns the subject category.
     *
     * @return the subject category
     */
    public abstract String getSubjectType();

    /** Displays the subject details. */
    public abstract void displaySubjectDetails();
}
