package com.checkout.settlement.loader.configuration;

import cko.card_processing_settlement.SettlementEvent;
import cko.card_processing_settlement_data_controls.CkoCardProcessingSettlementDataControls.RawSettlementRecord;
import cko.card_processing_settlement_data_controls.CkoCardProcessingSettlementDataControls.SettlementTotalsEventPublic;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaProducerConfig {

    @Bean
    public KafkaTemplate<String, SettlementEvent> kafkaTemplate(KafkaProperties kafkaProperties) {
        var kafkaPropertiesMap = kafkaProperties.buildProducerProperties(null);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaPropertiesMap));
    }

    @Bean
    public KafkaTemplate<String, RawSettlementRecord> rawSettlementRecordKafkaTemplate(KafkaProperties kafkaProperties) {
      var kafkaPropertiesMap = kafkaProperties.buildProducerProperties(null);
      return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaPropertiesMap));
    }

    @Bean
    public KafkaTemplate<String, SettlementTotalsEventPublic> settlementTotalsEventPublicKafkaTemplate(KafkaProperties kafkaProperties) {
      var kafkaPropertiesMap = kafkaProperties.buildProducerProperties(null);
      return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaPropertiesMap));
    }
}