package services;

public record ImportSummary(int totalRows, int importedRows, int failedRows, String logFile) {
}
