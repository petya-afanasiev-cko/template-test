package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.tc33;

import com.checkout.settlement.loader.batch.JobFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class Tc33JobFactory implements JobFactory {

  private final JobRepository jobRepository;
  private final Step tc33Step;

  @Override
  public Job mapFileToJob(String fileName, String s3key) {
    if (fileName.contains("TC33")) {
      return new JobBuilder("TC33", jobRepository)
          .start(tc33Step)
          .build();
    }
    throw new IllegalArgumentException("No job type for file: " + fileName);
  }
}
