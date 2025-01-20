package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.tc33;

import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.TestKafkaWriter;
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
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;

import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Configuration
@ComponentScan(basePackages = {"com.checkout.settlement.loader.reader", "com.checkout.settlement.{{ cookiecutter.scheme_slug }}.tc33", "com.checkout.settlement.{{ cookiecutter.scheme_slug }}.event"})
public class TestConfig {

  @Bean
  @Primary
  public ItemWriter<OutgoingEvent> dummyWriter() {
    return new TestKafkaWriter();
  }

  @Bean
  @StepScope
  public FlatFileItemReader<FileLineRecord> localFileReader(
      @Value("#{jobParameters}") Map<String, String> jobParameters
  ) {
    return new FlatFileItemReaderBuilder<FileLineRecord>()
        .name("LocalFileItemReader")
        .encoding(UTF_8.name())
        .resource(new ClassPathResource(jobParameters.get("fileName")))
        .lineMapper((l, ln) -> new UnknownRecord("f", ln, l)) // override in step
        .build();
  }
}
