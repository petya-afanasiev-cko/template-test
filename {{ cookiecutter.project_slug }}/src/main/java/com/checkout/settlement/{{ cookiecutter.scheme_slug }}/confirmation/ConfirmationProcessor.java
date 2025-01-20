package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.confirmation;

import static com.checkout.settlement.loader.utils.StringParser.moneyNanos;
import static com.checkout.settlement.loader.utils.StringParser.moneyUnits;
import static com.checkout.settlement.loader.utils.StringParser.parseBigDecimal;
import static com.checkout.settlement.loader.utils.StringParser.parseIsoDate;

import cko.card_processing_settlement.Scheme;
import cko.card_processing_settlement.Settlement;
import cko.card_processing_settlement.SettlementEvent;
import cko.card_processing_settlement.SettlementType;
import cko.card_processing_settlement_data_controls.CkoCardProcessingSettlementDataControls.RawSettlementRecord;
import cko.card_processing_settlement_data_controls.CkoCardProcessingSettlementDataControls.SettlementRecordMetadata;
import cko.card_processing_settlement_data_controls.CkoCardProcessingSettlementDataControls.SettlementTotalsEventData;
import cko.card_processing_settlement_data_controls.CkoCardProcessingSettlementDataControls.SettlementTotalsEventPublic;
import cko.card_processing_settlement_data_controls.CkoCardProcessingSettlementDataControls.SourceFileInfo;
import cko.card_processing_settlement_{{ cookiecutter.scheme_slug }}.CkoCardProcessingSettlement{{ cookiecutter.__scheme_no_space }};
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.confirmation.model.Charge;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.confirmation.model.ChargeTotal;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.confirmation.model.ConfirmationRecord;
import com.checkout.settlement.loader.processor.SettlementEventProcessor;
import com.checkout.settlement.loader.reader.FileTree.RecordContext;
import com.checkout.settlement.loader.utils.EventKeyGenerator;
import com.checkout.settlement.loader.writer.OutgoingEvent;
import com.checkout.settlement.loader.writer.OutgoingEventBuilders;
import com.google.protobuf.Timestamp;
import com.google.type.Money;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.annotations.NotNull;

@Log4j2
@Component
@StepScope
@RequiredArgsConstructor
public class ConfirmationProcessor extends SettlementEventProcessor<ConfirmationRecord> {
  @Override
  protected String generatePrimaryKey(RecordContext<ConfirmationRecord> item) {
      if (Objects.requireNonNull(item.value()) instanceof Charge charge) {
          return String.join("-", charge.referenceNumber(), charge.networkReferenceId());
      }
      return String.join("-", String.valueOf(item.value().lineNumber()), item.value().fileName());
  }

  @Override
  protected OutgoingEventBuilders processInternal(RecordContext<ConfirmationRecord> item) {
    var rawSettlementRecordBuilder = rawSettlementEventBuilder(item);
    var metadataBuilder = rawSettlementEventMetadataBuilder(item);
    var settlementTotalsEventPublic = (SettlementTotalsEventPublic.Builder)null;
    var settlementEvent = (SettlementEvent.Builder)null;

    if (item.value() instanceof Charge) {
      settlementEvent = presentmentSettled(item);
      rawSettlementRecordBuilder.setCharge(rawCharge(item));
      metadataBuilder.setRecordType(Charge.class.getSimpleName());
    }

    if (item.value() instanceof ChargeTotal) {
      settlementTotalsEventPublic = settlementTotalsEventPublic(item);
    }

    rawSettlementRecordBuilder.setEventMetadata(metadataBuilder.build());

    return new OutgoingEventBuilders(
        Optional.ofNullable(settlementEvent),
        Optional.ofNullable(settlementTotalsEventPublic),
        rawSettlementRecordBuilder);
  }

  static SettlementEvent.Builder presentmentSettled(@NotNull final RecordContext<ConfirmationRecord> item) {
    var charge = (Charge) item.value();
    return SettlementEvent.newBuilder()
        .setAcquirerReferenceNumber(charge.referenceNumber())
        .setCorrelationId("")
        .setEventTimestamp(Timestamp.newBuilder().setNanos(Instant.now().getNano()).build())
        .setOriginalFileName(charge.fileName())
        .setOriginalFileLineNumber(String.valueOf(charge.lineNumber()))
        .setScheme(Scheme.{{ cookiecutter.__scheme_no_space.upper() }})
        .setSettlementCountry("")
        .setSettlement(Settlement.newBuilder()
            .setSettlementType(SettlementType.PRESENTMENT_SETTLED)
            .setGrossSettlementAmount(Money.newBuilder()
                .setCurrencyCode("{{ cookiecutter.scheme_slug }}_recap.currency_key")
                .setUnits(moneyUnits(parseBigDecimal(charge.grossSettlementAmount())))
                .setNanos(moneyNanos(parseBigDecimal(charge.grossSettlementAmount())))
                .build())
            .setNetSettlementAmount(Money.newBuilder()
                .setCurrencyCode("USD")
                .setUnits(moneyUnits(parseBigDecimal(charge.netSettlementAmountInUSD())))
                .setNanos(moneyNanos(parseBigDecimal(charge.netSettlementAmountInUSD())))
                .build())
            .setPredictedValueDate(Timestamp.newBuilder().setNanos(Instant.now().getNano()).build())
            .setReleaseDate(Timestamp.newBuilder().setNanos(
                parseIsoDate(charge.settlementDate()).atStartOfDay().toInstant(ZoneOffset.UTC).getNano()).build())
            .setCentralProcessingDate(Timestamp.newBuilder().setNanos(Instant.now().getNano()).build())
            .setBankBatchId(charge.sequenceNumber())
            .build());
  }
  static CkoCardProcessingSettlement{{ cookiecutter.__scheme_no_space }}.Charge rawCharge(@NotNull final RecordContext<ConfirmationRecord> item) {
    var charge = (Charge) item.value();
    return CkoCardProcessingSettlement{{ cookiecutter.__scheme_no_space }}.Charge.newBuilder()
        .setRecordType(charge.recordType())
        .setSequenceNumber(charge.sequenceNumber())
        .setCardNumber(charge.cardNumber())
        .setChargeDate(charge.chargeDate())
        .setChargeType(charge.chargeType())
        .setTypeOfCharge(charge.typeOfCharge())
        .setAuthorizationNumber(charge.authorizationNumber())
        .setProgramTransactionRate(charge.programTransactionRate())
        .setInterchangePtfInUsd(charge.interchangePTFinUSD())
        .setGrossChargeAmount(charge.grossChargeAmount())
        .setNetChargeAmount(charge.netChargeAmount())
        .setAlternateCurrencyGrossAmount(charge.alternateCurrencyGrossAmount())
        .setAlternateCurrencyNetAmount(charge.alternateCurrencyNetAmount())
        .setInterchangeCommissionInTransactionCurrency(charge.interchangeCommissionInTransactionCurrency())
        .setInterchangeCommissionInAlternateCurrency(charge.interchangeCommissionInAlternateCurrency())
        .setGrossSettlementAmount(charge.grossSettlementAmount())
        .setNetSettlementAmount(charge.netSettlementAmount())
        .setInterchangeCommissionSettlementAmount(charge.interchangeCommissionSettlementAmount())
        .setGrossSettlementAmountInUsd(charge.grossSettlementAmountInUSD())
        .setNetSettlementAmountInUsd(charge.netSettlementAmountInUSD())
        .setInterchangeCommissionInUsd(charge.interchangeCommissionInUSD())
        .setInterchangePtfInSettlementCurrency(charge.interchangePTFInSettlementCurrency())
        .setPricingRuleName(charge.pricingRuleName())
        .setPricingRuleCode(charge.pricingRuleCode())
        .setPricingRuleSerialNumber(charge.pricingRuleSerialNumber())
        .setSettlementDate(charge.settlementDate())
        .setEci(charge.eci())
        .setCavv(charge.cavv())
        .setNetworkReferenceId(charge.networkReferenceId())
        .setAtmInterchangeFeeInUsd(charge.ATMInterchangeFeeInUSD())
        .setAtmSecurityFeeInUsd(charge.ATMSecurityFeeInUSD())
        .setAtmNetworkInternationalProcessingFeeInUsd(charge.ATMNetworkInternationalProcessingFeeInUSD())
        .setAtmInterchangeFeeInSettlementCurrency(charge.ATMInterchangeFeeInSettlementCurrency())
        .setAtmSecurityFeeInSettlementCurrency(charge.ATMSecurityFeeInSettlementCurrency())
        .setAtmNetworkInternationalProcessingFeeInSettlementCurrency(charge.ATMNetworkInternationalProcessingFeeInSettlementCurrency())
        .setSurchargeFee(charge.surchargeFee())
        .setAcquirerGeoCode(charge.acquirerGeoCode())
        .setCardProductType(charge.cardProductType())
        .setMccCode(charge.mccCode())
        .setIntesCode(charge.intesCode())
        .setMerchantId(charge.merchantId())
        .setCardholderPresent(charge.cardholderPresent())
        .setCardPresent(charge.cardPresent())
        .setCaptureMethod(charge.captureMethod())
        .setMerchantGeoCode(charge.merchantGeoCode())
        .setIssuerGeoCode(charge.issuerGeoCode())
        .setMerchantPan(charge.merchantPAN())
        .setSpecialConditionsIndicator(charge.specialConditionsIndicator())
        .build();
  }

  static RawSettlementRecord.Builder rawSettlementEventBuilder(@NotNull final RecordContext<ConfirmationRecord> recordContext) {
    return RawSettlementRecord.newBuilder()
        .setEventId(EventKeyGenerator.generateEventId())
        .setCorrelationId(EventKeyGenerator.generateCorrelationId())
        .setScheme(Scheme.{{ cookiecutter.__scheme_no_space.upper() }});
  }

  static SettlementRecordMetadata.Builder rawSettlementEventMetadataBuilder(@NotNull final RecordContext<ConfirmationRecord> recordContext) {
    return SettlementRecordMetadata.newBuilder()
        .setEventTimestamp(Timestamp.newBuilder().setNanos(Instant.now().getNano()).build())
        .setSourceFileInfo(SourceFileInfo.newBuilder()
            .setOriginalFileName(recordContext.value().fileName())
            .setOriginalFileLineNumber(recordContext.value().lineNumber())
        );
  }

  static SettlementTotalsEventPublic.Builder settlementTotalsEventPublic(@NotNull final RecordContext<ConfirmationRecord> recordContext) {
    return SettlementTotalsEventPublic.newBuilder()
        .setEventId(EventKeyGenerator.generateEventId())
        .setCorrelationId(EventKeyGenerator.generateCorrelationId())
        .setSettlementTotalsGroupKey("TODO: implement settlement_totals_group_key for {{ cookiecutter.__scheme_no_space.upper() }}")
        .setEventData(SettlementTotalsEventData.newBuilder()
            .setTotalGrossAmount(cko.Extensions.Money.newBuilder().setCurrencyCode("USD").setUnits(0).setNanos(0).build()).build())
        .setScheme(Scheme.{{ cookiecutter.__scheme_no_space.upper() }});
  }
}
