package com.elams.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.elams.dto.EmployeesDTO;

@FeignClient(name = "employee-service")
public interface EmployeeServiceClient {

    @GetMapping("/employees/{id}")
    EmployeesDTO getEmployeeById(@PathVariable("id") Long id);

    @GetMapping("/employees/manager/{managerId}")
    List<EmployeesDTO> getEmployeesByManager(@PathVariable("managerId") Long managerId);
      

}