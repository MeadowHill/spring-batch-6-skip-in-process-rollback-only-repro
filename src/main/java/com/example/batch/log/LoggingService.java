package com.example.batch.log;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoggingService {

	private final LogEntryRepository logEntryRepository;

	public LoggingService(LogEntryRepository logEntryRepository) {
		this.logEntryRepository = logEntryRepository;
	}

	/**
	 * Runs in its own transaction so the log entry is committed even if the surrounding
	 * chunk transaction is rolled back.
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void logException(Throwable throwable) {
		String message = throwable.getClass().getName() + ": " + throwable.getMessage();
		logEntryRepository.save(new LogEntry(message));
	}

}
