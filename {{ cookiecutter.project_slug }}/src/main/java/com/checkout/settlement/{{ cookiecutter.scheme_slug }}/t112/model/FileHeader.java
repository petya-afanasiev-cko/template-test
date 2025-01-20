package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.t112.model;

import lombok.Builder;

@Builder
public record FileHeader(
    String fileName,
    int lineNumber,
    String lineContents,
    String recordType,
    String transactionCode,
    String processingBIN,
    String processingDate,
    String reserved,
    String testOption,
    String reserved1,
    String securityCode,
    String reserved2,
    String outgoingFileID,
    String reserved3
) implements T112Record {
}