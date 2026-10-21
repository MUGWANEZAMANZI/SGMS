package services;

import logging.ApplicationLogger;
import report.StudentImportService;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Monitors a directory with NIO.2 WatchService and auto-imports files dropped into it.
 * CSV files whose name contains "grades" are imported as grade rows;
 * all other CSV/JSON/binary files are imported as student records.
 */
public final class ImportWatcher implements AutoCloseable {
    private final Path watchDirectory;
    private final BulkImportService bulkImportService;
    private final StudentImportService studentImportService;
    private final ExecutorService executor;

    public ImportWatcher(
            Path watchDirectory,
            BulkImportService bulkImportService,
            StudentImportService studentImportService) {
        this.watchDirectory = watchDirectory;
        this.bulkImportService = bulkImportService;
        this.studentImportService = studentImportService;
        this.executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "import-watcher");
            thread.setDaemon(true);
            return thread;
        });
    }

    /** Starts watching the directory in the background. */
    public void start() {
        executor.submit(this::watch);
    }

    private void watch() {
        try {
            Files.createDirectories(watchDirectory);
        } catch (IOException exception) {
            ApplicationLogger.error("ImportWatcher: cannot create watch directory", exception);
            return;
        }
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            watchDirectory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            ApplicationLogger.info("ImportWatcher active — watching: "
                    + watchDirectory.toAbsolutePath());
            while (!Thread.currentThread().isInterrupted()) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    @SuppressWarnings("unchecked")
                    Path filename = ((WatchEvent<Path>) event).context();
                    processFile(watchDirectory.resolve(filename));
                }
                if (!key.reset()) {
                    break;
                }
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        } catch (IOException exception) {
            ApplicationLogger.error("ImportWatcher stopped unexpectedly", exception);
        }
    }

    private void processFile(Path file) {
        String name = file.getFileName().toString().toLowerCase();
        try {
            if (name.contains("grades") && name.endsWith(".csv")) {
                ImportSummary summary = bulkImportService.importCSV(file.toString());
                ApplicationLogger.info("Auto-imported grades from " + file.getFileName()
                        + ": " + summary.importedRows() + "/" + summary.totalRows() + " rows");
            } else if (name.endsWith(".csv") || name.endsWith(".json") || name.endsWith(".bin")) {
                int count = studentImportService.importStudents(file);
                ApplicationLogger.info("Auto-imported " + count
                        + " students from " + file.getFileName());
            }
        } catch (IOException | RuntimeException exception) {
            ApplicationLogger.error(
                    "Auto-import failed for " + file.getFileName(), exception);
        }
    }

    @Override
    public void close() {
        executor.shutdownNow();
    }
}
