package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.t112;

import com.checkout.settlement.loader.batch.JobFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class T112JobFactory implements JobFactory {
  
  private final JobRepository jobRepository;
  private final Step t112Step;

  @Override
  public Job mapFileToJob(String fileName, String s3key) {
    if (fileName.contains("T112")) {
      return new JobBuilder("T112", jobRepository)
          .start(t112Step)
          .build();
    }
    throw new IllegalArgumentException("No job type for file: " + fileName);
  }
}
