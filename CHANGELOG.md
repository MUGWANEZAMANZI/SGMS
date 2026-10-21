# Changelog

All notable changes to the Student Grade Management System are documented in
this file.

The project follows a simplified version of
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and release entries
use semantic versioning where applicable.

## [3.0.0] — Advanced Edition

This release upgrades the system to an enterprise-grade application. Every
feature below maps directly to the Student Grade Mgt III specification.

### Collections & Data Structures

- `StudentManager` uses `LinkedHashMap<String, Student>` for O(1) lookup by
  student ID while preserving insertion order for consistent display.
- `GradeManager` now tracks unique course names in a `HashSet<String>`;
  `getUniqueCourses()` returns an unmodifiable view used wherever distinct
  subject names are needed.
- `RankingService.rankByGpa()` builds a `TreeMap<Double, List<StudentGPAResult>>`
  with `Comparator.reverseOrder()` so GPA rankings are always sorted
  highest-first without a separate sort pass; tied GPAs are broken
  alphabetically by student ID.
- `StatisticsCalculator` uses `HashMap` for frequency counting and subject
  totals, and `LinkedHashMap` for grade-distribution output to guarantee
  the A → F display order.
- `ScheduledGpaProcessor` tracks previous GPA snapshots in a
  `ConcurrentHashMap<String, Double>` so change notifications fire only
  when a value actually moves.

### Regex Validation (`validation.ValidationPatterns`)

- Student ID: `STU\d{3,}` (case-insensitive).
- Email: RFC 5321-compliant multi-part pattern.
- Phone: international and local formats (`+27…`, `(0…)`, etc.).
- Course code: alphanumeric segments separated by hyphens (`ENG-101`).
- ISO date: `YYYY-MM-DD` with `LocalDate.parse` confirmation.

### Concurrent & Scheduled Processing

- `ConcurrentReportService` — `FixedThreadPool(4)` generates one grade
  report per student in parallel; results are returned in input order.
- `StatisticsDashboard` — `ScheduledExecutorService` recalculates class
  statistics on a background daemon thread every 5 seconds and caches the
  latest result for instant retrieval by the UI.
- `ScheduledGpaProcessor` — separate scheduled thread recomputes every
  student's GPA on the same 5-second cycle and emits `GradeNotification`
  events when values change.
- `ParallelBatchProcessor<I, O>` — generic `FixedThreadPool` wrapper used
  by `BulkImportService.importCSVParallel()` to process CSV rows
  concurrently while preserving result order.
- `AuditTrail` — `LinkedBlockingQueue` feeds a single background writer
  thread; concurrent callers never block on file I/O.

### Multi-Format File I/O (NIO.2)

All file operations use the `java.nio.file` API (`Path`, `Files.lines()`,
`Files.writeString()`, `Files.newInputStream/OutputStream`).

- **Export:** `CsvGradeReportExporter`, `JsonGradeReportExporter`,
  `BinaryGradeReportExporter`, `TextGradeReportExporter`.
- **Import:** `CsvGradeReportImporter`, `JsonGradeReportImporter`,
  `BinaryGradeReportImporter`.
- **Student import:** CSV, JSON, and binary formats via
  `StudentImportService` (dispatches by file extension).
- **Bulk grade import:** `BulkImportService` streams the CSV with
  `Files.lines()`, writes a timestamped failure log, and returns an
  `ImportSummary` record.

### Directory Monitoring (`ImportWatcher`)

- New `services.ImportWatcher` uses `java.nio.file.WatchService` to monitor
  the `imports/` directory for newly created files.
- CSV files whose name contains `"grades"` are automatically sent to
  `BulkImportService`; all other `.csv`, `.json`, and `.bin` files are sent
  to `StudentImportService`.
- Runs on a single daemon thread; wired into `Main`'s `try-with-resources`
  block so it shuts down cleanly when the application exits.

### Advanced Search

- `StudentManager.findStudentsByPattern(String)` compiles the user's input
  as a case-insensitive `java.util.regex.Pattern` and filters the student
  map with a stream.
- `StudentSearchService.searchByPattern(String)` wraps the call with a
  `ThreadSafeLruCache` (100-entry, 5-minute TTL) so repeated patterns hit
  the cache instead of recompiling the regex.
- Search menu now offers five options: by ID, name, grade range, student
  type, and **By Pattern (Regex)**. Invalid patterns surface as
  `InvalidInputException` rather than crashing the menu loop.

### Caching (`cache.ThreadSafeLruCache<K, V>`)

- Access-ordered `LinkedHashMap` with `synchronized` methods implements LRU
  eviction: once the capacity (default 100) is reached, the least-recently-
  used entry is removed automatically.
- Each entry carries an `Instant expiresAt` TTL; expired entries are
  evicted on the next `get()` call.

### Application Architecture

- All long-lived services (`StatisticsDashboard`, `ScheduledGpaProcessor`,
  `ConcurrentReportService`, `ImportWatcher`) are opened together in a
  single `try-with-resources` block in `Main.start()`, guaranteeing
  thread-pool shutdown even if the menu throws.
- `BulkImportService` and `StudentImportService` are instantiated once and
  shared between `Menu` and `ImportWatcher` to avoid duplicate state.

### Testing

- 178 `@Test` methods across 33 test classes.
- Unit coverage: collections, regex patterns, stream pipelines, file I/O
  (CSV, JSON, binary), cache eviction, batch processing, and audit trail.
- Integration tests: `MockitoServiceIntegrationTest`, `ImportPipelineTest`,
  `PatternSearchCachingTest`, `ParallelBulkImportTest`.
- JaCoCo configured for HTML coverage reports.

---

## [Unreleased]

### Added

- Use-case documentation for the teacher workflow and system interactions.
- JUnit tests for console input validation, application actions, menu
  delegation, statistics output, GPA calculation, report exporting, and
  exception paths.
- Mockito-based service integration tests with injected dependencies.
- JaCoCo Maven integration for generating HTML coverage reports.

### Changed

- Refactored `Main` to use constructor-injected services instead of static
  application-wide dependencies.
- Refactored `Menu` to use the injected `ApplicationActions` abstraction
  instead of calling `Main` statically.
- Added timestamped application logging for startup, menu failures, report
  export failures, and CSV imports.
- Added bulk-import summaries and timestamped import failure logs.
- Added search-result actions for viewing details, exporting results, and
  starting another search.

### Fixed

- Replaced generic runtime exceptions in production code with domain-specific
  exceptions for invalid input, invalid grades, missing students, import
  format errors, capacity limits, and file-export failures.
- Fixed JUnit test construction so tests use `@BeforeEach` or injected test
  dependencies rather than unsupported constructor parameter resolution.
- Added explicit Mockito dependencies to the Maven build.

## [1.0.0]

### Added

- Student registration for Regular and Honors students.
- Automatic student and grade ID generation.
- Core and Elective subject support.
- Grade recording and validation for values from 0 to 100.
- Student averages, passing status, and grade reports.
- GPA conversion using the four-point grading scale.
- Class statistics including mean, median, mode, standard deviation, highest
  and lowest grades, subject averages, and student-type comparisons.
- Student search by ID, name, average range, and student type.
- Bulk grade import from CSV files.
- Text grade report export.
- JUnit 5 and Mockito test infrastructure.
- GitHub Actions automated test execution.
