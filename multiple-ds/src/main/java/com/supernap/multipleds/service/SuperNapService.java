package com.supernap.multipleds.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

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

	// Method to perform operations using MySQL JdbcTemplate
	public void performOperationWithMySQL() {

		mysqlJdbcTemplate.query("SELECT * FROM personnel", resultSet -> {
			// Process ResultSet
			String userId = resultSet.getString("user_id");

			log.info("MySQL => user id: " + userId);
		});
	}

	// Method to perform data sync operations
	public void performDataSyncOperation(){

		try {
			StopWatch watcher = new StopWatch("Execution Time Watch");
			watcher.start("Data Synchronizing");

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
						log.info("-------------------------------");
						// Process security expect result set
						UserDto userDto = new UserDto();
						userDto.setName(resultSet.getString("Name"));
						userDto.setUserId(resultSet.getString("UserID"));
						userDto.setPinNumber(resultSet.getString("PINNumber"));
						userDto.setUserCardNumber(resultSet.getString("UserCardNumber"));
						userDto.setPhoneNumber(resultSet.getString("PhoneNumber"));

						log.info("Fetch data from Security Expert => User: " + userDto.toString());

						// Hex to Base64 image string
						String hexPictureString = resultSet.getString("Picture");
						if (!isEmpty(hexPictureString)) {
							userDto.setPicture(HexToBase64(hexPictureString));
						}

						// TODO fetch data from 8i data source with user_id
						AtomicBoolean resultSetEmpty = new AtomicBoolean(true);
						PersonnelDto entity = new PersonnelDto();

						mysqlJdbcTemplate.query("SELECT * FROM personnel where user_id=" + userDto.getUserId(),
								mySqlResultSet -> {
									// Process MySQL result set
									resultSetEmpty.set(false); // Set the flag to indicate that the result set is not
																// empty
									entity.setId(mySqlResultSet.getLong("id"));
									entity.setName(mySqlResultSet.getString("name"));
									entity.setUserId(mySqlResultSet.getString("user_id"));
									entity.setCardNoId(mySqlResultSet.getString("card_no_id"));
									entity.setPhoneNo(mySqlResultSet.getString("phone_no"));
									entity.setAddress(mySqlResultSet.getString("address"));
									entity.setTag(mySqlResultSet.getString("tag"));
									entity.setDataSynced(mySqlResultSet.getInt("data_synced"));
									entity.setSyncedDatetime(mySqlResultSet.getDate("synced_datetime"));

									log.info("Fetch personnel data => " + entity);
								});

						if (resultSetEmpty.get()) {
							// Handle case where result set is empty
							log.info("8i result set is empty.");

							// TODO save into 8i database
							int rowsInserted = 0;

							log.info(">>> Save into 8i database > user id > " + userDto.getUserId());
							String personnelInertSql = """
									INSERT INTO personnel (user_id, name, card_no_id, face_id, phone_no, address, tag, type, data_synced, synced_datetime)
									VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
									""";

							rowsInserted = mysqlJdbcTemplate.update(personnelInertSql, userDto.getUserId(),
									userDto.getName(), userDto.getUserCardNumber(), userDto.getPicture(),
									userDto.getPhoneNumber(), "", "test", 1, 0, new Date());

							if (rowsInserted > 0) {
								log.info("Data inserted successfully.");

								List<PersonnelDto> results = mysqlJdbcTemplate.query(
										"SELECT * FROM personnel WHERE user_id=" + userDto.getUserId(),
										new PersonnelRowMapper());
								if (results.isEmpty()) {
									// Handle case where result is empty
									log.info("No rows found > user id: " + userDto.getUserId());
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
												System.out.println(">>> Data updated successfully, Synced!");
											} else {
												System.out.println(">>> Failed to update data, Unsynced!");
											}
										}
									}
								}

							} else {
								log.info("Failed to insert data.");
							}
						} else {
							// Handle case where result set is empty
							log.info("8i result set is not empty.");

							// TODO save/update into 8i database
							int rowsInsertedOrUpdated = 0;

							if (entity.getDataSynced() == 0) {
								log.info(">>> The data has not been synced.");

								log.info(">>> Update into 8i database > user id > " + userDto.getUserId());
								String personnelUpdateSql = """
										update `personnel` set `name`=?, `card_no_id`=?, `face_id`=?, `phone_no`=?, `address`=?, `tag`=?, `type`=? where `user_id`=?
										""";

								rowsInsertedOrUpdated = mysqlJdbcTemplate.update(personnelUpdateSql, userDto.getName(),
										userDto.getUserCardNumber(), userDto.getPicture(), userDto.getPhoneNumber(), "",
										"test", 1, userDto.getUserId());

								if (rowsInsertedOrUpdated > 0) {
									log.info("Data updated successfully");

									List<PersonnelDto> results = mysqlJdbcTemplate.query(
											"SELECT * FROM personnel WHERE user_id=" + userDto.getUserId(),
											new PersonnelRowMapper());
									if (results.isEmpty()) {
										// Handle case where result is empty
										log.info("No rows found > user id: " + userDto.getUserId());
									} else {
										// Process the results
										for (PersonnelDto personnel : results) {
											// TODO register face scanner terminal
											PersonnelDto registerPersonnel = personnelService
													.registerFaceScannerTerminal(personnel);

											// TODO update sync status base on API response
											if (registerPersonnel != null && registerPersonnel.getDataSynced() == 1) {
												String pUpdateSql = "UPDATE personnel SET data_synced = ?, synced_datetime = ? WHERE user_id = ?";
												int rowsUpdated = mysqlJdbcTemplate.update(pUpdateSql,
														registerPersonnel.getDataSynced(), new Date(),
														registerPersonnel.getUserId());

												if (rowsUpdated > 0) {
													System.out.println(">>> Data updated successfully, Synced!");
												} else {
													System.out.println(">>> Failed to update data, Unsynced!");
												}
											}
										}
									}

								} else {
									log.info("Failed to update data");
								}

							} else {
								log.info(">>> The data already synced.");
							}

						}
						log.info("-------------------------------");
					});

			watcher.stop();
			log.info(watcher.prettyPrint(TimeUnit.MINUTES));

			long elapsedTimeMillis = watcher.getTotalTimeMillis(); // Get elapsed time in milliseconds
			String formattedTime = formatTime(elapsedTimeMillis); // Convert to human-readable format
			log.info("====== Execution time =====: " + formattedTime);

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error happened while data sync...");
		}
	}

	private String HexToBase64(String hexString) {

		String base64String = "";

		try {

			// Convert hexadecimal string to byte array
			byte[] hexBytes = hexStringToByteArray(hexString);

			// Encode byte array to base64 string
			base64String = Base64.getEncoder().encodeToString(hexBytes);

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error happened while Hex to Base64...");
		}

		log.info(">>> Hex to Base64 string converted successfully.");
		return base64String;
	}

	// Method to convert hexadecimal string to byte array
	private byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	private boolean isEmpty(String str) {
		if (str != null && !str.isEmpty())
			return false;

		return true;
	}

	private static String formatTime(long milliseconds) {
		long seconds = milliseconds / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;

		return String.format("%02d:%02d:%02d.%03d", hours, minutes % 60, seconds % 60, milliseconds % 1000);
	}

}
