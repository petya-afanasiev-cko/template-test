package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.confirmation.model;

// Just for demo purposes to show how we can publish the SettlementTotalEventPublic if a scheme has a record with totals.
public record ChargeTotal(
    String fileName,
    int lineNumber,
    String lineContents,
    String totalGrossAmount) implements ConfirmationRecord {
}
