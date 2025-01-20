package com.checkout.settlement.integration;

import com.checkout.settlement.loader.reader.FileLineRecord;
import com.checkout.settlement.loader.reader.UnknownRecord;
import com.checkout.settlement.loader.writer.OutgoingEvent;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Configuration
@ComponentScan(basePackages = {
    "com.checkout.settlement.loader.batch",
    "com.checkout.settlement.loader.reader",
    "com.checkout.settlement.integration.reference"
})
public class TestConfig {

  @Bean
  public ItemWriter<OutgoingEvent> dummyWriter() {
    // Replace Kafka writer with test writer so that test has access to written events
    return new TestWriter();
  }

  @Bean
  @StepScope
  public FlatFileItemReader<FileLineRecord> localFileReader(
      @Value("#{jobParameters}") Map<String, String> jobParameters
  ) {
    // Replace S3 file reader with local file reader for testing
    return new FlatFileItemReaderBuilder<FileLineRecord>()
        .name("LocalFileItemReader")
        .encoding(UTF_8.name())
        .resource(new ClassPathResource(jobParameters.get("fileName")))
        .lineMapper((line, lineNumber) -> new UnknownRecord("filename", lineNumber, line)) // override in step
        .build();
  }
}
