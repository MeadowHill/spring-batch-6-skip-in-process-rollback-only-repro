package com.example.batch.job;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.ItemReader;

import com.example.batch.order.OrderRepository;

/**
 * Loads all order ids before the step starts, then hands them out one by one, from the
 * end of the list towards the start. Returns {@code null} (end of input) once the index
 * drops below 0.
 * <p>
 * Being a {@link StepExecutionListener}, it is registered automatically by the step
 * builder when set as the reader.
 */
public class OrderIdReader implements ItemReader<Long>, StepExecutionListener {

	private final OrderRepository orderRepository;

	private List<Long> orderIds = List.of();

	private final AtomicInteger remaining = new AtomicInteger();

	public OrderIdReader(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.orderIds = List.copyOf(orderRepository.findAllIds());
		this.remaining.set(this.orderIds.size());
	}

	@Override
	public @Nullable Long read() {
		int index = remaining.decrementAndGet();
		if (index < 0) {
			return null;
		}
		return orderIds.get(index);
	}

}
