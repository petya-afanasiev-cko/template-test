package com.checkout.settlement.loader.reader;

import com.checkout.settlement.loader.reader.FileTree.RecordContext;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import java.util.LinkedList;
import java.util.Queue;

import static java.util.Objects.isNull;

@Log4j2
@StepScope
@Component
@RequiredArgsConstructor
public class FileTreeReader<T extends FileLineRecord> implements ItemReader<RecordContext<T>> {

  private final Queue<RecordContext<FileLineRecord>> queue = new LinkedList<>();
  private FileTree<FileLineRecord> tree;
  @Setter
  private TreeConfiguration treeConfiguration;
  @Setter
  private ItemStreamReader<FileLineRecord> lineReader;

  @BeforeStep
  public void beforeStep(@NonNull StepExecution stepExecution) {
    tree = new FileTree<>(treeConfiguration);
    lineReader.open(stepExecution.getExecutionContext());
  }

  @AfterStep
  public void afterStep(@NonNull StepExecution stepExecution) {
    lineReader.close();
  }

  @Override
  public RecordContext<T> read() throws Exception {
    out: while (queue.isEmpty()) {
      var record = lineReader.read();
      if (record instanceof UnknownRecord) {
        log.error("Record not mappable from file {}, line {}", record.fileName(), record.lineNumber());
        continue;
      }
      var nodes = tree.add(record);
      for (var node : nodes) {
        if (isNull(node)) {
          break out;
        }
        queue.add(node.getEdgeToRoot());
      }
    }
    return queue.isEmpty() ? null : (RecordContext<T>) queue.remove();
  }
}
