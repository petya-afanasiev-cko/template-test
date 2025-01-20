package com.checkout.settlement.integration.reference.oneline.model;

import lombok.Builder;

@Builder
public record Header(
    String fileName,
    int lineNumber,
    String lineContents,
    String field1,
    int field2
) implements OnelineRecord {
}