package com.checkout.settlement.loader.writer.properties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka")
public record KafkaCustomProperties(
    String settlementTopic,
    String rawSettlementTopic,
    String settlementTotalsTopic
) {}
