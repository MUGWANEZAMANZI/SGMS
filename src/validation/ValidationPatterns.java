package validation;

import exceptions.InvalidInputException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Central validation rules for identifiers and formatted input.
 */
public final class ValidationPatterns {
    private static final Pattern STUDENT_ID =
            Pattern.compile("STU\\d{3,}", Pattern.CASE_INSENSITIVE);
    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+"
                    + "@[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?"
                    + "(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)+$");
    private static final Pattern PHONE =
            Pattern.compile("^\\+?[0-9](?:[0-9 ()-]{0,18}[0-9])?$");
    private static final Pattern COURSE_CODE =
            Pattern.compile("^[A-Z0-9]+(?:-[A-Z0-9]+)*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern ISO_DATE =
            Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    private ValidationPatterns() {
    }

    public static String requireStudentId(String value) {
        return requirePattern(value, STUDENT_ID, "Student ID", "STU001");
    }

    public static String requireEmail(String value) {
        return requirePattern(value, EMAIL, "Email", "name@example.com");
    }

    public static String requirePhone(String value) {
        return requirePattern(value, PHONE, "Phone", "+27123456789");
    }

    public static String requireCourseCode(String value) {
        return requirePattern(value, COURSE_CODE, "Course code", "CORE001");
    }

    public static LocalDate requireIsoDate(String value) {
        String date = requirePattern(value, ISO_DATE, "Date", "2026-01-31");
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException exception) {
            throw new InvalidInputException("Date must be a valid ISO date: " + date);
        }
    }

    private static String requirePattern(
            String value,
            Pattern pattern,
            String fieldName,
            String example) {
        if (value == null) {
            throw new InvalidInputException(fieldName + " cannot be null");
        }
        String normalized = value.trim();
        if (!pattern.matcher(normalized).matches()) {
            throw new InvalidInputException(
                    fieldName + " has an invalid format. Expected " + example);
        }
        return normalized;
    }
}
