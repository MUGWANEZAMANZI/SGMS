package app;

import input.InputReader;
import input.ConsoleInputReader;
import grading.GPACalculator;
import services.*;
import manager.GradeManager;
import manager.StudentManager;
import manager.StatisticsCalculator;
import report.GradeReportService;
import report.StudentImportService;
import report.GradeReportFileService;
import view.Menu;
import view.GPAView;
import view.GradeReportView;
import view.ExportReportView;
import view.StatisticsView;
import logging.ApplicationLogger;

import java.time.Duration;

/**
 * Wires the application's services and starts the menu.
 */
public class Main {
    private final StudentManager studentManager;
    private final GradeManager gradeManager;
    private final InputReader inputReader;
    private final GradeService gradeService;
    private final StudentSearchService studentSearchService;
    private final GPAView gpaView;
    private final GradeReportService reportService;
    private final GradeReportFileService reportFileService;

    public Main(
            StudentManager studentManager,
            GradeManager gradeManager,
            InputReader inputReader,
            GradeService gradeService,
            StudentSearchService studentSearchService,
            GPACalculator gpaCalculator,
            GradeReportService reportService) {

        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
        this.inputReader = inputReader;
        this.gradeService = gradeService;
        this.studentSearchService = studentSearchService;
        this.gpaView = new GPAView(
                studentManager,
                inputReader,
                new RankingService(gradeManager, gpaCalculator));
        this.reportService = reportService;
        this.reportFileService = new GradeReportFileService();
    }

    static void main(String[] args) {
        ApplicationLogger.info("Starting Student Grade Management System");
        createDefault().start();
    }

    private static Main createDefault() {
        StudentManager students = new StudentManager();
        GradeManager grades = new GradeManager();
        InputReader input = new ConsoleInputReader();
        GradeService gradeService = new GradeService(students, grades, input);
        StudentSearchService searchService = new StudentSearchService(students, input);
        return new Main(students, grades, input, gradeService, searchService,
                new GPACalculator(), new GradeReportService(students, grades));
    }

    private void start() {
        StudentApplicationService studentApplicationService =
                new StudentApplicationService(studentManager, inputReader);
        GradeReportView gradeReportView = new GradeReportView(inputReader, reportService);
        ExportReportView exportReportView =
                new ExportReportView(inputReader, reportService, reportFileService);

        try (StatisticsDashboard dashboard = new StatisticsDashboard(
                new StatisticsCalculator(studentManager, gradeManager),
                result -> {});
             ScheduledGpaProcessor gpaProcessor = new ScheduledGpaProcessor(
                     studentManager,
                     new RankingService(gradeManager, new GPACalculator()),
                     result -> {})) {
            StatisticsView statisticsView = new StatisticsView(dashboard::getLatest);
            dashboard.start(Duration.ofSeconds(5));
            gpaProcessor.start(Duration.ofSeconds(5));
            new Menu(
                studentApplicationService,
                gradeService,
                studentSearchService,
                new BulkImportService(studentManager, gradeManager),
                inputReader,
                statisticsView,
                gradeReportView,
                exportReportView,
                gpaView,
                new StudentImportService(studentManager)).start();
        }
    }
}
