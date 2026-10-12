# model.Student model.Grade Management System: Use-Case Diagram

This use-case diagram represents the current lab scope. The Teacher or
Academic Administrator is the primary actor and interacts with the console
application to manage students and grades.

```mermaid
flowchart TB
    teacher[Teacher / Academic Administrator]
    goal([Manage student grades and produce academic reports])
    teacher -->|uses| goal

    goal --> srcRoot
    goal --> testRoot

    subgraph srcRoot["src/ - application implementation"]
        direction TB
        main["app.Main<br/>application entry point"]
        menu["view.Menu<br/>console navigation"]
        input["input.ConsoleInputReader<br/>validated input"]
        studentApp["services.StudentApplicationService<br/>add/view students"]
        gradeService["services.GradeService<br/>record grades"]
        searchService["services.StudentSearchService<br/>find and act on results"]
        bulk["services.BulkImportService<br/>CSV import and summary"]
        gpa["grading.GPACalculator<br/>percentage to GPA"]
        statsView["view.StatisticsView<br/>class statistics"]
        reportService["report.GradeReportService<br/>build reports"]
        exporter["report.TextGradeReportExporter<br/>write report files"]
        studentManager["manager.StudentManager<br/>student storage/search"]
        gradeManager["manager.GradeManager<br/>grade storage/validation"]
        student["model.Student"]
        regular["model.RegularStudent"]
        honors["model.HonorsStudent"]
        subject["model.Subject"]
        core["model.CoreSubject"]
        elective["model.ElectiveSubject"]
        grade["model.Grade"]
        exceptions["exceptions/*<br/>domain-specific errors"]
        logger["logging.ApplicationLogger<br/>timestamped logs"]

        main --> menu
        main --> studentApp
        main --> gradeService
        main --> searchService
        main --> bulk
        main --> statsView
        menu --> input
        menu --> studentApp
        menu --> gradeService
        menu --> searchService
        menu --> bulk
        menu --> statsView
        menu --> mainActions["app.ApplicationActions"]
        studentApp --> studentManager
        gradeService --> studentManager
        gradeService --> gradeManager
        searchService --> studentManager
        bulk --> studentManager
        bulk --> gradeManager
        statsView --> stats["manager.StatisticsCalculator"]
        stats --> studentManager
        stats --> gradeManager
        reportService --> studentManager
        reportService --> gradeManager
        exporter --> report["report.GradeReport"]
        studentManager --> student
        gradeManager --> grade
        student --> regular
        student --> honors
        grade --> subject
        subject --> core
        subject --> elective
        studentApp -.-> exceptions
        gradeService -.-> exceptions
        bulk -.-> exceptions
        menu -.-> logger
        bulk -.-> logger
    end

    subgraph testRoot["test/ - verification and demonstration"]
        direction TB
        inputTests["input.ConsoleInputReaderTest"]
        mainTests["app.MainTest"]
        menuTests["view.MenuTest"]
        statsTests["view.StatisticsViewTest"]
        gpaTests["grading.GPACalculatorTest"]
        statsCalcTests["manager.StatisticsCalculatorTest"]
        reportTests["report.GradeReportExportTest"]
        serviceTests["services.MockitoServiceIntegrationTest"]
        exceptionTests["services.ExceptionPathTest"]
        statusTests["services.StudentApplicationServiceTest"]
        importTests["services.BulkImportServiceTest"]

        inputTests -. tests .-> input
        mainTests -. tests .-> main
        menuTests -. tests .-> menu
        statsTests -. tests .-> statsView
        gpaTests -. tests .-> gpa
        statsCalcTests -. tests .-> stats
        reportTests -. tests .-> exporter
        serviceTests -. mocks/tests .-> studentApp
        serviceTests -. mocks/tests .-> gradeService
        serviceTests -. mocks/tests .-> searchService
        serviceTests -. mocks/tests .-> bulk
        exceptionTests -. tests .-> exceptions
        statusTests -. tests .-> student
        importTests -. tests .-> bulk
    end
```

## Actors and use cases

| Element | Responsibility |
| --- | --- |
| Teacher / Academic Administrator | Uses the menu to manage student and grade information |
| Add student | Registers a Regular or Honors student |
| View all students | Displays student details, averages, and status |
| Record grade | Adds a Core or Elective grade to an existing student |
| View grade report | Shows a student's grade history and performance |
| Validate input | Ensures required fields and numeric ranges are valid |
| Calculate average and status | Determines the average and passing or failing status |
| Generate IDs | Creates unique student and grade identifiers |

## Implemented types shown in the diagram

| Type | Responsibility |
| --- | --- |
| `Main` | Coordinates the application operations and input validation |
| `Menu` | Displays the menu and controls navigation |
| `Student` | Abstract student state, grade array, and calculations |
| `RegularStudent` | model.Student subtype with a 50% passing threshold |
| `HonorsStudent` | model.Student subtype with a 60% passing threshold and honors eligibility |
| `Subject` | Abstract subject state and behavior |
| `CoreSubject` | Mandatory subject implementation |
| `ElectiveSubject` | Optional subject implementation |
| `Grade` | Validated grade value, subject, student ID, and date |
| `StudentManager` | Array-backed student storage and lookup |
| `GradeManager` | Array-backed grade history and lookup |
| `Gradable` | model.Grade validation and recording contract in `manager` |
| `StudentView` | model.Student display helper |
