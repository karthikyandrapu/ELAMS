package com.elams;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.elams.controllers.LeaveBalanceController;

@SpringBootTest
class LeaveBalanceServiceApplicationTests {

	@Autowired
	private LeaveBalanceController controller;
	@Test
	void contextLoads() {
		assertNotNull(controller);
	}
	   @Test
	    void main() {
	        LeaveBalanceServiceApplication.main(new String[]{});
	        assertNotNull(controller);
	    }

}
