# Changelog

All notable changes to the Student Grade Management System are documented in
this file.

The project follows a simplified version of
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and release entries
use semantic versioning where applicable.

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
