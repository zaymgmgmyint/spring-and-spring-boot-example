package com.zay.multiple.datasource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MultipleDataSourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultipleDataSourceApplication.class, args);
	}

}
