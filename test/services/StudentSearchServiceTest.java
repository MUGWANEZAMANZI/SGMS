package services;

import input.InputReader;
import manager.GradeManager;
import manager.Searchable;
import manager.StudentManager;
import model.CoreSubject;
import model.RegularStudent;
import model.Student;
import model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.nio.file.Files;
import java.nio.file.Path;

class StudentSearchServiceTest {
    private Searchable studentSearch;
    private StudentManager studentManager;
    private GradeManager gradeManager;

    @BeforeEach
    void setUp() {
        studentSearch = new StudentManager();
        studentManager = new StudentManager();
        gradeManager = new GradeManager();
    }


    @Test
    void searchWithInvalidIdTest() {
        Student student = studentManager.findStudentById("STU999");
        assertNull(student, "Expected no student to be found with invalid ID");

    }


    @Test
    void searchWithValidIdIdTest() {
        Student student = new RegularStudent("John Doe", 20, "john@doe.com", "0712345678");
        studentManager.addStudent(student);
        Student foundStudent = studentManager.findStudentById(student.getStudentId());
        assertInstanceOf(Student.class, foundStudent, "Expected object of Student class");
        assertEquals("John Doe", foundStudent.getName());
    }

    @Test
    void searchWithInvalidTypeTest() {
        Student student = new RegularStudent("John Doe", 20, "john@doe.com", "0712345678");
        studentManager.addStudent(student);
        String options = "Standard";
        Student[] students = studentManager.findStudentsByType(options);
        assertTrue(students.length == 0, "Expected no students to be found with invalid type");
    }

    @Test
    void searchWithValidTypeTest() {
        Student student = new RegularStudent("John Doe", 20, "john@doe.com", "0712345678");
        studentManager.addStudent(student);
        Student[] students = studentManager.findStudentsByType("Regular");
        assertTrue(students.length > 0, "Expected students to be found with valid type");
    }


    @Test
    void findStudentsByNameTest() {
        Student student = new RegularStudent("John Doe", 20, "john@doe.com", "0712345678");
        studentManager.addStudent(student);
        Student[] students = studentManager.findStudentsByName("John");
        assertTrue(students.length > 0, "Expected students to be found with valid name");
    }

    @Test
    void findStudentsByInvalidNameTest() {
        Student student = new RegularStudent("John Doe", 20, "john@doe.com", "0712345678");
        studentManager.addStudent(student);
        Student[] students = studentManager.findStudentsByName("Jane");
        assertTrue(students.length == 0, "Expected no students to be found with invalid name");
    }

    @Test
    void findStudentsByAverageTest() {
        Student student = new RegularStudent("John Doe", 20, "  john@doe.com", "0712345678");
        Subject subject = new CoreSubject("Mathematics", "CORE001");
        studentManager.addStudent(student);
        gradeManager.recordGrade(student, subject, 75);
        Student[] students = studentManager.findStudentsByAverage(10, 80.0);
        assertTrue(students.length > 0, "Expected students to be found with valid average");
    }

    @Test
    void findStudentsByInvalidAverageTest() {
        Student student = new RegularStudent("John Doe", 20, "john@doe.com", "0712345678");
        Subject subject = new CoreSubject("Mathematics", "CORE001");
        studentManager.addStudent(student);
        gradeManager.recordGrade(student, subject, 75);
        Student[] students = studentManager.findStudentsByAverage(80, 100);
        assertTrue(students.length == 0, "Expected no students to be found with invalid average");
    }

    @Test
    void searchDisplaysNoResultsWithoutRequestingAnAction() {
        Searchable search = mock(Searchable.class);
        InputReader input = mock(InputReader.class);
        when(input.readInt("Select search option: ", 1, 5)).thenReturn(1);
        when(input.readRequired("Enter student ID: ")).thenReturn("STU999");

        new StudentSearchService(search, input).search();

        verify(search).findStudentById("STU999");
        verify(input, never()).readInt("Select action: ", 1, 4);
    }

    @Test
    void searchExportsResultsWhenExportActionIsSelected() throws Exception {
        Searchable search = mock(Searchable.class);
        Student student = new RegularStudent(
                "Export Me", 20, "export@example.com", "0712345678");
        InputReader input = mock(InputReader.class);
        when(input.readInt("Select search option: ", 1, 5)).thenReturn(1);
        when(input.readRequired("Enter student ID: ")).thenReturn(student.getStudentId());
        when(input.readInt("Select action: ", 1, 4)).thenReturn(2);
        when(search.findStudentById(student.getStudentId())).thenReturn(student);

        try {
            new StudentSearchService(search, input).search();
            assertTrue(Files.exists(Path.of("grade_reports", "search-results.csv")));
        } finally {
            Files.deleteIfExists(Path.of("grade_reports", "search-results.csv"));
        }
    }
}