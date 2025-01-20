package com.checkout.settlement.loader.writer;

import cko.card_processing_settlement.SettlementEvent;
import cko.card_processing_settlement_data_controls.CkoCardProcessingSettlementDataControls.RawSettlementRecord;
import cko.card_processing_settlement_data_controls.CkoCardProcessingSettlementDataControls.SettlementTotalsEventPublic;
import java.util.Optional;

public record OutgoingEventBuilders (
  Optional<SettlementEvent.Builder> settlementEvent,
  Optional<SettlementTotalsEventPublic.Builder> settlementTotalsEvent,
  RawSettlementRecord.Builder rawEvent){
}
