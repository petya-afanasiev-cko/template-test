package com.checkout.settlement.loader.{{ cookiecutter.scheme_slug }}.{{ cookiecutter.first_file_type }};

import com.checkout.settlement.loader.reader.FileLineRecord;
import com.checkout.settlement.loader.reader.FileTree.RecordContext;
import com.checkout.settlement.loader.reader.FileTreeReader;
import com.checkout.settlement.loader.reader.TreeConfiguration;
import com.checkout.settlement.loader.writer.OutgoingEvent;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.checkout.settlement.loader.{{ cookiecutter.scheme_slug }}.{{ cookiecutter.first_file_type }}.model.Header;
import com.checkout.settlement.loader.{{ cookiecutter.scheme_slug }}.{{ cookiecutter.first_file_type }}.model.Item;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class File{{ cookiecutter.first_file_type }}Job {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final FileTreeReader<FileLineRecord> fileReader;
  private final FlatFileItemReader<FileLineRecord> lineReader;
  private final File{{ cookiecutter.first_file_type }}Mapper lineMapper;
  private final File{{ cookiecutter.first_file_type }}Processor eventProcessor;
  private final ItemWriter<OutgoingEvent> eventWriter;

  public static final String STEP_NAME = "single-line";

  public Job createJob() {
    return new JobBuilder(STEP_NAME, jobRepository)
        .start(createStep())
        .build();
  }

  private Step createStep() {
    return new StepBuilder(STEP_NAME, jobRepository)
        .<RecordContext<FileLineRecord>, OutgoingEvent>chunk(1, transactionManager)
        .listener(new StepExecutionListener() {
          @Override
          public void beforeStep(StepExecution stepExecution) {
            lineReader.setLineMapper(lineMapper);
            fileReader.setLineReader(lineReader);
            fileReader.setTreeConfiguration(singleLineConfig());
          }
        })
        .reader(fileReader)
        .processor(eventProcessor)
        .writer(eventWriter)
        .build();
  }

  public static TreeConfiguration singleLineConfig() {
    return () -> Map.of(
        Header.class.getSimpleName(), List.of(Item.class.getSimpleName()),
        Item.class.getSimpleName(), List.of()
    );
  }
}
