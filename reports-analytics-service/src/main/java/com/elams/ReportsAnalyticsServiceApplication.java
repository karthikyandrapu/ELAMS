package com.elams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;



/**
 * Main application class for the Reports Analytics Service.
 * This service provides analytics and reporting functionalities.
 * It is a Spring Boot application that registers with a service discovery server.
 */
@SpringBootApplication
@EnableDiscoveryClient(autoRegister = true)
@ComponentScan(basePackages = {"com.elams"})
public class ReportsAnalyticsServiceApplication {

    /**
     * The main method that starts the Spring Boot application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(ReportsAnalyticsServiceApplication.class, args);
    }
}
