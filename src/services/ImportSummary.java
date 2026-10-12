package services;

/**
 * Summarizes the result of a CSV import.
 *
 * @param totalRows total data rows processed
 * @param importedRows rows imported successfully
 * @param failedRows rows that failed validation or import
 * @param logFile path to the failure log
 */
public record ImportSummary(int totalRows, int importedRows, int failedRows, String logFile) {
}
