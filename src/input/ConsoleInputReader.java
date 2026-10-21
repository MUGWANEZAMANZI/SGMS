package input;

import exceptions.InvalidInputException;
import validation.ValidationPatterns;

/**
 * Reads validated values from the console.
 */
public class ConsoleInputReader implements InputReader {

    /**
     * Reads an integer within an inclusive range.
     *
     * @param prompt prompt shown to the user
     * @param minimum minimum accepted value
     * @param maximum maximum accepted value
     * @return the entered integer
     */
    @Override
    public  int readInt(String prompt, int minimum, int maximum) {
        while (true) {
            try {
                int value = Integer.parseInt(readRequired(prompt));
                if (value >= minimum && value <= maximum) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                IO.println("The enter value is not valid. Please enter a valid whole number.");
            }
            IO.println("Enter a whole number from %d to %d.".formatted(minimum, maximum));
        }
    }

    /**
     * Reads a decimal number within an inclusive range.
     *
     * @param prompt prompt shown to the user
     * @param minimum minimum accepted value
     * @param maximum maximum accepted value
     * @return the entered number
     */
    @Override
    public double readDouble(String prompt, double minimum, double maximum) {
        while (true) {
            try {
                double value = Double.parseDouble(readRequired(prompt));
                if (value >= minimum && value <= maximum) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                IO.println("The enter value is not valid. Please enter a valid number.");
            }
            IO.println("Enter a number from %.0f to %.0f.".formatted(minimum, maximum));
        }
    }

    /**
     * Reads a non-blank value.
     *
     * @param prompt prompt shown to the user
     * @return the entered value
     */
    @Override
    public  String readRequired(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
            IO.println("This value is required.");
        }
    }

    /**
     * Reads an email address matching the application's email format.
     *
     * @param prompt prompt shown to the user
     * @return the entered email address
     */
    @Override
    public String readEmail(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (value != null && !value.isBlank()) {
                try {
                    return ValidationPatterns.requireEmail(value);
                } catch (InvalidInputException exception) {
                    IO.println("Invalid email format.");
                }
            } else {
                IO.println("This Email field is required.");
            }
        }
    }

    /**
     * Reads a non-numeric name.
     *
     * @param prompt prompt shown to the user
     * @return the entered name
     */
    @Override
    public String readName(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (value != null && !value.isBlank()) {
                String trimmedValue = value.trim();
                try {
                    Double.parseDouble(trimmedValue);
                    IO.println("Invalid name format. A name cannot be a number.");
                } catch (NumberFormatException e) {
                    return trimmedValue;
                }
            } else {
                IO.println("This Name field is required.");
            }
        }
    }

    /**
     * Reads whatever the user typed, trimmed; may be blank.
     *
     * @param prompt prompt shown to the user
     * @return the entered value, possibly blank
     */
    @Override
    public String readOptional(String prompt) {
        String value = readLine(prompt);
        return value == null ? "" : value.trim();
    }

    /** Waits for the user to press Enter. */
    @Override
    public void pause(){
        readLine("\nPress Enter to continue...");
        // Drain any leftover \r or \n that Windows leaves in the buffer after Enter.
        try {
            long remaining = System.in.available();
            if (remaining > 0) System.in.skip(remaining);
        } catch (java.io.IOException ignored) {}
    }

    protected String readLine(String prompt) {
        ConsoleSync.setActivePrompt(prompt);
        return IO.readln(prompt);
    }
}
