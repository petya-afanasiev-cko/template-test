package com.checkout.settlement.dci.infrastructure.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Component;

import static com.checkout.settlement.dci.infrastructure.batch.JobParameter.JOB_PREFIX;

@Component
@RequiredArgsConstructor
public class JobFactory {
  
  private final JobRepository jobRepository;
  private final Step confirmationStep;

  public Job incomingFileJob(JobType jobType) {
    return switch (jobType) {
      case CONFIRMATION: yield confirmationJob(jobType, confirmationStep);
    };
  }

  private Job confirmationJob(JobType jobType, Step step) {
    return new JobBuilder(JOB_PREFIX + jobType, jobRepository)
        .start(step)
        .build();
  }
}
