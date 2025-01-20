package com.checkout.settlement.loader.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JobLaunchingGateway {

  private final JobLauncher jobLauncher;
  
  public JobExecution launch(JobFactory jobFactory, String s3Bucket, String s3Key, JobParameters additionalParams) throws JobExecutionException {
    var fileName = s3Key.substring(s3Key.lastIndexOf("/") + 1);
    Job job = jobFactory.mapFileToJob(fileName, s3Key);

    var paramsBuilder = new JobParametersBuilder()
        .addLong(JobParameter.LAUNCHED_AT, System.currentTimeMillis())
        .addString(JobParameter.S3_BUCKET, s3Bucket)
        .addString(JobParameter.S3_KEY, s3Key)
        .addString(JobParameter.FILE_NAME, fileName);
    Optional.ofNullable(additionalParams).ifPresent(paramsBuilder::addJobParameters);
    return jobLauncher.run(job, paramsBuilder.toJobParameters());
  }
}
