package com.supernap.multipleds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MultipleDsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultipleDsApplication.class, args);
	}

	@Bean
	LoggingSystem loggingSystem() {
		// Set the log file path programmatically
		LoggingSystem loggingSystem = LoggingSystem.get(getClass().getClassLoader());
		loggingSystem.setLogLevel("com.supernap.multipleds", LogLevel.INFO); // Set log level if needed
		//loggingSystem.setLogLevel("org.springframework", LogLevel.INFO); // Set log level for Spring
		//loggingSystem.setLogLevel("org.hibernate", LogLevel.INFO); // Set log level for Hibernate
		//loggingSystem.setLogLevel("org.apache", LogLevel.INFO); // Set log level for Apache libraries
		//loggingSystem.setLogLevel("org.thymeleaf", LogLevel.INFO); // Set log level for Thymeleaf
		return loggingSystem;
	}

}
