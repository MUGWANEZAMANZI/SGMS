public class HonorsStudent extends Student {

    public HonorsStudent(String name, int age, String email, String phone) {super(name, age, email, phone);}

    public boolean checkHonorsEligibility() {
        return calculateAverageGrade() >= 80.0;
    }

    @Override
    public String getStudentType() {
        return "Honors";
    }

    @Override
    public double getPassingGrade() {
        return 60.0;
    }
}
