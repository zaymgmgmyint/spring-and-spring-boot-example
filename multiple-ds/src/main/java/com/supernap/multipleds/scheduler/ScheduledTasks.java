package com.supernap.multipleds.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.supernap.multipleds.service.SuperNapService;

@Component
public class ScheduledTasks {

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Autowired
	private SuperNapService superNapService;

//	@Scheduled(cron = "* * * * * *")
	public void reportCurrentTime() {
		log.info("The time is now {}", dateFormat.format(new Date()));
	}

//	@Scheduled(cron = "1 * * * * *")
	public void performOperationWithMySQL() {
		log.info("=== Perform MySQL ===");

		superNapService.performOperationWithMySQL();
	}

	@Scheduled(cron = "1 * * * * *")
	public void performOperationWithMSSQL() {
		log.info("===== START Perform Data Sync =====");

		superNapService.performDataSyncOperation();

		log.info("===== END Perform Data Sync =====");
	}

}
