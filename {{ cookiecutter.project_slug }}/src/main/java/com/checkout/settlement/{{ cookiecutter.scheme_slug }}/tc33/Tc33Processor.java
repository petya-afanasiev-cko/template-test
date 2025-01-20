package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.tc33;

import cko.card_processing_settlement.EventType;
import cko.card_processing_settlement.SettlementEvent;
import cko.card_processing_settlement_data_controls.CkoCardProcessingSettlementDataControls.DefaultRecord;
import cko.card_processing_settlement_data_controls.CkoCardProcessingSettlementDataControls.RawSettlementRecord;
import cko.card_processing_settlement_data_controls.CkoCardProcessingSettlementDataControls.SettlementRecordMetadata;
import cko.card_processing_settlement_data_controls.CkoCardProcessingSettlementDataControls.SourceFileInfo;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.tc33.model.CAS;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.tc33.model.FileHeader;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.tc33.model.FileTrailer;
import com.checkout.settlement.{{ cookiecutter.scheme_slug }}.tc33.model.Tc33Record;
import com.checkout.settlement.loader.processor.SettlementEventProcessor;
import com.checkout.settlement.loader.reader.FileTree.RecordContext;
import com.checkout.settlement.loader.writer.OutgoingEvent;
import java.util.Optional;
import com.checkout.settlement.loader.writer.OutgoingEventBuilders;
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
public class Tc33Processor extends SettlementEventProcessor<Tc33Record> {
  
  @Override
  public OutgoingEventBuilders processInternal(@NotNull RecordContext<Tc33Record> item) {
    RawSettlementRecord.Builder rawEvent = RawSettlementRecord.newBuilder()
        .setEventMetadata(SettlementRecordMetadata.newBuilder()
            .setSourceFileInfo(SourceFileInfo.newBuilder()
                .setOriginalFileName(item.value().fileName())
                .setOriginalFileLineNumber(item.value().lineNumber())
                .build())
        )
        .setDefaultRecord(DefaultRecord.newBuilder()
            .setRecordContent(item.value().lineContents())
            .build());

    return new OutgoingEventBuilders(
        Optional.empty(),
        Optional.empty(),
        rawEvent
    );
  }

  @Override
  protected String generatePrimaryKey(RecordContext<Tc33Record> item) {
    return String.join("-", item.value().fileName(), String.valueOf(item.value().lineNumber()));
  }
}
