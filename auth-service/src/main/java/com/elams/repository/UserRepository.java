package com.elams.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.elams.entities.Users;
@Repository
public interface UserRepository extends CrudRepository<com.elams.entities.Users,Long>{

	Optional<Users> findByUserName(String username);

	Optional<Users> findByEmpId(Long empId);

}
