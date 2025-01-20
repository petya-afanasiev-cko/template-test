package com.checkout.settlement.integration.reference.oneline;

import com.checkout.settlement.loader.reader.FileLineRecord;
import com.checkout.settlement.loader.reader.UnknownRecord;
import com.checkout.settlement.integration.reference.oneline.model.Header;
import com.checkout.settlement.integration.reference.oneline.model.Item;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@StepScope
@Component
public class OnelineMapper implements LineMapper<FileLineRecord> {

    static final String HEADER = "01";
    static final String TRAILER = "02";

    @Value("${jobParameters.file_name:'dummy.txt'}")
    private String fileName;

    @Override
    public FileLineRecord mapLine(String line, int lineNumber) {
        var recordType = line.substring(0, 2);
        return switch (recordType) {
            case HEADER -> mapHeader(lineNumber, line);
            case TRAILER -> mapItem(lineNumber, line);
            default -> new UnknownRecord(fileName, lineNumber, line);
        };
    }

    private Header mapHeader(int lineNumber, String line) {
      return Header.builder()
          .fileName(fileName)
          .lineNumber(lineNumber)
          .lineContents(line)
          .field1(line.substring(2, 18))
          .field2(Integer.parseInt(line.substring(18, 20)))
          .build();
    }

    private Item mapItem(int lineNumber, String line) {
        return Item.builder()
                .fileName(fileName)
                .lineNumber(lineNumber)
                .lineContents(line)
                .field1(line.substring(2, 16))
                .field2(line.substring(16, 27))
                .build();
    }
}
