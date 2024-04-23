package com.supernap.multipleds.service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.supernap.multipleds.dto.PersonnelDto;
import com.supernap.multipleds.dto.UserDto;
import com.supernap.multipleds.repository.PersonnelRowMapper;
import com.supernap.multipleds.scheduler.ScheduledTasks;

@Service
public class SuperNapService {

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

	@Autowired
	private PersonnelService personnelService;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	// Method to perform operations using MySQL JdbcTemplate
	public void performOperationWithMySQL() {

		mysqlJdbcTemplate.query("SELECT * FROM personnel", resultSet -> {
			// Process ResultSet
			String userId = resultSet.getString("user_id");

			log.info("MySQL => user id: " + userId);
		});
	}

	// Method to perform data sync operations
	public void performDataSyncOperation() {

		// Get the current date and time
		LocalDateTime currentTime = LocalDateTime.now();

		// Subtract 10 minutes from the current time
		LocalDateTime previousTime = currentTime.minusMinutes(10);

		// Define the format of the output date and time
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		// TODO fetch data from Security Expect data source
		mssqlJdbcTemplate.query(
				"SELECT Users.Name, Users.UserId, Users.PINNumber, Users.UserCardNumber, Users.PhoneNumber, ImageData.Picture FROM Users"
						+ " INNER JOIN ImageData ON Users.ImageID = ImageData.ImageID WHERE Users.Created >= '"
						+ previousTime.format(formatter) + "'",
				resultSet -> {
					// Process security expect result set
					UserDto userDto = new UserDto();
					userDto.setName(resultSet.getString("Name"));
					userDto.setUserId(resultSet.getString("UserID"));
					userDto.setPinNumber(resultSet.getString("PINNumber"));
					userDto.setUserCardNumber(resultSet.getString("UserCardNumber"));
					userDto.setPicture(resultSet.getString("Picture"));
					userDto.setPhoneNumber(resultSet.getString("PhoneNumber"));

					log.info("MSSQL => User: " + userDto.toString());

					// TODO fetch data from 8i data source with user_id
					AtomicBoolean resultSetEmpty = new AtomicBoolean(true);
					PersonnelDto entity = new PersonnelDto();

					mysqlJdbcTemplate.query("SELECT * FROM personnel where user_id=" + userDto.getUserId(),
							mySqlResultSet -> {
								// Process MySQL result set
								resultSetEmpty.set(false); // Set the flag to indicate that the result set is not empty
								entity.setId(mySqlResultSet.getLong("id"));
								entity.setUserId(mySqlResultSet.getString("user_id"));
								entity.setCardNoId(mySqlResultSet.getString("card_no_id"));
								entity.setFaceId(mySqlResultSet.getString("face_id"));
								entity.setPhoneNo(mySqlResultSet.getString("phone_no"));
								entity.setAddress(mySqlResultSet.getString("address"));
								entity.setTag(mySqlResultSet.getString("tag"));
								entity.setDataSynced(mySqlResultSet.getInt("data_synced"));
								entity.setSyncedDatetime(mySqlResultSet.getDate("synced_datetime"));

								log.info("MySQL => Personnel: " + entity);
							});

					if (resultSetEmpty.get()) {
						// Handle case where result set is empty
						log.info("MySQL result set is empty.");

						// TODO save into 8i database
						int rowsInserted = 0;

						log.info("Save into 8i > user id > " + userDto.getUserId());
						String personnelInertSql = """
								INSERT INTO personnel (user_id, name, card_no_id, face_id, phone_no, address, tag, type, data_synced, synced_datetime)
								VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
								""";

						rowsInserted = mysqlJdbcTemplate.update(personnelInertSql, userDto.getUserId(),
								userDto.getName(), userDto.getUserCardNumber(), userDto.getPicture(),
								userDto.getPhoneNumber(), "", "test", 1, 0, new Date());

						if (rowsInserted > 0) {
							log.info("Data inserted successfully");

							List<PersonnelDto> results = mysqlJdbcTemplate.query(
									"SELECT * FROM personnel WHERE user_id=" + userDto.getUserId(),
									new PersonnelRowMapper());
							if (results.isEmpty()) {
								// Handle case where result is empty
								System.out.println("No rows found > user id: " + userDto.getUserId());
							} else {
								// Process the results
								for (PersonnelDto personnel : results) {
									// TODO register face scanner terminal
									PersonnelDto registerPersonnel = personnelService
											.registerFaceScannerTerminal(personnel);

									// TODO update sync status base on API response
									if (registerPersonnel != null && registerPersonnel.getDataSynced() == 1) {
										String personnelUpdateSql = "UPDATE personnel SET data_synced = ?, synced_datetime = ? WHERE user_id = ?";
										int rowsUpdated = mysqlJdbcTemplate.update(personnelUpdateSql,
												registerPersonnel.getDataSynced(), new Date(),
												registerPersonnel.getUserId());

										if (rowsUpdated > 0) {
											System.out.println("Data updated successfully. Synced!");
										} else {
											System.out.println("Failed to update data.");
										}
									}
								}
							}

						} else {
							log.info("Failed to insert data");
						}
					} else {
						// Handle case where result set is empty
						log.info("MySQL result set is not empty.");

						// TODO save/update into 8i database
						int rowsInsertedOrUpdated = 0;

						if (entity.getDataSynced() == 0) {
							log.info("The data has not been synced.");
							log.info("Update into 8i > user id > " + userDto.getUserId());
							String personnelUpdateSql = """
									update `personnel` set `name`=?, `card_no_id`=?, `face_id`=?, `phone_no`=?, `address`=?, `tag`=?, `type`=? where `user_id`=?
									""";

							rowsInsertedOrUpdated = mysqlJdbcTemplate.update(personnelUpdateSql, userDto.getName(),
									userDto.getUserCardNumber(), userDto.getPicture(), userDto.getPhoneNumber(), "",
									"test", 1, userDto.getUserId());
						}

						if (rowsInsertedOrUpdated > 0) {
							log.info("Data updated successfully");

							List<PersonnelDto> results = mysqlJdbcTemplate.query(
									"SELECT * FROM personnel WHERE user_id=" + userDto.getUserId(),
									new PersonnelRowMapper());
							if (results.isEmpty()) {
								// Handle case where result is empty
								System.out.println("No rows found > user id: " + userDto.getUserId());
							} else {
								// Process the results
								for (PersonnelDto personnel : results) {
									// TODO register face scanner terminal
									PersonnelDto registerPersonnel = personnelService
											.registerFaceScannerTerminal(personnel);

									// TODO update sync status base on API response
									if (registerPersonnel != null && registerPersonnel.getDataSynced() == 1) {
										String personnelUpdateSql = "UPDATE personnel SET data_synced = ?, synced_datetime = ? WHERE user_id = ?";
										int rowsUpdated = mysqlJdbcTemplate.update(personnelUpdateSql,
												registerPersonnel.getDataSynced(), new Date(),
												registerPersonnel.getUserId());

										if (rowsUpdated > 0) {
											System.out.println("Data updated successfully. Synced!");
										} else {
											System.out.println("Failed to update data.");
										}
									}
								}
							}

						} else {
							log.info("Failed to insert data");
						}
					}

				});
	}

}
