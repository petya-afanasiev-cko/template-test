package com.checkout.settlement.integration.reference.oneline.model;

import lombok.Builder;

@Builder(toBuilder=true)
public record Item(
    String fileName,
    int lineNumber,
    String lineContents,

    String field1,
    String field2
) implements OnelineRecord {
}
