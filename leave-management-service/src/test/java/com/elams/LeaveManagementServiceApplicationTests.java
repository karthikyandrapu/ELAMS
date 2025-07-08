package com.elams;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LeaveManagementServiceApplicationTests {

    @Test
    void contextLoads() {
        // This method is intentionally empty.
        // It's used to verify that the Spring application context loads successfully.
        // If the context fails to load, the test will fail.
    }

    @Test
    void test_main() {
        LeaveManagementServiceApplication.main(new String[] {});
        assertTrue(true); // Always true, ensures test runs without failure
    }
}