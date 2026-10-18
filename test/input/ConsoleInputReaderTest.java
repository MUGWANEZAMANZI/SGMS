package input;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleInputReaderTest {

    @Test
    void readIntReturnsValueWithinRange() {
        TestReader reader = new TestReader("7");

        assertEquals(7, reader.readInt("Enter number: ", 1, 10));
    }

    @Test
    void readIntRepeatsAfterInvalidInput() {
        TestReader reader = new TestReader("wrong", "20", "5");
        ByteArrayOutputStream output = captureOutput(() ->
                assertEquals(5, reader.readInt("Enter number: ", 1, 10)));

        assertEquals(3, reader.readCount);
        assertTrue(output.toString().contains("valid whole number"));
        assertTrue(output.toString().contains("from 1 to 10"));
    }

    @Test
    void readDoubleReturnsValueWithinRange() {
        assertEquals(5.5,
                new TestReader("5.5").readDouble("Enter number: ", 1, 10));
    }

    @Test
    void readDoubleRepeatsAfterInvalidInput() {
        TestReader reader = new TestReader("wrong", "20", "5");
        ByteArrayOutputStream output = captureOutput(() ->
                assertEquals(5.0, reader.readDouble("Enter number: ", 1, 10)));

        assertTrue(output.toString().contains("valid number"));
        assertTrue(output.toString().contains("from 1 to 10"));
    }

    @Test
    void readRequiredSkipsBlankInputAndTrimsValue() {
        assertEquals("Alice",
                new TestReader("", " Alice ").readRequired("Name: "));
    }

    @Test
    void readEmailRejectsMissingAtSymbol() {
        assertEquals("alice@example.com",
                new TestReader("invalid", "alice@example.com")
                        .readEmail("Email: "));
    }

    @Test
    void readNameRejectsNumericInput() {
        assertEquals("Alice Smith",
                new TestReader("12345", "Alice Smith").readName("Name: "));
    }

    @Test
    void readOptionalReturnsTrimmedValueWhenProvided() {
        assertEquals("Alice", new TestReader(" Alice ").readOptional("Name: "));
    }

    @Test
    void readOptionalReturnsBlankWhenUserPressesEnter() {
        assertEquals("", new TestReader("").readOptional("Name: "));
    }

    @Test
    void readOptionalDoesNotRetryOnBlankInput() {
        TestReader reader = new TestReader("");
        reader.readOptional("Name: ");
        assertEquals(1, reader.readCount);
    }

    @Test
    void pauseConsumesInput() {
        assertDoesNotThrow(() -> new TestReader("").pause());
    }

    private static ByteArrayOutputStream captureOutput(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output));
            action.run();
            return output;
        } finally {
            System.setOut(original);
        }
    }

    private static final class TestReader extends ConsoleInputReader {
        private final Queue<String> values = new ArrayDeque<>();
        private int readCount;

        private TestReader(String... values) {
            for (String value : values) {
                this.values.add(value);
            }
        }

        @Override
        protected String readLine(String prompt) {
            readCount++;
            return values.remove();
        }
    }
}
