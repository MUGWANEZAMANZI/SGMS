package services;

import input.InputReader;
import manager.Searchable;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.*;

class PatternSearchCachingTest {

    @Test
    void cachesPatternResultsAndReturnsDefensiveCopies() {
        Searchable search = mock(Searchable.class);
        InputReader inputReader = mock(InputReader.class);
        Student student = new RegularStudent(
                "Alice Smith", 20, "alice@example.com", "0712345678");
        when(search.findStudentsByPattern("Ali.*"))
                .thenReturn(new Student[]{student});

        StudentSearchService service = new StudentSearchService(search, inputReader);
        Student[] first = service.searchByPattern("Ali.*");
        Student[] second = service.searchByPattern("Ali.*");

        assertNotSame(first, second);
        assertEquals(1, second.length);
        verify(search, times(1)).findStudentsByPattern("Ali.*");
    }

    @Test
    void clearSearchCacheForcesFreshPatternLookup() {
        Searchable search = mock(Searchable.class);
        InputReader inputReader = mock(InputReader.class);
        when(search.findStudentsByPattern("A.*")).thenReturn(new Student[0]);
        StudentSearchService service = new StudentSearchService(search, inputReader);

        service.searchByPattern("A.*");
        service.clearSearchCache();
        service.searchByPattern("A.*");

        verify(search, times(2)).findStudentsByPattern("A.*");
    }

    @Test
    void managerAppliesCaseInsensitiveRegularExpressionToNames() {
        manager.StudentManager manager = new manager.StudentManager();
        Student alice = new RegularStudent(
                "Alice Smith", 20, "alice2@example.com", "0712345679");
        Student bob = new RegularStudent(
                "Bob Jones", 21, "bob@example.com", "0712345680");
        manager.addStudent(alice);
        manager.addStudent(bob);

        Student[] matches = manager.findStudentsByPattern("^alice");

        assertEquals(1, matches.length);
        assertEquals(alice.getStudentId(), matches[0].getStudentId());
    }
}
