package dev.zay.example.openapi.with.swagger.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.zay.example.openapi.with.swagger.model.User;
import dev.zay.example.openapi.with.swagger.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
public class UserController {

	private UserRepository userRepository;

	UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

	@Tag(name = "get", description = "GET method of User APIs")
	@GetMapping("/")
	public List<User> findUsers() {
		LOG.info("Fetch all user list...");
		return (List<User>) userRepository.findAll();
	}

	@Tag(name = "get", description = "GET method of User APIs")
	@GetMapping("/{id}")
	public User findById(
			@Parameter(description = "ID of user to be retrieved", required = true) @PathVariable Integer id)
			throws Exception {
		LOG.info("Search user by id...");
		return userRepository.findById(id).orElseThrow(() -> new Exception("User Not Found!"));
	}

	@Tag(name = "get", description = "GET method of User APIs")
	@GetMapping("/find")
	public List<User> findByName(@RequestParam String name) {
		LOG.info("Search user by name...");
		return userRepository.findByName(name);

	}

	@Tag(name = "put", description = "PUT method of User APIs")
	@Operation(summary = "Update an user", description = "Update an existing user, The response is updated User object with id, name, and status.")
	@PutMapping("/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public User updateUser(@PathVariable("id") final String id, @RequestBody final User user) {
		LOG.info("Update the user...");
		return user;

	}

}
