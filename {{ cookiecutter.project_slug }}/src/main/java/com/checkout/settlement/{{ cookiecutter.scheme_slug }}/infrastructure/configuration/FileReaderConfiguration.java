package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.infrastructure.configuration;

import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.infrastructure.reader.UnknownRecord;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.infrastructure.reader.FileLineRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import java.util.Map;

import static com.checkout.settlement.{{ cookiecutter.scheme_slug }}.infrastructure.batch.JobParameter.*;
import static java.nio.charset.StandardCharsets.UTF_8;

@Configuration
@RequiredArgsConstructor
public class FileReaderConfiguration {

  private final S3Client s3Client;

  @Bean
  @StepScope
  public FlatFileItemReader<FileLineRecord> s3FileReader(
      @Value("#{jobParameters}") Map<String, String> jobParameters
  ) {
    var s3Stream = s3Client.getObject(GetObjectRequest.builder()
        .bucket(jobParameters.get(S3_BUCKET))
        .key(jobParameters.get(S3_KEY)).build());
    return new FlatFileItemReaderBuilder<FileLineRecord>()
        .name("s3FlatFileItemReader")
        .encoding(UTF_8.name())
        .resource(new InputStreamResource(s3Stream))
        .lineMapper(UnknownRecord::new) // override in step
        .build();
  }

}
