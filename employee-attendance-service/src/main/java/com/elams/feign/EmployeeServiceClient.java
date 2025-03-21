package com.elams.feign;
//
//import com.elams.dtos.EmployeeDTO;
//import com.elams.aop.AppLogger;
//import org.slf4j.Logger;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
///**
// * Feign client interface for communicating with the employee service.
// * This interface defines methods to interact with the employee service's API.
// */
//@FeignClient(name = "employee-service")
//public interface EmployeeServiceClient {
//
//    Logger logger = AppLogger.getLogger(EmployeeServiceClient.class);
//
//    /**
//     * Retrieves an employee by their ID from the employee service.
//     *
//     * @param id The ID of the employee to retrieve.
//     * @return An {@link EmployeeDTO} representing the retrieved employee.
//     */
//    @GetMapping("/employees/{id}")
//    default EmployeeDTO getEmployeeById(@PathVariable("id") Long id) {
//        logger.info("Fetching employee with ID: {} from employee-service", id);
//        EmployeeDTO employeeDTO = getEmployeeByIdImpl(id); // Call the actual implementation
//        if (employeeDTO != null) {
//            logger.debug("Employee found: {}", employeeDTO);
//        } else {
//            logger.warn("Employee with ID: {} not found.", id);
//        }
//        return employeeDTO;
//    }
//
//    @GetMapping("/employees/{id}")
//    EmployeeDTO getEmployeeByIdImpl(@PathVariable("id") Long id); // Implementation provided by Feign
//
//}
//import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.elams.dtos.EmployeeDTO;

@FeignClient(name = "employee-service")
public interface EmployeeServiceClient {

    @GetMapping("/employees/{id}")
    EmployeeDTO getEmployeeById(@PathVariable("id") Long id);
//
//    @GetMapping("/employees/manager/{managerId}")
//    List<EmployeeDTO> getEmployeesByManager(@PathVariable("managerId") Long managerId); // Corrected
}
