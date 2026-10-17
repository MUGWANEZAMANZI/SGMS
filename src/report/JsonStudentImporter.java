package report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Imports the JSON object format produced for student records.
 */
public final class JsonStudentImporter implements StudentImporter {
    private static final Pattern OBJECT_PATTERN =
            Pattern.compile("\\{([^{}]*)\\}");
    private static final Pattern FIELD_PATTERN =
            Pattern.compile("\"([^\"]+)\"\\s*:\\s*(\"(?:\\\\.|[^\"])*\"|-?\\d+)");

    @Override
    public List<StudentRecord> importStudents(Path file) throws IOException {
        String json = Files.readString(file);
        Matcher objects = OBJECT_PATTERN.matcher(json);
        List<StudentRecord> records = new java.util.ArrayList<>();
        while (objects.find()) {
            java.util.Map<String, String> fields = new java.util.HashMap<>();
            Matcher values = FIELD_PATTERN.matcher(objects.group(1));
            while (values.find()) {
                fields.put(values.group(1), unquote(values.group(2)));
            }
            if (fields.containsKey("name")) {
                records.add(new StudentRecord(
                        required(fields, "name"),
                        Integer.parseInt(required(fields, "age")),
                        required(fields, "email"),
                        required(fields, "phone"),
                        required(fields, "type"),
                        required(fields, "status")));
            }
        }
        return List.copyOf(records);
    }

    private static String required(java.util.Map<String, String> fields, String key) {
        String value = fields.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing JSON student field: " + key);
        }
        return value;
    }

    private static String unquote(String value) {
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1)
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");
        }
        return value;
    }
}
