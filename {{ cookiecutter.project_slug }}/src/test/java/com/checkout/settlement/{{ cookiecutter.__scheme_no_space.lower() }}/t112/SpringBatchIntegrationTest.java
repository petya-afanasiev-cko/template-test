package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.t112;

import static org.assertj.core.api.Assertions.assertThat;

import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.TestKafkaWriter;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.confirmation.{{ cookiecutter.__scheme_no_space }}JobFactory;
import com.checkout.settlement.loader.batch.BatchConfiguration;
import com.checkout.settlement.loader.writer.OutgoingEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.support.ScopeConfiguration;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringBatchTest
@SpringJUnitConfig(classes = {BatchConfiguration.class, TestConfig.class, ScopeConfiguration.class,
    {{ cookiecutter.__scheme_no_space }}JobFactory.class, T112Job.class})
class SpringBatchIntegrationTest {

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;
  @Autowired
  private JobRepositoryTestUtils jobRepositoryTestUtils;
  @Autowired
  private T112JobFactory factory;
  @Autowired
  private ItemWriter<OutgoingEvent> kafkaWriter;

  @AfterEach
  public void cleanUp() {
    jobRepositoryTestUtils.removeJobExecutions();
  }

  private JobParameters defaultJobParameters() {
    var paramsBuilder = new JobParametersBuilder();
    paramsBuilder.addString("fileName",
        "com/checkout/settlement/{{ cookiecutter.scheme_slug }}/t112/TT112T0.2024-08-12.04-05-02.0072351.GB.A004");
    return paramsBuilder.toJobParameters();
  }

  @Test
  void givenBinaryFile_whenJobExecuted_thenSuccess() throws Exception {
    // given
    Job job = factory.mapFileToJob("T112", "");
    this.jobLauncherTestUtils.setJob(job);
    // when
    var jobExecution = jobLauncherTestUtils.launchJob(defaultJobParameters());

    // batch runs async.. wait for it to finish
    while (jobExecution.isRunning()) {
      Thread.sleep(50);
    }

    var actualJobInstance = jobExecution.getJobInstance();
    var actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo("T112");
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
    TestKafkaWriter testWriter = (TestKafkaWriter) kafkaWriter;
    assertThat(testWriter.getOutput().size()).isEqualTo(354);
  }

}