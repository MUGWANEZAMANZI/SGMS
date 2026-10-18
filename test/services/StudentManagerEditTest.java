package services;

import manager.StudentManager;
import model.HonorsStudent;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentManagerEditTest {

    @Test
    void findStudentByIdReturnsNullForUnknownId() {
        StudentManager manager = new StudentManager();
        assertNull(manager.findStudentById("STU999"));
    }

    @Test
    void findStudentByIdIsCaseInsensitive() {
        StudentManager manager = new StudentManager();
        RegularStudent student = new RegularStudent("Alice", 20, "alice@example.com", "0712345678");
        manager.addStudent(student);
        String id = student.getStudentId();
        assertSame(student, manager.findStudentById(id.toLowerCase()));
        assertSame(student, manager.findStudentById(id.toUpperCase()));
    }

    @Test
    void findStudentsByNameFindsPartialMatch() {
        StudentManager manager = new StudentManager();
        RegularStudent student = new RegularStudent("Alice Wonderland", 20, "alice@example.com", "0712345678");
        manager.addStudent(student);
        Student[] results = manager.findStudentsByName("alice");
        assertEquals(1, results.length);
        assertSame(student, results[0]);
    }

    @Test
    void findStudentsByNameIsCaseInsensitive() {
        StudentManager manager = new StudentManager();
        manager.addStudent(new RegularStudent("Bob Smith", 22, "bob@example.com", "0712345679"));
        Student[] results = manager.findStudentsByName("BOB");
        assertEquals(1, results.length);
    }

    @Test
    void findStudentsByNameReturnsEmptyForBlank() {
        StudentManager manager = new StudentManager();
        manager.addStudent(new RegularStudent("Alice", 20, "alice@example.com", "0712345678"));
        assertEquals(0, manager.findStudentsByName("").length);
        assertEquals(0, manager.findStudentsByName("   ").length);
        assertEquals(0, manager.findStudentsByName(null).length);
    }

    @Test
    void findStudentByIdReturnsNullForBlankOrNull() {
        StudentManager manager = new StudentManager();
        assertNull(manager.findStudentById(null));
        assertNull(manager.findStudentById(""));
        assertNull(manager.findStudentById("   "));
    }

    @Test
    void addStudentNullThrows() {
        StudentManager manager = new StudentManager();
        assertThrows(NullPointerException.class, () -> manager.addStudent(null));
    }

    @Test
    void getStudentCountReturnsCorrectCount() {
        StudentManager manager = new StudentManager();
        assertEquals(0, manager.getStudentCount());
        manager.addStudent(new RegularStudent("Alice", 20, "alice@example.com", "0712345678"));
        assertEquals(1, manager.getStudentCount());
        manager.addStudent(new HonorsStudent("Bob", 22, "bob@example.com", "0712345679"));
        assertEquals(2, manager.getStudentCount());
    }

    @Test
    void findStudentsByAverageReturnsStudentsInRange() {
        StudentManager manager = new StudentManager();
        RegularStudent alice = new RegularStudent("Alice", 20, "alice@example.com", "0712345678");
        alice.addGrade(new model.Grade(alice.getStudentId(), new model.CoreSubject("Math", "CORE001"), 75));
        manager.addStudent(alice);

        RegularStudent bob = new RegularStudent("Bob", 22, "bob@example.com", "0712345679");
        bob.addGrade(new model.Grade(bob.getStudentId(), new model.CoreSubject("Math", "CORE002"), 50));
        manager.addStudent(bob);

        Student[] high = manager.findStudentsByAverage(70, 100);
        assertEquals(1, high.length);
        assertSame(alice, high[0]);

        Student[] all = manager.findStudentsByAverage(0, 100);
        assertEquals(2, all.length);
    }

    @Test
    void findStudentsByTypeFiltersCorrectly() {
        StudentManager manager = new StudentManager();
        manager.addStudent(new RegularStudent("Alice", 20, "alice@example.com", "0712345678"));
        manager.addStudent(new HonorsStudent("Bob", 22, "bob@example.com", "0712345679"));

        Student[] regulars = manager.findStudentsByType("Regular");
        assertEquals(1, regulars.length);
        assertEquals("Alice", regulars[0].getName());

        Student[] honors = manager.findStudentsByType("Honors");
        assertEquals(1, honors.length);
        assertEquals("Bob", honors[0].getName());
    }

    @Test
    void getStudentsReturnsCopyNotReference() {
        StudentManager manager = new StudentManager();
        manager.addStudent(new RegularStudent("Alice", 20, "alice@example.com", "0712345678"));
        Student[] first = manager.getStudents();
        Student[] second = manager.getStudents();
        assertNotSame(first, second);
        assertEquals(first.length, second.length);
    }
}
