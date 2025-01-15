package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.event;

import cko.card_processing_settlement.Scheme;
import cko.card_processing_settlement.Settlement;
import cko.card_processing_settlement.SettlementEvent;
import cko.card_processing_settlement.SettlementType;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.confirmation.model.Charge;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.confirmation.model.ConfirmationRecord;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.confirmation.model.Header;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.infrastructure.reader.FileTree.RecordContext;
import com.google.protobuf.Timestamp;
import com.google.type.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.annotations.NotNull;
import java.time.Instant;

import static com.checkout.settlement.{{ cookiecutter.scheme_slug }}.utils.StringParser.moneyNanos;
import static com.checkout.settlement.{{ cookiecutter.scheme_slug }}.utils.StringParser.moneyUnits;

@Log4j2
@Component
@StepScope
@RequiredArgsConstructor
public class ConfirmationEventCreator implements ItemProcessor<RecordContext<ConfirmationRecord>, SettlementEvent> {
  
  @Override
  public SettlementEvent process(@NotNull RecordContext<ConfirmationRecord> item) throws Exception {
    if (item.value() instanceof Charge) {
      return presentmentSettled(item);
    }
    return null;
  }
  
  static SettlementEvent presentmentSettled(@NotNull final RecordContext<ConfirmationRecord> item) {
    var charge = (Charge) item.value();
    var header = item.context().get(Header.class.getSimpleName()).getFirst();
    return SettlementEvent.newBuilder()
        .setAcquirerReferenceNumber(charge.referenceNumber())
        .setCorrelationId("")
        .setEventTimestamp(Timestamp.newBuilder().setNanos(Instant.now().getNano()).build())
        .setIdempotencyKey("TODO: hash of the fields")
        .setOriginalFileName(charge.fileName())
        .setOriginalFileLineNumber(String.valueOf(charge.lineNumber()))
        .setPrimaryKey(String.join("-", charge.referenceNumber(), charge.networkReferenceId()))
        .setScheme(Scheme.{{ cookiecutter.scheme }})
        .setSettlementCountry("")
        .setSettlement(Settlement.newBuilder()
            .setSettlementType(SettlementType.PRESENTMENT_SETTLED)
            .setGrossSettlementAmount(Money.newBuilder()
                .setCurrencyCode("{{ cookiecutter.scheme_slug }}_recap.currency_key")
                .setUnits(moneyUnits(charge.grossSettlementAmount()))
                .setNanos(moneyNanos(charge.grossSettlementAmount()))
                .build())
            .setNetSettlementAmount(Money.newBuilder()
                .setCurrencyCode("USD")
                .setUnits(moneyUnits(charge.netSettlementAmountInUSD()))
                .setNanos(moneyNanos(charge.netSettlementAmountInUSD()))
                .build())
            .setPredictedValueDate(Timestamp.newBuilder().setNanos(Instant.now().getNano()).build())
            .setReleaseDate(Timestamp.newBuilder().setNanos(charge.settlementDate().getNano()).build())
            .setCentralProcessingDate(Timestamp.newBuilder().setNanos(Instant.now().getNano()).build())
            .setBankBatchId(charge.sequenceNumber())
            .build())
        .build();
  }
}
