package com.checkout.settlement.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.checkout.settlement.loader.batch.BatchConfiguration;
import com.checkout.settlement.loader.batch.JobLaunchingGateway;
import com.checkout.settlement.loader.writer.OutgoingEvent;
import com.checkout.settlement.integration.reference.TestJobFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.support.ScopeConfiguration;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBatchTest
@SpringJUnitConfig(classes = {BatchConfiguration.class, TestConfig.class, ScopeConfiguration.class})
class SpringBatchIntegrationTest {

  @Autowired
  private JobRepositoryTestUtils jobRepositoryTestUtils;
  @Autowired
  private ItemWriter<OutgoingEvent> kafkaWriter;
  @Autowired
  private JobLaunchingGateway gateway;
  @Autowired
  private TestJobFactory testJobFactory;
  @MockitoBean
  private S3Client s3Client;

  @AfterEach
  public void cleanUp() {
    jobRepositoryTestUtils.removeJobExecutions();
  }

  private JobParameters defaultJobParameters() {
    var paramsBuilder = new JobParametersBuilder();
    paramsBuilder.addString("fileName", "com/checkout/settlement/integration/reference/testfile_SINGLE.txt");
    return paramsBuilder.toJobParameters();
  }

  @Test
  void givenVisitorsFlatFile_whenJobExecuted_thenSuccess() throws Exception {
    // given

    var jobExecution = gateway.launch(testJobFactory, "", "/testfile_SINGLE.txt", defaultJobParameters());

    // batch runs async.. wait for it to finish
    while(jobExecution.isRunning()) {
      Thread.sleep(50);
    }

    var actualJobInstance = jobExecution.getJobInstance();
    var actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo("single-line");
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
    TestWriter testWriter = (TestWriter)kafkaWriter;
    assertThat(testWriter.getOutput().size()).isEqualTo(2);
  }

}