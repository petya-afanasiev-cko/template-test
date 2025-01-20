package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.t112;

import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.t112.model.FileHeader;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.t112.model.FileTrailer;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.t112.model.T112;
import com.checkout.settlement.loader.mapper.MultiSequentialLineMapper;
import com.checkout.settlement.loader.reader.FileLineRecord;
import com.checkout.settlement.loader.reader.UnknownRecord;
import java.util.List;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@StepScope
@Component
public class T112LineMapper implements MultiSequentialLineMapper<FileLineRecord> {

  static final String HEADER = "90";
  static final String TRAILER = "92";
  static final String TCR_PREFIX = "33";
  static final String TCR0 = "00";

  @Value("${jobParameters.file_name:'dummy.txt'}")
  private String fileName;

  @Override
  public FileLineRecord mapLine(String line, int lineNumber) {
    var recordType = line.substring(0, 2);
    return switch (recordType) {
      case HEADER -> mapHeader(lineNumber, line);
      case TRAILER -> mapTrailer(lineNumber, line);
      case TCR_PREFIX -> mapT112(lineNumber, line);
      default -> new UnknownRecord(fileName, lineNumber, line);
    };
  }

  // ToDo: WIP!!!
  private FileHeader mapHeader(int lineNumber, String line) {
    return FileHeader.builder()
        .fileName(fileName)
        .lineNumber(lineNumber)
        .lineContents(line)
        .recordType(line.substring(0, 2))
        .transactionCode("")
        .processingBIN("")
        .processingDate("")
        .reserved("")
        .testOption("")
        .reserved1("")
        .securityCode("")
        .reserved2("")
        .outgoingFileID("")
        .reserved3("")
        .build();
  }

  private FileTrailer mapTrailer(int lineNumber, String line) {
    return FileTrailer.builder()
        .fileName(fileName)
        .lineNumber(lineNumber)
        .lineContents(line)
        .recordType(line.substring(0, 2))
        .transactionCode("")
        .transactionCodeQualifier("")
        .transactionComponentSequenceNumber("")
        .bin("")
        .processingDate("")
        .destinationAmount("")
        .numberOfMonetaryTransactions("")
        .batchNumber("")
        .numberOfTCRs("")
        .reserved1("")
        .centerBatchID("")
        .numberOfTransactions("")
        .reserved2("")
        .sourceAmount("")
        .reserved3("")
        .reserved4("")
        .reserved5("")
        .reserved6("")
        .build();
  }

  private T112 mapT112(int lineNumber, String line) {
    return T112.builder()
        .fileName(fileName)
        .lineNumber(lineNumber)
        .lineContents(line)
        .recordType(line.substring(0, 2))
        .transactionCode("")
        .build();
  }

  @Override
  public Boolean isNextRecord(FileLineRecord line) {
    return false;
  }

  @Override
  public FileLineRecord postParse(List<FileLineRecord> multiLineObject) {
    throw new UnsupportedOperationException();
  }
}
