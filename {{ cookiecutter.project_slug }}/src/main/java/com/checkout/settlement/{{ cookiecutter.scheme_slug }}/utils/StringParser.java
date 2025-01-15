package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.utils;

import lombok.experimental.UtilityClass;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;
import static java.util.function.Predicate.not;

@UtilityClass
public final class StringParser {

  static final DateTimeFormatter BASIC_ISO_DATE_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
  static final DateTimeFormatter BASIC_ISO_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
  static final BigDecimal NANO_UNITS = BigDecimal.TEN.pow(9);

  public static Optional<String> optionalString(String value) {
    return Optional.ofNullable(value).filter(not(String::isBlank));
  }
  
  public static Optional<LocalDate> parseOptionalIsoDate(String date) {
    return optionalString(date).map(StringParser::parseIsoDate);
  }

  public static LocalDate parseIsoDate(String date) {
    return LocalDate.parse(date, BASIC_ISO_DATE);
  }
  
  public static LocalDateTime parseIsoDateTime(String dateTime) {
    return LocalDateTime.parse(dateTime, BASIC_ISO_DATE_TIME);
  }

  public static BigDecimal parseBigDecimal(String amount) {
    return optionalString(amount).map(BigDecimal::new).orElse(ZERO);
  }

  public static BigDecimal parseCurrencyAmount(String amount, String currencyCode) {
    return toCurrencyAmount(parseBigDecimal(amount), currencyCode);
  }

  public static BigDecimal toCurrencyAmount(BigDecimal amount, String currencyCode) {
    var fractionDigits = Currency.getInstance(currencyCode).getDefaultFractionDigits();
    // TOOD: validate scheme aligns with ISO standards
    return amount.movePointLeft(fractionDigits);
  }
  
  public static long moneyUnits(BigDecimal amount) {
    return amount.longValue();
  }

  public static int moneyNanos(BigDecimal amount) {
    return amount.subtract(BigDecimal.valueOf(amount.longValue())).multiply(NANO_UNITS).intValue();
  }
}
