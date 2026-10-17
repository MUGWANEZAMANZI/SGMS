package services;

import java.io.IOException;

/**
 * Imports grade data from CSV files.
 */
public interface CSVParser {
    String COMMA_DELIMITER = ",";

    /**
     * Imports data from a CSV file.
     *
     * @param filePath path to the CSV file
     * @return a summary of the import
     * @throws IOException if the file cannot be read or the log cannot be written
     */
    ImportSummary importCSV(String filePath) throws IOException;
}
