package input;

/**
 * Reads and validates values from an input source.
 */
public interface InputReader {
    /**
     * Reads an integer within an inclusive range.
     *
     * @param prompt prompt shown to the user
     * @param minimum minimum accepted value
     * @param max maximum accepted value
     * @return the entered integer
     */
    int readInt(String prompt, int minimum, int max);

    /**
     * Reads a decimal number within an inclusive range.
     *
     * @param prompt prompt shown to the user
     * @param minimum minimum accepted value
     * @param max maximum accepted value
     * @return the entered number
     */
    double readDouble(String prompt, double minimum, double max);

    /**
     * Reads a non-numeric name.
     *
     * @param prompt prompt shown to the user
     * @return the entered name
     */
    String readName(String prompt);

    /**
     * Reads an email address containing an at sign.
     *
     * @param prompt prompt shown to the user
     * @return the entered email address
     */
    String readEmail(String prompt);

    /**
     * Reads a non-blank value.
     *
     * @param prompt prompt shown to the user
     * @return the entered value
     */
    String readRequired(String prompt);

    /** Waits for the user to continue. */
    void pause();
}
