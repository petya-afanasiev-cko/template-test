package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.confirmation;

import com.checkout.settlement.loader.batch.JobFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class {{ cookiecutter.__scheme_no_space }}JobFactory implements JobFactory {
  
  private final JobRepository jobRepository;
  private final Step confirmationStep;

  @Override
  public Job mapFileToJob(String fileName, String s3key) {
    if (fileName.contains("ACQ")) {
      return new JobBuilder("{{ cookiecutter.__scheme_no_space.upper() }}", jobRepository)
          .start(confirmationStep)
          .build();
    }
    throw new IllegalArgumentException("No job type for file: " + fileName);
  }
}
