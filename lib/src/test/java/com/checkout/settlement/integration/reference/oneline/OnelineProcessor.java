package com.checkout.settlement.integration.reference.oneline;

import cko.card_processing_settlement.EventType;
import cko.card_processing_settlement.SettlementEvent;
import cko.card_processing_settlement_data_controls.CkoCardProcessingSettlementDataControls.DefaultRecord;
import cko.card_processing_settlement_data_controls.CkoCardProcessingSettlementDataControls.RawSettlementRecord;
import com.checkout.settlement.loader.processor.SettlementEventProcessor;
import com.checkout.settlement.loader.reader.FileLineRecord;
import com.checkout.settlement.loader.reader.FileTree.RecordContext;
import com.checkout.settlement.integration.reference.oneline.model.Header;
import com.checkout.settlement.integration.reference.oneline.model.Item;
import com.checkout.settlement.integration.reference.oneline.model.OnelineRecord;
import java.util.Optional;
import com.checkout.settlement.loader.writer.OutgoingEventBuilders;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.annotations.NotNull;

@Log4j2
@Component
@StepScope
@RequiredArgsConstructor
public class OnelineProcessor extends SettlementEventProcessor<FileLineRecord> {
  
  @Override
  protected OutgoingEventBuilders processInternal(@NotNull RecordContext<FileLineRecord> item) throws Exception {
    SettlementEvent.Builder settlementEvent = null;
    RawSettlementRecord.Builder rawSettlementRecord = RawSettlementRecord.newBuilder();
    if (item.value() instanceof Header header) {
      rawSettlementRecord.setDefaultRecord(DefaultRecord.newBuilder()
          .setRecordContent(header.lineContents())
      );
    } else if (item.value() instanceof Item itemValue) {
      rawSettlementRecord.setDefaultRecord(DefaultRecord.newBuilder()
          .setRecordContent(itemValue.lineContents())
      );
      settlementEvent = SettlementEvent.newBuilder()
          .setOriginalFileLineNumber(Integer.toString(itemValue.lineNumber()))
          .setOriginalFileName(itemValue.fileName())
          .setIdempotencyKey(itemValue.field1())
          .setCorrelationId(itemValue.field2())
          .setEventType(EventType.SETTLEMENT);
    }
    return new OutgoingEventBuilders(
        Optional.ofNullable(settlementEvent),
        Optional.empty(),
        rawSettlementRecord);
  }

  @Override
  protected String generatePrimaryKey(RecordContext<FileLineRecord> item) {
    return "myscheme"+item.value().fileName()+item.value().lineNumber();
  }

}
