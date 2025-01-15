package com.checkout.settlement.dci.confirmation;

import cko.card_processing_settlement.SettlementEvent;
import com.checkout.settlement.dci.confirmation.model.Charge;
import com.checkout.settlement.dci.confirmation.model.ConfirmationRecord;
import com.checkout.settlement.dci.confirmation.model.Header;
import com.checkout.settlement.dci.event.ConfirmationEventCreator;
import com.checkout.settlement.dci.infrastructure.reader.FileTreeReader;
import com.checkout.settlement.dci.infrastructure.reader.TreeConfiguration;
import com.checkout.settlement.dci.infrastructure.event.KafkaWriter;
import com.checkout.settlement.dci.infrastructure.reader.FileTree.RecordContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import java.util.List;
import java.util.Map;

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
      ConfirmationLineMapper confirmationLineMapper,
      ConfirmationEventCreator eventCreator,
      KafkaWriter eventWriter) {
    return new StepBuilder(STEP_NAME, jobRepository)
        .<RecordContext<ConfirmationRecord>, SettlementEvent>chunk(1, transactionManager)
        .listener(new StepExecutionListener() {
          @Override
          public void beforeStep(StepExecution stepExecution) {
            fileReader.setLineMapper(confirmationLineMapper);
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
