package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.confirmation.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

public record Header(
    String fileName,
    int lineNumber,
    String recordType,
    String acquiringInstitutionIdentificationCode,
    String acquiringIsoInstitutionIdentificationCode,
    String versionIndicator,
    Optional<LocalDate> originalFileDate,
    Optional<Integer> originalFileNumber,
    Instant timestamp,
    String emptyFileIndicator,
    String filler
) implements ConfirmationRecord {
}
