package com.checkout.settlement.loader.configuration;

import static com.checkout.settlement.loader.batch.JobParameter.S3_BUCKET;
import static com.checkout.settlement.loader.batch.JobParameter.S3_KEY;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.checkout.settlement.loader.reader.BinaryItemReader;
import com.checkout.settlement.loader.reader.FileLineRecord;
import com.checkout.settlement.loader.reader.UnknownRecord;
import java.io.IOException;
import java.util.Map;
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
        .lineMapper((line, lineNumber) -> new UnknownRecord("file", lineNumber, line)) // override in step
        .build();
  }

  @Bean
  @StepScope
  public BinaryItemReader<FileLineRecord> iso8583S3FileReader(
      @Value("#{jobParameters}") Map<String, String> jobParameters
  ) throws IOException {
    var s3Stream = s3Client.getObject(GetObjectRequest.builder()
        .bucket(jobParameters.get(S3_BUCKET))
        .key(jobParameters.get(S3_KEY)).build());
    return new BinaryItemReader<>((new InputStreamResource(s3Stream)).getInputStream(), 1024);
  }

}
