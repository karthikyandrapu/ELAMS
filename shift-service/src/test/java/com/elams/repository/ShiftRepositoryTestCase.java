package com.elams.repository;

import com.elams.ShiftServiceApplication;
import com.elams.entities.Shift;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Shift Repository Tests")
@ContextConfiguration(classes = {ShiftServiceApplication.class})
class ShiftRepositoryTestCase {

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("Save Shift - Positive Case")
    void testSave_positive() {
        Shift shift = new Shift();
        shift.setEmployeeId(1001L);
        shift.setShiftDate(LocalDate.now());
        shift.setShiftTime(LocalTime.of(9, 11));
        testEntityManager.persist(shift);
        Shift shiftSaved = shiftRepository.save(shift);
        assertEquals(1001L, shiftSaved.getEmployeeId());
    }

    @Test
    @DisplayName("Save Shift - Negative Case (Null Shift)")
    void testSave_negative() {
        Shift shift=null;
        try {
        	shiftRepository.save(shift);
        	assertTrue(false);
        	
        }catch(Exception e) {
        	assertTrue(true);
        }
    }

    @Test
    @DisplayName("Find Shifts by Employee ID - Positive Case")
    void findByEmployeeId_positive() {
        Shift shift = new Shift();
        shift.setEmployeeId(1001L);
        shift.setShiftDate(LocalDate.now());
        shift.setShiftTime(LocalTime.of(9, 11));
        testEntityManager.persist(shift);
        testEntityManager.flush();
        List<Shift> actual = shiftRepository.findByEmployeeId(shift.getEmployeeId());
        assertFalse(actual.isEmpty());
        assertEquals(1001L, actual.get(0).getEmployeeId());
        assertEquals(LocalDate.now(), actual.get(0).getShiftDate());
        assertEquals(LocalTime.of(9, 11), actual.get(0).getShiftTime());
    }

    @Test
    @DisplayName("Find Shifts by Employee ID - Negative Case (Employee ID Not Found)")
    void findByEmployeeId_negative() {
        List<Shift> actual = shiftRepository.findByEmployeeId(999L);
        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("Find Shifts by Employee IDs - Positive Case (Multiple Matches)")
    void findByEmployeeIdIn_positive_multipleMatches() {
        Shift shift1 = new Shift();
        shift1.setEmployeeId(1010L);
        shift1.setShiftDate(LocalDate.now());
        shift1.setShiftTime(LocalTime.of(1, 10));

        Shift shift2 = new Shift();
        shift2.setEmployeeId(1011L);
        shift2.setShiftDate(LocalDate.now());
        shift2.setShiftTime(LocalTime.of(1, 10));

        Shift shift3 = new Shift();
        shift3.setEmployeeId(1012L);
        shift3.setShiftDate(LocalDate.now());
        shift3.setShiftTime(LocalTime.of(1, 10));

        Shift shift4 = new Shift();
        shift4.setEmployeeId(1013L);
        shift4.setShiftDate(LocalDate.now());
        shift4.setShiftTime(LocalTime.of(1, 10));

        testEntityManager.persist(shift1);
        testEntityManager.persist(shift2);
        testEntityManager.persist(shift3);
        testEntityManager.persist(shift4);
        testEntityManager.flush();

        List<Shift> actual = shiftRepository.findByEmployeeIdIn(Arrays.asList(1010L, 1011L));
        assertEquals(2, actual.size());

        assertTrue(actual.stream().anyMatch(shift -> shift.getEmployeeId().equals(1010L)));
        assertTrue(actual.stream().anyMatch(shift -> shift.getEmployeeId().equals(1011L)));
    }

    @Test
    @DisplayName("Find Shifts by Employee IDs - Positive Case (Single Match)")
    void findByEmployeeIdIn_positive_singleMatch() {
        Shift shift1 = new Shift();
        shift1.setEmployeeId(1010L);
        shift1.setShiftDate(LocalDate.now());
        shift1.setShiftTime(LocalTime.of(1, 10));

        testEntityManager.persist(shift1);
        testEntityManager.flush();

        List<Shift> actual = shiftRepository.findByEmployeeIdIn(Arrays.asList(1010L));
        assertEquals(1, actual.size());
        assertEquals(1010L, actual.get(0).getEmployeeId());
    }

    @Test
    @DisplayName("Find Shifts by Employee IDs - Negative Case (No Matches)")
    void findByEmployeeIdIn_negative_noMatches() {
        Shift shift1 = new Shift();
        shift1.setEmployeeId(1001L);
        shift1.setShiftDate(LocalDate.now());
        shift1.setShiftTime(LocalTime.of(9, 0));

        testEntityManager.persist(shift1);
        testEntityManager.flush();

        List<Shift> actual = shiftRepository.findByEmployeeIdIn(Arrays.asList(1002L, 1003L));
        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("Exists by Employee ID and Shift Date - Positive Case")
    void existsByEmployeeIdAndShiftDate_positive() {
        Shift shift1 = new Shift();
        shift1.setEmployeeId(1001L);
        shift1.setShiftDate(LocalDate.now());
        shift1.setShiftTime(LocalTime.of(9, 0));

        Shift shift2 = new Shift();
        shift2.setEmployeeId(1002L);
        shift2.setShiftDate(LocalDate.now().plusDays(1));
        shift2.setShiftTime(LocalTime.of(10, 0));
        testEntityManager.persist(shift1);
        testEntityManager.persist(shift2);
        testEntityManager.flush();

        boolean exists = shiftRepository.existsByEmployeeIdAndShiftDate(1001L, LocalDate.now());
        assertTrue(exists);
    }

    @Test
    @DisplayName("Exists by Employee ID and Shift Date - Negative Case (Employee ID Not Found)")
    void existsByEmployeeIdAndShiftDate_negative_employeeIdDoesNotExist() {
        Shift shift1 = new Shift();
        shift1.setEmployeeId(1001L);
        shift1.setShiftDate(LocalDate.now());
        shift1.setShiftTime(LocalTime.of(9, 0));
        testEntityManager.persist(shift1);
        testEntityManager.flush();
        boolean exists = shiftRepository.existsByEmployeeIdAndShiftDate(999L, LocalDate.of(2024, 1, 21));
        assertFalse(exists);
    }

    @Test
    @DisplayName("Exists by Employee ID and Shift Date - Negative Case (Shift Date Not Found)")
    void existsByEmployeeIdAndShiftDate_negative_shiftDateDoesNotExist() {
        Shift shift1 = new Shift();
        shift1.setEmployeeId(1001L);
        shift1.setShiftDate(LocalDate.now());
        shift1.setShiftTime(LocalTime.of(9, 0));
        testEntityManager.persist(shift1);
        testEntityManager.flush();
        boolean exists = shiftRepository.existsByEmployeeIdAndShiftDate(1001L, LocalDate.of(2024,1, 22));
        assertFalse(exists);
    }
    @Test
    @DisplayName("Exists by Employee ID and Shift Date - Negative Case (Both Not Found)")
    void existsByEmployeeIdAndShiftDate_negative_bothDoNotExist() {
        boolean exists = shiftRepository.existsByEmployeeIdAndShiftDate(999L, LocalDate.of(2025, 1, 1));
        assertFalse(exists);
    }

    @Test
    @DisplayName("Find Shifts by Shift Date - Returns Correct Shift")
    void findByShiftDate_returnsCorrectShift() {
        Shift shift1 = new Shift();
        shift1.setEmployeeId(1001L);
        shift1.setShiftDate(LocalDate.now());
        shift1.setShiftTime(LocalTime.of(9, 0));
        testEntityManager.persist(shift1);

        Shift shift2 = new Shift();
        shift2.setEmployeeId(1002L);
        shift2.setShiftDate(LocalDate.now().plusDays(1));
        shift2.setShiftTime(LocalTime.of(9, 0));
        testEntityManager.persist(shift2);

        testEntityManager.flush();

        List<Shift> actual = shiftRepository.findByShiftDate(LocalDate.now());

        assertEquals(1, actual.size());
        assertEquals(1001L, actual.get(0).getEmployeeId());
        assertEquals(LocalDate.now(), actual.get(0).getShiftDate());

        List<Shift> actual2 = shiftRepository.findByShiftDate(LocalDate.now().plusDays(1));
        assertEquals(1, actual2.size());
        assertEquals(1002L, actual2.get(0).getEmployeeId());
    }

    @Test
    @DisplayName("Find Shifts by Shift Date - Returns Empty List When No Match")
    void findByShiftDate_returnsEmptyList_whenNoMatch() {
        Shift shift1 = new Shift();
        shift1.setEmployeeId(1001L);
        shift1.setShiftDate(LocalDate.now());
        shift1.setShiftTime(LocalTime.of(9, 0));
        testEntityManager.persist(shift1);
        testEntityManager.flush();

        List<Shift> actual = shiftRepository.findByShiftDate(LocalDate.now().plusDays(1));

        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("Exists by Employee ID, Shift Date, and Shift Time - Positive Case")
    void existsByEmployeeIdAndShiftDateAndShiftTime_positive() {
        Long employeeId = 1001L;
        LocalDate shiftDate = LocalDate.now();
        LocalTime shiftTime = LocalTime.of(9, 0);

        Shift shift = new Shift();
        shift.setEmployeeId(employeeId);
        shift.setShiftDate(shiftDate);
        shift.setShiftTime(shiftTime);
        testEntityManager.persist(shift);
        testEntityManager.flush();

        boolean exists = shiftRepository.existsByEmployeeIdAndShiftDateAndShiftTime(employeeId, shiftDate, shiftTime);

        assertTrue(exists);
    }

    @Test
    @DisplayName("Exists by Employee ID, Shift Date, and Shift Time - Negative Case (Employee ID Mismatch)")
    void existsByEmployeeIdAndShiftDateAndShiftTime_negative_employeeIdMismatch() {
        Long employeeId = 1001L;
        LocalDate shiftDate = LocalDate.now();
        LocalTime shiftTime = LocalTime.of(9, 0);

        Shift shift = new Shift();
        shift.setEmployeeId(employeeId);
        shift.setShiftDate(shiftDate);
        shift.setShiftTime(shiftTime);
        testEntityManager.persist(shift);
        testEntityManager.flush();

        boolean exists = shiftRepository.existsByEmployeeIdAndShiftDateAndShiftTime(1002L, shiftDate, shiftTime);
        assertFalse(exists);
    }

    @Test
    @DisplayName("Exists by Employee ID, Shift Date, and Shift Time - Negative Case (Shift Date Mismatch)")
    void existsByEmployeeIdAndShiftDateAndShiftTime_negative_shiftDateMismatch() {
        Long employeeId = 1001L;
        LocalDate shiftDate = LocalDate.now();
        LocalTime shiftTime = LocalTime.of(9, 0);

        Shift shift = new Shift();
        shift.setEmployeeId(employeeId);
        shift.setShiftDate(shiftDate);
        shift.setShiftTime(shiftTime);
        testEntityManager.persist(shift);
        testEntityManager.flush();

        boolean exists = shiftRepository.existsByEmployeeIdAndShiftDateAndShiftTime(employeeId,LocalDate.now().plusDays(1), shiftTime);
        assertFalse(exists);
    }

    @Test
    @DisplayName("Exists by Employee ID, Shift Date, and Shift Time - Negative Case (Shift Time Mismatch)")
    void existsByEmployeeIdAndShiftDateAndShiftTime_negative_shiftTimeMismatch() {
        Long employeeId = 1001L;
        LocalDate shiftDate = LocalDate.now();
        LocalTime shiftTime = LocalTime.of(9, 0);

        Shift shift = new Shift();
        shift.setEmployeeId(employeeId);
        shift.setShiftDate(shiftDate);
        shift.setShiftTime(shiftTime);
        testEntityManager.persist(shift);
        testEntityManager.flush();

        boolean exists = shiftRepository.existsByEmployeeIdAndShiftDateAndShiftTime(employeeId, shiftDate, LocalTime.of(10, 0));
        assertFalse(exists);
    }

    @Test
    @DisplayName("Exists by Employee ID, Shift Date, and Shift Time - Negative Case (No Matching Shift)")
    void existsByEmployeeIdAndShiftDateAndShiftTime_negative_noMatchingShift() {
        Long employeeId = 1001L;
        LocalDate shiftDate = LocalDate.now();
        LocalTime shiftTime = LocalTime.of(9, 0);

        boolean exists = shiftRepository.existsByEmployeeIdAndShiftDateAndShiftTime(employeeId, shiftDate, shiftTime);
        assertFalse(exists);
    }

    @Test
    @DisplayName("Find Shift by Employee ID, Shift Date, and Shift Time - Positive Case")
    void findByEmployeeIdAndShiftDateAndShiftTime_positive() {
        Long employeeId = 1001L;
        LocalDate shiftDate = LocalDate.now();
        LocalTime shiftTime = LocalTime.of(9, 0);

        Shift shift = new Shift();
        shift.setEmployeeId(employeeId);
        shift.setShiftDate(shiftDate);
        shift.setShiftTime(shiftTime);
        testEntityManager.persist(shift);
        testEntityManager.flush();

        Shift foundShift = shiftRepository.findByEmployeeIdAndShiftDateAndShiftTime(employeeId, shiftDate, shiftTime);

        assertEquals(employeeId, foundShift.getEmployeeId());
        assertEquals(shiftDate, foundShift.getShiftDate());
        assertEquals(shiftTime, foundShift.getShiftTime());
    }

    @Test
    @DisplayName("Find Shift by Employee ID, Shift Date, and Shift Time - Negative Case (Employee ID Mismatch)")
    void findByEmployeeIdAndShiftDateAndShiftTime_negative_employeeIdMismatch() {
        Long employeeId = 1001L;
        LocalDate shiftDate = LocalDate.now();
        LocalTime shiftTime = LocalTime.of(9, 0);

        Shift shift = new Shift();
        shift.setEmployeeId(employeeId);
        shift.setShiftDate(shiftDate);
        shift.setShiftTime(shiftTime);
        testEntityManager.persist(shift);
        testEntityManager.flush();

        Shift foundShift = shiftRepository.findByEmployeeIdAndShiftDateAndShiftTime(1002L, shiftDate, shiftTime);
        assertNull(foundShift);
    }

    @Test
    @DisplayName("Find Shift by Employee ID, Shift Date, and Shift Time - Negative Case (Shift Date Mismatch)")
    void findByEmployeeIdAndShiftDateAndShiftTime_negative_shiftDateMismatch() {
        Long employeeId = 1001L;
        LocalDate shiftDate =LocalDate.now();
        LocalTime shiftTime = LocalTime.of(9, 0);

        Shift shift = new Shift();
        shift.setEmployeeId(employeeId);
        shift.setShiftDate(shiftDate);
        shift.setShiftTime(shiftTime);
        testEntityManager.persist(shift);
        testEntityManager.flush();

        Shift foundShift = shiftRepository.findByEmployeeIdAndShiftDateAndShiftTime(employeeId, LocalDate.now().plusDays(1), shiftTime);
        assertNull(foundShift);
    }
    @Test
    @DisplayName("Find Shift by Employee ID, Shift Date, and Shift Time - Negative Case (Shift Time Mismatch)")
    void findByEmployeeIdAndShiftDateAndShiftTime_negative_shiftTimeMismatch() {
        Long employeeId = 1001L;
        LocalDate shiftDate = LocalDate.now();
        LocalTime shiftTime = LocalTime.of(9, 0);

        Shift shift = new Shift();
        shift.setEmployeeId(employeeId);
        shift.setShiftDate(shiftDate);
        shift.setShiftTime(shiftTime);
        testEntityManager.persist(shift);
        testEntityManager.flush();

        Shift foundShift = shiftRepository.findByEmployeeIdAndShiftDateAndShiftTime(employeeId, shiftDate, LocalTime.of(10, 0));
        assertNull(foundShift);
    }

    @Test
    @DisplayName("Find Shift by Employee ID, Shift Date, and Shift Time - Negative Case (No Matching Shift)")
    void findByEmployeeIdAndShiftDateAndShiftTime_negative_noMatchingShift() {
        Long employeeId = 1001L;
        LocalDate shiftDate = LocalDate.now();
        LocalTime shiftTime = LocalTime.of(9, 0);

        Shift foundShift = shiftRepository.findByEmployeeIdAndShiftDateAndShiftTime(employeeId, shiftDate, shiftTime);
        assertNull(foundShift);
    }

    @Test
    @DisplayName("Find Shift by Employee ID and Shift Date - Positive Case")
    void findByEmployeeIdAndShiftDate_positive() {
        Long employeeId = 1001L;
        LocalDate shiftDate = LocalDate.now();
        LocalTime shiftTime = LocalTime.of(9, 0);

        Shift shift = new Shift();
        shift.setEmployeeId(employeeId);
        shift.setShiftDate(shiftDate);
        shift.setShiftTime(shiftTime);
        testEntityManager.persist(shift);
        testEntityManager.flush();

        Shift foundShift = shiftRepository.findByEmployeeIdAndShiftDate(employeeId, shiftDate);

        assertEquals(employeeId, foundShift.getEmployeeId());
        assertEquals(shiftDate, foundShift.getShiftDate());
    }

    @Test
    @DisplayName("Find Shift by Employee ID and Shift Date - Negative Case (Employee ID Mismatch)")
    void findByEmployeeIdAndShiftDate_negative_employeeIdMismatch() {
        Long employeeId = 1001L;
        LocalDate shiftDate = LocalDate.now();
        LocalTime shiftTime = LocalTime.of(9, 0);

        Shift shift = new Shift();
        shift.setEmployeeId(employeeId);
        shift.setShiftDate(shiftDate);
        shift.setShiftTime(shiftTime);
        testEntityManager.persist(shift);
        testEntityManager.flush();

        Shift foundShift = shiftRepository.findByEmployeeIdAndShiftDate(1002L, shiftDate);
        assertNull(foundShift);
    }

    @Test
    @DisplayName("Find Shift by Employee ID and Shift Date - Negative Case (Shift Date Mismatch)")
    void findByEmployeeIdAndShiftDate_negative_shiftDateMismatch() {
        Long employeeId = 1001L;
        LocalDate shiftDate = LocalDate.now();
        LocalTime shiftTime = LocalTime.of(9, 0);

        Shift shift = new Shift();
        shift.setEmployeeId(employeeId);
        shift.setShiftDate(shiftDate);
        shift.setShiftTime(shiftTime);
        testEntityManager.persist(shift);
        testEntityManager.flush();

        Shift foundShift = shiftRepository.findByEmployeeIdAndShiftDate(employeeId, LocalDate.now().plusDays(1));
        assertNull(foundShift);
    }

    @Test
    @DisplayName("Find Shift by Employee ID and Shift Date - Negative Case (No Matching Shift)")
    void findByEmployeeIdAndShiftDate_negative_noMatchingShift() {
        Long employeeId = 1001L;
        LocalDate shiftDate = LocalDate.now();

        Shift foundShift = shiftRepository.findByEmployeeIdAndShiftDate(employeeId, shiftDate);
        assertNull(foundShift);
    }
    
    
}