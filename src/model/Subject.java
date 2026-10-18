package model;

import exceptions.InvalidInputException;
import validation.ValidationPatterns;

/**
 * Base class for subjects that can receive grades.
 */
public abstract class Subject {

    private final String subjectName;
    private final String subjectCode;

    protected Subject(String subjectName, String subjectCode) {
        if (subjectName == null || subjectName.isBlank()) {
            throw new InvalidInputException("Subject name cannot be blank");
        }
        this.subjectCode = ValidationPatterns.requireCourseCode(subjectCode);
        this.subjectName = subjectName.trim();
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

}
