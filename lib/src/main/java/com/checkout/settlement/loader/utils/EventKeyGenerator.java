package com.checkout.settlement.loader.utils;

import cko.card_processing_settlement.SettlementEvent;
import cko.card_processing_settlement_data_controls.CkoCardProcessingSettlementDataControls.RawSettlementRecord;
import cko.card_processing_settlement_data_controls.CkoCardProcessingSettlementDataControls.SettlementTotalsEventPublic;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EventKeyGenerator {

  // Generate a new guid for a correlationId
  public static String generateCorrelationId() {
    return java.util.UUID.randomUUID().toString();
  }

  // Generate a new guid for a eventId
  public static String generateEventId() {
    return java.util.UUID.randomUUID().toString();
  }

  public static String generateSettlementEventIdempotencyKey(SettlementEvent.Builder event) {
    try {
      var eventTimestamp = event.getEventTimestamp();
      var correlationId = event.getCorrelationId();

      event.clearCorrelationId();
      event.clearEventTimestamp();
      event.clearIdempotencyKey();

      byte[] byteArray = event.build().toByteArray();
      var idempotencyKey = calculateSHA256Hash(byteArray);

      event.setEventTimestamp(eventTimestamp);
      event.setCorrelationId(correlationId);
      return idempotencyKey;
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate idempotency key for SettlementEvent", e);
    }
  }

  public static String generateRawSettlementRecordIdempotencyKey(RawSettlementRecord.Builder event) {
    try {
      var eventId = event.getEventId();
      var correlationId = event.getCorrelationId();
      var eventMetadata = event.getEventMetadata();

      event.clearCorrelationId();
      event.clearEventId();
      event.clearEventMetadata();

      byte[] byteArray = event.build().toByteArray();
      var idempotencyKey = (calculateSHA256Hash(byteArray));

      event.setEventId(eventId);
      event.setCorrelationId(correlationId);
      event.setEventMetadata(eventMetadata);
      return idempotencyKey;
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate idempotency key for RawSettlementEvent", e);
    }
  }

  public static String generateSettlementTotalEventIdempotencyKey(SettlementTotalsEventPublic.Builder event) {
    try {
      var eventId = event.getEventId();
      var correlationId = event.getCorrelationId();
      var eventMetadata = event.getEventMetadata();

      event.clearCorrelationId();
      event.clearEventId();
      event.clearEventMetadata();

      byte[] byteArray = event.build().toByteArray();
      var idempotencyKey = (calculateSHA256Hash(byteArray));

      event.setEventId(eventId);
      event.setCorrelationId(correlationId);
      event.setEventMetadata(eventMetadata);
      return idempotencyKey;
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate idempotency key for SettlementTotalsEventPublic", e);
    }
  }

    public static String calculateSHA256Hash(String... input) {
      String concatenatedInput = String.join("-", input);
      return calculateSHA256Hash(concatenatedInput.getBytes());
    }

    private static String calculateSHA256Hash(byte[] input) {
      try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(input);
        return Base64.getEncoder().encodeToString(hashBytes);
      } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException("Hash generation failed", e);
      }
    }
}