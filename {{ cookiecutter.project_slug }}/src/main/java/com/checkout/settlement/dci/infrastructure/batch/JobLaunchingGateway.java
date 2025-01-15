package com.checkout.settlement.dci.infrastructure.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.checkout.settlement.dci.infrastructure.batch.JobParameter.*;

@Component
@RequiredArgsConstructor
public class JobLaunchingGateway {

  private final JobLauncher jobLauncher;
  private final JobFactory jobFactory;
  
  public JobExecution launch(String s3Bucket, String s3Key, Optional<JobParameters> additionalParams) throws JobExecutionException {
    var fileName = s3Key.substring(s3Key.lastIndexOf("/") + 1);
    var jobType = JobType.fromFileName(fileName);
    var paramsBuilder = new JobParametersBuilder()
        .addLong(LAUNCHED_AT, System.currentTimeMillis())
        .addString(S3_BUCKET, s3Bucket)
        .addString(S3_KEY, s3Key)
        .addString(FILE_NAME, fileName)
        .addString(JOB_TYPE, JobType.fromFileName(fileName).toString());
    additionalParams.ifPresent(paramsBuilder::addJobParameters);
    return jobLauncher.run(jobFactory.incomingFileJob(jobType), paramsBuilder.toJobParameters());
  }
}
