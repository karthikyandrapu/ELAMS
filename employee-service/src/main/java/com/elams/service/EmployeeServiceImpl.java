package com.elams.service;
 
import com.elams.dto.EmployeeDTO;

import com.elams.entities.Employee;

import com.elams.mapper.EmployeeMapper;

import com.elams.repository.EmployeeRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;
 
@Service

public class EmployeeServiceImpl implements EmployeeService {
 
    @Autowired

    private EmployeeRepository employeeRepository;
 
    @Autowired

    private EmployeeMapper employeeMapper;
 
    @Override

    public EmployeeDTO getEmployeeById(Long id) {

        return employeeRepository.findById(id)

                .map(employee -> employeeMapper.toDTO(employee))

                .orElse(null);

    }
 
    @Override

    public List<EmployeeDTO> getAllEmployees() {

        return employeeRepository.findAll()

                .stream()

                .map(employeeMapper::toDTO)

                .collect(Collectors.toList());

    }
 
    @Override

    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {

        Employee employee = employeeMapper.toEntity(employeeDTO);

        Employee savedEmployee = employeeRepository.save(employee);

        return employeeMapper.toDTO(savedEmployee);

    }
 
    @Override

    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {

        if (employeeRepository.existsById(id)) {

            Employee employee = employeeMapper.toEntity(employeeDTO);

            employee.setId(id);

            Employee updatedEmployee = employeeRepository.save(employee);

            return employeeMapper.toDTO(updatedEmployee);

        }

        return null;

    }
 
    @Override

    public void deleteEmployee(Long id) {

        employeeRepository.deleteById(id);

    }
 
    @Override

    public List<EmployeeDTO> getEmployeesByManager(Long managerId) {

        return employeeRepository.findByManagerId(managerId)

                .stream()

                .map(employeeMapper::toDTO)

                .collect(Collectors.toList());

    }
 
    @Override

    public EmployeeDTO updateEmployeeManager(Long id, Long managerId) {

        Employee employee = employeeRepository.findById(id).orElse(null);

        if (employee != null) {

            employee.setManagerId(managerId);

            Employee updatedEmployee = employeeRepository.save(employee);

            return employeeMapper.toDTO(updatedEmployee);

        }

        return null;

    }

    @Override
    public List findAllEmployeeId() {
        List<Employee> employees = employeeRepository.findAll();
        List<Long> employeeIds = new ArrayList<>();
        for (Employee employee : employees) {
            employeeIds.add(employee.getId());
        }
        return employeeIds;
    }

	@Override
	public List findAllEmployeeIdsByManagerId(Long managerId) {
		List<Employee> employees = employeeRepository.findAll();
        List<Long> employeeIds = new ArrayList<>();
        for (Employee employee : employees) {
        	if(employee.getManagerId() != null && employee.getManagerId().toString().equals(managerId.toString())) {
        		employeeIds.add(employee.getId());
        	}
        }
        return employeeIds;
	}

}
 