package dev.zay.example.openapi.with.wagger.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.zay.example.openapi.with.wagger.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer>{
	
	List<User> findByName(String name);
	
	Optional<User> findById(Integer id);

}
