package dev.zay.example.security.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@GetMapping("/")
	public String sayHello() {
		return "Hello, Spring Security!";
	}

	@GetMapping("/greeting")
	public String getMethodName() {
		return "Hello World!";
	}

}
