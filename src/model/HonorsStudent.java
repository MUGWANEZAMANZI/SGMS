package model;

/**
 * Represents a student eligible for the honors' category.
 */
public class HonorsStudent extends Student {

    /**
     * Creates an honors' student.
     *
     * @param name student's name
     * @param age student's age
     * @param email student's email address
     * @param phone student's phone number
     */
    public HonorsStudent(String name, int age, String email, String phone) {
        super(name, age, email, phone);
    }

    /**
     * Determines whether the student meets the honors threshold.
     *
     * @return {@code true} when the student's average is at least 85 percent
     */
    public boolean checkHonorsEligibility() {
        return calculateAverageGrade() >= 85.0;
    }

    /** Returns the honors student category. */
    @Override
    public String getStudentType() {
        return "Honors";
    }

    /** Returns the honors student's passing percentage. */
    @Override
    public double getPassingGrade() {
        return 60.0;
    }

}
