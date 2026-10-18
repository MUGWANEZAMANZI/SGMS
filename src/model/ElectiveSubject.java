package model;

/**
 * Represents an optional elective subject.
 */
public class ElectiveSubject extends Subject {

    /**
     * Creates an elective subject.
     *
     * @param subjectName subject name
     * @param subjectCode subject code
     */
    public ElectiveSubject(String subjectName, String subjectCode) {
        super(subjectName, subjectCode);
    }

    /** Returns the elective subject category. */
    @Override
    public String getSubjectType() {
        return "Elective";
    }

}
