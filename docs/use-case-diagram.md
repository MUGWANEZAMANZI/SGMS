# Student Grade Management System: Use-Case Diagram

This use-case diagram represents the current lab scope. The Teacher or
Academic Administrator is the primary actor and interacts with the console
application to manage students and grades.

```mermaid
flowchart LR
    teacher[Teacher / Academic Administrator]

    subgraph system[Student Grade Management System]
        add((Add student))
        view((View all students))
        record((Record grade))
        report((View grade report))
        validate((Validate input))
        average((Calculate average and status))
        ids((Generate student and grade IDs))
        subjects((Select Core or Elective subject))
    end

    subgraph classes[Implemented Java classes and types]
        main[Main]
        menu[Menu]
        student[Student]
        regular[RegularStudent]
        honors[HonorsStudent]
        subject[Subject]
        core[CoreSubject]
        elective[ElectiveSubject]
        grade[Grade]
        studentManager[StudentManager]
        gradeManager[GradeManager]
        gradable[Gradable]
        viewHelper[StudentView]
    end

    teacher --> add
    teacher --> view
    teacher --> record
    teacher --> report

    add -. includes .-> validate
    add -. includes .-> ids
    record -. includes .-> validate
    record -. includes .-> subjects
    record -. includes .-> ids
    view -. includes .-> average
    report -. includes .-> average

    menu -. starts .-> main
    main -. coordinates .-> add
    main -. coordinates .-> view
    main -. coordinates .-> record
    main -. coordinates .-> report
    student -. specialized by .-> regular
    student -. specialized by .-> honors
    subject -. specialized by .-> core
    subject -. specialized by .-> elective
    studentManager -. manages .-> student
    gradeManager -. manages .-> grade
    gradeManager -. implements .-> gradable
    grade -. references .-> subject
    viewHelper -. displays .-> student
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
| `RegularStudent` | Student subtype with a 50% passing threshold |
| `HonorsStudent` | Student subtype with a 60% passing threshold and honors eligibility |
| `Subject` | Abstract subject state and behavior |
| `CoreSubject` | Mandatory subject implementation |
| `ElectiveSubject` | Optional subject implementation |
| `Grade` | Validated grade value, subject, student ID, and date |
| `StudentManager` | Array-backed student storage and lookup |
| `GradeManager` | Array-backed grade history and lookup |
| `Gradable` | Grade validation and recording contract |
| `StudentView` | Student display helper |
