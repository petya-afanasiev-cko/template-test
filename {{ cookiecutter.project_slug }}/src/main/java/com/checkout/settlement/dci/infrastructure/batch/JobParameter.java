package com.checkout.settlement.dci.infrastructure.batch;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class JobParameter {
  public static final String JOB_PREFIX = "DCI-";
  public static final String JOB_TYPE = "job_type";
  public static final String LAUNCHED_AT = "launched_at";
  public static final String S3_BUCKET = "s3_bucket";
  public static final String S3_KEY = "s3_key";
  public static final String FILE_NAME = "file_name";
}
