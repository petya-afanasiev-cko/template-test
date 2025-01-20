package com.checkout.settlement.loader.reader;

public record UnknownRecord(
    String fileName,
    int lineNumber,
    String lineContents
) implements FileLineRecord {
}
