package com.elams.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;

import com.elams.ReportsAnalyticsServiceApplication;
import com.elams.entities.AttendanceReport;


@DataJpaTest
@ContextConfiguration(classes = {ReportsAnalyticsServiceApplication.class})
class AttendanceReportRepositoryTest {

    @Autowired 
    private AttendanceReportRepository attendanceReportRepository;
    
    @Autowired
    private TestEntityManager testEntityManager;


    @BeforeEach
    void setUp() {
        SessionFactoryImpl sessionFactory = (SessionFactoryImpl) testEntityManager.getEntityManager().getEntityManagerFactory();
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
        registry.getEventListenerGroup(EventType.PRE_INSERT).clearListeners();
    }
   

    @Test
    void testFindAll_positive() {
        AttendanceReport report = new AttendanceReport();
        report.setEmployeeId(1L);
        report.setDateRange("2025-03-01 to 2025-03-31");
        report.setAbsenteeism(5);
        report.setTotalAttendance(25);
        testEntityManager.persist(report);
        
        AttendanceReport report1 = new AttendanceReport();
        report1.setAbsenteeism(6);
        report1.setEmployeeId(2L);
        report1.setTotalAttendance(24);
        report1.setDateRange("2025-02-01 to 2025-02-31");
        
        Iterable<AttendanceReport> actual = attendanceReportRepository.findAll();
        assertTrue(actual.iterator().hasNext());
    }
    

    @Test
    void testFindAll_negative() {
        Iterable<AttendanceReport> actual = attendanceReportRepository.findAll();
        assertFalse(actual.iterator().hasNext());
    }

    @Test
    void testFindById_positive() {
        AttendanceReport report = new AttendanceReport();
        report.setEmployeeId(1L);
        report.setDateRange("2025-03-01 to 2025-03-31");
        report.setTotalAttendance(20);
        report.setAbsenteeism(2);
        testEntityManager.persist(report);
        
        Optional<AttendanceReport> actual = attendanceReportRepository.findById(report.getReportId());
        assertTrue(actual.isPresent());
    }

    @Test
    void testFindById_negative() {
        Optional<AttendanceReport> actual = attendanceReportRepository.findById(1L);
        assertFalse(actual.isPresent());
    }

    @Test
    void testSave_positive() {
        AttendanceReport report = new AttendanceReport();
        report.setEmployeeId(1L);
        report.setDateRange("2025-03-01 to 2025-03-31");
        report.setTotalAttendance(20);
        report.setAbsenteeism(2);
        testEntityManager.persist(report);

        AttendanceReport savedReport = attendanceReportRepository.save(report);
        assertEquals(report.getReportId(), savedReport.getReportId());
    }

    @Test
    void testSave_negative() {
        try {
            AttendanceReport report = null;
            attendanceReportRepository.save(report);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void testDelete_positive() {
        AttendanceReport report = new AttendanceReport();
        report.setEmployeeId(1L);
        report.setDateRange("2025-03-01 to 2025-03-31");
        report.setTotalAttendance(20);
        report.setAbsenteeism(2);
        testEntityManager.persist(report);
        
        attendanceReportRepository.deleteById(report.getReportId());
        Optional<AttendanceReport> deletedReport = attendanceReportRepository.findById(report.getReportId());
        assertFalse(deletedReport.isPresent());
    }
    
    @Test
   void testFindByEmployeeId_positive() {
    	AttendanceReport report = new AttendanceReport();
    	report.setEmployeeId(1L);
    	report.setAbsenteeism(2);
    	report.setDateRange("2025-03-01 to 2025-03-31");
    	report.setTotalAttendance(28);
    	testEntityManager.persist(report);
    	
    	List<AttendanceReport> actual = attendanceReportRepository.findByEmployeeId(1L);
    	assertFalse(actual.isEmpty());
    	assertEquals(1L,actual.get(0).getEmployeeId());
    }
    @Test
    void testFindByEmployeeId_negative() {
    	List<AttendanceReport> actual = attendanceReportRepository.findByEmployeeId(999L);
    	assertTrue(actual.isEmpty());
    	
    }

    
    @Test
    void testFindLatestByEmployeeId_positive() {
    	AttendanceReport report1 = new AttendanceReport();
    	report1.setEmployeeId(1L);
    	report1.setDateRange("2024-01-01 to 2024-01-31");
    	report1.setAbsenteeism(2);
    	report1.setTotalAttendance(28);
    	testEntityManager.persist(report1);
    	
    	AttendanceReport report2 = new AttendanceReport();
    	report2.setAbsenteeism(3);
    	report2.setTotalAttendance(27);
    	report2.setDateRange("2024-03-01 to 2024-03-31");
    	report2.setEmployeeId(1L);
    	testEntityManager.persist(report2);
    	
    	Optional<AttendanceReport> actual = attendanceReportRepository.findLatestByEmployeeId(1L);
    	assertTrue(actual.isPresent());
    	assertEquals(27,actual.get().getTotalAttendance());
    }
    @Test
    void testFindLatestByEmployeeId_negative() {
    	Optional<AttendanceReport> actual = attendanceReportRepository.findLatestByEmployeeId(999L);
    	assertFalse(actual.isPresent());
    }
    
    @Test
    void testDelete_negative() {
        try {
            attendanceReportRepository.deleteById(null);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
    @Test
    void testFindByEmployeeIdSingle_positive() {
        AttendanceReport report = new AttendanceReport();
        report.setEmployeeId(1L);
        report.setDateRange("2025-04-01 to 2025-04-30");
        report.setTotalAttendance(22);
        report.setAbsenteeism(8);
        testEntityManager.persist(report);

        Optional<AttendanceReport> actual = attendanceReportRepository.findByEmployeeIdSingle(1L);
        assertTrue(actual.isPresent());
        assertEquals(1L, actual.get().getEmployeeId());
    }

    

    @Test
    void testSave_updateExisting() {
        AttendanceReport report = new AttendanceReport();
        report.setEmployeeId(1L);
        report.setDateRange("2025-05-01 to 2025-05-31");
        report.setTotalAttendance(20);
        report.setAbsenteeism(2);
        testEntityManager.persist(report);

        report.setTotalAttendance(21);
        AttendanceReport updatedReport = attendanceReportRepository.save(report);

        assertEquals(21, updatedReport.getTotalAttendance());
    }

    @Test
    void testDeleteAll() {
        AttendanceReport report1 = new AttendanceReport();
        report1.setEmployeeId(1L);
        report1.setDateRange("2025-06-01 to 2025-06-30");
        report1.setTotalAttendance(20);
        report1.setAbsenteeism(2);
        testEntityManager.persist(report1);

        AttendanceReport report2 = new AttendanceReport();
        report2.setEmployeeId(2L);
        report2.setDateRange("2025-07-01 to 2025-07-31");
        report2.setTotalAttendance(22);
        report2.setAbsenteeism(3);
        testEntityManager.persist(report2);

        attendanceReportRepository.deleteAll();
        assertEquals(0, attendanceReportRepository.count());
    }

    @Test
    void testSave_nullDateRange_negative() {
        AttendanceReport report = new AttendanceReport();
        report.setEmployeeId(1L);
        report.setTotalAttendance(20);
        report.setAbsenteeism(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            attendanceReportRepository.save(report);
        });
    }

    @Test
    void testSave_zeroAttendanceAndAbsenteeism() {
        AttendanceReport report = new AttendanceReport();
        report.setEmployeeId(1L);
        report.setDateRange("2025-08-01 to 2025-08-31");
        report.setTotalAttendance(0);
        report.setAbsenteeism(0);

        AttendanceReport savedReport = attendanceReportRepository.save(report);

        assertEquals(0, savedReport.getTotalAttendance());
        assertEquals(0, savedReport.getAbsenteeism());
    }
   
   

   


    @Test
    void testSave_null_negative() {
        try {
            AttendanceReport report = null;
            attendanceReportRepository.save(report);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
    
    

    

    @Test
    void testSave_negative_nullEmployeeId() {
        AttendanceReport report = new AttendanceReport();
        report.setDateRange("2025-09-01 to 2025-09-30");
        report.setTotalAttendance(20);
        report.setAbsenteeism(2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            attendanceReportRepository.save(report);
        });
    }

    

    @Test
    void testSave_negative_invalidTotalAttendance() {
        AttendanceReport report = new AttendanceReport();
        report.setEmployeeId(1L);
        report.setDateRange("2025-09-01 to 2025-09-30");
        report.setAbsenteeism(2);
        report.setTotalAttendance(-1); // or some other invalid value

        assertThrows(DataIntegrityViolationException.class, () -> {
            attendanceReportRepository.save(report);
        });
    }

    @Test
    void testSave_nullAbsenteeism_allowed() {
        AttendanceReport report = new AttendanceReport();
        report.setEmployeeId(1L);
        report.setDateRange("2025-09-01 to 2025-09-30");
        report.setTotalAttendance(20);
        report.setAbsenteeism(null);

        AttendanceReport savedReport = attendanceReportRepository.save(report);
        assertNull(savedReport.getAbsenteeism());
    }
    @Test
    void testSave_nullAttendance_allowed() {
        AttendanceReport report = new AttendanceReport();
        report.setEmployeeId(1L);
        report.setDateRange("2025-09-01 to 2025-09-30");
        report.setTotalAttendance(null);
        report.setAbsenteeism(10);

        AttendanceReport savedReport = attendanceReportRepository.save(report);
        assertNull(savedReport.getTotalAttendance());
    }

    @Test
    void testFindByEmployeeId_negative_emptyDatabase() {
        List<AttendanceReport> actual = attendanceReportRepository.findByEmployeeId(1L);
        assertTrue(actual.isEmpty());
    }

    @Test
    void testFindLatestByEmployeeId_negative_emptyDatabase() {
        Optional<AttendanceReport> actual = attendanceReportRepository.findLatestByEmployeeId(1L);
        assertFalse(actual.isPresent());
    }

    @Test
    void testFindByEmployeeIdSingle_negative_emptyDatabase() {
        Optional<AttendanceReport> actual = attendanceReportRepository.findByEmployeeIdSingle(1L);
        assertFalse(actual.isPresent());
    }
    
    

   

   
   
}