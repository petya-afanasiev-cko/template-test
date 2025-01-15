package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.infrastructure.reader;

public record UnknownRecord(
    String fileName,
    int lineNumber
) implements FileLineRecord {
}
