package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.tc33.model;

import lombok.Builder;

@Builder
public record PartiallyParsedLine(
    String fileName,
    int lineNumber,
    String lineContents
) implements Tc33Record {
}