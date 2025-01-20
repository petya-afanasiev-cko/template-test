package com.checkout.settlement.loader.{{ cookiecutter.scheme_slug }};

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.batch.core.Job;
import org.springframework.stereotype.Component;

import com.checkout.settlement.loader.{{ cookiecutter.scheme_slug }}.{{ cookiecutter.first_file_type }}.File{{ cookiecutter.first_file_type }}Job;
//import com.checkout.settlement.loader.{{ cookiecutter.scheme_slug }}.FILETYPE2.FILETYPE2Job;


@Component
@AllArgsConstructor
public class JobFactory implements com.checkout.settlement.loader.batch.JobFactory {

  // Job implementation gets injected to this variable (using Spring constructor autowiring)
  // TODO: individual file jobs go here
  private final File{{ cookiecutter.first_file_type }}Job file{{ cookiecutter.first_file_type }}Job;
  // private final FILETYPE2Job filetype2Job;

  /**
   * Identifies the filetype (based on for example name and/or S3 folder) and delegates to the appropriate filetype job.
   */
  @Override
  public Job mapFileToJob(String fileName, String s3key) {
    if (fileName.contains("{{ cookiecutter.first_file_type }}_IDENTIFIER")) {
      return file{{ cookiecutter.first_file_type }}Job.createJob();
    }
    /*
    if (fileName.contains("FILETYPE2_IDENTIFIER")) {
      return filetype2Job.createJob();
    }
    */
    throw new NotImplementedException("No job type for file: " + fileName);
  }
}
