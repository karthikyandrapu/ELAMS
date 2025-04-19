package com.elams.service;

import com.elams.dto.EmployeeDTO;
import com.elams.dto.UserDTO;
import com.elams.dto.UserSignUpDTO;
import com.elams.entities.Users;
import com.elams.feign.EmployeeServiceClient;
import com.elams.repository.UserRepository;
import com.elams.exception.EmployeesResourceNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeServiceClient employeeServiceClient;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, EmployeeServiceClient employeeServiceClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.employeeServiceClient = employeeServiceClient;
    }

    @Override
    public List<Users> listOfUsers() {
        return (List<Users>) userRepository.findAll();
    }

    @Override
    public UserDTO authenticateUser(String username, String password) {
        Optional<Users> userOptional = userRepository.findByUserName(username);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword()) && !user.isAccountLocked()) {
                return UserDTO.builder()
                        .empId(user.getEmpId())
                        .userName(user.getUserName())
                        .role(user.getRole())
                        .isAccountLocked(user.isAccountLocked())
                        .build();
            }
        }
        return new UserDTO(); // Return an empty DTO if authentication fails
    }

    @Override
    public void registerValidUser(UserSignUpDTO userSignUp) throws EmployeesResourceNotFoundException {
        // 1. Check if the employee exists using the Feign client
        EmployeeDTO employee = employeeServiceClient.getEmployeeById(userSignUp.getEmpId());
        if (employee == null) {
            throw new EmployeesResourceNotFoundException("Employee not found with ID: " + userSignUp.getEmpId());
        }

        // 2. Verify employee details match the registration request (optional but recommended)
        if (!employee.getRole().equals(userSignUp.getEmpRole())) {
            throw new IllegalArgumentException("Employee details do not match.");
        }

        // 3. Check if the username already exists
        if (userRepository.findByUserName(userSignUp.getUserName()).isPresent()) {
            throw new IllegalArgumentException("Username already exists.");
        }

        // 4. Check if the employee ID is already registered
        if (userRepository.findByEmpId(userSignUp.getEmpId()).isPresent()) {
            throw new IllegalArgumentException("Employee ID is already registered.");
        }

        // 5. Create a new Users entity
        Users newUser = Users.builder()
                .empId(userSignUp.getEmpId())
                .userName(userSignUp.getUserName())
                .password(passwordEncoder.encode(userSignUp.getPassword()))
                .role(userSignUp.getEmpRole())
                .isAccountLocked(false)
                .build();

        // 6. Save the new user to the database
        userRepository.save(newUser);
    }
}