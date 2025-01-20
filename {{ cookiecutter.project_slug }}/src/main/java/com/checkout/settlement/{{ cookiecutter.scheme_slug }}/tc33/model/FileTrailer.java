package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.tc33.model;

import lombok.Builder;

@Builder
public record FileTrailer(
    String fileName,
    int lineNumber,
    String lineContents,
    String recordType,
    String transactionCode,
    String transactionCodeQualifier,
    String transactionComponentSequenceNumber,
    String bin,
    String processingDate,
    String destinationAmount,
    String numberOfMonetaryTransactions,
    String batchNumber,
    String numberOfTCRs,
    String reserved1,
    String centerBatchID,
    String numberOfTransactions,
    String reserved2,
    String sourceAmount,
    String reserved3,
    String reserved4,
    String reserved5,
    String reserved6
) implements Tc33Record {
  /*
  public static final String TRANSACTION_CODE = "92";

  public FileTrailer {
    if (!transactionCode.equals(TRANSACTION_CODE)) {
      throw new IllegalArgumentException("Invalid Transaction Code");
    }
    // You might want to add validation for other fields as needed, similar to transactionCode
  }

  public FileTrailer(String numberOfMonetaryTransactions, String batchNumber, String numberOfTCRs, String centerBatchID, String numberOfTransactions, String sourceAmount) {
    this(TRANSACTION_CODE,
        "0",
        "0",
        "000000",
        "00000",
        "000000000000000",
        numberOfMonetaryTransactions,
        batchNumber,
        numberOfTCRs,
        "      ",
        centerBatchID,
        numberOfTransactions,
        "                  ",
        sourceAmount,
        "               ",
        "               ",
        "               ",
        "       ");
  }*/
}