package view;

import exceptions.FileExportException;
import input.InputReader;
import logging.ApplicationLogger;
import report.GradeReport;
import report.GradeReportFileService;
import report.GradeReportService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ExportReportView {
    private final InputReader inputReader;
    private final GradeReportService reportService;
    private final GradeReportFileService reportFileService;

    public ExportReportView(
            InputReader inputReader,
            GradeReportService reportService,
            GradeReportFileService reportFileService) {
        this.inputReader = inputReader;
        this.reportService = reportService;
        this.reportFileService = reportFileService;
    }

    public void export() {
        IO.println("\n========== EXPORT GRADE REPORT ==========");
        String studentId = inputReader.readRequired(
                "Enter the student ID to export: ");
        GradeReport report = reportService.createReport(studentId);

        Path directory = Path.of("grade_reports");
        Path file = directory.resolve(studentId.toUpperCase() + "_report.txt");
        try {
            Files.createDirectories(directory);
            reportFileService.export(report, file);
            IO.println("Report exported to " + file.toAbsolutePath());
        } catch (IOException exception) {
            ApplicationLogger.error("Could not export grade report", exception);
            throw new FileExportException("Could not export report: "
                    + exception.getMessage(), exception);
        }
    }
}
