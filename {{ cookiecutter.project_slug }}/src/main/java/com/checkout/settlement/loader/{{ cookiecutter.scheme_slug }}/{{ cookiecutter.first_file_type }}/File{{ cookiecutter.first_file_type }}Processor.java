package com.checkout.settlement.loader.{{ cookiecutter.scheme_slug }}.{{ cookiecutter.first_file_type }};

import cko.card_processing_settlement.EventType;
import cko.card_processing_settlement.SettlementEvent;
import com.checkout.settlement.loader.reader.FileTree.RecordContext;
import com.checkout.settlement.loader.writer.OutgoingEvent;
import com.checkout.settlement.loader.reader.FileLineRecord;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.annotations.NotNull;

import com.checkout.settlement.loader.{{ cookiecutter.scheme_slug }}.{{ cookiecutter.first_file_type }}.model.Header;
import com.checkout.settlement.loader.{{ cookiecutter.scheme_slug }}.{{ cookiecutter.first_file_type }}.model.Item;

@Log4j2
@Component
@StepScope
@RequiredArgsConstructor
public class File{{ cookiecutter.first_file_type }}Processor implements ItemProcessor<RecordContext<FileLineRecord>, OutgoingEvent> {
  
  @Override
  public OutgoingEvent process(@NotNull RecordContext<FileLineRecord> item) throws Exception {
    SettlementEvent settlementEvent = null;
    if (item.value() instanceof Header header) {
      settlementEvent = SettlementEvent.newBuilder()
          .setOriginalFileLineNumber(Integer.toString(header.lineNumber()))
          .setOriginalFileName(header.fileName())
          .setIdempotencyKey(header.field1())
          .setCorrelationId(header.field2())
          .setEventType(EventType.EVENT_TYPE_UNSPECIFIED)
          .build();
    } else if (item.value() instanceof Item itemValue) {
      settlementEvent = SettlementEvent.newBuilder()
          .setOriginalFileLineNumber(Integer.toString(itemValue.lineNumber()))
          .setOriginalFileName(itemValue.fileName())
          .setIdempotencyKey(itemValue.field1())
          .setCorrelationId(itemValue.field2())
          .setEventType(EventType.SETTLEMENT)
          .build();
    }
    // TODO: implement RawSettlementRecord and settlementTotalsEvent creation
    return new OutgoingEvent(Optional.ofNullable(settlementEvent), null, null);
  }

}
