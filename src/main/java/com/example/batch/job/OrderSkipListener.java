package com.example.batch.job;

import org.springframework.batch.core.listener.SkipListener;
import org.springframework.stereotype.Component;

import com.example.batch.log.LoggingService;
import com.example.batch.order.Order;

@Component
public class OrderSkipListener implements SkipListener<Long, Order> {

	private final LoggingService loggingService;

	public OrderSkipListener(LoggingService loggingService) {
		this.loggingService = loggingService;
	}

	@Override
	public void onSkipInRead(Throwable t) {
		loggingService.logException(t);
	}

	@Override
	public void onSkipInProcess(Long orderId, Throwable t) {
		loggingService.logException(t);
	}

	@Override
	public void onSkipInWrite(Order order, Throwable t) {
		loggingService.logException(t);
	}

}
