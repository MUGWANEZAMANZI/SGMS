package report;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Imports student records written as length-prefixed binary strings.
 */
public final class BinaryStudentImporter implements StudentImporter {
    @Override
    public List<StudentRecord> importStudents(Path file) throws IOException {
        List<StudentRecord> records = new ArrayList<>();
        try (DataInputStream input = new DataInputStream(Files.newInputStream(file))) {
            int count = input.readInt();
            if (count < 0) {
                throw new IOException("Invalid student record count: " + count);
            }
            for (int index = 0; index < count; index++) {
                records.add(new StudentRecord(
                        input.readUTF(),
                        input.readInt(),
                        input.readUTF(),
                        input.readUTF(),
                        input.readUTF(),
                        input.readUTF()));
            }
        }
        return List.copyOf(records);
    }
}
