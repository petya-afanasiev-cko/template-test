package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.t112.model;

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
) implements T112Record {
}