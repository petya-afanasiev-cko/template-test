package com.checkout.settlement.dci.infrastructure.reader;

public interface FileLineRecord {
  String fileName();
  int lineNumber();
  default String getType() {
    return getClass().getSimpleName();
  }
}
