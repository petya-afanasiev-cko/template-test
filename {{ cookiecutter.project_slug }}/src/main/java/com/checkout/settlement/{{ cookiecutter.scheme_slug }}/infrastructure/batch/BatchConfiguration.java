package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.infrastructure.batch;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.ResourcelessJobRepository;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfiguration {

  @Bean
  public JobRepository jobRepository() {
    return new ResourcelessJobRepository();
  }

  @Bean
  public PlatformTransactionManager transactionManager() {
    return new ResourcelessTransactionManager();
  }
  
  @Bean
  public TaskExecutor asyncTaskExecutor() {
    return new SimpleAsyncTaskExecutor();
  }
  
  @Bean
  public JobLauncher jobLauncher(JobRepository jobRepository,
      TaskExecutor asyncTaskExecutor) {
    var jobLauncher = new TaskExecutorJobLauncher();
    jobLauncher.setJobRepository(jobRepository);
    jobLauncher.setTaskExecutor(asyncTaskExecutor);
    return jobLauncher;
  }
}
