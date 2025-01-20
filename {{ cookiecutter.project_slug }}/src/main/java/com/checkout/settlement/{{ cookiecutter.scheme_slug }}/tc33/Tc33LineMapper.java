package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.tc33;

import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.tc33.model.CAS;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.tc33.model.FileHeader;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.tc33.model.FileTrailer;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.tc33.model.PartiallyParsedLine;
import com.checkout.settlement.loader.mapper.MultiSequentialLineMapper;
import com.checkout.settlement.loader.reader.FileLineRecord;
import com.checkout.settlement.loader.reader.UnknownRecord;
import java.util.List;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@StepScope
@Component
public class Tc33LineMapper implements MultiSequentialLineMapper<FileLineRecord> {

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
            case TCR_PREFIX -> {
                if (line.substring(2, 4).equals(TCR0)) {  // todo this does not work for more complex cases, requires statefulness
                    yield mapCASTcr0(lineNumber, line);
                } else {
                    yield mapPartialLine(lineNumber, line);
                }
            }
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

    private CAS mapCASTcr0(int lineNumber, String line) {
      return CAS.builder()
          .fileName(fileName)
          .lineNumber(lineNumber)
          .lineContents(line)
          .recordType(line.substring(0, 2))
          .transactionCode("")
          .transactionCodeQualifier("")
          .transactionComponentSequenceNumber("")
          .destinationBin("")
          .sourceBin("")
          .casAdviceRecordId("")
          .originatorRecipientIndicator("")
          .transactionCodeOfFinancialTransaction("")
          .tcqOfFinancialTransaction("")
          .centralProcessingDate("")
          .transactionIdentifier("")
          .accountNumber("")
          .accountNumberExtension("")
          .acquirerReferenceNumber("")
          .cardAcceptorId("")
          .terminalId("")
          .sourceAmount("")
          .sourceCurrencyCode("")
          .settlementFlag("")
          .settlementServiceId("")
          .settlementCurrency("")
          .leafLevelSreId("")
          .fundsTransferSreId("")
          .settlementAmountInterchange("")
          .settlementAmountSign("")
          .usageCode("")
          .reclassificationIndicator("")
          .requestedPaymentService("")
          .reserved("")
          .build();
    }

    private CAS mapCas2ndLine(CAS casRecord, String line) {
      return casRecord.toBuilder()
                .recordType("")
                .transactionCode("")
                .transactionCodeQualifier("")
                .transactionComponentSequenceNumber("")
                .interchangeFeeAmount("")
                .interchangeFeeSign("")
                .merchantVerificationValue("")
                .feeProgramIndicator("")
                .feeDescriptor("")
                .conversionDate("")
                .settlementDate("")
                .base2UniqueFileId("")
                .reserved("")
                .persistentFxAppliedIndicator("")
                .rateTableId("")
                .additionalTokenResponseInformation("")
                .agreementId("")
                .enablerVerificationValue("")
                .visaMerchantIdentifier("")
          // just pull something from line as an example
                .reserved1(line.substring(0, 1))
                .build();
    }

  private FileLineRecord mapPartialLine(int lineNumber, String line) {
    return PartiallyParsedLine.builder()
        .fileName(fileName)
        .lineNumber(lineNumber)
        .lineContents(line)
        .build();
  }

  @Override
  public Boolean isNextRecord(FileLineRecord line) {
    return !(line instanceof PartiallyParsedLine);
  }

  @Override
  public FileLineRecord postParse(List<FileLineRecord> multiLineObject) {
    FileLineRecord mainObject = multiLineObject.get(0);
    if (mainObject instanceof CAS casRecord) {
      // parse this record type.. probably add fields to main object from sub-lines
      PartiallyParsedLine secondLine = (PartiallyParsedLine)multiLineObject.get(1);
      mainObject = mapCas2ndLine(casRecord, secondLine.lineContents());
    }
    return mainObject;
  }
}
