package com.elams.repository;
 
import com.elams.entities.Employee;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
 
import java.util.List;
 
@Repository

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByManagerId(Long managerId); // Added method to find employees by managerId

}
 