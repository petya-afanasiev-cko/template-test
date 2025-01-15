package com.checkout.settlement.dci.infrastructure.reader;

public record UnknownRecord(
    String fileName,
    int lineNumber
) implements FileLineRecord {
}
