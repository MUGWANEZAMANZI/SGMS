package services;

import grading.GPACalculator;
import manager.GradeManager;
import manager.StudentManager;
import model.CoreSubject;
import model.ElectiveSubject;
import model.RegularStudent;
import model.Student;
import model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies stream-based operations: map, filter, sorted, and collect/toList
 * as used in RankingService and StudentManager pattern search.
 */
class StreamProcessingTest {

    private StudentManager studentManager;
    private GradeManager gradeManager;
    private RankingService rankingService;

    @BeforeEach
    void setUp() {
        studentManager = new StudentManager();
        gradeManager = new GradeManager();
        rankingService = new RankingService(gradeManager, new GPACalculator());
    }

    // -------------------------------------------------------------------------
    // map — each Student maps to a StudentRanking
    // -------------------------------------------------------------------------

    @Test
    void rankByMarksMapsTwoStudentsToTwoRankings() {
        addWithGrade("Alice", "alice@test.com", 90);
        addWithGrade("Bob",   "bob@test.com",   70);

        List<StudentRanking> rankings = rankingService.rankByMarks(studentManager.getStudents());

        assertEquals(2, rankings.size());
        List<String> names = rankings.stream()
                .map(r -> r.student().getName())
                .toList();
        assertTrue(names.contains("Alice"));
        assertTrue(names.contains("Bob"));
    }

    @Test
    void rankByGpaMapsTwoStudentsToTwoRankings() {
        addWithGrade("Carol", "carol@test.com", 85);
        addWithGrade("Dave",  "dave@test.com",  65);

        List<StudentRanking> rankings = rankingService.rankByGpa(studentManager.getStudents());

        assertEquals(2, rankings.size());
        List<String> names = rankings.stream()
                .map(r -> r.student().getName())
                .toList();
        assertTrue(names.contains("Carol"));
        assertTrue(names.contains("Dave"));
    }

    // -------------------------------------------------------------------------
    // sorted — output order must be descending by mark / GPA
    // -------------------------------------------------------------------------

    @Test
    void rankByMarksSortsDescendingByAverage() {
        addWithGrade("Low",  "low@test.com",  60);
        addWithGrade("High", "high@test.com", 95);
        addWithGrade("Mid",  "mid@test.com",  75);

        List<StudentRanking> rankings = rankingService.rankByMarks(studentManager.getStudents());

        for (int i = 0; i < rankings.size() - 1; i++) {
            assertTrue(rankings.get(i).averageMark() >= rankings.get(i + 1).averageMark(),
                    "Each entry must have average >= the one after it");
        }
    }

    @Test
    void rankByGpaSortsDescendingByCumulativeGpa() {
        addWithGrade("Low",  "low@test.com",  55);
        addWithGrade("High", "high@test.com", 92);

        List<StudentRanking> rankings = rankingService.rankByGpa(studentManager.getStudents());

        assertTrue(rankings.get(0).cumulativeGpa() >= rankings.get(1).cumulativeGpa());
    }

    // -------------------------------------------------------------------------
    // filter — findStudentsByPattern uses stream + filter internally
    // -------------------------------------------------------------------------

    @Test
    void patternFilterReturnsOnlyMatchingStudents() {
        Student alice = new RegularStudent("Alice", 20, "alice@test.com", "0712345678");
        Student bob   = new RegularStudent("Bob",   21, "bob@test.com",   "0712345679");
        studentManager.addStudent(alice);
        studentManager.addStudent(bob);

        Student[] found = studentManager.findStudentsByPattern("^Al");

        assertEquals(1, found.length);
        assertEquals("Alice", found[0].getName());
    }

    @Test
    void patternFilterWithNoMatchReturnsEmptyArray() {
        Student alice = new RegularStudent("Alice", 20, "alice@test.com", "0712345678");
        studentManager.addStudent(alice);

        Student[] found = studentManager.findStudentsByPattern("^Z");

        assertEquals(0, found.length);
    }

    @Test
    void patternFilterIsCaseInsensitive() {
        Student alice = new RegularStudent("Alice", 20, "alice@test.com", "0712345678");
        studentManager.addStudent(alice);

        Student[] found = studentManager.findStudentsByPattern("alice");

        assertEquals(1, found.length);
    }

    @Test
    void patternFilterMatchesMultipleStudents() {
        studentManager.addStudent(new RegularStudent("Anna", 20, "anna@test.com", "0711111111"));
        studentManager.addStudent(new RegularStudent("Andrew", 21, "andrew@test.com", "0722222222"));
        studentManager.addStudent(new RegularStudent("Bob", 22, "bob@test.com", "0733333333"));

        Student[] found = studentManager.findStudentsByPattern("^An");

        assertEquals(2, found.length);
    }

    // -------------------------------------------------------------------------
    // collect / reduce — averageMark is effectively reduce(sum)/count
    // -------------------------------------------------------------------------

    @Test
    void averageMarkIsCorrectReduceOfGradeValues() {
        Student alice = new RegularStudent("Alice", 20, "alice@test.com", "0712345678");
        studentManager.addStudent(alice);
        gradeManager.recordGrade(alice, new CoreSubject("Maths", "C001"), 80);
        gradeManager.recordGrade(alice, new ElectiveSubject("Art",   "E001"), 60);

        StudentGPAResult result = rankingService.calculateStudentGPA(alice);

        assertEquals(70.0, result.averageMark(), 0.01,
                "Average of 80 and 60 must be 70");
    }

    @Test
    void collectToListPreservesAllRankings() {
        addWithGrade("A", "a@test.com", 90);
        addWithGrade("B", "b@test.com", 80);
        addWithGrade("C", "c@test.com", 70);

        List<StudentRanking> byMarks = rankingService.rankByMarks(studentManager.getStudents());
        List<StudentRanking> byGpa   = rankingService.rankByGpa(studentManager.getStudents());

        assertEquals(3, byMarks.size());
        assertEquals(3, byGpa.size());
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private void addWithGrade(String name, String email, double grade) {
        Student student = new RegularStudent(name, 20, email, "0712345678");
        studentManager.addStudent(student);
        gradeManager.recordGrade(student, new CoreSubject("Maths", "CORE001"), grade);
    }
}
