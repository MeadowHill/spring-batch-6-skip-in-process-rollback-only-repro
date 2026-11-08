package com.example.batch.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderProcessingService {

	private final OrderRepository orderRepository;

	public OrderProcessingService(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	/** Joins the chunk transaction (propagation REQUIRED). */
	@Transactional
	public Order process(Long orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new IllegalStateException("Order " + orderId + " not found"));
		if (order.getOrderNumber() % 2 != 0) {
			throw new DemoIntegrityException(
					"Order " + order.getId() + " has odd order number " + order.getOrderNumber());
		}
		order.setWasProcessed(true);
		return order;
	}

}
