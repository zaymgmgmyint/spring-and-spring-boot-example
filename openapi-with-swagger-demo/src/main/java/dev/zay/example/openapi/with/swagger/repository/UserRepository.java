package dev.zay.example.openapi.with.swagger.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.zay.example.openapi.with.swagger.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer>{
	
	List<User> findByName(String name);
	
	Optional<User> findById(Integer id);

}
