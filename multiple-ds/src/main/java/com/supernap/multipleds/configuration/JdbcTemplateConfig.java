package com.supernap.multipleds.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class JdbcTemplateConfig {

	@Bean
	JdbcTemplate mysqlJdbcTemplate(@Qualifier("mysqlDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	JdbcTemplate mssqlJdbcTemplate(@Qualifier("mssqlDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

}
