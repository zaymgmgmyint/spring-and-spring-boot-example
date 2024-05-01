package com.supernap.multiple.data.source;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

import com.supernap.multiple.datasource.MultipleDataSourceApplication;

@TestConfiguration(proxyBeanMethods = false)
public class TestMultipleDsApplication {

	public static void main(String[] args) {
		SpringApplication.from(MultipleDataSourceApplication::main).with(TestMultipleDsApplication.class).run(args);
	}

}
