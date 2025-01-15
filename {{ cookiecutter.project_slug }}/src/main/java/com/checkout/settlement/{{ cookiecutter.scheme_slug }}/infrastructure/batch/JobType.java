package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.infrastructure.batch;

import org.apache.commons.lang3.NotImplementedException;
import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public enum JobType {
  CONFIRMATION("Confirmation");
  
  final String value;
  JobType(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }

  static final Map<String, JobType> map;
  static {
    map = Arrays.stream(values()).collect(toMap(e -> e.value, e -> e));
  }
  public static JobType from(String value) {
    return map.getOrDefault(value, null);
  }
 
  public static JobType fromFileName(String fileName) {
    if (fileName.contains("ACQ")) {
      return CONFIRMATION;
    }
    throw new NotImplementedException("No job type for file: " + fileName);
  }
}
