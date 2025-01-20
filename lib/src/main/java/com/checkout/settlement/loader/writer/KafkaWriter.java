package com.checkout.settlement.loader.writer;

import cko.card_processing_settlement.SettlementEvent;
import cko.card_processing_settlement_data_controls.CkoCardProcessingSettlementDataControls.RawSettlementRecord;
import cko.card_processing_settlement_data_controls.CkoCardProcessingSettlementDataControls.SettlementTotalsEventPublic;
import com.checkout.settlement.loader.writer.properties.KafkaCustomProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.ExecutionException;

@Log4j2
@Component
@RequiredArgsConstructor
public class KafkaWriter implements ItemWriter<OutgoingEvent> {
  
  private final KafkaTemplate<String, SettlementEvent> sdpSettlementKafkaTemplate;
  private final KafkaTemplate<String, RawSettlementRecord> rawSettlementRecordKafkaTemplate;
  private final KafkaTemplate<String, SettlementTotalsEventPublic> settlementTotalsKafkaTemplate;
  private final KafkaCustomProperties kafkaCustomProperties;

  @Override
  public void write(Chunk<? extends OutgoingEvent> chunk)
      throws ExecutionException, InterruptedException {
    for (var event : chunk) {
      if (event.settlementEvent().isPresent()) {
        var future = sdpSettlementKafkaTemplate.send(kafkaCustomProperties.settlementTopic(), event.settlementEvent().get().getPrimaryKey(), event.settlementEvent().get());
        future.whenComplete((result, ex) -> {
          if (ex == null) {
            log.info("Sent event {} to topic {}", result.getProducerRecord().value(), kafkaCustomProperties.settlementTopic());
          } else {
            log.error("An error occurred while sending kafka message for event with value {}", event);
          }
        }).get();
      }

      if (event.settlementTotalsEvent().isPresent()) {
        var future = settlementTotalsKafkaTemplate.send(kafkaCustomProperties.settlementTotalsTopic(), event.settlementTotalsEvent().get().getPrimaryKey(), event.settlementTotalsEvent().get());
        future.whenComplete((result, ex) -> {
          if (ex == null) {
            log.info("Sent event {} to topic {}", result.getProducerRecord().value(), kafkaCustomProperties.settlementTotalsTopic());
          } else {
            log.error("An error occurred while sending kafka message for event with value {}", event);
          }
        }).get();
      }

      var future = rawSettlementRecordKafkaTemplate.send(kafkaCustomProperties.rawSettlementTopic(), event.rawEvent().getPrimaryKey(), event.rawEvent());
      future.whenComplete((result, ex) -> {
        if (ex == null) {
          log.info("Sent raw event {} to topic {}", result.getProducerRecord().value(), kafkaCustomProperties.rawSettlementTopic());
        } else {
          log.error("An error occurred while sending kafka message for event with value {}", event);
        }
      }).get();
    }
  }
}
