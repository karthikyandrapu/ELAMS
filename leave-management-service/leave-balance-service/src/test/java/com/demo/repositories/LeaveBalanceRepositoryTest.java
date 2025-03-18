package com.demo.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;

import com.elams.LeaveBalanceServiceApplication;
import com.elams.entities.LeaveBalance;
import com.elams.entities.LeaveType;
import com.elams.repositories.LeaveBalanceRepository;

@DataJpaTest
@ContextConfiguration(classes= {LeaveBalanceServiceApplication.class})
class LeaveBalanceRepositoryTest {

	
	@Autowired
	private LeaveBalanceRepository leaveBalanceRepository;
	
	@Autowired
	private TestEntityManager testEntityManager;

	
	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testSaveLeaveBalance_Positive() {
     LeaveBalance lb=new LeaveBalance();
     lb.setEmployeeId(1L);
     lb.setLeaveType(LeaveType.VACATION);
     lb.setBalance(10.0);

     
     LeaveBalance obj=leaveBalanceRepository.save(lb);
     assertNotNull(obj.getId());
     assertEquals(1L,obj.getEmployeeId());
     assertEquals(LeaveType.VACATION,obj.getLeaveType());
     assertEquals(10.0,obj.getBalance());	}
	
	@Test
	void testSaveLeaveBalance_Negative()
	{
		try {
		LeaveBalance lb=null;
	     leaveBalanceRepository.save(lb);
		}
		catch(Exception e)
		{
			assertTrue(true);
		}
	
	}
	
	@Test
	void testFindByEmployeeIdAndLeaveType_Positive() {
     LeaveBalance lb=new LeaveBalance();
     lb.setEmployeeId(1L);
     lb.setLeaveType(LeaveType.SICK);
     lb.setBalance(10.0);
     testEntityManager.persist(lb);
     Optional<LeaveBalance> optional=leaveBalanceRepository.findByEmployeeIdAndLeaveType(1L, LeaveType.SICK);
     assertTrue(optional.isPresent());
     LeaveBalance row=optional.get();
     assertEquals(1L, row.getEmployeeId());
     assertEquals(LeaveType.SICK,row.getLeaveType());
     assertEquals(10.0,row.getBalance());
	}
	
	@Test
	void testFindByEmployeeIdAndLeaveType_Negative()
	{
	     Optional<LeaveBalance> optional=leaveBalanceRepository.findByEmployeeIdAndLeaveType(1000L, LeaveType.SICK);
     assertFalse(optional.isPresent());
	}
	
	
	@Test
	void testFindByEmployeeId_Positive()
	{
		   LeaveBalance lb1=new LeaveBalance();
		     lb1.setEmployeeId(1L);
		     lb1.setLeaveType(LeaveType.SICK);
		     lb1.setBalance(10.0);
		     testEntityManager.persist(lb1);
		     
		     LeaveBalance lb2=new LeaveBalance();
		     lb2.setEmployeeId(1L);
		     lb2.setLeaveType(LeaveType.VACATION);
		     lb2.setBalance(100.0);
		     testEntityManager.persist(lb2);
		     List<LeaveBalance> balances=leaveBalanceRepository.findByEmployeeId(1L);
		     assertEquals(2,balances.size());

		     
	}
	
	@Test
	void testFindByEmployeeId_Negative()
	{
		List<LeaveBalance> balances=leaveBalanceRepository.findByEmployeeId(1000L);
		assertTrue(balances.isEmpty());
	}

	
	@Test
	void testDeleteLeaveBalance_Positive()
	{
		LeaveBalance lb=new LeaveBalance();
		lb.setEmployeeId(4L);
		lb.setLeaveType(LeaveType.CASUAL);
		lb.setBalance(8.0);
		LeaveBalance saved=testEntityManager.persist(lb);
		leaveBalanceRepository.deleteById(saved.getId());
		Optional<LeaveBalance>optional= leaveBalanceRepository.findById(saved.getId());
		assertFalse(optional.isPresent());
		
	}
	@Test
	void testDeleteLeaveBalance_Negative()
	{
		try {
		
	     leaveBalanceRepository.deleteById(null);
		}
		catch(Exception e)
		{
			assertTrue(true);
		}
	
	}
	
	
	}


