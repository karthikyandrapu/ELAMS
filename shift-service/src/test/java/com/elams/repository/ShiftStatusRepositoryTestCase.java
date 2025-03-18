package com.elams.repository;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import com.elams.ShiftServiceApplication;
import com.elams.entities.ShiftStatus;
import com.elams.enums.ShiftStatusType;

@DataJpaTest
@DisplayName("Shift Status Repository Tests")
@ContextConfiguration(classes = {ShiftServiceApplication.class})
class ShiftStatusRepositoryTestCase {

    @Autowired
    private ShiftStatusRepository shiftStatusRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("findByShiftId - Positive - ShiftStatus Found")
    void findByShiftId_positive_shiftStatusFound() {
        Long shiftId = 1L;
        ShiftStatus shiftStatus = new ShiftStatus();
        shiftStatus.setShiftId(shiftId);
        testEntityManager.persist(shiftStatus);
        testEntityManager.flush();

        ShiftStatus foundStatus = shiftStatusRepository.findByShiftId(shiftId);

        assertNotNull(foundStatus);
        assertEquals(shiftId, foundStatus.getShiftId());
    }

    @Test
    @DisplayName("findByShiftId - Negative - ShiftStatus Not Found")
    void findByShiftId_negative_shiftStatusNotFound() {
        Long shiftId = 2L;

        ShiftStatus foundStatus = shiftStatusRepository.findByShiftId(shiftId);

        assertNull(foundStatus);
    }

    @Test
    @DisplayName("findByShiftId - Negative - Null ShiftId")
    void findByShiftId_negative_nullShiftId() {
        Long shiftId = null;

        ShiftStatus foundStatus = shiftStatusRepository.findByShiftId(shiftId);

        assertNull(foundStatus);
    }
    
    @Test
    @DisplayName("existsByShiftId - Positive - ShiftStatus Exists")
    void existsByShiftId_positive_shiftStatusExists() {
        // Arrange
        Long shiftId = 1L;
        ShiftStatus shiftStatus = new ShiftStatus();
        shiftStatus.setShiftId(shiftId);
        testEntityManager.persist(shiftStatus);
        testEntityManager.flush();

        boolean exists = shiftStatusRepository.existsByShiftId(shiftId);
        assertTrue(exists);
    }

    @Test
    @DisplayName("existsByShiftId - Negative - ShiftStatus Does Not Exist")
    void existsByShiftId_negative_shiftStatusDoesNotExist() {
        Long shiftId = 2L;
        boolean exists = shiftStatusRepository.existsByShiftId(shiftId);
        assertFalse(exists);
    }

    @Test
    @DisplayName("existsByShiftId - Negative - Null ShiftId")
    void existsByShiftId_negative_nullShiftId() {
        Long shiftId = null;
        boolean exists = shiftStatusRepository.existsByShiftId(shiftId);
        assertFalse(exists); 
    }
    @Test
    @DisplayName("deleteByShiftId - Positive - ShiftStatus Deleted")
    void deleteByShiftId_positive_shiftStatusDeleted() {
        Long shiftId = 1L;
        ShiftStatus shiftStatus = new ShiftStatus();
        shiftStatus.setShiftId(shiftId);
        testEntityManager.persist(shiftStatus);
        testEntityManager.flush();

        shiftStatusRepository.deleteByShiftId(shiftId);
        testEntityManager.flush();

        assertNull(testEntityManager.find(ShiftStatus.class, shiftId));
        assertFalse(shiftStatusRepository.existsByShiftId(shiftId)); 
    }

    @Test
    @DisplayName("deleteByShiftId - Negative - ShiftStatus Not Found")
    void deleteByShiftId_negative_shiftStatusNotFound() {
        Long shiftId = 2L;

        shiftStatusRepository.deleteByShiftId(shiftId); 

        assertNull(testEntityManager.find(ShiftStatus.class, shiftId)); 
    }
    @Test
    @DisplayName("findByStatus - Positive - ShiftStatuses Found")
    void findByStatus_positive_shiftStatusesFound() {
        // Arrange
        ShiftStatusType status = ShiftStatusType.COMPLETED;
        ShiftStatus shiftStatus1 = new ShiftStatus();
        shiftStatus1.setStatus(status);
        ShiftStatus shiftStatus2 = new ShiftStatus();
        shiftStatus2.setStatus(status);
        ShiftStatus shiftStatus3 = new ShiftStatus();
        shiftStatus3.setStatus(ShiftStatusType.OPEN); 

        testEntityManager.persist(shiftStatus1);
        testEntityManager.persist(shiftStatus2);
        testEntityManager.persist(shiftStatus3);
        testEntityManager.flush();

        List<ShiftStatus> foundStatuses = shiftStatusRepository.findByStatus(status);

        assertNotNull(foundStatuses);
        assertEquals(2, foundStatuses.size());
        for (ShiftStatus foundStatus : foundStatuses) {
            assertEquals(status, foundStatus.getStatus());
        }
    }

    @Test
    @DisplayName("findByStatus - Negative - ShiftStatuses Not Found")
    void findByStatus_negative_shiftStatusesNotFound() {
        ShiftStatusType status = ShiftStatusType.COMPLETED; 
        ShiftStatus shiftStatus1 = new ShiftStatus();
        shiftStatus1.setStatus(ShiftStatusType.OPEN);

        testEntityManager.persist(shiftStatus1);
        testEntityManager.flush();

        List<ShiftStatus> foundStatuses = shiftStatusRepository.findByStatus(status);

        assertNotNull(foundStatuses);
        assertTrue(foundStatuses.isEmpty());
    }

    @Test
    @DisplayName("findByStatus - Negative - Null Status")
    void findByStatus_negative_nullStatus() {
        // Arrange
        ShiftStatusType status = null;
        List<ShiftStatus> foundStatuses = shiftStatusRepository.findByStatus(status);

        assertNotNull(foundStatuses);
        assertTrue(foundStatuses.isEmpty());
    }
    @Test
    @DisplayName("findByStatusIn - Positive - ShiftStatuses Found")
    void findByStatusIn_positive_shiftStatusesFound() {
        ShiftStatusType confirmed = ShiftStatusType.SCHEDULED;
        ShiftStatusType open = ShiftStatusType.OPEN;
        List<ShiftStatusType> statuses = Arrays.asList(confirmed, open);

        ShiftStatus shiftStatus1 = new ShiftStatus();
        shiftStatus1.setStatus(confirmed);
        ShiftStatus shiftStatus2 = new ShiftStatus();
        shiftStatus2.setStatus(open);
        ShiftStatus shiftStatus3 = new ShiftStatus();
        shiftStatus3.setStatus(ShiftStatusType.COMPLETED); 

        testEntityManager.persist(shiftStatus1);
        testEntityManager.persist(shiftStatus2);
        testEntityManager.persist(shiftStatus3);
        testEntityManager.flush();

        List<ShiftStatus> foundStatuses = shiftStatusRepository.findByStatusIn(statuses);

        assertNotNull(foundStatuses);
        assertEquals(2, foundStatuses.size());
        for (ShiftStatus foundStatus : foundStatuses) {
            assertTrue(statuses.contains(foundStatus.getStatus()));
        }
    }

    @Test
    @DisplayName("findByStatusIn - Negative - ShiftStatuses Not Found")
    void findByStatusIn_negative_shiftStatusesNotFound() {
        List<ShiftStatusType> statuses = Arrays.asList(ShiftStatusType.COMPLETED);

        ShiftStatus shiftStatus1 = new ShiftStatus();
        shiftStatus1.setStatus(ShiftStatusType.OPEN);

        testEntityManager.persist(shiftStatus1);
        testEntityManager.flush();

        List<ShiftStatus> foundStatuses = shiftStatusRepository.findByStatusIn(statuses);

        assertNotNull(foundStatuses);
        assertTrue(foundStatuses.isEmpty());
    }

    @Test
    @DisplayName("findByStatusIn - Negative - Null Status List")
    void findByStatusIn_negative_nullStatusList() {
        List<ShiftStatusType> statuses = null;

        List<ShiftStatus> foundStatuses = shiftStatusRepository.findByStatusIn(statuses);

        assertNotNull(foundStatuses);
        assertTrue(foundStatuses.isEmpty()); 
    }

    @Test
    @DisplayName("findByStatusIn - Negative - Empty Status List")
    void findByStatusIn_negative_emptyStatusList() {
        List<ShiftStatusType> statuses = List.of();

        List<ShiftStatus> foundStatuses = shiftStatusRepository.findByStatusIn(statuses);

        assertNotNull(foundStatuses);
        assertTrue(foundStatuses.isEmpty());
    }
    
    @Test
    @DisplayName("save - Positive - ShiftStatus Saved Successfully")
    void save_positive_shiftStatusSavedSuccessfully() {
        ShiftStatus shiftStatus = new ShiftStatus();
        shiftStatus.setShiftId(1L); 
        shiftStatus.setStatus(ShiftStatusType.SCHEDULED);

        ShiftStatus savedStatus = shiftStatusRepository.save(shiftStatus);
        testEntityManager.persist(savedStatus);
        testEntityManager.flush(); 

        assertNotNull(savedStatus);
        assertEquals(shiftStatus.getShiftId(), savedStatus.getShiftId());
    }
    @Test
    @DisplayName("save - Negative")
    void save_negative_nullShiftId() {
        ShiftStatus shiftStatus = null;
        try {
        	 shiftStatusRepository.save(shiftStatus);
        	assertTrue(false);
        	
        }catch(Exception e) {
        	assertTrue(true);
        }
       
     
    }

   

   

}