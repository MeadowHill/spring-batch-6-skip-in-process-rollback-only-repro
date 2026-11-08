package com.example.batch.job;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batch.order.Order;
import com.example.batch.order.OrderProcessingService;
import com.example.batch.order.OrderRepository;

@Configuration
public class OrderJobConfig {

	public static final String JOB_NAME = "processOrdersJob";

	public static final String STEP_NAME = "processOrdersStep";

	@Bean
	public Job processOrdersJob(JobRepository jobRepository, Step processOrdersStep) {
		return new JobBuilder(JOB_NAME, jobRepository).start(processOrdersStep).build();
	}

	@Bean
	public Step processOrdersStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			OrderIdReader orderIdReader, ItemProcessor<Long, Order> orderProcessor, ItemWriter<Order> orderWriter,
			OrderSkipListener orderSkipListener) {
		// single-threaded (no task executor), chunk size 1
		return new StepBuilder(STEP_NAME, jobRepository).<Long, Order>chunk(1)
			.transactionManager(transactionManager)
			.reader(orderIdReader)
			.processor(orderProcessor)
			.writer(orderWriter)
			.faultTolerant()
			.skip(Exception.class)
			.skipLimit(Integer.MAX_VALUE)
			.skipListener(orderSkipListener)
			.build();
	}

	/** Step scoped: a fresh reader (with fresh state) for every step execution. */
	@Bean
	@StepScope
	public OrderIdReader orderIdReader(OrderRepository orderRepository) {
		return new OrderIdReader(orderRepository);
	}

	@Bean
	public ItemProcessor<Long, Order> orderProcessor(OrderProcessingService orderProcessingService) {
		return orderProcessingService::process;
	}

	@Bean
	public ItemWriter<Order> orderWriter(OrderRepository orderRepository) {
		return chunk -> orderRepository.saveAll(chunk.getItems());
	}

}
