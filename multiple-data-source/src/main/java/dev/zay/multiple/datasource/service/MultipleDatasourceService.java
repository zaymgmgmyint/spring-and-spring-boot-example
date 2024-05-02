package dev.zay.multiple.datasource.service;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import dev.zay.multiple.datasource.scheduler.ScheduledTasks;

@Service
public class MultipleDatasourceService {

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	@Autowired
	@Qualifier("mysqlDataSource")
	private DataSource mysqlDataSource;

	@Autowired
	@Qualifier("mssqlDataSource")
	private DataSource mssqlDataSource;

	@Autowired
	@Qualifier("mysqlJdbcTemplate")
	private JdbcTemplate mysqlJdbcTemplate;

	@Autowired
	@Qualifier("mssqlJdbcTemplate")
	private JdbcTemplate mssqlJdbcTemplate;

	// Method to perform operations using MySQL JdbcTemplate
	public void performOperationWithMySQL() {

		mysqlJdbcTemplate.query("SELECT * FROM user", resultSet -> {
			// Process ResultSet
			String userId = resultSet.getString("user_id");

			log.info("MySQL => user id: " + userId);
		});
	}

	// Method to perform operations using MSSQL JdbcTemplate
	public void performOperationWithMSSQL() {

		mysqlJdbcTemplate.query("SELECT * FROM user", resultSet -> {
			// Process ResultSet
			String userId = resultSet.getString("user_id");

			log.info("MySQL => user id: " + userId);
		});
	}
}
