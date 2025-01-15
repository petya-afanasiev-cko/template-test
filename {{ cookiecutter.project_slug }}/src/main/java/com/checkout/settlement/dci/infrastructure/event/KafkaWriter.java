package com.checkout.settlement.dci.infrastructure.event;

import cko.card_processing_settlement.SettlementEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class KafkaWriter implements ItemWriter<SettlementEvent> {

  @Override
  public void write(Chunk<? extends SettlementEvent> chunk) {
    for (var event : chunk) {
      log.info("writing event {}", event);
    }
  }
}
