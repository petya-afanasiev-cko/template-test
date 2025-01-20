package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.confirmation;

import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.confirmation.model.Charge;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.confirmation.model.ConfirmationRecord;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.confirmation.model.Header;
import com.checkout.settlement.loader.reader.FileLineRecord;
import com.checkout.settlement.loader.reader.FileTree.RecordContext;
import com.checkout.settlement.loader.reader.FileTreeReader;
import com.checkout.settlement.loader.reader.TreeConfiguration;
import com.checkout.settlement.loader.writer.OutgoingEvent;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class ConfirmationJob {

  public static final String STEP_NAME = "confirmation-step";
 
  @Bean
  public Step confirmationStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      FileTreeReader<ConfirmationRecord> fileReader,
      FlatFileItemReader<FileLineRecord> lineReader,
      ConfirmationLineMapper confirmationLineMapper,
      ConfirmationProcessor eventCreator,
      ItemWriter<OutgoingEvent> eventWriter) {

    return new StepBuilder(STEP_NAME, jobRepository)
        .<RecordContext<ConfirmationRecord>, OutgoingEvent>chunk(1, transactionManager)
        .listener(new StepExecutionListener() {
          @Override
          public void beforeStep(StepExecution stepExecution) {
            lineReader.setLineMapper(confirmationLineMapper);
            fileReader.setLineReader(lineReader);
            fileReader.setTreeConfiguration(confirmationFileConfig());
          }
        })
        .reader(fileReader)
        .processor(eventCreator)
        .writer(eventWriter)
        .build();
  }
  
  public static TreeConfiguration confirmationFileConfig() {
    return () -> Map.of(Header.class.getSimpleName(), List.of(Charge.class.getSimpleName()));
  }
}
