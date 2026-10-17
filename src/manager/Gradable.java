package manager;

/**
 * Defines grade validation operations.
 */
public interface Gradable {

    /**
     * Validates a grade value.
     *
     * @param grade grade value to validate
     * @return whether the value is valid
     */
    boolean recordGrade(double grade);

    /**
     * Checks whether a grade is between 0 and 100 inclusive.
     *
     * @param grade grade value to validate
     * @return whether the value is valid
     */
    boolean validateGrade(double grade);

}
