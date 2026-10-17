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

    /**
     * Indicates whether this subject is mandatory.
     *
     * @return always {@code false}
     */
    public boolean isMandatory() {
        return false;
    }

    /** Displays the elective subject details. */
    @Override
    public void displaySubjectDetails() {
        IO.println("%s (%s) - Elective".formatted(getSubjectName(), getSubjectCode()));
    }
}
