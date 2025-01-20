package com.checkout.settlement.{{ cookiecutter.scheme_slug }};

import static com.checkout.settlement.loader.batch.JobParameter.S3_BUCKET;
import static com.checkout.settlement.loader.batch.JobParameter.S3_KEY;

import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.confirmation.{{ cookiecutter.__scheme_no_space }}JobFactory;
import com.checkout.settlement.loader.batch.JobLaunchingGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.support.ScopeConfiguration;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;
import org.springframework.util.Assert;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@RequiredArgsConstructor
@ConfigurationPropertiesScan
@Import({ ScopeConfiguration.class })
public class SettlementApplication implements ApplicationRunner {
  
  private final JobLaunchingGateway jobLaunchGateway;
  private final {{ cookiecutter.__scheme_no_space }}JobFactory jobFactory;

	public static void main(String[] args) {
    SpringApplication.run(SettlementApplication.class, args);
	}

  @Override
  public void run(ApplicationArguments args) throws Exception {
    String s3Bucket = System.getenv(S3_BUCKET);
    String s3Key = System.getenv(S3_KEY);

    if (args.getOptionValues(S3_BUCKET).size() == 1){
      s3Bucket = args.getOptionValues(S3_BUCKET).getFirst();
    }

    if (args.getOptionValues(S3_KEY).size() == 1){
      s3Key = args.getOptionValues(S3_KEY).getFirst();
    }

    Assert.isTrue(s3Bucket != null,
        "Single 's3_bucket' parameter is required");
    Assert.isTrue(s3Key != null,
        "Single 's3_key' parameter is required");

    var parameters = new JobParametersBuilder().toJobParameters();
    jobLaunchGateway.launch(
        jobFactory,
        s3Bucket,
        s3Key,
        parameters);
  }

}
