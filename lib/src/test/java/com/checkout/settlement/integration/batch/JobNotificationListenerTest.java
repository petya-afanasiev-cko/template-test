package com.checkout.settlement.integration.batch;

import com.checkout.settlement.loader.batch.JobNotificationListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingRequest;

import static com.checkout.settlement.loader.batch.JobParameter.S3_BUCKET;
import static com.checkout.settlement.loader.batch.JobParameter.S3_KEY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JobNotificationListenerTest {

  @Mock
  private S3Client s3Client;

  @InjectMocks
  private JobNotificationListener listener;

  private JobExecution jobExecution;

  @BeforeEach
  void setUp() {
    JobParameters jobParameters = new JobParametersBuilder()
        .addString(S3_BUCKET, "test-bucket")
        .addString(S3_KEY, "test-key")
        .toJobParameters();

    JobInstance jobInstance = new JobInstance(1L, "testJob");
    jobExecution = new JobExecution(jobInstance, jobParameters);
  }

  @Test
  void testAfterJobCompleted() {
    jobExecution.setStatus(BatchStatus.COMPLETED);

    listener.afterJob(jobExecution);

    verify(s3Client).putObjectTagging(any(PutObjectTaggingRequest.class));
  }

  @Test
  void testAfterJobNotCompleted() {
    jobExecution.setStatus(BatchStatus.FAILED);

    listener.afterJob(jobExecution);

    verify(s3Client, never()).putObjectTagging(any(PutObjectTaggingRequest.class));
  }
}