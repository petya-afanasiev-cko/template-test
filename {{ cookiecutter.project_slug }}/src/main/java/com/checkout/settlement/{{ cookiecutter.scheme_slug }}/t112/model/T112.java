package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.t112.model;

import lombok.Builder;

@Builder(toBuilder=true)
public record T112(
    String fileName,
    int lineNumber,
    String lineContents,
    String recordType,
    String transactionCode
) implements T112Record {
}
