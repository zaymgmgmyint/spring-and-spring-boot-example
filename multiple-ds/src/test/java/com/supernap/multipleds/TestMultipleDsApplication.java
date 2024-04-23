package com.supernap.multipleds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestMultipleDsApplication {

	public static void main(String[] args) {
		SpringApplication.from(MultipleDsApplication::main).with(TestMultipleDsApplication.class).run(args);
	}

}
