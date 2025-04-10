package com.elams.service;
 
import com.elams.dto.EmployeeDTO;

import java.util.List;
 
public interface EmployeeService {

    EmployeeDTO getEmployeeById(Long id);

    List<EmployeeDTO> getAllEmployees();

    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);

    EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO);

    void deleteEmployee(Long id);

    List<EmployeeDTO> getEmployeesByManager(Long managerId); // Added method

    EmployeeDTO updateEmployeeManager(Long id, Long managerId); // Added method

    List<Long[]> findAllEmployeeId();

	List findAllEmployeeIdsByManagerId(Long managerId);
}
 