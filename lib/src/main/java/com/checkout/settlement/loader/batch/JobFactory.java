package com.checkout.settlement.loader.batch;

import org.springframework.batch.core.Job;

public interface JobFactory {

    Job mapFileToJob(String fileName, String s3key);
}
