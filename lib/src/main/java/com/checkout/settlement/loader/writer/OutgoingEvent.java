package com.checkout.settlement.loader.writer;

import cko.card_processing_settlement.SettlementEvent;
import cko.card_processing_settlement_data_controls.CkoCardProcessingSettlementDataControls.RawSettlementRecord;
import cko.card_processing_settlement_data_controls.CkoCardProcessingSettlementDataControls.SettlementTotalsEventPublic;
import java.util.Optional;

public record OutgoingEvent(
    Optional<SettlementEvent> settlementEvent,
    Optional<SettlementTotalsEventPublic> settlementTotalsEvent,
    RawSettlementRecord rawEvent){
}
