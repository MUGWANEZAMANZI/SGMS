package model;


public abstract class Subject {

    private final String subjectName;
    private final String subjectCode;

    protected Subject(String subjectName, String subjectCode) {
        if (subjectName == null || subjectName.isBlank()) {
            throw new IllegalArgumentException("model.Subject name cannot be blank");
        }
        if (subjectCode == null || subjectCode.isBlank()) {
            throw new IllegalArgumentException("model.Subject code cannot be blank");
        }
        this.subjectName = subjectName.trim();
        this.subjectCode = subjectCode.trim();


    }
    public String getSubjectName() {
        return subjectName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public abstract String getSubjectType();

    public abstract void displaySubjectDetails();
}
