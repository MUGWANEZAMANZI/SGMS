package model;

public class CoreSubject extends Subject {

    public CoreSubject(String subjectName, String subjectCode) {
        super(subjectName, subjectCode);
    }

    @Override
    public String getSubjectType() {
        return "Core";
    }

    public boolean isMandatory() {
        return true;
    }

    @Override
    public void displaySubjectDetails() {
        IO.println("%s (%s) - Core".formatted(getSubjectName(), getSubjectCode()));
    }
}
