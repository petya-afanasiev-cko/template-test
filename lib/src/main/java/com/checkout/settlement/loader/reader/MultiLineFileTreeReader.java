package com.checkout.settlement.loader.reader;

import static java.util.Objects.isNull;

import com.checkout.settlement.loader.mapper.MultiSequentialLineMapper;
import com.checkout.settlement.loader.reader.FileTree.RecordContext;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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

@Log4j2
@StepScope
@Component
@RequiredArgsConstructor
public class MultiLineFileTreeReader<T extends FileLineRecord> implements
    ItemReader<RecordContext<T>> {

  private final Queue<RecordContext<FileLineRecord>> queue = new LinkedList<>();
  private FileTree<FileLineRecord> tree;
  @Setter
  private TreeConfiguration treeConfiguration;
  @Setter
  private ItemStreamReader<FileLineRecord> lineReader;
  @Setter
  private MultiSequentialLineMapper<FileLineRecord> lineMapper;

  private FileLineRecord peekedLine;

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
      List<FileLineRecord> collectedRecord = new ArrayList<>();
      if (peekedLine == null) {
        peekedLine = lineReader.read();
      }
      collectedRecord.add(peekedLine);

      while ((peekedLine = lineReader.read()) != null) {
        if (lineMapper.isNextRecord(peekedLine)) {
          break;
        }
        collectedRecord.add(peekedLine);
      }
      var record = lineMapper.postParse(collectedRecord);

      // do not offer UnknownRecord to tree
      if (record instanceof UnknownRecord) {
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
