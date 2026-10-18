package model;

/**
 * Represents a mandatory core subject.
 */
public class CoreSubject extends Subject {

    /**
     * Creates a core subject.
     *
     * @param subjectName subject name
     * @param subjectCode subject code
     */
    public CoreSubject(String subjectName, String subjectCode) {
        super(subjectName, subjectCode);
    }

    /** Returns the core subject category. */
    @Override
    public String getSubjectType() {
        return "Core";
    }

}
