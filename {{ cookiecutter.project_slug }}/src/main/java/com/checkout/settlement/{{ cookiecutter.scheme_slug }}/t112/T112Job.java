package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.t112;

import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.t112.model.FileHeader;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.t112.model.FileTrailer;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.t112.model.T112;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.t112.model.T112Record;
import com.checkout.settlement.loader.reader.BinaryItemReader;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class T112Job {

    public static final String STEP_NAME = "t112-step";

    @Bean
    public Step t112Step(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FileTreeReader<T112Record> fileReader,
            BinaryItemReader<FileLineRecord> lineReader,
            T112LineMapper lineMapper,
            T112Processor eventCreator,
            ItemWriter<OutgoingEvent> eventWriter) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<RecordContext<T112Record>, OutgoingEvent>chunk(1, transactionManager)
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        lineReader.setLineMapper(lineMapper);
                        fileReader.setLineReader(lineReader);
                        fileReader.setTreeConfiguration(t112FileConfig());
                    }
                })
                .reader(fileReader)
                .processor(eventCreator)
                .writer(eventWriter)
                .build();
    }

    public static TreeConfiguration t112FileConfig() {
        return () -> Map.of(
                FileHeader.class.getSimpleName(), List.of(T112.class.getSimpleName(), FileTrailer.class.getSimpleName()),
                T112.class.getSimpleName(), List.of()
        );
    }
}
