package com.elams.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;

import com.elams.LeaveManagementServiceApplication;
import com.elams.entities.LeaveRequest;
import com.elams.entities.LeaveStatus;
import com.elams.entities.LeaveType;

@DataJpaTest
@ContextConfiguration(classes = { LeaveManagementServiceApplication.class })
class LeaveRequestRepositoryTest {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void testFindAll_positive() {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployeeId(12345L);
        leaveRequest.setLeaveType(LeaveType.SICK);
        leaveRequest.setStartDate(LocalDate.of(2025, 5, 10));
        leaveRequest.setEndDate(LocalDate.of(2025, 5, 12));
        leaveRequest.setStatus(LeaveStatus.PENDING);
        testEntityManager.persist(leaveRequest);
        Iterable<LeaveRequest> actual = leaveRequestRepository.findAll();
        assertTrue(actual.iterator().hasNext());
    }

    @Test
    void testFindAll_negative() {
        Iterable<LeaveRequest> actual = leaveRequestRepository.findAll();
        assertFalse(actual.iterator().hasNext());
    }

    @Test
    void testFindById_positive() {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployeeId(12345L);
        leaveRequest.setLeaveType(LeaveType.SICK);
        leaveRequest.setStartDate(LocalDate.of(2025, 5, 10));
        leaveRequest.setEndDate(LocalDate.of(2025, 5, 12));
        leaveRequest.setStatus(LeaveStatus.PENDING);

        LeaveRequest persisted = testEntityManager.persist(leaveRequest);
        Optional<LeaveRequest> optional = leaveRequestRepository.findById(persisted.getLeaveId());
        assertTrue(optional.isPresent());
    }

    @Test
    void testFindById_negative() {
        Optional<LeaveRequest> optional = leaveRequestRepository.findById(100L);
        assertFalse(optional.isPresent());
    }

    @Test
    void testFindById_positive_correctEntity() {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployeeId(2L);
        leaveRequest.setLeaveType(LeaveType.CASUAL);
        leaveRequest.setStartDate(LocalDate.of(2025, 6, 1));
        leaveRequest.setEndDate(LocalDate.of(2025, 6, 2));
        leaveRequest.setStatus(LeaveStatus.PENDING);
        LeaveRequest persisted = testEntityManager.persist(leaveRequest);
        Optional<LeaveRequest> optional = leaveRequestRepository.findById(persisted.getLeaveId());
        LeaveRequest found = optional.get();
        assertEquals(persisted.getLeaveId(), found.getLeaveId());
        assertEquals(2L, found.getEmployeeId());
    }

    @Test
    void testSave_positive() {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployeeId(2L);
        leaveRequest.setLeaveType(LeaveType.CASUAL);
        leaveRequest.setStartDate(LocalDate.of(2025, 6, 1));
        leaveRequest.setEndDate(LocalDate.of(2025, 6, 2));
        leaveRequest.setStatus(LeaveStatus.PENDING);
        leaveRequestRepository.save(leaveRequest);
        assertNotNull(leaveRequestRepository.findById(leaveRequest.getLeaveId()));
    }

    @Test
    void testSave_negative() {
        LeaveRequest lr = null;
        try {
            leaveRequestRepository.save(lr);
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void testDelete_positive() {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployeeId(5L);
        leaveRequest.setLeaveType(LeaveType.CASUAL);
        leaveRequest.setStartDate(LocalDate.of(2025, 9, 1));
        leaveRequest.setEndDate(LocalDate.of(2025, 9, 2));
        leaveRequest.setStatus(LeaveStatus.PENDING);
        LeaveRequest persisted = testEntityManager.persist(leaveRequest);
        leaveRequestRepository.deleteById(persisted.getLeaveId());
        Optional<LeaveRequest> optional = leaveRequestRepository.findById(persisted.getLeaveId());
        assertFalse(optional.isPresent(), "Leave request should be deleted");
    }

    @Test
    void testDelete_negative() {
        try {
            leaveRequestRepository.deleteById(null);
            fail("Expected exception when deleting by null ID");
        } catch (Exception e) {
            assertTrue(true, "Exception thrown as expected");
        }
    }

    @Test
    void testUpdate_positive() {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployeeId(4L);
        leaveRequest.setLeaveType(LeaveType.SICK);
        leaveRequest.setStartDate(LocalDate.of(2025, 8, 1));
        leaveRequest.setEndDate(LocalDate.of(2025, 8, 3));
        leaveRequest.setStatus(LeaveStatus.PENDING);
        LeaveRequest persisted = testEntityManager.persist(leaveRequest);
        persisted.setStatus(LeaveStatus.APPROVED);
        leaveRequestRepository.save(persisted);
        testEntityManager.flush();
        testEntityManager.clear();

        Optional<LeaveRequest> optional = leaveRequestRepository.findById(persisted.getLeaveId());
        LeaveRequest updated = optional.get();
        assertEquals(LeaveStatus.APPROVED, updated.getStatus(), "Leave status should be updated to APPROVED");
    }

    @Test
    void testFindByEmployeeId_positive() {
        LeaveRequest request1 = new LeaveRequest();
        request1.setEmployeeId(10L);
        request1.setLeaveType(LeaveType.VACATION);
        request1.setStartDate(LocalDate.of(2025, 10, 1));
        request1.setEndDate(LocalDate.of(2025, 10, 5));
        request1.setStatus(LeaveStatus.PENDING);
        testEntityManager.persist(request1);

        LeaveRequest request2 = new LeaveRequest();
        request2.setEmployeeId(10L);
        request2.setLeaveType(LeaveType.SICK);
        request2.setStartDate(LocalDate.of(2025, 10, 10));
        request2.setEndDate(LocalDate.of(2025, 10, 12));
        request2.setStatus(LeaveStatus.PENDING);
        testEntityManager.persist(request2);

        LeaveRequest request3 = new LeaveRequest();
        request3.setEmployeeId(20L);
        request3.setLeaveType(LeaveType.CASUAL);
        request3.setStartDate(LocalDate.of(2025, 11, 1));
        request3.setEndDate(LocalDate.of(2025, 11, 2));
        request3.setStatus(LeaveStatus.PENDING);
        testEntityManager.persist(request3);

        List<LeaveRequest> results = leaveRequestRepository.findByEmployeeId(10L);
        assertNotNull(results, "Results should not be null");
        assertEquals(2, results.size(), "There should be 2 leave requests for employee id 10");
        results.forEach(request -> assertEquals(10L, request.getEmployeeId(), "Each leave request should have employee id 10"));
    }

   
}