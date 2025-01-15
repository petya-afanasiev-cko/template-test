package com.checkout.settlement.dci.infrastructure.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.services.s3.S3Client;
import java.net.URI;

import static software.amazon.awssdk.regions.Region.EU_WEST_1;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class AwsConfiguration {

  private final EnvironmentConfiguration environmentConfiguration;

  @Value("${localstack.endpoint}")
  private String localstackEndpointOverride;


  @Bean
  public AwsCredentialsProvider awsCredentialsProvider() {
    log.debug("Creating credentials provider bean");
    return DefaultCredentialsProvider.builder()
        .asyncCredentialUpdateEnabled(true)
        .reuseLastProviderEnabled(true)
        .build();
  }

  @Bean
  public S3Client s3Client(AwsCredentialsProvider awsCredentialsProvider) {
    var s3ClientBuilder =
        S3Client.builder()
            .credentialsProvider(awsCredentialsProvider)
            .httpClientBuilder(UrlConnectionHttpClient.builder())
            .forcePathStyle(
                true) //Without this the library added the .localstack part "bucket.localstack" to the bucket URL. If it does not do that in QA, then move this flag to localstack if block.
            .region(EU_WEST_1);

    if (environmentConfiguration.isLocalDev()) {
      s3ClientBuilder.endpointOverride(URI.create(localstackEndpointOverride));
    }

    return s3ClientBuilder.build();
  }
}
