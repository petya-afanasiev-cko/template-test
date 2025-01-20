package com.checkout.settlement.integration.reference;

import com.checkout.settlement.loader.batch.JobFactory;
import com.checkout.settlement.integration.reference.oneline.OnelineJob;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.batch.core.Job;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TestJobFactory implements JobFactory {

  // Job implementation gets injected to this variable (Spring constructor autowiring)
  private final OnelineJob singleLineJob;

  @Override
  public Job mapFileToJob(String fileName, String s3key) {
    // choose the correct job based on file
    if (fileName.contains("SINGLE")) {
      return singleLineJob.createJob();
    }
    /*
    if (fileName.contains("MULTI")) {
      return TC33;
    }
     */
    throw new NotImplementedException("No job type for file: " + fileName);
  }
}
