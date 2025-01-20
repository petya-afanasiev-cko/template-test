package com.checkout.settlement.loader.{{ cookiecutter.scheme_slug }};

import com.checkout.settlement.loader.batch.JobLaunchingGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.support.ScopeConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.util.Assert;

import static com.checkout.settlement.loader.batch.JobParameter.S3_BUCKET;
import static com.checkout.settlement.loader.batch.JobParameter.S3_KEY;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@RequiredArgsConstructor
@ComponentScan(basePackages = {"com.checkout.settlement.loader"})
@Import({ ScopeConfiguration.class })
public class SettlementApplication implements ApplicationRunner {
  // These values are injected at startup with the following fallback:
  // program arguments > ENV variables > application.yml
  // for program arguments use: java -jar JARNAME.jar --s3_bucket="from_args"
  // for ENV variables add the following variable: export s3_bucket=from_env
  // for application.yml add the following to resources/application.yml:
  // s3_bucket: from_application.yml
  @Value("${" + S3_BUCKET + "}")
  private String S3Bucket;
  @Value("${" + S3_KEY + "}")
  private String S3Key;
  
  private final JobLaunchingGateway jobLaunchGateway;
  private final JobFactory jobFactory;
  
  public static void main(String[] args) {
    SpringApplication.run(SettlementApplication.class, args);
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    Assert.notNull(S3Bucket, "Single 's3_bucket' parameter is required");
    Assert.notNull(S3Key, "Single 's3_key' parameter is required");
    jobLaunchGateway.launch(
        jobFactory,
        args.getOptionValues(S3_BUCKET).getFirst(),
        args.getOptionValues(S3_KEY).getFirst(),
        null
    );
  }
}
