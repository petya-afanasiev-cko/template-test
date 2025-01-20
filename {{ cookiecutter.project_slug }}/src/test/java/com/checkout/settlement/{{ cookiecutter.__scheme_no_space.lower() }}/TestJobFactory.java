package com.checkout.settlement.{{ cookiecutter.scheme_slug }};

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.confirmation.{{ cookiecutter.__scheme_no_space }}JobFactory;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.t112.T112JobFactory;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.tc33.Tc33JobFactory;
import com.checkout.settlement.loader.batch.JobFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.repository.JobRepository;

import java.util.stream.Stream;

class TestJobFactory {
  @ParameterizedTest
  @MethodSource("provideValidJobData")
  void givenValidFile_whenJobCreated_thenJobNameIsCorrect(String fileType, String expectedJobName, JobFactory jobFactory) {
    Job job = jobFactory.mapFileToJob(fileType, "");
    assertThat(job.getName()).isEqualTo(expectedJobName);
  }

  @ParameterizedTest
  @MethodSource("provideInvalidJobData")
  void givenInvalidFile_whenJobCreated_thenThrowsException(String fileType, JobFactory jobFactory) {
    assertThrows(IllegalArgumentException.class, () -> jobFactory.mapFileToJob(fileType, ""));
  }

  private static Stream<Arguments> provideValidJobData() {
    JobRepository jobRepository = mock(JobRepository.class);
    return Stream.of(
        Arguments.of("T112", "T112", new T112JobFactory(jobRepository, null)),
        Arguments.of("ACQ", "{{ cookiecutter.__scheme_no_space.upper() }}", new {{ cookiecutter.__scheme_no_space }}JobFactory(jobRepository, null)),
        Arguments.of("TC33", "TC33", new Tc33JobFactory(jobRepository, null))
    );
  }

  private static Stream<Arguments> provideInvalidJobData() {
    JobRepository jobRepository = mock(JobRepository.class);
    return Stream.of(
        Arguments.of("INVALID", new T112JobFactory(jobRepository, null)),
        Arguments.of("INVALID", new {{ cookiecutter.__scheme_no_space }}JobFactory(jobRepository, null)),
        Arguments.of("INVALID", new Tc33JobFactory(jobRepository, null))
    );
  }
}