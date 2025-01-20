package com.checkout.settlement.loader.processor;

import cko.card_processing_settlement.SettlementEvent;
import cko.card_processing_settlement_data_controls.CkoCardProcessingSettlementDataControls.RawSettlementRecord;
import cko.card_processing_settlement_data_controls.CkoCardProcessingSettlementDataControls.SettlementTotalsEventPublic;
import com.checkout.settlement.loader.reader.FileLineRecord;
import com.checkout.settlement.loader.reader.FileTree.RecordContext;
import com.checkout.settlement.loader.utils.EventKeyGenerator;
import com.checkout.settlement.loader.writer.OutgoingEvent;
import com.checkout.settlement.loader.writer.OutgoingEventBuilders;
import org.springframework.lang.NonNull;
import org.springframework.batch.item.ItemProcessor;

public abstract class SettlementEventProcessor<T extends FileLineRecord> implements
    ItemProcessor<RecordContext<T>, OutgoingEvent> {
  @Override
  public OutgoingEvent process(@NonNull RecordContext<T> item) throws Exception{
    OutgoingEventBuilders events = processInternal(item);
    String primaryKey = generatePrimaryKey(item);
    events.rawEvent()
        .setPrimaryKey(primaryKey)
        .setIdempotencyKey(generateIdempotencyKey(events.rawEvent()));
    events.settlementEvent().ifPresent(e -> {
      e.setPrimaryKey(primaryKey);
      e.setIdempotencyKey(generateIdempotencyKey(e));
    });
    events.settlementTotalsEvent().ifPresent(e -> {
      e.setPrimaryKey(primaryKey);
      e.setIdempotencyKey(generateIdempotencyKey(e));
    });
    return new OutgoingEvent(
        events.settlementEvent().map(SettlementEvent.Builder::build),
        events.settlementTotalsEvent().map(SettlementTotalsEventPublic.Builder::build),
        events.rawEvent().build()
        );
  }

  protected String generateIdempotencyKey(SettlementTotalsEventPublic.Builder event){
    return EventKeyGenerator.generateSettlementTotalEventIdempotencyKey(event);
  }
  protected String generateIdempotencyKey(RawSettlementRecord.Builder event){
    return EventKeyGenerator.generateRawSettlementRecordIdempotencyKey(event);
  }
  protected String generateIdempotencyKey(SettlementEvent.Builder event){
    return EventKeyGenerator.generateSettlementEventIdempotencyKey(event);
  }
  protected abstract String generatePrimaryKey(RecordContext<T> item);

  protected abstract OutgoingEventBuilders processInternal(RecordContext<T> item) throws Exception;
}
