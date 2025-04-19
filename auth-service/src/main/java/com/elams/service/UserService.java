package com.elams.service;

import java.util.List;
import com.elams.dto.UserDTO;
import com.elams.dto.UserSignUpDTO;
import com.elams.entities.Users;
import com.elams.exception.EmployeesResourceNotFoundException;
public interface UserService {
	
	public List<Users> listOfUsers();
	public UserDTO authenticateUser(String username,String password);
	public void registerValidUser(UserSignUpDTO userSignUp) throws EmployeesResourceNotFoundException;

}
