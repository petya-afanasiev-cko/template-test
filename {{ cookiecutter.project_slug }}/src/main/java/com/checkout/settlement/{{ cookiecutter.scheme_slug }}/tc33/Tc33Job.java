package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.tc33;

import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.tc33.model.CAS;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.tc33.model.FileHeader;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.tc33.model.FileTrailer;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.tc33.model.Tc33Record;
import com.checkout.settlement.loader.reader.FileLineRecord;
import com.checkout.settlement.loader.reader.FileTree.RecordContext;
import com.checkout.settlement.loader.reader.MultiLineFileTreeReader;
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
public class Tc33Job {

  public static final String STEP_NAME = "tc33-step";

  @Bean
  public Step tc33Step(
      JobRepository jobRepository, PlatformTransactionManager transactionManager,
      MultiLineFileTreeReader<Tc33Record> fileReader,
      FlatFileItemReader<FileLineRecord> lineReader,
      Tc33LineMapper lineMapper,
      Tc33Processor eventCreator,
      ItemWriter<OutgoingEvent> eventWriter) {
    return new StepBuilder(STEP_NAME, jobRepository)
        .<RecordContext<Tc33Record>, OutgoingEvent>chunk(1, transactionManager)
        .listener(new StepExecutionListener() {
          @Override
          public void beforeStep(StepExecution stepExecution) {
            lineReader.setLineMapper(lineMapper);
            fileReader.setLineReader(lineReader);
            fileReader.setLineMapper(lineMapper);
            fileReader.setTreeConfiguration(tc33FileConfig());
          }
        })
        .reader(fileReader)
        .processor(eventCreator)
        .writer(eventWriter)
        .build();
  }
  
  public static TreeConfiguration tc33FileConfig() {
    return () -> Map.of(
        FileHeader.class.getSimpleName(), List.of(CAS.class.getSimpleName(), FileTrailer.class.getSimpleName()),
        CAS.class.getSimpleName(), List.of()
    );
  }
}
