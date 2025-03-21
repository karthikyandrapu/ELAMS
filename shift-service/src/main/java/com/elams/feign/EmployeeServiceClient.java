package com.elams.feign;

import com.elams.dtos.EmployeeDTO;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client interface for communicating with the employee-service.
 * This interface defines methods to retrieve employee data from the employee-service.
 */
@FeignClient(name = "api-gateway")
public interface EmployeeServiceClient {

    /**
     * Retrieves an EmployeeDTO by its ID.
     *
     * @param id The ID of the employee to retrieve.
     * @return The EmployeeDTO corresponding to the given ID.
     */
    @GetMapping("/employees/{id}")
    EmployeeDTO getEmployeeById(@PathVariable("id") Long id);

    /**
     * Retrieves a list of EmployeeDTOs managed by a given manager ID.
     *
     * @param managerId The ID of the manager whose employees are to be retrieved.
     * @return A list of EmployeeDTOs managed by the given manager.
     */
    @GetMapping("/employees/manager/{managerId}")
    List<EmployeeDTO> getEmployeesByManager(@PathVariable("managerId") Long managerId);
}