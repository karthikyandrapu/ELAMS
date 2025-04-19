package com.elams.controller;

import com.elams.dto.UserDTO;
import com.elams.dto.UserRequest;
import com.elams.dto.UserSignUpDTO;
import com.elams.exception.EmployeesResourceNotFoundException;
import com.elams.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authenticate")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticationController {

    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("users")
    public ResponseEntity<?> authenticate(@RequestBody UserRequest userRequest){
        UserDTO userDTO=userService.authenticateUser(userRequest.getUserName(), userRequest.getPassword());
        if(userDTO.getUserName()!=null) {
            return new ResponseEntity<>(userDTO, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }


    @PostMapping("register")
    public ResponseEntity<?> registerUser(@RequestBody UserSignUpDTO userSignUp ) {
        try {
            userService.registerValidUser(userSignUp);
            return new ResponseEntity<>(HttpStatus.CREATED); // Indicate successful registration
        } catch (EmployeesResourceNotFoundException e) {
            return new ResponseEntity<>("Employee not found with the provided ID.", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // Handle validation errors
        } 
    }
}