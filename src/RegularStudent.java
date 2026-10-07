public class RegularStudent extends Student {

    public RegularStudent(String name, int age, String email, String phone) {
        super(name, age, email, phone);
    }

    @Override
    public String getStudentType() {
        return "Regular";
    }

    @Override
    public double getPassingGrade() {
        return 50.0;
    }
}
