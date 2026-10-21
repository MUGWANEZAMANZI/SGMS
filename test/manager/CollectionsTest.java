package manager;

import grading.GPACalculator;
import model.CoreSubject;
import model.ElectiveSubject;
import model.RegularStudent;
import model.Student;
import model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.RankingService;
import services.StudentRanking;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies the TreeMap-based GPA ranking and HashSet unique-course tracking
 * introduced in the Advanced Edition (v3.0).
 */
class CollectionsTest {

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
    // TreeMap — rankByGpa
    // -------------------------------------------------------------------------

    @Test
    void rankByGpaReturnsSortedDescending() {
        Student alice = addWithGrade("Alice", "alice@test.com", 95);
        Student bob   = addWithGrade("Bob",   "bob@test.com",   75);
        Student carol = addWithGrade("Carol", "carol@test.com", 85);

        List<StudentRanking> rankings = rankingService.rankByGpa(studentManager.getStudents());

        assertEquals(3, rankings.size());
        assertTrue(rankings.get(0).cumulativeGpa() >= rankings.get(1).cumulativeGpa(),
                "First rank should have highest GPA");
        assertTrue(rankings.get(1).cumulativeGpa() >= rankings.get(2).cumulativeGpa(),
                "Second rank should have higher GPA than third");
    }

    @Test
    void rankByGpaAssignsConsecutiveRankNumbers() {
        addWithGrade("Alice", "alice@test.com", 90);
        addWithGrade("Bob",   "bob@test.com",   80);
        addWithGrade("Carol", "carol@test.com", 70);

        List<StudentRanking> rankings = rankingService.rankByGpa(studentManager.getStudents());

        for (int i = 0; i < rankings.size(); i++) {
            assertEquals(i + 1, rankings.get(i).rank(),
                    "Rank numbers must be consecutive starting at 1");
        }
    }

    @Test
    void rankByGpaTiedGpasAreBrokenAlphabeticallyById() {
        Student alice = addWithGrade("Alice", "alice@test.com", 80);
        Student bob   = addWithGrade("Bob",   "bob@test.com",   80);

        List<StudentRanking> rankings = rankingService.rankByGpa(studentManager.getStudents());

        assertEquals(2, rankings.size());
        assertEquals(1, rankings.get(0).rank());
        assertEquals(2, rankings.get(1).rank());
        // Both should have the same GPA
        assertEquals(rankings.get(0).cumulativeGpa(), rankings.get(1).cumulativeGpa(), 0.001);
    }

    @Test
    void rankByGpaEmptyInputReturnsEmptyList() {
        assertTrue(rankingService.rankByGpa(new Student[0]).isEmpty());
    }

    @Test
    void rankByGpaSingleStudentIsRankedFirst() {
        addWithGrade("Alice", "alice@test.com", 88);
        List<StudentRanking> rankings = rankingService.rankByGpa(studentManager.getStudents());
        assertEquals(1, rankings.size());
        assertEquals(1, rankings.get(0).rank());
    }

    @Test
    void rankByGpaResultIsUnmodifiable() {
        addWithGrade("Alice", "alice@test.com", 80);
        List<StudentRanking> rankings = rankingService.rankByGpa(studentManager.getStudents());
        assertThrows(UnsupportedOperationException.class, () -> rankings.add(null));
    }

    // -------------------------------------------------------------------------
    // HashSet — getUniqueCourses
    // -------------------------------------------------------------------------

    @Test
    void uniqueCoursesIsEmptyWithNoGrades() {
        assertTrue(gradeManager.getUniqueCourses().isEmpty());
    }

    @Test
    void uniqueCoursesTracksDistinctSubjectNames() {
        Student alice = new RegularStudent("Alice", 20, "alice@test.com", "0712345678");
        studentManager.addStudent(alice);

        Subject math  = new CoreSubject("Mathematics", "CORE001");
        Subject music = new ElectiveSubject("Music", "ELEC001");

        gradeManager.recordGrade(alice, math,  80);
        gradeManager.recordGrade(alice, music, 70);
        gradeManager.recordGrade(alice, math,  90); // duplicate subject — must not inflate count

        Set<String> courses = gradeManager.getUniqueCourses();
        assertEquals(2, courses.size());
        assertTrue(courses.contains("Mathematics"));
        assertTrue(courses.contains("Music"));
    }

    @Test
    void uniqueCoursesGrowsAsNewSubjectsAreAdded() {
        Student alice = new RegularStudent("Alice", 20, "alice@test.com", "0712345678");
        studentManager.addStudent(alice);

        gradeManager.recordGrade(alice, new CoreSubject("Maths", "C001"), 70);
        assertEquals(1, gradeManager.getUniqueCourses().size());

        gradeManager.recordGrade(alice, new CoreSubject("Science", "C002"), 80);
        assertEquals(2, gradeManager.getUniqueCourses().size());

        gradeManager.recordGrade(alice, new ElectiveSubject("Art", "E001"), 75);
        assertEquals(3, gradeManager.getUniqueCourses().size());
    }

    @Test
    void uniqueCoursesViewIsUnmodifiable() {
        Set<String> courses = gradeManager.getUniqueCourses();
        assertThrows(UnsupportedOperationException.class, () -> courses.add("Hacking"));
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private Student addWithGrade(String name, String email, double grade) {
        Student student = new RegularStudent(name, 20, email, "0712345678");
        studentManager.addStudent(student);
        gradeManager.recordGrade(student, new CoreSubject("Maths", "CORE001"), grade);
        return student;
    }
}
