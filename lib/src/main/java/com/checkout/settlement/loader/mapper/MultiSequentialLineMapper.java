package com.checkout.settlement.loader.mapper;

import com.checkout.settlement.loader.reader.FileLineRecord;
import org.springframework.batch.item.file.LineMapper;
import java.util.List;

public interface MultiSequentialLineMapper<T extends FileLineRecord> extends LineMapper<T> {
  Boolean isNextRecord(T line);
  T postParse(List<T> multiLineObject);
}
