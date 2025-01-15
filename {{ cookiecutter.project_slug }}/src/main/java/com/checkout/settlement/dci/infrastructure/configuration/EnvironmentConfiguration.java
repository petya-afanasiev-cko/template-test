package com.checkout.settlement.dci.infrastructure.configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import java.util.Arrays;
import java.util.List;

@Configuration
@Log4j2
@RequiredArgsConstructor
public class EnvironmentConfiguration {

  private final Environment environment;
  private final BuildProperties buildProperties;
  private List<String> activeProfiles;

  @PostConstruct
  public void init() {
    activeProfiles = Arrays.asList(environment.getActiveProfiles());
    ThreadContext.put("Application.BuiltOn", buildProperties.getTime().toString());
  }

  public boolean isLocalDev() {
    return activeProfiles.contains("localdev");
  }
}

