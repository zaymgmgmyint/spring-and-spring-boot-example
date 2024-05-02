package dev.zay.multiple.datasource.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dev.zay.multiple.datasource.service.MultipleDatasourceService;

@Component
public class ScheduledTasks {

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Autowired
	private MultipleDatasourceService dataSourceService;

	@Scheduled(cron = "* * * * * *")
	public void reportCurrentTime() {
		log.info("The time is now {}", dateFormat.format(new Date()));
	}

	@Scheduled(cron = "5 * * * * *")
	public void performOperationWithMySQL() {
		log.info("=== Perform MySQL ===");

		dataSourceService.performOperationWithMySQL();
	}

	@Scheduled(cron = "10 * * * * *")
	public void performOperationWithMSSQL() {
		log.info("===== START Perform MSSQL =====");

		dataSourceService.performOperationWithMSSQL();

		log.info("===== END Perform MSSQL =====");
		System.out.println("");
	}

}
