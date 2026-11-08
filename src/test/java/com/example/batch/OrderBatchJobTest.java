package com.example.batch;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

import com.example.batch.log.LogEntry;
import com.example.batch.log.LogEntryRepository;
import com.example.batch.order.DemoIntegrityException;
import com.example.batch.order.Order;
import com.example.batch.order.OrderRepository;

/**
 * Scenario: a fault-tolerant chunk step (chunk size 1, skip all exceptions, unlimited
 * skips) whose processor delegates to a {@code @Transactional} (REQUIRED) service. For
 * odd order numbers the service throws {@link DemoIntegrityException}.
 * <p>
 * Expected: the failing items are skipped (and logged by the skip listener), the other
 * items are processed, and the job completes.
 */
@SpringBootTest
@AutoConfigureMockMvc
class OrderBatchJobTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private LogEntryRepository logEntryRepository;

	@BeforeEach
	void givenFiveUnprocessedOrders() {
		logEntryRepository.deleteAll();
		orderRepository.deleteAll();
		orderRepository.saveAll(List.of(new Order(2, false), new Order(3, false), new Order(4, false),
				new Order(5, false), new Order(6, false)));
	}

	@Test
	void evenOrdersAreProcessedAndOddOrdersAreSkippedWithoutStoppingTheJob() throws Exception {
		// when
		String response = mockMvc.perform(post("/api/batch/process-orders"))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
		String jobStatus = JsonPath.read(response, "$.status");

		// then
		List<Order> orders = orderRepository.findAll();
		List<LogEntry> logEntries = logEntryRepository.findAll();

		SoftAssertions softly = new SoftAssertions();

		softly.assertThat(jobStatus)
			.as("job status")
			.isEqualTo("COMPLETED");

		softly.assertThat(orders)
			.as("processed orders (even order numbers)")
			.filteredOn(Order::isWasProcessed)
			.extracting(Order::getOrderNumber)
			.containsExactlyInAnyOrder(2, 4, 6);

		softly.assertThat(orders)
			.as("unprocessed orders (odd order numbers, skipped)")
			.filteredOn(order -> !order.isWasProcessed())
			.extracting(Order::getOrderNumber)
			.containsExactlyInAnyOrder(3, 5);

		softly.assertThat(logEntries)
			.as("log entries written by the skip listener")
			.extracting(LogEntry::getExceptionMessage)
			.hasSize(2)
			.allMatch(message -> message.startsWith(DemoIntegrityException.class.getName()));

		softly.assertAll();
	}

}
