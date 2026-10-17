package view;

import input.InputReader;
import manager.StudentManager;
import services.RankingService;
import services.StudentGPAResult;
import services.StudentRanking;

import java.util.List;
import java.util.Objects;

/**
 * Collects GPA lookup input and displays calculated GPA and class rankings.
 */
public final class GPAView {
    private final StudentManager studentManager;
    private final InputReader inputReader;
    private final RankingService rankingService;

    /**
     * Creates a GPA view.
     *
     * @param studentManager source of students
     * @param inputReader source of user input
     * @param rankingService GPA and ranking service
     */
    public GPAView(
            StudentManager studentManager,
            InputReader inputReader,
            RankingService rankingService) {
        this.studentManager = Objects.requireNonNull(studentManager);
        this.inputReader = Objects.requireNonNull(inputReader);
        this.rankingService = Objects.requireNonNull(rankingService);
    }

    /** Prompts for a student and displays subject GPA and class rankings. */
    public void displayStudentGPA() {
        IO.println("\n============= CALCULATE GPA =============");
        String studentId = inputReader.readRequired(
                "Enter the student ID to calculate GPA: ");
        var student = studentManager.findStudentById(studentId);
        if (student == null) {
            IO.println("No student found with ID " + studentId + ".");
            return;
        }

        StudentGPAResult result = rankingService.calculateStudentGPA(student);
        IO.println("\nStudent: %s - %s".formatted(
                student.getStudentId(), student.getName()));
        IO.println("Overall average: %.2f%%".formatted(result.averageMark()));
        IO.println("\n%-22s %-12s %-8s %-8s".formatted(
                "SUBJECT", "PERCENTAGE", "GPA", "LETTER"));
        IO.println("--------------------------------------------------------");
        for (StudentGPAResult.SubjectGPAResult subject : result.subjectResults()) {
            var point = subject.gradePoint();
            IO.println("%-22s %9.2f%% %8.1f %-8s".formatted(
                    subject.subjectName(),
                    point.percentage(),
                    point.gpa(),
                    point.letterGrade()));
        }
        IO.println("--------------------------------------------------------");
        IO.println("Cumulative GPA: %.2f / 4.00".formatted(result.cumulativeGpa()));

        List<StudentRanking> rankings = rankingService.rankByGpa(
                studentManager.getStudents());
        int classRank = rankings.stream()
                .filter(ranking -> ranking.student().getStudentId()
                        .equalsIgnoreCase(student.getStudentId()))
                .mapToInt(StudentRanking::rank)
                .findFirst()
                .orElse(0);
        IO.println("Class rank: %d of %d".formatted(
                classRank, rankings.size()));
        IO.println("Status: " + statusText(student));

        displayRanking("CLASS GPA RANKING", rankings, true);
        displayRanking("CLASS MARK RANKING",
                rankingService.rankByMarks(studentManager.getStudents()), false);
    }

    private static void displayRanking(
            String title,
            List<StudentRanking> rankings,
            boolean useGpa) {
        IO.println("\n" + title);
        IO.println("---------------------------");
        for (StudentRanking ranking : rankings) {
            double value = useGpa
                    ? ranking.cumulativeGpa()
                    : ranking.averageMark();
            String suffix = useGpa ? " GPA" : "%";
            IO.println("%d. %s - %.2f%s".formatted(
                    ranking.rank(),
                    ranking.student().getName(),
                    value,
                    suffix));
        }
    }

    private static String statusText(model.Student student) {
        if (student.getGrades().length == 0) {
            return "No grades";
        }
        return student.isPassing() ? "Passing" : "Failing";
    }
}
