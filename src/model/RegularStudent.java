package model;

/**
 * Represents a student using the regular passing threshold.
 */
public class RegularStudent extends Student {
    /**
     * Creates a regular student.
     *
     * @param name student's name
     * @param age student's age
     * @param email student's email address
     * @param phone student's phone number
     */
    public RegularStudent(String name, int age, String email, String phone) {
        super(name, age, email, phone);
    }

    /** Returns the regular student category. */
    @Override
    public String getStudentType() {
        return "Regular";
    }

    /** Returns the regular student's passing percentage. */
    @Override
    public double getPassingGrade() {
        return 50.0;
    }

}
