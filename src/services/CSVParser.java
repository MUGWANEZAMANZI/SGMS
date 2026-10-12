package services;

import java.io.IOException;

public interface CSVParser {
    String COMMA_DELIMITER = ",";

    ImportSummary importCSV(String filePath) throws IOException;
}
