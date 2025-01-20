package com.checkout.settlement.loader.{{ cookiecutter.scheme_slug }}.{{ cookiecutter.first_file_type }}.model;

import lombok.Builder;

@Builder(toBuilder=true)
public record Item(
    String fileName,
    int lineNumber,
    String lineContents,

    String field1,
    String field2
) implements File{{ cookiecutter.first_file_type }}Record {
}
