package com.example.batch.web;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch")
public class OrderBatchController {

	private final JobOperator jobOperator;

	private final Job processOrdersJob;

	public OrderBatchController(JobOperator jobOperator, Job processOrdersJob) {
		this.jobOperator = jobOperator;
		this.processOrdersJob = processOrdersJob;
	}

	/** Runs the job synchronously and returns its outcome. */
	@PostMapping("/process-orders")
	public JobRunResponse processOrders() throws Exception {
		JobParameters parameters = new JobParametersBuilder()
			.addLong("startedAt", System.currentTimeMillis()) // new job instance per call
			.toJobParameters();
		JobExecution execution = jobOperator.start(processOrdersJob, parameters);
		return new JobRunResponse(execution.getId(), execution.getStatus().name(),
				execution.getExitStatus().getExitCode());
	}

	public record JobRunResponse(Long executionId, String status, String exitCode) {
	}

}
