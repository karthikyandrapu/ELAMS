package com.elams;

import com.elams.aop.AppLogger;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * The main application class for the Attendance Service.
 * This class is responsible for bootstrapping the Spring Boot application,
 * enabling Feign clients for inter-service communication, and registering
 * the service with the service discovery mechanism.
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class AttendanceServiceApplication {

    private static final Logger logger = AppLogger.getLogger(AttendanceServiceApplication.class);

    /**
     * The entry point of the Attendance Service application.
     * This method starts the Spring Boot application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        logger.info("Starting Attendance Service Application...");
        SpringApplication.run(AttendanceServiceApplication.class, args);
        logger.info("Attendance Service Application started successfully.");
    }
}