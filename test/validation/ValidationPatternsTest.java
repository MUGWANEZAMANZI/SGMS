package validation;

import exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Direct unit tests for every regex pattern in ValidationPatterns.
 */
class ValidationPatternsTest {

    // -------------------------------------------------------------------------
    // Student ID  STU\d{3,}
    // -------------------------------------------------------------------------

    @Test void studentId_validAccepted()        { assertEquals("STU123", ValidationPatterns.requireStudentId("STU123")); }
    @Test void studentId_longNumberAccepted()   { assertDoesNotThrow(() -> ValidationPatterns.requireStudentId("STU9999")); }
    @Test void studentId_caseInsensitive()      { assertDoesNotThrow(() -> ValidationPatterns.requireStudentId("stu001")); }
    @Test void studentId_missingPrefixRejected(){ assertThrows(InvalidInputException.class, () -> ValidationPatterns.requireStudentId("123")); }
    @Test void studentId_tooShortRejected()     { assertThrows(InvalidInputException.class, () -> ValidationPatterns.requireStudentId("STU12")); }
    @Test void studentId_nullRejected()         { assertThrows(InvalidInputException.class, () -> ValidationPatterns.requireStudentId(null)); }
    @Test void studentId_emptyRejected()        { assertThrows(InvalidInputException.class, () -> ValidationPatterns.requireStudentId("")); }

    // -------------------------------------------------------------------------
    // Email
    // -------------------------------------------------------------------------

    @Test void email_simpleAccepted()        { assertEquals("user@example.com", ValidationPatterns.requireEmail("user@example.com")); }
    @Test void email_subdomainAccepted()     { assertDoesNotThrow(() -> ValidationPatterns.requireEmail("u@mail.uni.edu")); }
    @Test void email_missingAtRejected()     { assertThrows(InvalidInputException.class, () -> ValidationPatterns.requireEmail("userexample.com")); }
    @Test void email_missingDomainRejected() { assertThrows(InvalidInputException.class, () -> ValidationPatterns.requireEmail("user@")); }
    @Test void email_nullRejected()          { assertThrows(InvalidInputException.class, () -> ValidationPatterns.requireEmail(null)); }
    @Test void email_plainStringRejected()   { assertThrows(InvalidInputException.class, () -> ValidationPatterns.requireEmail("notanemail")); }

    // -------------------------------------------------------------------------
    // Phone
    // -------------------------------------------------------------------------

    @Test void phone_internationalAccepted()  { assertDoesNotThrow(() -> ValidationPatterns.requirePhone("+27123456789")); }
    @Test void phone_localDigitsAccepted()    { assertDoesNotThrow(() -> ValidationPatterns.requirePhone("0712345678")); }
    @Test void phone_lettersRejected()        { assertThrows(InvalidInputException.class, () -> ValidationPatterns.requirePhone("abc123")); }
    @Test void phone_nullRejected()           { assertThrows(InvalidInputException.class, () -> ValidationPatterns.requirePhone(null)); }
    @Test void phone_emptyRejected()          { assertThrows(InvalidInputException.class, () -> ValidationPatterns.requirePhone("")); }

    // -------------------------------------------------------------------------
    // Course code  e.g. ENG101  or  ENG-101
    // -------------------------------------------------------------------------

    @Test void courseCode_simpleAccepted()      { assertEquals("ENG101", ValidationPatterns.requireCourseCode("ENG101")); }
    @Test void courseCode_hyphenatedAccepted()  { assertDoesNotThrow(() -> ValidationPatterns.requireCourseCode("ENG-101")); }
    @Test void courseCode_spaceRejected()       { assertThrows(InvalidInputException.class, () -> ValidationPatterns.requireCourseCode("ENG 101")); }
    @Test void courseCode_nullRejected()        { assertThrows(InvalidInputException.class, () -> ValidationPatterns.requireCourseCode(null)); }

    // -------------------------------------------------------------------------
    // ISO date  YYYY-MM-DD
    // -------------------------------------------------------------------------

    @Test void isoDate_validAccepted() {
        LocalDate date = ValidationPatterns.requireIsoDate("2024-01-15");
        assertEquals(LocalDate.of(2024, 1, 15), date);
    }

    @Test void isoDate_wrongFormatRejected() {
        assertThrows(InvalidInputException.class, () -> ValidationPatterns.requireIsoDate("15-01-2024"));
    }

    @Test void isoDate_invalidDayRejected() {
        assertThrows(InvalidInputException.class, () -> ValidationPatterns.requireIsoDate("2024-02-30"));
    }

    @Test void isoDate_nullRejected() {
        assertThrows(InvalidInputException.class, () -> ValidationPatterns.requireIsoDate(null));
    }

    @Test void isoDate_slashFormatRejected() {
        assertThrows(InvalidInputException.class, () -> ValidationPatterns.requireIsoDate("2024/01/15"));
    }
}
