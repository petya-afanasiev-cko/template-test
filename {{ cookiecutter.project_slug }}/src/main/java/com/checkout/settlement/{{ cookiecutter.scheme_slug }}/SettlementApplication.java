package com.checkout.settlement.{{ cookiecutter.scheme_slug }};

import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.infrastructure.batch.JobLaunchingGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.support.ScopeConfiguration;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.util.Assert;

import java.util.Optional;

import static com.checkout.settlement.{{ cookiecutter.scheme_slug }}.infrastructure.batch.JobParameter.S3_BUCKET;
import static com.checkout.settlement.{{ cookiecutter.scheme_slug }}.infrastructure.batch.JobParameter.S3_KEY;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@RequiredArgsConstructor
@Import({ ScopeConfiguration.class })
public class SettlementApplication implements ApplicationRunner {
  
  private final JobLaunchingGateway jobLaunchGateway;
  
	public static void main(String[] args) {
    SpringApplication.run(SettlementApplication.class, args);
	}

  @Override
  public void run(ApplicationArguments args) throws Exception {
    Assert.isTrue(args.containsOption(S3_BUCKET) && args.getOptionValues(S3_BUCKET).size() == 1,
        "Single 's3_bucket' parameter is required");
    Assert.isTrue(args.containsOption(S3_KEY)  && args.getOptionValues(S3_KEY).size() == 1,
        "Single 's3_key' parameter is required");
    jobLaunchGateway.launch(
        args.getOptionValues(S3_BUCKET).getFirst(),
        args.getOptionValues(S3_KEY).getFirst(),
        Optional.empty());
  }

}
