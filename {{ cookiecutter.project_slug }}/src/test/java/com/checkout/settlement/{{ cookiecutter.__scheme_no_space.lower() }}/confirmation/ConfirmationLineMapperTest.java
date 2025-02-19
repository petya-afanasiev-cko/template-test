package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.confirmation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ConfirmationLineMapperTest {

  @Test
  void mapLineTest() {
    // Given
    var target = new ConfirmationLineMapper();
    var line = "010580000040004400221           20241106000000                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  ";
    var actual = target.mapLine(line, 1);
    assertThat(actual.getType()).isEqualTo("Header");
  }
}