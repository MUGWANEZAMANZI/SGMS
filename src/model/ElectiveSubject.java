package model;

public class ElectiveSubject extends Subject {

    public ElectiveSubject(String subjectName, String subjectCode) {
        super(subjectName, subjectCode);
    }

    @Override
    public String getSubjectType() {
        return "Elective";
    }

    public boolean isMandatory() {
        return false;
    }

    @Override
    public void displaySubjectDetails() {
        IO.println("%s (%s) - Elective".formatted(getSubjectName(), getSubjectCode()));
    }
}
