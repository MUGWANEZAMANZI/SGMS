package contract;

import java.io.IOException;

public interface CSVParser {
    String COMMA_DELIMITER = ",";

    void importCSV(String filePath) throws IOException;
}
