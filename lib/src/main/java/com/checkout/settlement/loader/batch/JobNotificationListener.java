package com.checkout.settlement.loader.batch;

import static com.checkout.settlement.loader.batch.JobParameter.S3_BUCKET;
import static com.checkout.settlement.loader.batch.JobParameter.S3_KEY;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.Tagging.Builder;


@Log4j2
@Component
@RequiredArgsConstructor
public class JobNotificationListener implements JobExecutionListener {

  private static final String AWS_BATCH_JOB_ID_ENV = "AWS_BATCH_JOB_ID";
  private final S3Client s3Client;

  @Override
  public void beforeJob(JobExecution jobExecution) {
    log.info("[{}] job execution start", jobExecution.getJobInstance().getJobName());
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    var executionStatus = jobExecution.getStatus();
    log.info("[{}] job finished, status {}",
        jobExecution.getJobInstance().getJobName(),
        executionStatus);

    if (executionStatus != BatchStatus.COMPLETED) {
      return;
    }

    tagProcessedFile(jobExecution);
  }

  private void tagProcessedFile(JobExecution jobExecution) {
    var bucket = jobExecution.getJobParameters().getString(S3_BUCKET);
    var key = jobExecution.getJobParameters().getString(S3_KEY);
    var batchJobId = System.getenv(AWS_BATCH_JOB_ID_ENV);

    var request = PutObjectTaggingRequest.builder().bucket(bucket).key(key).tagging(createTaggingConsumer(batchJobId));
    s3Client.putObjectTagging(request.build());

    log.info("Tagged file {}/{} with processed=true and aws_batch_job_id={}",
        bucket, key, batchJobId);
  }

  private Consumer<Builder> createTaggingConsumer(String batchJobId) {
    return tagging -> tagging
        .tagSet(Tag.builder()
                .key("processed")
                .value("true")
                .build(),
            Tag.builder()
                .key("aws_batch_job_id")
                .value(batchJobId)
                .build());
  }
}