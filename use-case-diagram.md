# model.Student model.Grade Management System: Use-Case Diagram

This use-case diagram represents the current lab scope. The Teacher or
Academic Administrator is the primary actor and interacts with the console
application to manage students and grades.

```mermaid
flowchart TB
    teacher[Teacher / Academic Administrator]
    goal([Manage student grades and produce academic reports])
    teacher -->|uses| goal

    goal --> srcPath
    goal --> testPath

    subgraph srcPath["SRC PATH - application implementation"]
        direction TB
        srcEntry["src/app/Main<br/>application entry point"]
        srcMenu["src/view/Menu<br/>console navigation"]
        srcInput["src/input/ConsoleInputReader<br/>validated input"]
        srcServices["src/services/*<br/>student, grade, search, import"]
        srcManagers["src/manager/*<br/>storage and statistics"]
        srcDomain["src/model/*<br/>students, subjects, grades"]
        srcFeatures["src/grading and report<br/>GPA and exports"]
        srcSupport["src/exceptions and logging<br/>errors and timestamps"]

        srcEntry --> srcMenu --> srcInput --> srcServices --> srcManagers
        srcManagers --> srcDomain --> srcFeatures --> srcSupport

        srcMenu -. uses .-> srcServices
        srcServices -. uses .-> srcManagers
        srcServices -. creates .-> srcDomain
        srcFeatures -. reads .-> srcManagers
    end

    subgraph testPath["TEST PATH - verification and demonstration"]
        direction TB
        testEntry["test/*<br/>JUnit 5 + Mockito"]
        testInput["input.ConsoleInputReaderTest<br/>input validation"]
        testApp["app.MainTest and view.MenuTest<br/>entry points and navigation"]
        testServices["services.MockitoServiceIntegrationTest<br/>mocked service flows"]
        testFeatures["GPACalculatorTest and StatisticsCalculatorTest<br/>business calculations"]
        testReports["GradeReportExportTest and StatisticsViewTest<br/>reports and output"]
        testExceptions["ExceptionPathTest<br/>domain error paths"]
        testImport["BulkImportServiceTest and StudentApplicationServiceTest<br/>import and student status"]
        testCoverage["JaCoCo coverage report<br/>target/site/jacoco/index.html"]

        testEntry --> testInput --> testApp --> testServices
        testServices --> testFeatures --> testReports --> testExceptions
        testExceptions --> testImport --> testCoverage

        testInput -. verifies .-> srcInput
        testApp -. verifies .-> srcEntry
        testServices -. mocks .-> srcServices
        testFeatures -. verifies .-> srcFeatures
        testReports -. verifies .-> srcFeatures
        testExceptions -. verifies .-> srcSupport
        testImport -. verifies .-> srcServices
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
