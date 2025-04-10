package com.elams.controller;
 
import com.elams.dto.EmployeeDTO;

import com.elams.service.EmployeeService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@RestController

@RequestMapping("/employees")
@CrossOrigin(origins = "http://localhost:4200") // Replace with your Angular app's URL
public class EmployeeController {
 
    @Autowired

    private EmployeeService employeeService;
 
    @GetMapping("/{id}")

    public EmployeeDTO getEmployeeById(@PathVariable Long id) {

        return employeeService.getEmployeeById(id);

    }
 
    @GetMapping

    public List<EmployeeDTO> getAllEmployees() {

        return employeeService.getAllEmployees();

    }
 
    @GetMapping("/employeeIds")
    public List findAllEmployeeId(){
    	return employeeService.findAllEmployeeId();
    }
    

    @PostMapping

    @ResponseStatus(HttpStatus.CREATED)

    public EmployeeDTO createEmployee(@RequestBody EmployeeDTO employeeDTO) {

        return employeeService.createEmployee(employeeDTO);

    }
 
    @PutMapping("/{id}")

    public EmployeeDTO updateEmployee(@PathVariable Long id, @RequestBody EmployeeDTO employeeDTO) {

        return employeeService.updateEmployee(id, employeeDTO);

    }
 
    @DeleteMapping("/{id}")

    @ResponseStatus(HttpStatus.NO_CONTENT)

    public void deleteEmployee(@PathVariable Long id) {

        employeeService.deleteEmployee(id);

    }
 
    @GetMapping("/manager/{managerId}")

    public List<EmployeeDTO> getEmployeesByManager(@PathVariable Long managerId) {

        return employeeService.getEmployeesByManager(managerId);

    }
    
    
    @GetMapping("/employeeIds/{managerId}")
    public List findAllEmployeeIdsByManagerId(@PathVariable Long managerId){
    	return employeeService.findAllEmployeeIdsByManagerId(managerId);
    }
    
 
    @PutMapping("/{id}/manager")

    public EmployeeDTO updateEmployeeManager(@PathVariable Long id, @RequestParam Long managerId) {

        return employeeService.updateEmployeeManager(id, managerId);

    }

}
 